package net.intelie.disq;


import com.google.common.base.Strings;
import net.intelie.introspective.ThreadResources;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class InternalQueueTest {
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Test
    public void testAllocations() throws IOException, InterruptedException {
        DiskRawQueue disk = new DiskRawQueue(temp.getRoot().toPath(), 100000000);

        Buffer buffer = new Buffer("test".getBytes(StandardCharsets.UTF_8));

        InternalQueue queue = new InternalQueue(disk);
        Buffer buffer2 = new Buffer();


        //warmup
        for (int i = 0; i < 10000; i++) {
            queue.push(buffer);
            queue.flush();
        }
        for (int i = 0; i < 10000; i++)
            queue.peek(buffer2);
        for (int i = 0; i < 10000; i++) {
            queue.blockingPop(buffer2);
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
            queue.blockingPop(buffer2);
            queue.flush();
        }
        for (int i = 0; i < 10000; i++) {
            queue.push(buffer);
            queue.blockingPop(buffer2);
        }

        long end = ThreadResources.allocatedBytes(Thread.currentThread()) - start;
        assertThat(end).isZero();
        assertThat(disk.files()).isEqualTo(1);
        assertThat(queue.count()).isEqualTo(0);
    }

    @Test
    public void testPushAndCloseThenOpenAndPop() throws Exception {
        DiskRawQueue bq = new DiskRawQueue(temp.getRoot().toPath(), 1000);
        InternalQueue queue = new InternalQueue(bq);
        Adapter adapter = new Adapter(queue);

        assertThat(queue.rawQueue()).isEqualTo(bq);
        assertThat(queue.fallbackQueue()).isInstanceOf(ArrayRawQueue.class);

        for (int i = 0; i < 20; i++)
            adapter.push("test" + i);
        for (int i = 0; i < 10; i++)
            assertThat(adapter.pop()).isEqualTo("test" + i);
        queue.close();
        queue.reopen();

        assertThat(queue.remainingBytes()).isEqualTo(512 * 121 - 230);
        assertThat(queue.remainingCount()).isEqualTo(2683);
        assertThat(queue.count()).isEqualTo(10);
        assertThat(queue.bytes()).isEqualTo(230);
        assertThat(adapter.peek()).isEqualTo("test10");

        for (int i = 10; i < 20; i++)
            assertThat(adapter.pop()).isEqualTo("test" + i);

        assertThat(queue.count()).isEqualTo(0);
        assertThat(queue.bytes()).isEqualTo(230);
        assertThat(queue.remainingBytes()).isEqualTo(512 * 121 - 230);
        assertThat(queue.remainingCount()).isEqualTo(15488);
        assertThat(adapter.pop()).isNull();
        assertThat(adapter.peek()).isNull();
    }

    @Test
    public void testWhenTheDirectoryIsReadOnly() throws Exception {
        DiskRawQueue bq = new DiskRawQueue(temp.getRoot().toPath(), 1000);
        InternalQueue queue = new InternalQueue(bq, 1000);
        Adapter adapter = new Adapter(queue);

        for (int i = 0; i < 10; i++)
            adapter.push("test" + i);

        assertThat(queue.count()).isEqualTo(10);
        assertThat(queue.remainingCount()).isEqualTo(5622);
        assertThat(queue.bytes()).isEqualTo(110);
        assertThat(queue.remainingBytes()).isEqualTo(512 * 121 - 110);

        new File(temp.getRoot(), "state").setWritable(false);
        queue.reopen();


        for (int i = 10; i < 20; i++)
            adapter.push("test" + i);

        assertThat(queue.count()).isEqualTo(10);
        assertThat(queue.remainingCount()).isEqualTo(0);
        assertThat(queue.bytes()).isEqualTo(120);
        assertThat(queue.remainingBytes()).isEqualTo(0);

        for (int i = 10; i < 20; i++)
            assertThat(adapter.pop()).isEqualTo("test" + i);

        assertThat(adapter.pop()).isEqualTo(null);
        assertThat(adapter.peek()).isEqualTo(null);

        new File(temp.getRoot(), "state").setWritable(true);
        queue.reopen();

        for (int i = 0; i < 10; i++)
            assertThat(adapter.pop()).isEqualTo("test" + i);
    }


    @Test(timeout = 3000)
    public void testBlockingTimeout() throws Exception {
        DiskRawQueue bq = new DiskRawQueue(temp.getRoot().toPath(), 1000, true, true);
        InternalQueue queue = new InternalQueue(bq, 1 << 16);
        Adapter adapter = new Adapter(queue);

        String s = Strings.repeat("a", 506);

        for (int i = 0; i < 121; i++)
            adapter.push(s);

        for (int i = 0; i < 121; i++)
            assertThat(adapter.blockingPop(10, TimeUnit.MILLISECONDS)).isEqualTo(s);
        assertThat(adapter.blockingPop(10, TimeUnit.MILLISECONDS)).isEqualTo(null);
    }

    @Test(timeout = 3000)
    public void testBlockingRead() throws Throwable {
        DiskRawQueue bq = new DiskRawQueue(temp.getRoot().toPath(), 1000000, true, true);
        InternalQueue queue = new InternalQueue(bq, 1 << 16);
        queue.setPaused(true);

        String s = Strings.repeat("a", 9900);

        ReaderThread t2 = new ReaderThread(queue, s, 100, 200);
        t2.start();
        while (t2.getState() != Thread.State.TIMED_WAITING) {
            Thread.sleep(10);
        }

        WriterThread t1 = new WriterThread(queue, s);
        t1.start();

        t1.waitFinish();

        assertThat(queue.count()).isEqualTo(100);
        queue.setPaused(false);

        t2.waitFinish();
    }

    @Test
    public void canPushBigCompressing() throws Exception {
        DiskRawQueue bq = new DiskRawQueue(temp.getRoot().toPath(), 1000000);
        InternalQueue queue = new InternalQueue(bq, 1 << 16);
        Adapter adapter = new Adapter(queue);

        adapter.push(Strings.repeat("a", 10000));

        //assertThat(queue.bytes()).isLessThan(10000);
        assertThat(queue.bytes()).isEqualTo(10006);
    }

    @Test
    public void canClear() throws Exception {
        DiskRawQueue bq = new DiskRawQueue(temp.getRoot().toPath(), 1000);
        InternalQueue queue = new InternalQueue(bq, 1 << 16);
        Adapter adapter = new Adapter(queue);

        for (int i = 0; i < 20; i++)
            adapter.push("test" + i);
        assertThat(queue.count()).isEqualTo(20);
        queue.clear();
        assertThat(queue.count()).isEqualTo(0);
    }

    @Test
    public void canAvoidFlush() throws Exception {
        DiskRawQueue bq = new DiskRawQueue(temp.getRoot().toPath(), 1000, false, false);
        InternalQueue queue = new InternalQueue(bq, 1 << 16);
        Adapter adapter = new Adapter(queue);

        for (int i = 0; i < 20; i++)
            adapter.push("test" + i);
        assertThat(queue.count()).isEqualTo(20);
        assertThat(new File(temp.getRoot(), "state").length()).isEqualTo(0);
        queue.flush();
        assertThat(new File(temp.getRoot(), "state").length()).isEqualTo(512);
    }


    private static abstract class ThrowableThread extends Thread {
        private Throwable t;

        public abstract void runThrowable() throws Throwable;

        @Override
        public void run() {
            try {
                runThrowable();
            } catch (Throwable throwable) {
                t = throwable;
            }
        }

        public void waitFinish() throws Throwable {
            join();
            if (t != null) throw t;
        }
    }

    private static class WriterThread extends ThrowableThread {
        private final Adapter queue;
        private final String s;

        public WriterThread(InternalQueue queue, String s) {
            this.queue = new Adapter(queue);
            this.s = s;
            this.setName("WRITER");
        }

        @Override
        public void runThrowable() throws Exception {
            for (int i = 0; i < 200; i++) {
                queue.push(s + i);
            }
        }
    }

    private static class ReaderThread extends ThrowableThread {
        private final Adapter queue;
        private final String s;
        private final int from;
        private final int to;

        public ReaderThread(InternalQueue queue, String s, int from, int to) {
            this.queue = new Adapter(queue);
            this.s = s;
            this.from = from;
            this.to = to;
            this.setName("READER");
        }

        @Override
        public void runThrowable() throws Throwable {
            for (int i = from; i < to; i++) {
                String popped = queue.blockingPop();
                assertThat(popped).isEqualTo(s + i);
            }
        }
    }

    private static class Adapter {
        private final SerializerPool<String> pool = new SerializerPool<>(
                new GsonSerializer<>(String.class),
                1000, -1);
        private final InternalQueue queue;

        private Adapter(InternalQueue queue) {
            this.queue = queue;
        }

        public void push(String s) throws IOException {
            try (SerializerPool<String>.Slot slot = pool.acquire()) {
                slot.push(queue, s);
            }
        }

        public String pop() throws IOException {
            try (SerializerPool<String>.Slot slot = pool.acquire()) {
                return slot.pop(queue);
            }
        }

        public String blockingPop() throws InterruptedException, IOException {
            try (SerializerPool<String>.Slot slot = pool.acquire()) {
                return slot.blockingPop(queue);
            }
        }

        public String blockingPop(long amount, TimeUnit unit) throws InterruptedException, IOException {
            try (SerializerPool<String>.Slot slot = pool.acquire()) {
                return slot.blockingPop(queue, amount, unit);
            }
        }

        public String peek() throws IOException {
            try (SerializerPool<String>.Slot slot = pool.acquire()) {
                return slot.peek(queue);
            }
        }
    }

}
