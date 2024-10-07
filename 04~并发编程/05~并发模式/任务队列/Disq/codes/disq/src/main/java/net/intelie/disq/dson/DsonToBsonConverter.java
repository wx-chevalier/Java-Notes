package net.intelie.disq.dson;

import net.intelie.disq.Buffer;

public class DsonToBsonConverter {
    private static final String TRUE = "true";
    private static final String FALSE = "false";
    private static final String VALUE = "value";
    private static final int BSON_DOCUMENT = 0x03;
    private static final int BSON_ARRAY = 0x04;
    private static final int BSON_DOUBLE = 0x01;
    private static final int BSON_STRING = 0x02;
    private static final int BSON_BOOLEAN = 0x08;
    private static final int BSON_NULL = 0x0a;

    private final UnicodeView unicodeView = new UnicodeView();
    private final Latin1View latin1View = new Latin1View();
    private final StringBuilder stringBuilder = new StringBuilder();

    public void convert(Buffer.InStream in, Buffer.OutStream out) {
        DsonType type = DsonBinaryRead.readType(in);
        if (type == DsonType.OBJECT)
            convertDocument(in, out);
        else
            fakeDocument(type, in, out);
    }

    private void fakeDocument(DsonType type, Buffer.InStream in, Buffer.OutStream out) {
        int start = out.position();
        DsonBinaryWrite.writeInt32(out, 0); //reserving space to write size
        int typePosition = out.position();
        out.write(0); //reserving space to write type

        writeCharacters(out, VALUE, true);
        convertValueWithoutType(type, typePosition, in, out);

        out.write(0);
        writeInt32At(out, start, out.position() - start);
    }

    private void convertDocument(Buffer.InStream in, Buffer.OutStream out) {
        int start = out.position();
        DsonBinaryWrite.writeInt32(out, 0); //reserving space to write size

        int count = DsonBinaryRead.readCount(in);
        for (int i = 0; i < count; i++) {
            int typePosition = out.position();
            out.write(0); //reserving space to write type
            convertKey(in, out);
            convertValue(typePosition, in, out);
        }

        out.write(0);
        writeInt32At(out, start, out.position() - start);
    }

    private void convertArray(Buffer.InStream in, Buffer.OutStream out) {
        int start = out.position();
        DsonBinaryWrite.writeInt32(out, 0);  //reserving space to write size

        int count = DsonBinaryRead.readCount(in);
        for (int i = 0; i < count; i++) {
            int typePosition = out.position();
            out.write(0); //reserving space to write type
            stringBuilder.setLength(0);
            stringBuilder.append(i);
            writeCharacters(out, stringBuilder, true);
            convertValue(typePosition, in, out);
        }

        out.write(0);
        writeInt32At(out, start, out.position() - start);
    }

    private void convertValue(int typePosition, Buffer.InStream in, Buffer.OutStream out) {
        convertValueWithoutType(DsonBinaryRead.readType(in), typePosition, in, out);
    }

    private void convertValueWithoutType(DsonType type, int typePosition, Buffer.InStream in, Buffer.OutStream out) {
        switch (type) {
            case OBJECT:
                writeByteAt(out, typePosition, BSON_DOCUMENT);
                convertDocument(in, out);
                break;
            case ARRAY:
                writeByteAt(out, typePosition, BSON_ARRAY);
                convertArray(in, out);
                break;
            case DOUBLE:
                writeByteAt(out, typePosition, BSON_DOUBLE);
                double numberValue = DsonBinaryRead.readNumber(in);
                DsonBinaryWrite.writeNumber(out, numberValue);
                break;
            case STRING:
                writeByteAt(out, typePosition, BSON_STRING);
                DsonBinaryRead.readUnicode(in, unicodeView);
                writeString(out, unicodeView);
                break;
            case STRING_LATIN1:
                writeByteAt(out, typePosition, BSON_STRING);
                DsonBinaryRead.readLatin1(in, latin1View);
                writeString(out, latin1View);
                break;
            case BOOLEAN:
                writeByteAt(out, typePosition, BSON_BOOLEAN);
                boolean boolValue = DsonBinaryRead.readBoolean(in);
                out.write(boolValue ? 1 : 0);
                break;
            case NULL:
                writeByteAt(out, typePosition, BSON_NULL);
                break;
            default:
                throw new IllegalStateException("(value) unknown DSON type");
        }
    }

    private void writeString(Buffer.OutStream out, CharSequence str) {
        int start = out.position();
        DsonBinaryWrite.writeInt32(out, 0);
        writeCharacters(out, str, false);
        writeInt32At(out, start, out.position() - start - 4);
    }


    private void convertKey(Buffer.InStream in, Buffer.OutStream out) {
        for (int remaining = 1; remaining > 0; remaining--) {
            switch (DsonBinaryRead.readType(in)) {
                case OBJECT:
                    remaining += 2 * DsonBinaryRead.readCount(in);
                    break;
                case ARRAY:
                    remaining += DsonBinaryRead.readCount(in);
                    break;
                case DOUBLE:
                    double numberValue = DsonBinaryRead.readNumber(in);
                    stringBuilder.setLength(0);
                    stringBuilder.append(numberValue);
                    writeStringBody(out, stringBuilder, true);
                    break;
                case STRING:
                    DsonBinaryRead.readUnicode(in, unicodeView);
                    writeStringBody(out, unicodeView, true);
                    break;
                case STRING_LATIN1:
                    DsonBinaryRead.readLatin1(in, latin1View);
                    writeStringBody(out, latin1View, true);
                    break;
                case BOOLEAN:
                    boolean boolValue = DsonBinaryRead.readBoolean(in);
                    writeStringBody(out, boolValue ? TRUE : FALSE, true);
                    break;
                case NULL:
                    break;
                default:
                    throw new IllegalStateException("(key) unknown DSON type");
            }
        }
        out.write((byte) 0);
    }

    public static void writeByteAt(Buffer.OutStream out, int position, int value) {
        int current = out.position();
        out.position(position);
        out.write(value);
        out.position(current);
    }

    public static void writeInt32At(Buffer.OutStream out, int position, int value) {
        int current = out.position();
        out.position(position);
        DsonBinaryWrite.writeInt32(out, value);
        out.position(current);
    }

    private void writeCharacters(Buffer.OutStream out, CharSequence str, boolean ignoreNullChars) {
        writeStringBody(out, str, ignoreNullChars);
        out.write((byte) 0);
    }

    private void writeStringBody(Buffer.OutStream out, CharSequence str, boolean ignoreNullChars) {
        for (int i = 0; i < str.length(); ) {
            int c = Character.codePointAt(str, i);

            if (ignoreNullChars && c == 0x0) {
                //do nothing
            } else if (c < 0x80) {
                out.write((byte) c);
            } else if (c < 0x800) {
                out.write((byte) (0xc0 + (c >> 6)));
                out.write((byte) (0x80 + (c & 0x3f)));
            } else if (c < 0x10000) {
                out.write((byte) (0xe0 + (c >> 12)));
                out.write((byte) (0x80 + ((c >> 6) & 0x3f)));
                out.write((byte) (0x80 + (c & 0x3f)));
            } else {
                out.write((byte) (0xf0 + (c >> 18)));
                out.write((byte) (0x80 + ((c >> 12) & 0x3f)));
                out.write((byte) (0x80 + ((c >> 6) & 0x3f)));
                out.write((byte) (0x80 + (c & 0x3f)));
            }

            i += Character.charCount(c);
        }
    }
}
