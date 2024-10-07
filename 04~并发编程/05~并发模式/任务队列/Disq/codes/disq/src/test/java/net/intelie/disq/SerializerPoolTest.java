package net.intelie.disq;

import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

public class SerializerPoolTest {
    @Test
    public void willClearBufferOnRelease() {
        MySerializer serializer = new MySerializer();
        SerializerPool<Object> pool = new SerializerPool<>(serializer, 100, 1000);

        try (SerializerPool<Object>.Slot slot = pool.acquire()) {
            slot.buffer().write().write(new byte[]{1, 2, 3});
            serializer.count.incrementAndGet();
        }

        try (SerializerPool<Object>.Slot slot = pool.acquire()) {
            assertThat(slot.buffer().read().read()).isEqualTo(-1);
            assertThat(serializer.count.get()).isEqualTo(0);
        }
    }

    @Test
    public void willClearBufferBeforePop() throws IOException {
        MySerializer serializer = new MySerializer();
        SerializerPool<Object> pool = new SerializerPool<>(serializer, 100, 1000);

        InternalQueue queue = new InternalQueue(new ArrayRawQueue(1000));

        try (SerializerPool<Object>.Slot slot = pool.acquire()) {
            slot.buffer().write().write(new byte[]{1, 2, 3});
            slot.push(queue, "does not matter");

            slot.buffer().clear();
            slot.buffer().write().write(new byte[]{1, 2, 3});
            slot.peek(queue);

            slot.buffer().clear();
            slot.buffer().write().write(new byte[]{1, 2, 3});
            slot.pop(queue);
        }
    }

    @Test
    public void willClearBufferBeforeBlockingPop() throws IOException, InterruptedException {
        MySerializer serializer = new MySerializer();
        SerializerPool<Object> pool = new SerializerPool<>(serializer, 100, 1000);

        InternalQueue queue = new InternalQueue(new ArrayRawQueue(1000));

        try (SerializerPool<Object>.Slot slot = pool.acquire()) {
            slot.buffer().write().write(new byte[]{1, 2, 3});
            slot.push(queue, "does not matter");

            slot.buffer().clear();
            slot.buffer().write().write(new byte[]{1, 2, 3});
            slot.blockingPop(queue);
        }
    }

    @Test
    public void willClearBufferBeforeBlockingPopWithAmount() throws IOException, InterruptedException {
        MySerializer serializer = new MySerializer();
        SerializerPool<Object> pool = new SerializerPool<>(serializer, 100, 1000);

        InternalQueue queue = new InternalQueue(new ArrayRawQueue(1000));

        try (SerializerPool<Object>.Slot slot = pool.acquire()) {
            slot.buffer().write().write(new byte[]{1, 2, 3});
            slot.push(queue, "does not matter");

            slot.buffer().clear();
            slot.buffer().write().write(new byte[]{1, 2, 3});
            slot.blockingPop(queue, 1, TimeUnit.MINUTES);
        }
    }

    static class MySerializer implements SerializerFactory<Object> {
        final AtomicInteger count = new AtomicInteger(0);

        @Override
        public Serializer<Object> create() {
            return new Serializer<Object>() {
                @Override
                public void serialize(Buffer buffer, Object obj) throws IOException {
                    buffer.write().write(42);
                    assertThat(buffer.count()).isEqualTo(1);
                }

                @Override
                public Object deserialize(Buffer buffer) throws IOException {
                    assertThat(buffer.count()).isEqualTo(1);
                    assertThat(buffer.read().read()).isEqualTo(42);
                    return 42;
                }

                @Override
                public void clear() {
                    count.set(0);
                }
            };
        }


    }
}
