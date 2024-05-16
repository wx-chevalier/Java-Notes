package net.intelie.disq;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;

public class StateFile implements Closeable {
    public static final short MAX_FILES = 121;
    public static final short MAX_FILE_ID = Short.MAX_VALUE / MAX_FILES * MAX_FILES;
    //we use this to allow identifying when read = write because they are in fact same file
    //or we just have a full queue

    public static final int EXPECTED_SIZE = 2 * 2 + 2 * 4 + 2 * 8 + MAX_FILES * 4;
    public static final long MIN_QUEUE_SIZE = MAX_FILES * 512;
    public static final long MAX_QUEUE_SIZE = MAX_FILES * (long) Integer.MAX_VALUE;

    private final RandomAccessFile randomWrite;
    private final ByteBuffer buffer = ByteBuffer.allocate(EXPECTED_SIZE);
    private final boolean readonly;
    private int readFile, writeFile;
    private int readPosition, writePosition;
    private long count;
    private long bytes;
    private final int[] fileCounts;
    private long unflushed;
    private boolean dirty;

    public StateFile(Path file, boolean readonly) throws IOException {
        this.readonly = readonly;
        this.fileCounts = new int[MAX_FILES];
        this.randomWrite = readonly ? null : new RandomAccessFile(file.toFile(), "rw");
        if (Files.exists(file) && Files.size(file) == EXPECTED_SIZE) {
            try (DataInputStream stream = new DataInputStream(new FileInputStream(file.toFile()))) {
                readFile = stream.readShort();
                writeFile = stream.readShort();
                readPosition = stream.readInt();
                writePosition = stream.readInt();
                count = stream.readLong();
                bytes = stream.readLong();
                for (int i = 0; i < MAX_FILES; i++)
                    fileCounts[i] = stream.readInt();
                unflushed = 0;
                dirty = false;
            }
        } else {
            dirty = true;
        }
    }

    public boolean isInUse(int file) {
        int readFile = getReadFile();
        int writeFile = getWriteFile();
        boolean same = sameFileReadWrite();

        return same && readFile == file && (readPosition != 0 || writePosition != 0) ||
                !same && (readFile <= file && file <= writeFile ||
                        writeFile <= readFile && readFile <= file ||
                        file <= writeFile && writeFile <= readFile);

    }

    public void flush() throws IOException {
        if (!dirty || readonly) return;
        buffer.position(0);
        buffer.putShort((short) readFile);
        buffer.putShort((short) writeFile);
        buffer.putInt(readPosition);
        buffer.putInt(writePosition);
        buffer.putLong(count);
        buffer.putLong(bytes);
        for (int i = 0; i < MAX_FILES; i++)
            buffer.putInt(fileCounts[i]);

        randomWrite.seek(0);
        randomWrite.write(buffer.array());
        unflushed = 0;
        dirty = false;
    }

    public int getReadFile() {
        return readFile % MAX_FILES;
    }

    public boolean sameFileReadWrite() {
        return readFile == writeFile;
    }

    public int advanceReadFile(long oldBytes) {
        int oldCount = fileCounts[getReadFile()];
        fileCounts[getReadFile()] = 0;
        count -= oldCount;
        bytes -= oldBytes;
        readFile++;
        readFile %= MAX_FILE_ID;
        readPosition = 0;
        dirty = true;
        return oldCount;
    }

    public void advanceWriteFile() {
        writeFile++;
        writeFile %= MAX_FILE_ID;
        writePosition = 0;
        dirty = true;

    }

    public long getReadPosition() {
        return readPosition >= 0 ? readPosition : readPosition + (1L << 31);
    }

    public int getWriteFile() {
        return writeFile % MAX_FILES;
    }

    public long getCount() {
        return count;
    }

    public long getBytes() {
        return bytes;
    }

    public int getNumberOfFiles() {
        return (writeFile >= readFile ?
                writeFile - readFile :
                MAX_FILE_ID - readFile + writeFile) +
                (writePosition > 0 ? 1 : 0);
    }

    public void addWriteCount(int bytes) {
        this.count += 1;
        this.unflushed += 1;
        this.bytes += bytes;
        this.writePosition += bytes;
        this.fileCounts[getWriteFile()] += 1;
        this.dirty = true;
    }

    public void addReadCount(int bytes) {
        this.count -= 1;
        //does not decrement this.bytes, only when the file is deleted
        this.readPosition += bytes;
        this.fileCounts[getReadFile()] -= 1;
        this.dirty = true;
    }

    public void clear() {
        readFile = writeFile = 0;
        readPosition = writePosition = 0;
        count = bytes = 0;
        unflushed = 0;
        for (int i = 0; i < MAX_FILES; i++)
            fileCounts[i] = 0;
        dirty = true;
    }

    public long getWritePosition() {
        return writePosition >= 0 ? writePosition : writePosition + (1L << 31);
    }

    @Override
    public void close() throws IOException {
        flush();
        if (randomWrite != null)
            randomWrite.close();
    }

    public int getFileCount(int file) {
        return fileCounts[file];
    }

    public boolean fixCounts(long totalCount, long totalBytes) {
        if (totalBytes != bytes || totalCount != count) {
            bytes = totalBytes;
            count = totalCount;
            dirty = true;
            return true;
        }
        return false;
    }

    public boolean readFileEof() {
        return fileCounts[getReadFile()] <= 0;
    }

    public long getFlushedCount() {
        return count - unflushed;
    }

    public long getUnflushedCount() {
        return unflushed;
    }

    public boolean needsFlushBeforePop() {
        return unflushed > 0 && unflushed == count;
    }
}
