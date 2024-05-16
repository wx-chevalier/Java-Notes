package net.intelie.disq;

import com.google.common.base.Strings;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class PersistentQueueTest {
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Test
    public void testPushAndCloseThenOpenAndPop() throws Exception {
        DiskRawQueue bq = new DiskRawQueue(temp.getRoot().toPath(), 1000);
        PersistentQueue<Object> queue = new PersistentQueue<>(new InternalQueue(bq),
                new SerializerPool<>(GsonSerializer.make(), 32, 1 << 16));

        assertThat(queue.rawQueue()).isEqualTo(bq);
        assertThat(queue.fallbackQueue()).isInstanceOf(ArrayRawQueue.class);

        for (int i = 0; i < 20; i++)
            queue.push("test" + i);
        for (int i = 0; i < 10; i++)
            assertThat(queue.pop()).isEqualTo("test" + i);
        queue.close();
        queue.reopen();

        assertThat(queue.remainingBytes()).isEqualTo(512 * 121 - 230);
        assertThat(queue.remainingCount()).isEqualTo(2683);
        assertThat(queue.count()).isEqualTo(10);
        assertThat(queue.bytes()).isEqualTo(230);
        assertThat(queue.peek()).isEqualTo("test10");

        for (int i = 10; i < 20; i++)
            assertThat(queue.pop()).isEqualTo("test" + i);

        assertThat(queue.count()).isEqualTo(0);
        assertThat(queue.bytes()).isEqualTo(230);
        assertThat(queue.remainingBytes()).isEqualTo(512 * 121 - 230);
        assertThat(queue.remainingCount()).isEqualTo(15488);
        assertThat(queue.pop()).isNull();
        assertThat(queue.peek()).isNull();
    }

    @Test
    public void notYouCanPushNulls() throws Exception {
        //you couldn't in an earlier version
        DiskRawQueue bq = new DiskRawQueue(temp.getRoot().toPath(), 1000);
        PersistentQueue<Object> queue = new PersistentQueue<>(new InternalQueue(bq),
                new SerializerPool<>(GsonSerializer.make(), 32, 1 << 16));

        queue.push(null);
        assertThat(queue.pop()).isEqualTo(null);
    }

    @Test
    public void testWhenTheDirectoryIsReadOnly() throws Exception {
        DiskRawQueue bq = new DiskRawQueue(temp.getRoot().toPath(), 1000);
        PersistentQueue<Object> queue = new PersistentQueue<>(new InternalQueue(bq, 1000),
                new SerializerPool<>(GsonSerializer.make(), 32, 1 << 16));

        for (int i = 0; i < 10; i++)
            queue.push("test" + i);

        assertThat(queue.count()).isEqualTo(10);
        assertThat(queue.remainingCount()).isEqualTo(5622);
        assertThat(queue.bytes()).isEqualTo(110);
        assertThat(queue.remainingBytes()).isEqualTo(512 * 121 - 110);

        new File(temp.getRoot(), "state").setWritable(false);
        queue.reopen();


        for (int i = 10; i < 20; i++)
            queue.push("test" + i);

        assertThat(queue.count()).isEqualTo(10);
        assertThat(queue.remainingCount()).isEqualTo(0);
        assertThat(queue.bytes()).isEqualTo(120);
        assertThat(queue.remainingBytes()).isEqualTo(0);

        for (int i = 10; i < 20; i++)
            assertThat(queue.pop()).isEqualTo("test" + i);

        assertThat(queue.pop()).isEqualTo(null);
        assertThat(queue.peek()).isEqualTo(null);

        new File(temp.getRoot(), "state").setWritable(true);
        queue.reopen();

        for (int i = 0; i < 10; i++)
            assertThat(queue.pop()).isEqualTo("test" + i);
    }

    @Test(timeout = 3000)
    public void testBlockingTimeout() throws Exception {
        DiskRawQueue bq = new DiskRawQueue(temp.getRoot().toPath(), 1000, true, true);
        PersistentQueue<Object> queue = new PersistentQueue<>(new InternalQueue(bq),
                new SerializerPool<>(GsonSerializer.make(), 32, 1 << 16));

        String s = Strings.repeat("a", 506);

        for (int i = 0; i < 121; i++)
            queue.push(s);

        for (int i = 0; i < 121; i++)
            assertThat(queue.blockingPop(10, TimeUnit.MILLISECONDS)).isEqualTo(s);
        assertThat(queue.blockingPop(10, TimeUnit.MILLISECONDS)).isEqualTo(null);
    }

    @Test(timeout = 3000)
    public void testBlockingRead() throws Throwable {
        DiskRawQueue bq = new DiskRawQueue(temp.getRoot().toPath(), 1000000, true, true);
        PersistentQueue<Object> queue = new PersistentQueue<>(new InternalQueue(bq),
                new SerializerPool<>(GsonSerializer.make(), 32, 1 << 16));
        queue.setPaused(true);

        String s = Strings.repeat("a", 508);

        ReaderThread t2 = new ReaderThread(queue, s);
        t2.start();
        while (t2.getState() != Thread.State.TIMED_WAITING) {
            Thread.sleep(10);
        }

        WriterThread t1 = new WriterThread(queue, s);
        t1.start();
        t1.waitFinish();

        assertThat(queue.count()).isEqualTo(200);

        queue.setPaused(false);

        t2.waitFinish();
    }

    @Test
    public void canPushBigCompressing() throws Exception {
        DiskRawQueue bq = new DiskRawQueue(temp.getRoot().toPath(), 1000000);
        PersistentQueue<Object> queue = new PersistentQueue<>(new InternalQueue(bq),
                new SerializerPool<>(GsonSerializer.make(), 32, 1 << 16));

        queue.push(Strings.repeat("a", 10000));

        //assertThat(queue.bytes()).isLessThan(10000);
        assertThat(queue.bytes()).isEqualTo(10006);
    }

    @Test
    public void canClear() throws Exception {
        DiskRawQueue bq = new DiskRawQueue(temp.getRoot().toPath(), 1000);
        PersistentQueue<Object> queue = new PersistentQueue<>(new InternalQueue(bq),
                new SerializerPool<>(GsonSerializer.make(), 32, 1 << 16));

        for (int i = 0; i < 20; i++)
            queue.push("test" + i);
        assertThat(queue.count()).isEqualTo(20);
        queue.clear();
        assertThat(queue.count()).isEqualTo(0);
    }

    @Test
    public void canAvoidFlush() throws Exception {
        DiskRawQueue bq = new DiskRawQueue(temp.getRoot().toPath(), 1000, false, false);
        PersistentQueue<Object> queue = new PersistentQueue<>(new InternalQueue(bq),
                new SerializerPool<>(GsonSerializer.make(), 32, 1 << 16));

        for (int i = 0; i < 20; i++)
            queue.push("test" + i);
        assertThat(queue.count()).isEqualTo(20);
        assertThat(new File(temp.getRoot(), "state").length()).isEqualTo(0);
        queue.flush();
        assertThat(new File(temp.getRoot(), "state").length()).isEqualTo(512);
    }

    @Test
    public void serializerException() throws Exception {
        RuntimeException exc1 = new RuntimeException();
        RuntimeException exc2 = new RuntimeException();
        @SuppressWarnings("unchecked")
        SerializerFactory<Object> factory = mock(SerializerFactory.class, RETURNS_DEEP_STUBS);
        Serializer<Object> serializer = factory.create();
        doThrow(exc1).when(serializer).serialize(any(), any());
        doThrow(exc2).when(serializer).deserialize(any());

        DiskRawQueue bq = new DiskRawQueue(temp.getRoot().toPath(), 1000, false, false);
        bq.push(new Buffer());

        PersistentQueue<Object> queue = new PersistentQueue<>(new InternalQueue(bq),
                new SerializerPool<>(factory, 32, 1 << 16));

        assertThat(queue.count()).isEqualTo(1);
        assertThatThrownBy(() -> queue.push("abc")).isEqualTo(exc1);
        assertThat(queue.count()).isEqualTo(1);
        assertThatThrownBy(() -> queue.pop()).isEqualTo(exc2);
        assertThat(queue.count()).isEqualTo(0);
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
        private final PersistentQueue<Object> queue;
        private final String s;

        public WriterThread(PersistentQueue<Object> queue, String s) {
            this.queue = queue;
            this.s = s;
        }

        @Override
        public void runThrowable() throws Exception {
            for (int i = 0; i < 200; i++) {
                queue.push(s + i);
            }
        }
    }

    private static class ReaderThread extends ThrowableThread {
        private final PersistentQueue<Object> queue;
        private final String s;

        public ReaderThread(PersistentQueue<Object> queue, String s) {
            this.queue = queue;
            this.s = s;
        }

        @Override
        public void runThrowable() throws Throwable {
            for (int i = 0; i < 200; i++) {
                assertThat(queue.blockingPop()).isEqualTo(s + i);
            }
        }
    }

}
