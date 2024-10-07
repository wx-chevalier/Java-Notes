package net.intelie.disq;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class DisqBuilder<T> {
    private final Processor<T> processor;

    private SerializerFactory<T> serializer = new DefaultSerializer<>();
    private Path directory = null; //default to temp directory
    private long maxSize = Long.MAX_VALUE;
    private boolean flushOnPop = true;
    private boolean flushOnPush = true;
    private long autoFlushMs = -1;

    private int initialBufferCapacity = 4096;
    private int maxBufferCapacity = -1;
    private int fallbackBufferCapacity = 0;
    private int threadCount = 1;
    private ThreadFactory threadFactory = Executors.defaultThreadFactory();

    public DisqBuilder(Processor<T> processor) {
        this.processor = processor;
    }

    public DisqBuilder<T> setSerializer(SerializerFactory<T> serializer) {
        this.serializer = serializer;
        return this;
    }

    public DisqBuilder<T> setDirectory(Path directory) {
        this.directory = directory;
        return this;
    }

    public DisqBuilder<T> setDirectory(String first, String... rest) {
        return setDirectory(Paths.get(first, rest));
    }

    public DisqBuilder<T> setMaxSize(long maxSize) {
        this.maxSize = maxSize;
        return this;
    }

    public DisqBuilder<T> setFlushOnPop(boolean flushOnPop) {
        this.flushOnPop = flushOnPop;
        return this;
    }

    public DisqBuilder<T> setFlushOnPush(boolean flushOnPush) {
        this.flushOnPush = flushOnPush;
        return this;
    }

    public DisqBuilder<T> setAutoFlushMs(long autoFlushMs) {
        this.autoFlushMs = autoFlushMs;
        return this;
    }

    public DisqBuilder<T> setInitialBufferCapacity(int initialBufferCapacity) {
        this.initialBufferCapacity = initialBufferCapacity;
        return this;
    }

    public DisqBuilder<T> setMaxBufferCapacity(int maxBufferCapacity) {
        this.maxBufferCapacity = maxBufferCapacity;
        return this;
    }

    public DisqBuilder<T> setFallbackBufferCapacity(int fallbackBufferCapacity) {
        this.fallbackBufferCapacity = fallbackBufferCapacity;
        return this;
    }

    public DisqBuilder<T> setThreadCount(int threadCount) {
        this.threadCount = threadCount;
        return this;
    }

    public DisqBuilder<T> setNamedThreadFactory(String nameFormat) {
        return setThreadFactory(new NamedThreadFactory(nameFormat));
    }

    public DisqBuilder<T> setThreadFactory(ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
        return this;
    }

    public Disq<T> build() {
        return build(false);
    }

    public Disq<T> build(boolean paused) {
        InternalQueue queue = buildInternalQueue();
        queue.setPaused(paused);

        return new Disq<>(threadFactory, threadCount, autoFlushMs,
                buildSerializerPool(), processor, queue);
    }

    public SerializerPool<T> buildSerializerPool() {
        return new SerializerPool<>(serializer, initialBufferCapacity, maxBufferCapacity);
    }

    public PersistentQueue<T> buildPersistentQueue() {
        return new PersistentQueue<>(
                buildInternalQueue(),
                buildSerializerPool());
    }

    public InternalQueue buildInternalQueue() {
        return new InternalQueue(buildRawQueue(), fallbackBufferCapacity);
    }

    public DiskRawQueue buildRawQueue() {
        return new DiskRawQueue(directory, maxSize, flushOnPop, flushOnPush);
    }

}
