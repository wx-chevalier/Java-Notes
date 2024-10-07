package net.intelie.disq;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class PersistentQueue<T> implements Closeable {
    private final InternalQueue queue;
    private final SerializerPool<T> pool;

    public PersistentQueue(InternalQueue queue, SerializerPool<T> pool) {
        this.queue = queue;
        this.pool = pool;
    }

    public SerializerPool<T> pool() {
        return pool;
    }

    public RawQueue rawQueue() {
        return queue.rawQueue();
    }

    public ArrayRawQueue fallbackQueue() {
        return queue.fallbackQueue();
    }

    public void setPaused(boolean paused) {
        queue.setPaused(paused);
    }

    public void reopen() throws IOException {
        queue.reopen();
    }

    public long bytes() {
        return queue.bytes();
    }

    public long count() {
        return queue.count();
    }

    public long remainingBytes() {
        return queue.remainingBytes();
    }

    public long remainingCount() {
        return queue.remainingCount();
    }

    public void clear() throws IOException {
        queue.clear();
    }

    public void flush() throws IOException {
        queue.flush();
    }

    public T blockingPop(long amount, TimeUnit unit) throws InterruptedException, IOException {
        try (SerializerPool<T>.Slot slot = pool.acquire()) {
            return slot.blockingPop(queue, amount, unit);
        }
    }

    public T blockingPop() throws InterruptedException, IOException {
        try (SerializerPool<T>.Slot slot = pool.acquire()) {
            return slot.blockingPop(queue);
        }

    }

    public T pop() throws IOException {
        try (SerializerPool<T>.Slot slot = pool.acquire()) {
            return slot.pop(queue);
        }
    }

    public void push(T obj) throws IOException {
        try (SerializerPool<T>.Slot slot = pool.acquire()) {
            slot.push(queue, obj);
        }
    }

    public T peek() throws IOException {
        try (SerializerPool<T>.Slot slot = pool.acquire()) {
            return slot.peek(queue);
        }
    }

    public void close() {
        queue.close();
    }
}
