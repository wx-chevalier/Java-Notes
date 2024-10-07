package net.intelie.disq;

import java.io.IOException;

public class ArrayRawQueue implements RawQueue {
    private final byte[] memory;
    private int begin = 0, bytes = 0, count = 0;

    public ArrayRawQueue(int maxSize) {
        this.memory = new byte[maxSize];
    }

    @Override
    public void reopen() {

    }

    @Override
    public void touch() {

    }

    @Override
    public synchronized long bytes() {
        return bytes;
    }

    @Override
    public synchronized long count() {
        return count;
    }

    @Override
    public synchronized long remainingBytes() {
        return memory.length - bytes;
    }

    @Override
    public synchronized long remainingCount() {
        if (count() == 0) return memory.length / 4;
        return (long) (remainingBytes() / (bytes() / (double) count()));
    }

    @Override
    public synchronized void clear() {
        begin = count = bytes = 0;
    }

    @Override
    public synchronized boolean pop(Buffer buffer) throws IOException {
        if (!peek(buffer)) return false;
        int read = 4 + buffer.count();
        begin = (begin + read) % memory.length;
        bytes -= read;
        count--;
        return true;
    }

    @Override
    public synchronized boolean peek(Buffer buffer) throws IOException {
        if (bytes == 0) return false;
        int size = readInt();
        buffer.setCount(size, false);

        int myBegin = (begin + 4) % memory.length;
        int firstSize = Math.min(memory.length - myBegin, size);
        System.arraycopy(memory, myBegin, buffer.buf(), 0, firstSize);

        if (firstSize < size)
            System.arraycopy(memory, 0, buffer.buf(), firstSize, size - firstSize);

        return true;
    }

    @Override
    public synchronized void push(Buffer buffer) {
        int size = buffer.count();
        while (count > 0 && this.bytes + size + 4 > memory.length) {
            int oldSize = readInt();
            begin = (begin + 4 + oldSize) % memory.length;
            bytes -= 4 + oldSize;
            count--;
        }
        if (this.bytes + size + 4 > memory.length) return;

        writeInt(size);
        int myBegin = (begin + this.bytes + 4) % memory.length;
        int firstSize = Math.min(memory.length - myBegin, size);
        System.arraycopy(buffer.buf(), 0, memory, myBegin, firstSize);
        if (firstSize < size)
            System.arraycopy(buffer.buf(), firstSize, memory, 0, size - firstSize);

        bytes += 4 + buffer.count();
        count++;
    }

    @Override
    public void flush() {

    }

    @Override
    public void close() {

    }

    private int readInt() {
        int ret = 0;
        for (int i = 0; i < 4; i++) {
            ret <<= 8;
            ret |= (int) memory[(begin + i) % memory.length] & 0xFF;
        }
        return ret;
    }

    private void writeInt(int value) {
        for (int i = 0; i < 4; i++) {
            memory[(begin + this.bytes + 3 - i) % memory.length] = (byte) (value & 0xFF);
            value >>= 8;
        }
    }
}
