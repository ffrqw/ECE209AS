package rx.internal.util;

import java.util.Queue;
import rx.Subscription;
import rx.exceptions.MissingBackpressureException;
import rx.internal.operators.NotificationLite;
import rx.internal.util.unsafe.SpmcArrayQueue;
import rx.internal.util.unsafe.SpscArrayQueue;
import rx.internal.util.unsafe.UnsafeAccess;

public final class RxRingBuffer implements Subscription {
    public static final int SIZE = _size;
    private static ObjectPool<Queue<Object>> SPMC_POOL = new ObjectPool<Queue<Object>>() {
        protected final /* bridge */ /* synthetic */ Object createObject() {
            return new SpmcArrayQueue(RxRingBuffer.SIZE);
        }
    };
    private static ObjectPool<Queue<Object>> SPSC_POOL = new ObjectPool<Queue<Object>>() {
        protected final /* bridge */ /* synthetic */ Object createObject() {
            return new SpscArrayQueue(RxRingBuffer.SIZE);
        }
    };
    static int _size;
    private static final NotificationLite<Object> on = NotificationLite.instance();
    private final ObjectPool<Queue<Object>> pool;
    private Queue<Object> queue;
    private final int size;
    public volatile Object terminalState;

    public static RxRingBuffer getSpscInstance() {
        if (UnsafeAccess.isUnsafeAvailable()) {
            return new RxRingBuffer(SPSC_POOL, SIZE);
        }
        return new RxRingBuffer();
    }

    static {
        _size = 128;
        if (PlatformDependent.isAndroid()) {
            _size = 16;
        }
        String sizeFromProperty = System.getProperty("rx.ring-buffer.size");
        if (sizeFromProperty != null) {
            try {
                _size = Integer.parseInt(sizeFromProperty);
            } catch (Exception e) {
                System.err.println("Failed to set 'rx.buffer.size' with value " + sizeFromProperty + " => " + e.getMessage());
            }
        }
    }

    private RxRingBuffer(Queue<Object> queue, int size) {
        this.queue = queue;
        this.pool = null;
        this.size = size;
    }

    private RxRingBuffer(ObjectPool<Queue<Object>> pool, int size) {
        this.pool = pool;
        this.queue = (Queue) pool.borrowObject();
        this.size = size;
    }

    public final synchronized void release() {
        Queue<Object> q = this.queue;
        ObjectPool<Queue<Object>> p = this.pool;
        if (!(p == null || q == null)) {
            q.clear();
            this.queue = null;
            p.returnObject(q);
        }
    }

    public final void unsubscribe() {
        release();
    }

    RxRingBuffer() {
        this(new SynchronizedQueue(SIZE), SIZE);
    }

    public final void onNext(Object o) throws MissingBackpressureException {
        boolean iae = false;
        boolean mbe = false;
        synchronized (this) {
            Queue<Object> q = this.queue;
            if (q != null) {
                mbe = !q.offer(NotificationLite.next(o));
            } else {
                iae = true;
            }
        }
        if (iae) {
            throw new IllegalStateException("This instance has been unsubscribed and the queue is no longer usable.");
        } else if (mbe) {
            throw new MissingBackpressureException();
        }
    }

    public final boolean isEmpty() {
        Queue<Object> q = this.queue;
        if (q == null) {
            return true;
        }
        return q.isEmpty();
    }

    public final Object poll() {
        Object obj;
        synchronized (this) {
            Queue<Object> q = this.queue;
            if (q == null) {
                obj = null;
            } else {
                obj = q.poll();
                Object obj2 = this.terminalState;
            }
        }
        return obj;
    }

    public final boolean isUnsubscribed() {
        return this.queue == null;
    }
}
