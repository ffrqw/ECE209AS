package rx.internal.util;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import rx.Scheduler.Worker;
import rx.functions.Action0;
import rx.internal.util.unsafe.MpmcArrayQueue;
import rx.internal.util.unsafe.UnsafeAccess;
import rx.schedulers.Schedulers;

public abstract class ObjectPool<T> {
    private final int maxSize;
    private Queue<T> pool;
    private Worker schedulerWorker;

    protected abstract T createObject();

    public ObjectPool() {
        this(0, 0, 67);
    }

    private ObjectPool(int min, int max, long validationInterval) {
        this.maxSize = 0;
        initialize(0);
        this.schedulerWorker = Schedulers.computation().createWorker();
        this.schedulerWorker.schedulePeriodically(new Action0(0, 0) {
            final /* synthetic */ int val$min;

            public final void call() {
                int size = ObjectPool.this.pool.size();
                int i;
                if (size < this.val$min) {
                    int sizeToBeAdded = 0 - size;
                    for (i = 0; i < sizeToBeAdded; i++) {
                        ObjectPool.this.pool.add(ObjectPool.this.createObject());
                    }
                } else if (size > 0) {
                    int sizeToBeRemoved = size - 0;
                    for (i = 0; i < sizeToBeRemoved; i++) {
                        ObjectPool.this.pool.poll();
                    }
                }
            }
        }, 67, 67, TimeUnit.SECONDS);
    }

    public final T borrowObject() {
        T object = this.pool.poll();
        if (object == null) {
            return createObject();
        }
        return object;
    }

    public final void returnObject(T object) {
        if (object != null) {
            this.pool.offer(object);
        }
    }

    private void initialize(int min) {
        if (UnsafeAccess.isUnsafeAvailable()) {
            this.pool = new MpmcArrayQueue(Math.max(this.maxSize, 1024));
        } else {
            this.pool = new ConcurrentLinkedQueue();
        }
        for (int i = 0; i < min; i++) {
            this.pool.add(createObject());
        }
    }
}
