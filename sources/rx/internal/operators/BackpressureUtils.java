package rx.internal.operators;

import java.util.concurrent.atomic.AtomicLongFieldUpdater;

public final class BackpressureUtils {
    public static <T> long getAndAddRequest(AtomicLongFieldUpdater<T> requested, T object, long n) {
        long current;
        long next;
        do {
            current = requested.get(object);
            next = current + n;
            if (next < 0) {
                next = Long.MAX_VALUE;
            }
        } while (!requested.compareAndSet(object, current, next));
        return current;
    }
}
