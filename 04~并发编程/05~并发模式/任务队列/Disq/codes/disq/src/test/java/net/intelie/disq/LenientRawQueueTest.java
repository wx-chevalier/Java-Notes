package net.intelie.disq;

import com.google.common.base.Strings;
import net.intelie.introspective.ThreadResources;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class LenientRawQueueTest {
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Test
    public void testAbleToRecoverOnDataFilesDelete() throws Exception {
        LenientRawQueue queue = new LenientRawQueue(new DiskRawQueue(temp.getRoot().toPath(), 512));

        String s = Strings.repeat("a", 512);

        for (int i = 0; i < 5; i++)
            push(queue, s);
        push(queue, "aaa");

        for (String file : temp.getRoot().list()) {
            if (file.startsWith("data"))
                new File(temp.getRoot(), file).delete();
        }

        push(queue, s);
        assertThat(pop(queue)).isNull();
        push(queue, "abc");
        assertThat(pop(queue)).isEqualTo("abc");
        assertBytesAndCount(queue, 7, 0);
        assertThat(temp.getRoot().list()).containsOnly("state", "data06");
    }

    @Test
    public void testAbleToRecoverOnDataFilesMadeReadOnly() throws Exception {
        LenientRawQueue queue = new LenientRawQueue(new DiskRawQueue(temp.getRoot().toPath(), 512));

        String s = Strings.repeat("a", 512);

        for (int i = 0; i < 5; i++)
            push(queue, s);
        push(queue, "aaa");

        temp.getRoot().setWritable(false);

        for (int i = 0; i < 5; i++)
            assertThat(pop(queue)).isEqualTo(s);
        assertThat(pop(queue)).isEqualTo("aaa");
        assertThat(temp.getRoot().list()).containsOnly("data00", "data01", "data02", "data03", "data04", "state", "data05");

        temp.getRoot().setWritable(true);
        queue.reopen();
        queue.touch();
        assertThat(temp.getRoot().list()).containsOnly("state", "data05");
    }

    @Test
    public void testAbleToDetectCorruptedFiles() throws Exception {
        LenientRawQueue queue = new LenientRawQueue(new DiskRawQueue(temp.getRoot().toPath(), 512));

        String s = Strings.repeat("a", 512);

        for (int i = 0; i < 5; i++)
            push(queue, s);

        try (Writer writer = Files.newBufferedWriter(new File(temp.getRoot(), "data00").toPath())) {
            writer.write("abcdeqwefwefwewefger");
        }

        for (int i = 0; i < 32; i++)
            assertThatThrownBy(() -> pop(queue))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Buffer overflowed");

        for (int i = 0; i < 4; i++)
            assertThat(pop(queue)).isEqualTo(s);
        String[] files = temp.getRoot().list();
        Arrays.sort(files);
        assertThat(files.length).isEqualTo(2);
        assertThat(files).contains("state");
        assertThat(files[0]).startsWith("data00").endsWith(".corrupted");
    }

    @Test
    public void testAbleToDetectSingleCorruptedFile() throws Exception {
        LenientRawQueue queue = new LenientRawQueue(new DiskRawQueue(temp.getRoot().toPath(), 512));

        String s = Strings.repeat("a", 100);

        push(queue, s);

        try (Writer writer = Files.newBufferedWriter(new File(temp.getRoot(), "data00").toPath())) {
            writer.write("abcdeqwefwefwewefger");
        }

        for (int i = 0; i < 32; i++)
            assertThatThrownBy(() -> pop(queue))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Buffer overflowed");

        push(queue, s);
        assertThat(pop(queue)).isEqualTo(null);
        push(queue, s);
        assertThat(pop(queue)).isEqualTo(s);
        assertThat(pop(queue)).isEqualTo(null);

        String[] files = temp.getRoot().list();
        Arrays.sort(files);
        assertThat(files.length).isEqualTo(3);
        assertThat(files).contains("state");
        assertThat(files[1]).startsWith("data00").endsWith(".corrupted");
    }

    @Test
    public void testAbleToDetectCorruptedFileManyPerFile() throws Exception {
        LenientRawQueue queue = new LenientRawQueue(new DiskRawQueue(temp.getRoot().toPath(), 512));

        String s = Strings.repeat("a", 100);

        for (int i = 0; i < 20; i++) {
            push(queue, s);
        }

        try (Writer writer = Files.newBufferedWriter(new File(temp.getRoot(), "data00").toPath())) {
            writer.write("abcdeqwefwefwewefger");
        }

        for (int i = 0; i < 32; i++)
            assertThatThrownBy(() -> pop(queue))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Buffer overflowed");


        push(queue, s);
        for (int i = 0; i < 15; i++) {
            assertThat(pop(queue)).isEqualTo(s);
        }


        String[] files = temp.getRoot().list();
        Arrays.sort(files);
        assertThat(files.length).isEqualTo(3);
        assertThat(files).contains("state");
        assertThat(files).contains("data04");
        assertThat(files[0]).startsWith("data00").endsWith(".corrupted");
    }


    @Test
    public void testAbleToRecoverOnDirectoryDelete() throws Exception {
        LenientRawQueue queue = new LenientRawQueue(new DiskRawQueue(temp.getRoot().toPath(), 512));

        String s = Strings.repeat("a", 512);

        for (int i = 0; i < 5; i++)
            push(queue, s);

        for (String file : temp.getRoot().list()) {
            new File(temp.getRoot(), file).delete();
        }

        temp.getRoot().delete();

        push(queue, s);
        assertThat(pop(queue)).isNull();
        push(queue, "abc");
        assertThat(pop(queue)).isEqualTo("abc");
        assertBytesAndCount(queue, 7, 0);
    }


    @Test
    public void testAllocations() throws IOException {
        DiskRawQueue disk = new DiskRawQueue(temp.getRoot().toPath(), 100000000);
        LenientRawQueue queue = new LenientRawQueue(disk);

        Buffer buffer = new Buffer("test".getBytes(StandardCharsets.UTF_8));
        Buffer buffer2 = new Buffer();

        //warmup
        for (int i = 0; i < 10000; i++) {
            queue.push(buffer);
            queue.flush();
        }
        for (int i = 0; i < 10000; i++)
            queue.peek(buffer2);
        for (int i = 0; i < 10000; i++) {
            queue.pop(buffer2);
            queue.flush();
        }

        ThreadResources.allocatedBytes(Thread.currentThread());
        long start = ThreadResources.allocatedBytes(Thread.currentThread());
        for (int i = 0; i < 10000; i++) {
            queue.push(buffer);
            queue.flush();
        }
        for (int i = 0; i < 10000; i++)
            queue.peek(buffer2);
        for (int i = 0; i < 10000; i++) {
            queue.pop(buffer2);
            queue.flush();
        }

        long end = ThreadResources.allocatedBytes(Thread.currentThread()) - start;
        assertThat(end / 10000.0).isLessThan(1);
        assertThat(disk.files()).isEqualTo(1);
        assertThat(queue.count()).isEqualTo(0);
    }

    private void assertBytesAndCount(LenientRawQueue queue, int bytes, int count) {
        assertThat(queue.bytes()).isEqualTo(bytes);
        assertThat(queue.count()).isEqualTo(count);
    }

    private void push(LenientRawQueue queue, String s) throws IOException {
        queue.push(new Buffer(s.getBytes(StandardCharsets.UTF_8)));
    }

    private String pop(LenientRawQueue queue) throws IOException {
        Buffer buffer = new Buffer();
        if (!queue.pop(buffer)) return null;
        return new String(buffer.buf(), 0, buffer.count(), StandardCharsets.UTF_8);
    }

    private String peek(LenientRawQueue queue) throws IOException {
        Buffer buffer = new Buffer();
        if (!queue.peek(buffer)) return null;
        return new String(buffer.buf(), 0, buffer.count(), StandardCharsets.UTF_8);
    }

}
