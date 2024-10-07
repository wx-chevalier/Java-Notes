package net.intelie.disq;

import java.io.*;
import java.nio.file.Path;

public class DataFileWriter implements Closeable {
    public static final int OVERHEAD = 4;
    private final DataOutputStream stream;
    private final FileOutputStream fos;
    private final File file;

    public DataFileWriter(Path file, long position) throws IOException {
        this.file = file.toFile();
        setLength(this.file, position);
        fos = new FileOutputStream(this.file, true);
        stream = new DataOutputStream(new BufferedOutputStream(fos, 1024 * 1024));
    }

    private void setLength(File file, long size) throws IOException {
        RandomAccessFile rand = new RandomAccessFile(file, "rws");
        rand.setLength(size);
        rand.close();
    }

    public int write(Buffer buffer) throws IOException {
        stream.writeInt(buffer.count());
        stream.write(buffer.buf(), 0, buffer.count());
        //stream.flush();
        return buffer.count() + OVERHEAD;
    }

    public void flush() throws IOException {
        stream.flush();
    }

    @Override
    public void close() throws IOException {
        stream.close();
    }
}
