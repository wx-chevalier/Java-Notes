package net.intelie.disq;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

public class DiskQueueReader implements Closeable {
    private final StateFile state;
    private final Path directory;
    private DataFileReader reader;

    public DiskQueueReader(Path directory) throws IOException {
        this.directory = directory;
        this.state = new StateFile(this.directory.resolve("state"), true);
        this.reader = openReader();
    }

    private DataFileReader openReader() throws IOException {
        Path file = makeDataPath(state.getReadFile());
        if (!Files.exists(file)) return null;
        return new DataFileReader(file, state.getReadPosition());
    }

    public long bytes() {
        return state.getBytes();
    }

    public long count() {
        return state.getCount();
    }

    public boolean moveNext(Buffer buffer) throws IOException {
        if (checkReadEOF())
            return false;
        int read = reader.read(buffer);
        state.addReadCount(read);
        return true;
    }

    private Path makeDataPath(int state) {
        return directory.resolve(String.format((Locale) null, "data%02x", state));
    }

    private boolean checkReadEOF() throws IOException {
        while (state.readFileEof())
            if (!maybeAdvanceFile())
                break;
        return state.getCount() == 0;
    }

    private boolean maybeAdvanceFile() throws IOException {
        state.advanceReadFile(reader.size());
        reader.close();
        reader = openReader();
        return reader != null;
    }

    @Override
    public void close() throws IOException {
        if (reader != null)
            reader.close();
    }
}
