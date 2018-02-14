package rx.internal.schedulers;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import rx.Scheduler;
import rx.Scheduler.Worker;
import rx.Subscription;
import rx.functions.Action0;
import rx.internal.util.RxThreadFactory;
import rx.internal.util.SubscriptionList;
import rx.subscriptions.CompositeSubscription;
import rx.subscriptions.Subscriptions;

public final class EventLoopsScheduler extends Scheduler {
    static final int MAX_THREADS;
    private static final RxThreadFactory THREAD_FACTORY = new RxThreadFactory("RxComputationThreadPool-");
    final FixedSchedulerPool pool = new FixedSchedulerPool();

    private static class EventLoopWorker extends Worker {
        private final SubscriptionList both = new SubscriptionList(this.serial, this.timed);
        private final PoolWorker poolWorker;
        private final SubscriptionList serial = new SubscriptionList();
        private final CompositeSubscription timed = new CompositeSubscription();

        EventLoopWorker(PoolWorker poolWorker) {
            this.poolWorker = poolWorker;
        }

        public final void unsubscribe() {
            this.both.unsubscribe();
        }

        public final boolean isUnsubscribed() {
            return this.both.isUnsubscribed();
        }

        public final Subscription schedule(Action0 action) {
            if (this.both.isUnsubscribed()) {
                return Subscriptions.unsubscribed();
            }
            return this.poolWorker.scheduleActual(action, 0, null, this.serial);
        }

        public final Subscription schedule(Action0 action, long delayTime, TimeUnit unit) {
            if (this.both.isUnsubscribed()) {
                return Subscriptions.unsubscribed();
            }
            return this.poolWorker.scheduleActual(action, delayTime, unit, this.timed);
        }
    }

    static final class FixedSchedulerPool {
        final int cores = EventLoopsScheduler.MAX_THREADS;
        final PoolWorker[] eventLoops = new PoolWorker[this.cores];
        long n;

        FixedSchedulerPool() {
            for (int i = 0; i < this.cores; i++) {
                this.eventLoops[i] = new PoolWorker(EventLoopsScheduler.THREAD_FACTORY);
            }
        }

        public final PoolWorker getEventLoop() {
            PoolWorker[] poolWorkerArr = this.eventLoops;
            long j = this.n;
            this.n = 1 + j;
            return poolWorkerArr[(int) (j % ((long) this.cores))];
        }
    }

    private static final class PoolWorker extends NewThreadWorker {
        PoolWorker(ThreadFactory threadFactory) {
            super(threadFactory);
        }
    }

    static {
        int max;
        int maxThreads = Integer.getInteger("rx.scheduler.max-computation-threads", 0).intValue();
        int ncpu = Runtime.getRuntime().availableProcessors();
        if (maxThreads <= 0 || maxThreads > ncpu) {
            max = ncpu;
        } else {
            max = maxThreads;
        }
        MAX_THREADS = max;
    }

    public final Worker createWorker() {
        return new EventLoopWorker(this.pool.getEventLoop());
    }

    public final Subscription scheduleDirect(Action0 action) {
        return this.pool.getEventLoop().scheduleActual(action, -1, TimeUnit.NANOSECONDS);
    }
}
