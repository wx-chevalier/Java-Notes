package net.intelie.disq;

import java.io.IOException;

public class LenientRawQueue implements RawQueue {
    private final RawQueue queue;
    private final Lenient.Op reopen;
    private final Lenient.Op bytes;
    private final Lenient.Op count;
    private final Lenient.Op remainingBytes;
    private final Lenient.Op remainingCount;
    private final Lenient.Op touch;
    private final Lenient.Op clear;
    private final Lenient.Op pop;
    private final Lenient.Op peek;
    private final Lenient.Op push;
    private final Lenient.Op flush;
    private final Lenient.Op close;

    public LenientRawQueue(RawQueue queue) {
        this.queue = queue;
        this.reopen = x -> {
            queue.reopen();
            return 1;
        };
        this.bytes = x -> queue.bytes();
        this.count = x -> queue.count();
        this.remainingBytes = x -> queue.remainingBytes();
        this.remainingCount = x -> queue.remainingCount();
        this.touch = x -> {
            queue.touch();
            return 1;
        };
        this.clear = x -> {
            queue.clear();
            return 1;
        };
        this.pop = x -> queue.pop(x) ? 1 : 0;
        this.peek = x -> queue.peek(x) ? 1 : 0;
        this.push = x -> {
            queue.push(x);
            return 1;
        };
        this.flush = x -> {
            queue.flush();
            return 1;
        };
        this.close = x -> {
            queue.close();
            return 1;
        };
    }

    @Override
    public void reopen() {
        Lenient.performSafe(queue, null, reopen, 0);
    }

    @Override
    public long bytes() {
        return Lenient.performSafe(queue, null, bytes, 0);
    }

    @Override
    public long count() {
        return Lenient.performSafe(queue, null, count, 0);
    }

    @Override
    public long remainingBytes() {
        return Lenient.performSafe(queue, null, remainingBytes, 0);

    }

    @Override
    public long remainingCount() {
        return Lenient.performSafe(queue, null, remainingCount, 0);
    }

    @Override
    public void touch() throws IOException {
        Lenient.perform(queue, null, touch);
    }

    @Override
    public void clear() throws IOException {
        Lenient.perform(queue, null, clear);
    }

    @Override
    public boolean pop(Buffer buffer) throws IOException {
        return Lenient.perform(queue, buffer, pop) > 0;
    }

    @Override
    public boolean peek(Buffer buffer) throws IOException {
        return Lenient.perform(queue, buffer, peek) > 0;
    }

    @Override
    public void push(Buffer buffer) throws IOException {
        Lenient.perform(queue, buffer, push);
    }

    @Override
    public void flush() throws IOException {
        Lenient.perform(queue, null, flush);
    }

    @Override
    public void close() {
        Lenient.performSafe(queue, null, close, 0);
    }
}
