package net.intelie.disq;

import com.google.common.base.Strings;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

public class DiskQueueReaderTest {
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Test
    public void canRead() throws IOException {
        String s = Strings.repeat("a", 512);

        Path path = temp.getRoot().toPath();
        try (DiskRawQueue queue = new DiskRawQueue(path, 512)) {
            for (int i = 0; i < 5; i++)
                push(queue, s);
        }

        try (DiskQueueReader reader = new DiskQueueReader(path)) {
            assertThat(reader.count()).isEqualTo(5);
            assertThat(reader.bytes()).isEqualTo(5 * (512 + 4));

            Buffer buffer = new Buffer();
            while (reader.moveNext(buffer)) {
                assertBuffer(buffer, s);
            }
        }
    }

    private void assertBuffer(Buffer buffer, String s) {
        assertThat(new String(buffer.buf(), 0, buffer.count(), StandardCharsets.UTF_8)).isEqualTo(s);
    }

    @Test
    public void canReadTwice() throws IOException {
        String s1 = Strings.repeat("a", 512);
        String s2 = Strings.repeat("b", 512);

        Path path = temp.getRoot().toPath();
        try (DiskRawQueue queue = new DiskRawQueue(path, 512)) {
            push(queue, s1);
            push(queue, s2);
        }

        try (DiskQueueReader reader = new DiskQueueReader(path)) {
            Buffer buffer = new Buffer();
            reader.moveNext(buffer);
            assertBuffer(buffer, s1);
        }

        try (DiskQueueReader reader = new DiskQueueReader(path)) {
            Buffer buffer = new Buffer();
            reader.moveNext(buffer);
            assertBuffer(buffer, s1);
            reader.moveNext(buffer);
            assertBuffer(buffer, s2);
        }
    }

    private void push(DiskRawQueue queue, String s) throws IOException {
        queue.push(new Buffer(s.getBytes(StandardCharsets.UTF_8)));
    }

    private String pop(DiskRawQueue queue) throws IOException {
        Buffer buffer = new Buffer();
        if (!queue.pop(buffer)) return null;
        return new String(buffer.buf(), 0, buffer.count(), StandardCharsets.UTF_8);
    }
}
