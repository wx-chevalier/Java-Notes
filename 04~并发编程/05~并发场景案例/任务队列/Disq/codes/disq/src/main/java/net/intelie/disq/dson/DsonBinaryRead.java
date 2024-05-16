package net.intelie.disq.dson;

import net.intelie.disq.Buffer;

public abstract class DsonBinaryRead {
    public static void readUnicode(Buffer.InStream stream, StringView view) {
        int len = readCount(stream);
        view.set(stream.buf(), stream.position(), len);
        stream.position(stream.position() + len * 2);
    }

    public static void readLatin1(Buffer.InStream stream, StringView view) {
        int len = readCount(stream);
        view.set(stream.buf(), stream.position(), len);
        stream.position(stream.position() + len);
    }

    public static DsonType readType(Buffer.InStream stream) {
        return DsonType.findByValue(stream.read());
    }

    public static double readNumber(Buffer.InStream stream) {
        return Double.longBitsToDouble(readInt64(stream));
    }

    public static boolean readBoolean(Buffer.InStream stream) {
        return stream.read() > 0;
    }

    public static int readCount(Buffer.InStream stream) {
        int read = stream.read();
        if (read < 128)
            return read;
        int read2 = stream.read();
        if (read2 < 128)
            return (read & 0x7F) | read2 << 7;
        return (read & 0x7F) |
                (read2 & 0x7F) << 7 |
                stream.read() << 14 |
                stream.read() << 22 |
                stream.read() << 30;

    }

    public static int readInt32(Buffer.InStream stream) {
        return stream.read() |
                stream.read() << 8 |
                stream.read() << 16 |
                stream.read() << 24;
    }

    public static long readInt64(Buffer.InStream stream) {
        return (long) stream.read() |
                (long) stream.read() << 8 |
                (long) stream.read() << 16 |
                (long) stream.read() << 24 |
                (long) stream.read() << 32 |
                (long) stream.read() << 40 |
                (long) stream.read() << 48 |
                (long) stream.read() << 56;

    }

}
