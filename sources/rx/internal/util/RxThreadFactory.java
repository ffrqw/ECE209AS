package rx.internal.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

public final class RxThreadFactory implements ThreadFactory {
    static final AtomicLongFieldUpdater<RxThreadFactory> COUNTER_UPDATER = AtomicLongFieldUpdater.newUpdater(RxThreadFactory.class, "counter");
    volatile long counter;
    final String prefix;

    public RxThreadFactory(String prefix) {
        this.prefix = prefix;
    }

    public final Thread newThread(Runnable r) {
        Thread t = new Thread(r, this.prefix + COUNTER_UPDATER.incrementAndGet(this));
        t.setDaemon(true);
        return t;
    }
}
