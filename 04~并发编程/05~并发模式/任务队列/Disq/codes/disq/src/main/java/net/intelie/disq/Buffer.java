package net.intelie.disq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public class Buffer {
    private static final Logger LOGGER = LoggerFactory.getLogger(Buffer.class);

    private final int maxCapacity;
    private final OutStream out = new OutStream();
    private final InStream in = new InStream();
    private byte[] buf;
    private int count;

    public Buffer() {
        this(-1);
    }

    public Buffer(int maxCapacity) {
        this(32, maxCapacity);
    }

    public Buffer(int initialCapacity, int maxCapacity) {
        this(new byte[initialCapacity], 0, maxCapacity);
    }

    public Buffer(byte[] buf) {
        this(buf, buf.length, buf.length);
    }

    private Buffer(byte[] buf, int count, int maxCapacity) {
        this.buf = buf;
        this.count = count;
        this.maxCapacity = maxCapacity;
    }

    public int currentCapacity() {
        return buf.length;
    }

    public int count() {
        return count;
    }

    public void clear() {
        count = 0;
    }

    public byte[] buf() {
        return buf;
    }

    public byte[] toArray() {
        return Arrays.copyOf(buf, count);
    }

    public void setCountAtLeast(int newCount, boolean preserve) {
        if (newCount > count) {
            setCount(newCount, preserve);
        }
    }

    public void setCount(int newCount, boolean preserve) {
        ensureCapacity(newCount, preserve);
        count = newCount;
    }

    public void ensureCapacity(int capacity) {
        ensureCapacity(capacity, false);
    }

    public void ensureCapacity(int capacity, boolean preserve) {
        if (capacity <= buf.length) return;
        int newCapacity = findBestNewCapacity(capacity);

        if (capacity > newCapacity) {
            LOGGER.info("Buffer overflowed. Len={}, Max={}", capacity, maxCapacity);
            throw new IllegalStateException("Buffer overflowed: " + capacity + "/" + maxCapacity + " bytes");
        }

        if (preserve) buf = Arrays.copyOf(buf, newCapacity);
        else buf = new byte[newCapacity];
    }

    private int findBestNewCapacity(int capacity) {
        int newCapacity = (1 << (Integer.SIZE - Integer.numberOfLeadingZeros(capacity) - 1));
        if (newCapacity < capacity) newCapacity <<= 1;
        if (maxCapacity >= 0) newCapacity = Math.min(newCapacity, maxCapacity);
        return newCapacity;
    }

    public OutStream write() {
        return write(0);
    }

    public OutStream write(int start) {
        out.position(start);
        return out;
    }

    public InStream read() {
        return read(0);
    }

    public InStream read(int start) {
        in.position(start);
        return in;
    }

    public class OutStream extends OutputStream {
        private int position;

        public void position(int start) {
            position = start;
        }

        public int position() {
            return position;
        }

        public byte[] buf() {
            return Buffer.this.buf();
        }

        @Override
        public void write(byte[] data) {
            writeAt(position, data, 0, data.length);
            position += data.length;
        }

        @Override
        public void write(int data) {
            writeAt(position, data);
            position++;
        }

        public void unsafePrepare(int length) {
            setCountAtLeast(position + length, true);
        }

        public void unsafeWrite(int data) {
            buf[position++] = (byte) data;
        }

        @Override
        public void write(byte[] data, int off, int len) {
            writeAt(position, data, off, len);
            position += len;
        }

        public void writeAt(int position, int data) {
            setCountAtLeast(position + 1, true);
            buf[position] = (byte) data;
        }

        public void writeAt(int position, byte[] data, int offset, int len) {
            setCountAtLeast(position + len, true);
            System.arraycopy(data, offset, buf, position, len);
        }

        @Override
        public void flush() {
        }

        @Override
        public void close() {
        }
    }

    public class InStream extends InputStream {
        private int marked = 0;
        private int position = 0;

        public void position(int start) {
            position = marked = start;
        }

        public int position() {
            return position;
        }

        public int marked() {
            return marked;
        }

        public byte[] buf() {
            return Buffer.this.buf();
        }

        @Override
        public void close() {
        }

        @Override
        public boolean markSupported() {
            return true;
        }

        @Override
        public int read(byte[] b) {
            return read(b, 0, b.length);
        }

        @Override
        public int read() {
            int value = readAt(position);
            if (value >= 0)
                position++;
            return value;
        }

        @Override
        public int read(byte[] b, int off, int len) {
            int read = readAt(position, b, off, len);
            if (read >= 0)
                position += read;
            return read;
        }

        public int readAt(int position) {
            if (position + 1 > count)
                return -1;
            return buf[position] & 0xFF;
        }

        public int readAt(int position, byte[] b, int off, int len) {
            if (position >= count)
                return -1;
            int toRead = Math.min(count - position, len);
            System.arraycopy(buf, position, b, off, toRead);
            return toRead;
        }


        @Override
        public long skip(long n) {
            long toSkip = Math.min(count - position, n);
            position += toSkip;
            return toSkip;
        }

        @Override
        public int available() {
            return count - position;
        }

        @Override
        public synchronized void mark(int readlimit) {
            marked = position;
        }

        @Override
        public synchronized void reset() {
            position = marked;
        }
    }
}
