package net.intelie.disq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class Disq<T> implements AutoCloseable {
    private static final Logger LOGGER = LoggerFactory.getLogger(Disq.class);

    private final List<Thread> threads;
    private final long autoFlushNanos;
    private final SerializerPool<T> serializerPool;
    private final List<Object> locks;
    private final InternalQueue queue;
    private final AtomicLong nextFlush;
    private final AtomicBoolean open;

    public Disq(ThreadFactory factory, int threads, long autoFlushMs, SerializerPool<T> serializerPool,
                Processor<T> processor, InternalQueue queue) {
        if (autoFlushMs > 0)
            threads = Math.max(threads, 1);

        this.threads = new ArrayList<>(threads);
        this.autoFlushNanos = autoFlushMs * 1_000_000;
        this.serializerPool = serializerPool;
        this.locks = new ArrayList<>();
        this.queue = queue;
        this.open = new AtomicBoolean(true);
        this.nextFlush = autoFlushNanos > 0 ? new AtomicLong(System.nanoTime() + autoFlushNanos) : null;

        for (int i = 0; i < threads; i++) {
            Object shutdownLock = new Object();
            Thread thread = factory.newThread(new WorkerRunnable(queue, shutdownLock, processor));
            this.locks.add(shutdownLock);
            this.threads.add(thread);

            thread.start();
        }
    }

    public static <T> DisqBuilder<T> builder() {
        return new DisqBuilder<>(null);
    }

    public static <T> DisqBuilder<T> builder(Processor<T> processor) {
        return new DisqBuilder<>(processor);
    }

    public InternalQueue queue() {
        return queue;
    }

    public long count() {
        return queue.count();
    }

    public long bytes() {
        return queue.bytes();
    }

    public long remainingBytes() {
        return queue.remainingBytes();
    }

    public boolean submit(T obj) throws IOException {
        if (!open.get()) return false;
        try (SerializerPool<T>.Slot slot = serializerPool.acquire()) {
            slot.push(queue, obj);
        }
        return true;
    }

    public void pause() {
        queue.setPaused(true);
    }

    public void resume() {
        queue.setPaused(false);
    }

    public void clear() throws IOException {
        queue.clear();
    }

    public void flush() throws IOException {
        queue.flush();
    }

    @Override
    public void close() throws InterruptedException {
        if (!open.getAndSet(false))
            return;
        try {
            for (int i = 0; i < threads.size(); i++) {
                synchronized (locks.get(i)) {
                    threads.get(i).interrupt();
                }
            }
            for (Thread thread : threads)
                thread.join();
        } finally {
            queue.close();
        }
    }

    private class WorkerRunnable implements Runnable {
        private final InternalQueue queue;
        private final Object shutdownLock;
        private final Processor<T> processor;

        public WorkerRunnable(InternalQueue queue, Object shutdownLock, Processor<T> processor) {
            this.queue = queue;
            this.shutdownLock = shutdownLock;
            this.processor = processor;
        }

        @Override
        public void run() {
            while (open.get()) {
                try (SerializerPool<T>.Slot slot = serializerPool.acquire()) {
                    long nextFlushNanos = nextFlush != null ? nextFlush.get() : 0;
                    T obj = blockingPop(slot, nextFlushNanos);

                    process(obj);

                    maybeFlush(nextFlushNanos);
                } catch (Throwable e) {
                    LOGGER.info("Exception processing element", e);
                }
            }
        }

        private void process(T obj) throws Exception {
            if (obj == null || processor == null)
                return;
            synchronized (shutdownLock) {
                //this lock only exists to avoid a regular interrupt
                //during processor execution
                boolean interrupted = Thread.interrupted();
                try {
                    processor.process(obj);
                } finally {
                    if (interrupted) Thread.currentThread().interrupt();
                }
            }
        }

        private void maybeFlush(long nextFlushNanos) throws IOException {
            long now = System.nanoTime();
            if (nextFlush != null && now >= nextFlushNanos && nextFlush.compareAndSet(nextFlushNanos, now + autoFlushNanos))
                queue.flush();
        }

        private T blockingPop(SerializerPool<T>.Slot slot, long nextFlushNanos) throws IOException {
            try {
                if (nextFlush != null) {
                    long wait = Math.max(nextFlushNanos - System.nanoTime(), 0);
                    return slot.blockingPop(queue, wait, TimeUnit.NANOSECONDS);
                } else {
                    return slot.blockingPop(queue);
                }
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
            return null;
        }
    }

}
