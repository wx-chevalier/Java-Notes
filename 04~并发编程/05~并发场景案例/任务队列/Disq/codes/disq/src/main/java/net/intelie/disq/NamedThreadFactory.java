package net.intelie.disq;

import java.util.Locale;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

public class NamedThreadFactory implements ThreadFactory {
    private final String nameFormat;
    private final AtomicLong count = new AtomicLong(0);

    public NamedThreadFactory(String nameFormat) {
        this.nameFormat = nameFormat;
    }

    @Override
    public Thread newThread(Runnable r) {
        long number = count.getAndIncrement();
        Thread thread = new Thread(r);
        thread.setName(String.format((Locale) null, nameFormat, number));
        return thread;
    }
}
