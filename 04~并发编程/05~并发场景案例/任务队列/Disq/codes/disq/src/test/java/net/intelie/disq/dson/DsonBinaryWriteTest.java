package net.intelie.disq.dson;

import net.intelie.disq.Buffer;
import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class DsonBinaryWriteTest {
    @Test
    public void writeVInt() {
        Buffer buffer = new Buffer();
        for (int i = -100000; i < 100000; i++) {
            buffer.clear();
            DsonBinaryWrite.writeCount(buffer.write(), i);
            assertThat(DsonBinaryRead.readCount(buffer.read())).isEqualTo(i);
        }
    }

    @Test
    public void testWriteLatin1() throws IOException {
        Buffer buf = new Buffer();
        Buffer.OutStream write = buf.write();
        DsonBinaryWrite.writeLatin1(write, "ação");
        DsonBinaryWrite.writeLatin1(write, "lâmpada");

        Latin1View view = new Latin1View();

        Buffer.InStream read = buf.read();
        DsonBinaryRead.readLatin1(read, view);
        assertThat(view.toString()).isEqualTo("ação");
        assertThat(view.subSequence(1, 3).toString()).isEqualTo("çã");

        DsonBinaryRead.readLatin1(read, view);
        assertThat(view.toString()).isEqualTo("lâmpada");
        assertThat(view.subSequence(1, 3).toString()).isEqualTo("âm");
    }

    @Test
    public void testWriteString() throws IOException {
        Buffer buf = new Buffer();
        Buffer.OutStream write = buf.write();
        DsonBinaryWrite.writeUnicode(write, "ação");
        DsonBinaryWrite.writeUnicode(write, "lâmpada");

        UnicodeView view = new UnicodeView();

        Buffer.InStream read = buf.read();
        DsonBinaryRead.readUnicode(read, view);
        assertThat(view.toString()).isEqualTo("ação");
        assertThat(view.subSequence(1, 3).toString()).isEqualTo("çã");

        DsonBinaryRead.readUnicode(read, view);
        assertThat(view.toString()).isEqualTo("lâmpada");
        assertThat(view.subSequence(1, 3).toString()).isEqualTo("âm");
    }

    @Test
    public void testWriteCrazyString() throws IOException {
        Buffer buf = new Buffer();
        Buffer.OutStream write = buf.write();
        DsonBinaryWrite.writeUnicode(write, "(╯°□°)╯︵ ┻━┻");
        DsonBinaryWrite.writeUnicode(write, "( ͡° ͜ʖ ͡°)");

        UnicodeView view = new UnicodeView();

        Buffer.InStream read = buf.read();
        DsonBinaryRead.readUnicode(read, view);
        assertThat(view.toString()).isEqualTo("(╯°□°)╯︵ ┻━┻");
        assertThat(view.subSequence(1, 5).toString()).isEqualTo("╯°□°");

        DsonBinaryRead.readUnicode(read, view);
        assertThat(view.toString()).isEqualTo("( ͡° ͜ʖ ͡°)");
        assertThat(view.subSequence(2, view.length()-1).toString()).isEqualTo("͡° ͜ʖ ͡°");
    }

    @Test
    public void testWriteLong() throws IOException {
        for (long i = 3; i < Long.MAX_VALUE / 3; i *= 3) {
            Buffer buf = new Buffer();
            DsonBinaryWrite.writeInt64(buf.write(), i);
            assertThat(buf.count()).isEqualTo(8);
            assertThat(DsonBinaryRead.readInt64(buf.read())).isEqualTo(i);
        }
    }

    @Test
    public void testWriteDouble() throws IOException {
        for (double i = Double.MIN_VALUE; i < Double.MAX_VALUE; i *= 3) {
            Buffer buf = new Buffer();
            DsonBinaryWrite.writeNumber(buf.write(), i);
            assertThat(buf.count()).isEqualTo(8);
            assertThat(DsonBinaryRead.readNumber(buf.read())).isEqualTo(i);
        }
    }

    @Test
    public void testWriteInt() throws IOException {
        for (int i = 3; i < Integer.MAX_VALUE / 3; i *= 3) {
            Buffer buf = new Buffer();
            DsonBinaryWrite.writeInt32(buf.write(), i);
            assertThat(buf.count()).isEqualTo(4);
            assertThat(DsonBinaryRead.readInt32(buf.read())).isEqualTo(i);
        }
    }
}