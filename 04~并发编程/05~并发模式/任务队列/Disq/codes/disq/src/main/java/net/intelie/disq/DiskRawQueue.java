package net.intelie.disq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

public class DiskRawQueue implements RawQueue {
    public static final int FAILED_READ_THRESHOLD = 64;
    private static final Logger LOGGER = LoggerFactory.getLogger(DiskRawQueue.class);
    private final long maxSize;
    private final long dataFileLimit;
    private final boolean flushOnRead;
    private final boolean flushOnWrite;

    private boolean temp;
    private Path directory;
    private boolean closed = false;
    private StateFile state;
    private DataFileReader reader;
    private DataFileWriter writer;
    private int failedReads = 0;
    private long flushCount = 0;

    public DiskRawQueue(Path directory, long maxSize) {
        this(directory, maxSize, true, true);
    }

    public DiskRawQueue(Path directory, long maxSize, boolean flushOnPop, boolean flushOnPush) {
        this.directory = directory;
        this.maxSize = Math.max(Math.min(maxSize, StateFile.MAX_QUEUE_SIZE), StateFile.MIN_QUEUE_SIZE);
        this.dataFileLimit = Math.max(512, this.maxSize / StateFile.MAX_FILES + (this.maxSize % StateFile.MAX_FILES > 0 ? 1 : 0));

        this.flushOnRead = flushOnPop;
        this.flushOnWrite = flushOnPush;
        this.temp = false;

        reopen();
    }

    @Override
    public synchronized void reopen() {
        internalClose();
        closed = false;
    }

    public Path path() {
        return directory;
    }

    private void internalOpen() throws IOException {
        internalClose();
        if (this.directory == null) {
            this.directory = Files.createTempDirectory("disq");
            this.temp = true;
        }
        Files.createDirectories(this.directory);
        this.state = new StateFile(this.directory.resolve("state"), false);
        this.writer = null;
        this.reader = null;
        gc();
    }

    private synchronized boolean safeTouch() {
        try {
            touch();
            return true;
        } catch (Throwable e) {
            return false;
        }
    }

    public synchronized void touch() throws IOException {
        checkNotClosed();
        if (state == null)
            internalOpen();
    }

    private void checkNotClosed() {
        if (closed)
            throw new IllegalStateException("This queue is already closed.");
    }

    @Override
    public synchronized long bytes() {
        if (!safeTouch()) return 0;
        return state.getBytes();
    }

    @Override
    public synchronized long count() {
        if (!safeTouch()) return 0;
        return state.getCount();
    }

    public synchronized long files() {
        if (!safeTouch()) return 0;
        return state.getNumberOfFiles();
    }

    @Override
    public synchronized long remainingBytes() {
        if (!safeTouch()) return 0;
        return maxSize - state.getBytes();
    }

    public long flushCount() {
        return flushCount;
    }

    @Override
    public synchronized long remainingCount() {
        if (!safeTouch()) return 0;

        if (state.getCount() == 0) return maxSize / 4;
        double bytesPerElement = state.getBytes() / (double) state.getCount();
        return (long) ((maxSize - state.getBytes()) / bytesPerElement);
    }

    @Override
    public synchronized void clear() throws IOException {
        touch();

        state.clear();
        internalFlush();
        reopen();
    }

    @Override
    public synchronized boolean pop(Buffer buffer) throws IOException {
        touch();

        if (!checkFailedReads())
            return false;

        if (checkReadEOF())
            return false;

        int read = innerRead(buffer);

        state.addReadCount(read);
        if (flushOnRead)
            internalFlush();

        checkReadEOF();
        return true;
    }

    private boolean checkFailedReads() throws IOException {
        if (failedReads >= FAILED_READ_THRESHOLD) {
            LOGGER.info("Detected corrupted file #{}, backing up and moving on.", state.getReadFile());
            boolean wasSame = state.sameFileReadWrite();
            deleteOldestFile(true);
            if (wasSame) {
                clear();
                return false;
            }
        }
        return true;
    }

    private int innerRead(Buffer buffer) throws IOException {
        try {
            int read = reader().read(buffer);
            failedReads = 0;
            return read;
        } catch (Throwable e) {
            failedReads++;
            throw e;
        }
    }

    @Override
    public synchronized boolean peek(Buffer buffer) throws IOException {
        touch();

        if (checkReadEOF())
            return false;

        reader().peek(buffer);
        return true;
    }

