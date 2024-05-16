package net.intelie.disq;

import com.google.common.base.Strings;
import com.google.common.io.CharStreams;
import org.junit.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class BufferTest {
    @Test
    public void testWriter() throws IOException {
        Buffer buffer = new Buffer();
        Buffer.OutStream writer = buffer.write();

        assertThat(writer.buf()).isSameAs(buffer.buf());
        assertThat(writer.position()).isEqualTo(0);
        writer.write(new byte[]{1, 2, 3});

        assertThat(writer.position()).isEqualTo(3);

        Buffer.OutStream writer2 = buffer.write(1);
        assertThat(writer2).isSameAs(writer);
        assertThat(writer2.position()).isEqualTo(1);
    }

    @Test
    public void testReader() throws IOException {
        Buffer buffer = new Buffer();
        buffer.write().write(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 0});

        Buffer.InStream reader = buffer.read();
        assertThat(reader.buf()).isSameAs(buffer.buf());
        assertThat(reader.position()).isEqualTo(0);
        reader.read(new byte[3]);

        assertThat(reader.position()).isEqualTo(3);

        Buffer.InStream writer2 = buffer.read(1);
        assertThat(writer2).isSameAs(reader);
        assertThat(writer2.position()).isEqualTo(1);
    }

    @Test
    public void testEnsureCapacity() throws Exception {
        Buffer buffer = new Buffer(13, 1 << 25);
        assertThat(buffer.currentCapacity()).isEqualTo(13);
        buffer.ensureCapacity(15);
        assertThat(buffer.currentCapacity()).isEqualTo(16);
        buffer.ensureCapacity(17);
        assertThat(buffer.currentCapacity()).isEqualTo(32);
        buffer.ensureCapacity(32);
        assertThat(buffer.currentCapacity()).isEqualTo(32);
        buffer.ensureCapacity(64);
        assertThat(buffer.currentCapacity()).isEqualTo(64);
    }

    @Test
    public void canReadBytes() {
        Buffer buffer = new Buffer("0123456789".getBytes(StandardCharsets.UTF_8));
        byte[] bytes = new byte[4];
        assertThat(buffer.read(3).read(bytes)).isEqualTo(4);
        assertThat(bytes).containsExactly('3', '4', '5', '6');
    }

    @Test
    public void testWriteBigString() throws Exception {
        Buffer buffer = new Buffer();

        PrintStream stream = new PrintStream(buffer.write(), false, StandardCharsets.UTF_8.name());

        String s = Strings.repeat("a", 511);
        stream.print(s);

        assertThat(buffer.currentCapacity()).isEqualTo(512);

        stream.write('a');
        stream.write('a');
        assertThat(buffer.count()).isEqualTo(513);
        assertThat(buffer.currentCapacity()).isEqualTo(1024);
        assertThat(buffer.toArray()).isEqualTo((s + "aa").getBytes(StandardCharsets.UTF_8));
    }

    @Test
    public void testSetCapacityGreaterThanWhatIsAllowed() throws Exception {
        Buffer buffer = new Buffer(100, 1000);

        assertThatThrownBy(() -> buffer.ensureCapacity(1001))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("1000");
    }

    @Test
    public void testWriteSmallString() throws Exception {
        Buffer buffer = new Buffer();

        PrintStream stream = new PrintStream(buffer.write(), false, StandardCharsets.UTF_8.name());
        stream.print("aa");

        assertThat(buffer.currentCapacity()).isEqualTo(32);
    }

    @Test
    public void testWriteBigStringThatExceedsCapacity() throws Exception {
        Buffer buffer = new Buffer(300);

        Buffer.OutStream stream = buffer.write();

        String s = Strings.repeat("a", 300);
        stream.write(s.getBytes(StandardCharsets.UTF_8));

        assertThat(buffer.currentCapacity()).isEqualTo(300);

        assertThatThrownBy(() -> stream.write('a'))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Buffer overflowed")
                .hasMessageContaining("301/300 bytes");
    }

    @Test
    public void testExpandWithoutPreserving() throws Exception {
        Buffer buffer = new Buffer();
        PrintStream stream = new PrintStream(buffer.write(), false, StandardCharsets.UTF_8.name());
        String s = Strings.repeat("a", 32);
        stream.print(s);

        assertThat(buffer.buf()[0]).isEqualTo((byte) 'a');

        buffer.setCountAtLeast(100, false);

        assertThat(buffer.count()).isEqualTo(100);
        assertThat(buffer.currentCapacity()).isEqualTo(128);
        assertThat(buffer.buf()[0]).isEqualTo((byte) 0);
    }

    @Test
    public void testWriteBigStringExact() throws Exception {
        Buffer buffer = new Buffer();

        PrintStream stream = new PrintStream(buffer.write(12), false, StandardCharsets.UTF_8.name());

        String s = Strings.repeat("a", 500);
        stream.print(s);

        assertThat(buffer.count()).isEqualTo(512);
        assertThat(buffer.currentCapacity()).isEqualTo(512);

        stream.write('a');
        assertThat(buffer.count()).isEqualTo(513);
        assertThat(buffer.currentCapacity()).isEqualTo(1024);
    }

    @Test
    public void testReadEverything() throws Exception {
        Buffer buffer = new Buffer();

        PrintStream stream = new PrintStream(buffer.write(), false, StandardCharsets.UTF_8.name());
        stream.print("0123456789012345678901234567890123456789");

        assertThat(CharStreams.toString(new InputStreamReader(buffer.read(), StandardCharsets.UTF_8))).isEqualTo(
                "0123456789012345678901234567890123456789");
    }

    @Test
    public void testReadEverythingMarkReset() throws Exception {
        Buffer buffer = new Buffer();

        PrintStream stream = new PrintStream(buffer.write(), false, StandardCharsets.UTF_8.name());
        stream.print("0123456789012345678901234567890123456789");

        InputStream read = buffer.read();
        assertThat(read.markSupported()).isTrue();
        read.mark(0);

        assertThat(CharStreams.toString(new InputStreamReader(read, StandardCharsets.UTF_8))).isEqualTo(
                "0123456789012345678901234567890123456789");
        read.reset();
        assertThat(CharStreams.toString(new InputStreamReader(read, StandardCharsets.UTF_8))).isEqualTo(
                "0123456789012345678901234567890123456789");
        read.reset();
        assertThat(CharStreams.toString(new InputStreamReader(read, StandardCharsets.UTF_8))).isEqualTo(
                "0123456789012345678901234567890123456789");
    }

    @Test
    public void testReadAll() throws Exception {
        Buffer buffer = new Buffer();

        PrintStream stream = new PrintStream(buffer.write(), false, StandardCharsets.UTF_8.name());
        stream.print("0123456789012345678901234567890123456789");

        byte[] bytes = new byte[100];

        Buffer.InStream read = buffer.read();
        int read1 = read.read(bytes);
        assertThat(read1).isEqualTo(40);
        assertThat(new String(bytes, 0, read1, StandardCharsets.UTF_8))
                .isEqualTo("0123456789012345678901234567890123456789");

        assertThat(read.read(bytes)).isEqualTo(-1);
        assertThat(read.read(bytes)).isEqualTo(-1);
    }

    @Test
    public void testMarkInMiddle() throws Exception {
        Buffer buffer = new Buffer();

        PrintStream stream = new PrintStream(buffer.write(), false, StandardCharsets.UTF_8.name());
        stream.print("0123456789012345678901234567890123456789");

        Buffer.InStream read = buffer.read();
        for (int i = 0; i < 7; i++) {
            read.read();
        }
        read.mark(0);
        assertThat(read.marked()).isEqualTo(7);
        assertThat(read.position()).isEqualTo(7);

        assertThat(CharStreams.toString(new InputStreamReader(read, StandardCharsets.UTF_8))).isEqualTo(
                "789012345678901234567890123456789");
        assertThat(read.marked()).isEqualTo(7);
        assertThat(read.position()).isEqualTo(40);

        read.reset();
        assertThat(CharStreams.toString(new InputStreamReader(read, StandardCharsets.UTF_8))).isEqualTo(
                "789012345678901234567890123456789");
        read.reset();
        assertThat(CharStreams.toString(new InputStreamReader(read, StandardCharsets.UTF_8))).isEqualTo(
                "789012345678901234567890123456789");
    }

    @Test
    public void testClear() throws Exception {
        Buffer buffer = new Buffer();

        PrintStream stream = new PrintStream(buffer.write(), false, StandardCharsets.UTF_8.name());
        stream.print("0123456789012345678901234567890123456789");

        InputStream read = buffer.read();
        buffer.clear();

        assertThat(CharStreams.toString(new InputStreamReader(read, StandardCharsets.UTF_8))).isEqualTo("");
    }

    @Test
    public void testReadBytes() throws Exception {
        Buffer buffer = new Buffer();

        OutputStream writeB = buffer.write();
        PrintStream stream = new PrintStream(writeB, false, StandardCharsets.UTF_8.name());
        stream.print("012");
        stream.flush();
        writeB.write(0xC8);

        InputStream read = buffer.read();
        assertThat(read.read()).isEqualTo('0');
        assertThat(read.read()).isEqualTo('1');
        assertThat(read.read()).isEqualTo('2');
        assertThat(read.read()).isEqualTo(200);
        assertThat(read.read()).isEqualTo(-1);
    }

    @Test
    public void testReadEverythingStarting() throws Exception {
        Buffer buffer = new Buffer();

        PrintStream stream = new PrintStream(buffer.write(), false, StandardCharsets.UTF_8.name());
        stream.print("0123456789012345678901234567890123456789");

        assertThat(CharStreams.toString(new InputStreamReader(buffer.read(12), StandardCharsets.UTF_8))).isEqualTo(
                "2345678901234567890123456789");
    }

    @Test
    public void testSkip() throws Exception {
        Buffer buffer = new Buffer();

        PrintStream stream = new PrintStream(buffer.write(), false, StandardCharsets.UTF_8.name());
        stream.print("0123456789012345678901234567890123456789");

        InputStream input = buffer.read(12);
        assertThat(input.skip(13)).isEqualTo(13);
        assertThat(CharStreams.toString(new InputStreamReader(input, StandardCharsets.UTF_8))).isEqualTo(
                "567890123456789");
    }
}
