package rx.schedulers;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import rx.Scheduler;
import rx.Scheduler.Worker;
import rx.Subscription;
import rx.functions.Action0;
import rx.internal.schedulers.NewThreadWorker;
import rx.internal.util.RxThreadFactory;
import rx.subscriptions.CompositeSubscription;
import rx.subscriptions.Subscriptions;

final class CachedThreadScheduler extends Scheduler {
    private static final RxThreadFactory EVICTOR_THREAD_FACTORY = new RxThreadFactory("RxCachedWorkerPoolEvictor-");
    private static final RxThreadFactory WORKER_THREAD_FACTORY = new RxThreadFactory("RxCachedThreadScheduler-");

    private static final class CachedWorkerPool {
        private static CachedWorkerPool INSTANCE = new CachedWorkerPool(60, TimeUnit.SECONDS);
        private final ScheduledExecutorService evictExpiredWorkerExecutor = Executors.newScheduledThreadPool(1, CachedThreadScheduler.EVICTOR_THREAD_FACTORY);
        private final ConcurrentLinkedQueue<ThreadWorker> expiringWorkerQueue = new ConcurrentLinkedQueue();
        private final long keepAliveTime;

        private CachedWorkerPool(long keepAliveTime, TimeUnit unit) {
            this.keepAliveTime = unit.toNanos(60);
            this.evictExpiredWorkerExecutor.scheduleWithFixedDelay(new Runnable() {
                public final void run() {
                    CachedWorkerPool.this.evictExpiredWorkers();
                }
            }, this.keepAliveTime, this.keepAliveTime, TimeUnit.NANOSECONDS);
        }

        final ThreadWorker get() {
            while (!this.expiringWorkerQueue.isEmpty()) {
                ThreadWorker threadWorker = (ThreadWorker) this.expiringWorkerQueue.poll();
                if (threadWorker != null) {
                    return threadWorker;
                }
            }
            return new ThreadWorker(CachedThreadScheduler.WORKER_THREAD_FACTORY);
        }

        final void evictExpiredWorkers() {
            if (!this.expiringWorkerQueue.isEmpty()) {
                long currentTimestamp = System.nanoTime();
                Iterator i$ = this.expiringWorkerQueue.iterator();
                while (i$.hasNext()) {
                    ThreadWorker threadWorker = (ThreadWorker) i$.next();
                    if (threadWorker.getExpirationTime() > currentTimestamp) {
                        return;
                    }
                    if (this.expiringWorkerQueue.remove(threadWorker)) {
                        threadWorker.unsubscribe();
                    }
                }
            }
        }

        final void release(ThreadWorker threadWorker) {
            threadWorker.setExpirationTime(System.nanoTime() + this.keepAliveTime);
            this.expiringWorkerQueue.offer(threadWorker);
        }
    }

    private static final class EventLoopWorker extends Worker {
        static final AtomicIntegerFieldUpdater<EventLoopWorker> ONCE_UPDATER = AtomicIntegerFieldUpdater.newUpdater(EventLoopWorker.class, "once");
        private final CompositeSubscription innerSubscription = new CompositeSubscription();
        volatile int once;
        private final ThreadWorker threadWorker;

        EventLoopWorker(ThreadWorker threadWorker) {
            this.threadWorker = threadWorker;
        }

        public final void unsubscribe() {
            if (ONCE_UPDATER.compareAndSet(this, 0, 1)) {
                CachedWorkerPool.INSTANCE.release(this.threadWorker);
            }
            this.innerSubscription.unsubscribe();
        }

        public final boolean isUnsubscribed() {
            return this.innerSubscription.isUnsubscribed();
        }

        public final Subscription schedule(Action0 action) {
            return schedule(action, 0, null);
        }

        public final Subscription schedule(Action0 action, long delayTime, TimeUnit unit) {
            if (this.innerSubscription.isUnsubscribed()) {
                return Subscriptions.unsubscribed();
            }
            Subscription s = this.threadWorker.scheduleActual(action, delayTime, unit);
            this.innerSubscription.add(s);
            s.addParent(this.innerSubscription);
            return s;
        }
    }

    private static final class ThreadWorker extends NewThreadWorker {
        private long expirationTime = 0;

        ThreadWorker(ThreadFactory threadFactory) {
            super(threadFactory);
        }

        public final long getExpirationTime() {
            return this.expirationTime;
        }

        public final void setExpirationTime(long expirationTime) {
            this.expirationTime = expirationTime;
        }
    }

    CachedThreadScheduler() {
    }

    public final Worker createWorker() {
        return new EventLoopWorker(CachedWorkerPool.INSTANCE.get());
    }
}