    private void deleteOldestFile(boolean renameFile) throws IOException {
        int currentFile = state.getReadFile();
        state.advanceReadFile(reader().size());
        reader.close();
        failedReads = 0;

        internalFlush();
        reader = null;
        tryDeleteFile(currentFile, renameFile);
    }


    @Override
    public synchronized void push(Buffer buffer) throws IOException {
        touch();

        checkWriteEOF();
        deleteOldIfNeeded(buffer.count());

        int written = writer().write(buffer);
        state.addWriteCount(written);
        if (flushOnWrite)
            internalFlush();

        checkWriteEOF();
    }


    private void deleteOldIfNeeded(int count) throws IOException {
        while (!state.sameFileReadWrite() && willOverflow(count))
            deleteOldestFile(false);
    }

    @Override
    public synchronized void flush() throws IOException {
        touch();
        internalFlush();
    }

    private void internalFlush() throws IOException {
        if (writer != null)
            writer.flush();
        state.flush();
        flushCount++;
    }

    @Override
    public synchronized void close() {
        closed = true;
        internalClose();
    }

    private void internalClose() {
        Lenient.safeClose(reader);
        reader = null;

        Lenient.safeClose(writer);
        writer = null;

        Lenient.safeClose(state);
        state = null;

        if (temp) {
            Lenient.safeDelete(directory);
            directory = null;
            temp = false;
        }
    }


    private boolean willOverflow(int count) throws IOException {
        return bytes() + count + DataFileWriter.OVERHEAD > maxSize || files() >= StateFile.MAX_FILES;
    }

    private boolean checkReadEOF() throws IOException {
        while (!state.sameFileReadWrite() && state.readFileEof())
            deleteOldestFile(false);
        if (state.needsFlushBeforePop())
            internalFlush();
        return state.getCount() == 0;
    }

    private DataFileReader reader() throws IOException {
        return reader != null ? reader : (reader = openReader());
    }

    private void checkWriteEOF() throws IOException {
        if (state.getWritePosition() >= dataFileLimit)
            advanceWriteFile();
    }

    private DataFileWriter writer() throws IOException {
        return writer != null ? writer : (writer = openWriter());
    }

    private void advanceWriteFile() throws IOException {
        writer().close();
        state.advanceWriteFile();
        internalFlush();
        writer = null;
    }

    private void gc() throws IOException {
        Path file = makeDataPath(state.getReadFile());
        boolean shouldFlush = false;
        while (!Files.exists(file) && !state.sameFileReadWrite()) {
            state.advanceReadFile(0);
            file = makeDataPath(state.getReadFile());
            shouldFlush = true;
        }
        long totalBytes = 0;
        long totalCount = 0;
        for (int i = 0; i < StateFile.MAX_FILES; i++) {
            Path path = makeDataPath(i);
            if (Files.exists(path)) {
                if (!state.isInUse(i)) {
                    tryDeleteFile(i, false);
                } else {
                    totalBytes += Files.size(path);
                    totalCount += state.getFileCount(i);
                }
            }
        }

        shouldFlush |= state.fixCounts(totalCount, totalBytes);

        if (shouldFlush)
            internalFlush();

    }

    private void tryDeleteFile(int file, boolean renameFile) {
        Path from = makeDataPath(file);
        try {
            if (renameFile) {
                Path to = makeCorruptedPath(file);
                LOGGER.info("Backing up {} as {}", from, to);
                Files.move(from, to);
            } else {
                Files.delete(from);
            }
        } catch (Exception e) {
            LOGGER.info("Unable to delete file {}: {}", from, e.getMessage());
            LOGGER.debug("Stacktrace", e);
        }
    }

    private Path makeDataPath(int state) {
        return directory.resolve(String.format((Locale) null, "data%02x", state));
    }

    private Path makeCorruptedPath(int state) {
        return directory.resolve(String.format((Locale) null, "data%02x.%d.corrupted", state, System.currentTimeMillis()));
    }

    private DataFileReader openReader() throws IOException {
        return new DataFileReader(makeDataPath(state.getReadFile()), state.getReadPosition());
    }

    private DataFileWriter openWriter() throws IOException {
        Files.createDirectories(directory);
        return new DataFileWriter(makeDataPath(state.getWriteFile()), state.getWritePosition());
    }
}
