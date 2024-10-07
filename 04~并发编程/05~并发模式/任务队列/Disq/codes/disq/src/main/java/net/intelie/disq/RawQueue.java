package net.intelie.disq;

import java.io.Closeable;
import java.io.IOException;

public interface RawQueue extends Closeable {
    void reopen();

    long bytes();

    long count();

    long remainingBytes();

    long remainingCount();

    void touch() throws IOException;

    void clear() throws IOException;

    boolean pop(Buffer buffer) throws IOException;

    boolean peek(Buffer buffer) throws IOException;

    void push(Buffer buffer) throws IOException;

    void flush() throws IOException;

    void close();
}
