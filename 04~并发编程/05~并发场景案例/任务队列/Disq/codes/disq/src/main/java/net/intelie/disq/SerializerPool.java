package net.intelie.disq;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class SerializerPool<T> {
    private final ObjectPool<Slot> pool;
    private final SerializerFactory<T> factory;
    private final int initialBufferSize;
    private final int maxBufferSize;

    public SerializerPool(SerializerFactory<T> factory, int initialBufferSize, int maxBufferSize) {
        this.factory = factory;
        this.initialBufferSize = initialBufferSize;
        this.maxBufferSize = maxBufferSize;
        this.pool = new ObjectPool<>(Slot::new, 0, 5);
    }

    public Slot acquire() {
        return pool.acquire().obj();
    }

    public class Slot implements Closeable {
        private final ObjectPool<Slot>.Ref ref;
        private final Serializer<T> serializer;
        private final Buffer buffer;

        public Slot(ObjectPool<Slot>.Ref ref) {
            this.ref = ref;
            this.serializer = factory.create();
            this.buffer = new Buffer(initialBufferSize, maxBufferSize);
        }

        @Override
        public void close() {
            buffer.clear();
            serializer.clear();
            this.ref.close();
        }

        public void push(InternalQueue queue, T obj) throws IOException {
            buffer.clear();
            serializer.serialize(buffer, obj);
            queue.push(buffer);
        }

        public T pop(InternalQueue queue) throws IOException {
            buffer.clear();
            if (!queue.pop(buffer))
                return null;
            return serializer.deserialize(buffer);
        }

        public T peek(InternalQueue queue) throws IOException {
            buffer.clear();
            if (!queue.peek(buffer))
                return null;
            return serializer.deserialize(buffer);
        }

        public T blockingPop(InternalQueue queue) throws InterruptedException, IOException {
            buffer.clear();
            queue.blockingPop(buffer);
            return serializer.deserialize(buffer);
        }

        public T blockingPop(InternalQueue queue, long amount, TimeUnit unit) throws InterruptedException, IOException {
            buffer.clear();
            if (!queue.blockingPop(buffer, amount, unit))
                return null;
            return serializer.deserialize(buffer);
        }

        public Buffer buffer() {
            return buffer;
        }
    }
}
