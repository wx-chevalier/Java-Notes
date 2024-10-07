package net.intelie.disq;

import com.google.common.base.Strings;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

public class ArrayRawQueueTest {

    @Test
    public void reopenFlushCloseDoNothing() throws Exception {
        ArrayRawQueue queue = new ArrayRawQueue(10);
        queue.reopen();
        queue.touch();
        queue.flush();
        queue.close();
    }

    @Test
    public void testPushBufferLargerThan256Bytes() throws Exception {
        ArrayRawQueue queue = new ArrayRawQueue(280);

        String s = Strings.repeat("a", 257);
        push(queue, s);
        assertThat(pop(queue)).isEqualTo(s);
    }

    @Test
    public void testSimplePushsAndPops() throws Exception {
        ArrayRawQueue queue = new ArrayRawQueue(10);

        for (int i = 0; i < 20; i++) {
            String s = "test" + i;

            push(queue, s);
            assertThat(queue.bytes()).isGreaterThanOrEqualTo(9);
            assertThat(queue.count()).isEqualTo(1);
            assertThat(queue.remainingBytes()).isLessThanOrEqualTo(10);
            assertThat(queue.remainingCount()).isEqualTo(0);

            assertThat(pop(queue)).isEqualTo(s);
            assertThat(queue.bytes()).isEqualTo(0);
            assertThat(queue.count()).isEqualTo(0);
            assertThat(queue.remainingBytes()).isLessThanOrEqualTo(20);
            assertThat(queue.remainingCount()).isEqualTo(2);
        }
    }

    @Test
    public void testClear() throws Exception {
        ArrayRawQueue queue = new ArrayRawQueue(200);
        for (int i = 0; i < 20; i++) {
            String s = "test" + String.format((Locale) null, "%02x", i);
            push(queue, s);
        }
        assertThat(queue.count()).isEqualTo(20);
        assertThat(queue.bytes()).isEqualTo(200);
        assertThat(queue.remainingBytes()).isEqualTo(0);
        assertThat(queue.remainingCount()).isEqualTo(0);

        queue.clear();
        assertThat(queue.count()).isEqualTo(0);
        assertThat(queue.bytes()).isEqualTo(0);
        assertThat(queue.remainingBytes()).isEqualTo(200);
        assertThat(queue.remainingCount()).isEqualTo(50);
    }

    @Test
    public void testPushManyThenPopMany() throws Exception {
        ArrayRawQueue queue = new ArrayRawQueue(200);

        for (int i = 0; i < 20; i++) {
            String s = "test" + String.format((Locale) null, "%02x", i);
            push(queue, s);
        }
        for (int i = 0; i < 20; i++) {
            String s = "test" + String.format((Locale) null, "%02x", i);
            assertThat(pop(queue)).isEqualTo(s);
        }
    }

    @Test
    public void testPushOverflow() throws Exception {
        ArrayRawQueue queue = new ArrayRawQueue(50);
        for (int i = 0; i < 5; i++) {
            push(queue, "012345");
        }
        assertThat(queue.count()).isEqualTo(5);

        push(queue, "x");
        push(queue, "y");
        push(queue, "z");
        assertThat(queue.count()).isEqualTo(6);


        for (int i = 0; i < 3; i++)
            assertThat(pop(queue)).isEqualTo("012345");

        assertThat(pop(queue)).isEqualTo("x");
        assertThat(pop(queue)).isEqualTo("y");
        assertThat(pop(queue)).isEqualTo("z");
        assertThat(pop(queue)).isEqualTo(null);
    }

    @Test
    public void testReplaceEverythingOnOverflow() throws Exception {
        ArrayRawQueue queue = new ArrayRawQueue(50);
        for (int i = 0; i < 5; i++) {
            push(queue, "012345");
        }
        assertThat(queue.count()).isEqualTo(5);
        push(queue, Strings.repeat("a", 47));
        assertThat(queue.count()).isEqualTo(0);
        push(queue, Strings.repeat("a", 46));
        assertThat(queue.count()).isEqualTo(1);

        assertThat(pop(queue)).isEqualTo(Strings.repeat("a", 46));
        assertThat(pop(queue)).isEqualTo(null);
    }

    private void push(ArrayRawQueue queue, String s) throws IOException {
        queue.push(new Buffer(s.getBytes(StandardCharsets.UTF_8)));
    }

    private String pop(ArrayRawQueue queue) throws IOException {
        Buffer buffer = new Buffer();
        if (!queue.pop(buffer)) return null;
        return new String(buffer.buf(), 0, buffer.count(), StandardCharsets.UTF_8);
    }

    private String peek(ArrayRawQueue queue) throws IOException {
        Buffer buffer = new Buffer();
        if (!queue.peek(buffer)) return null;
        return new String(buffer.buf(), 0, buffer.count(), StandardCharsets.UTF_8);
    }
}
