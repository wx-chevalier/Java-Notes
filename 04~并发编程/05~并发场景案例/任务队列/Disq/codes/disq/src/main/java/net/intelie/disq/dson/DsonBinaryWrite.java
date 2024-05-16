package net.intelie.disq.dson;

import net.intelie.disq.Buffer;

public abstract class DsonBinaryWrite {
    public static void writeUnicode(Buffer.OutStream stream, CharSequence str) {
        int length = str.length();
        writeCount(stream, length);
        stream.unsafePrepare(length * 2);
        for (int i = 0; i < length; i++) {
            char c = str.charAt(i);
            stream.unsafeWrite((c) & 0xFF);
            stream.unsafeWrite((c >>> 8) & 0xFF);
        }
    }

    public static void writeLatin1(Buffer.OutStream stream, CharSequence str) {
        int length = str.length();
        writeCount(stream, length);
        stream.unsafePrepare(length);
        for (int i = 0; i < length; i++) {
            char c = str.charAt(i);
            stream.unsafeWrite(c);
        }
    }

    public static void writeType(Buffer.OutStream stream, DsonType string) {
        stream.unsafePrepare(1);
        stream.unsafeWrite(string.getValue());
    }

    public static void writeNumber(Buffer.OutStream stream, double value) {
        writeInt64(stream, Double.doubleToRawLongBits(value));
    }

    public static void writeBoolean(Buffer.OutStream stream, boolean value) {
        stream.unsafePrepare(1);
        stream.write(value ? 1 : 0);
    }

    public static void writeCount(Buffer.OutStream stream, int value) {
        if ((value & 0x7F) == value) {
            stream.unsafePrepare(1);
            stream.unsafeWrite(value & 0x7F);
        } else if ((value & 0x3FFF) == value) {
            stream.unsafePrepare(2);
            stream.unsafeWrite(0x80 | value & 0x7F);
            stream.unsafeWrite((value >> 7) & 0x7F);
        } else {
            stream.unsafePrepare(5);
            stream.unsafeWrite(0x80 | value & 0x7F);
            stream.unsafeWrite(0x80 | (value >> 7) & 0x7F);
            stream.unsafeWrite((value >> 14) & 0xFF);
            stream.unsafeWrite((value >> 22) & 0xFF);
            stream.unsafeWrite((value >> 30) & 0xFF);
        }
    }

    public static void writeInt32(Buffer.OutStream stream, int value) {
        stream.unsafePrepare(4);
        stream.unsafeWrite((value) & 0xFF);
        stream.unsafeWrite((value >> 8) & 0xFF);
        stream.unsafeWrite((value >> 16) & 0xFF);
        stream.unsafeWrite((value >> 24) & 0xFF);
    }

    public static void writeInt64(Buffer.OutStream stream, long value) {
        stream.unsafePrepare(8);
        stream.unsafeWrite((int) ((value) & 0xFF));
        stream.unsafeWrite((int) ((value >>> 8) & 0xFF));
        stream.unsafeWrite((int) ((value >>> 16) & 0xFF));
        stream.unsafeWrite((int) ((value >>> 24) & 0xFF));
        stream.unsafeWrite((int) ((value >>> 32) & 0xFF));
        stream.unsafeWrite((int) ((value >>> 40) & 0xFF));
        stream.unsafeWrite((int) ((value >>> 48) & 0xFF));
        stream.unsafeWrite((int) ((value >>> 56) & 0xFF));
    }
}
