package rx.schedulers;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import rx.Scheduler;
import rx.Scheduler.Worker;
import rx.Subscription;
import rx.functions.Action0;
import rx.subscriptions.BooleanSubscription;
import rx.subscriptions.Subscriptions;

public final class TrampolineScheduler extends Scheduler {
    private static final TrampolineScheduler INSTANCE = new TrampolineScheduler();

    private static class InnerCurrentThreadScheduler extends Worker implements Subscription {
        private static final AtomicIntegerFieldUpdater<InnerCurrentThreadScheduler> COUNTER_UPDATER = AtomicIntegerFieldUpdater.newUpdater(InnerCurrentThreadScheduler.class, "counter");
        volatile int counter;
        private final BooleanSubscription innerSubscription;
        private final PriorityBlockingQueue<TimedAction> queue;
        private final AtomicInteger wip;

        private InnerCurrentThreadScheduler() {
            this.queue = new PriorityBlockingQueue();
            this.innerSubscription = new BooleanSubscription();
            this.wip = new AtomicInteger();
        }

        private Subscription enqueue(Action0 action, long execTime) {
            if (this.innerSubscription.isUnsubscribed()) {
                return Subscriptions.unsubscribed();
            }
            final TimedAction timedAction = new TimedAction(action, Long.valueOf(execTime), COUNTER_UPDATER.incrementAndGet(this));
            this.queue.add(timedAction);
            if (this.wip.getAndIncrement() != 0) {
                return Subscriptions.create(new Action0() {
                    public final void call() {
                        InnerCurrentThreadScheduler.this.queue.remove(timedAction);
                    }
                });
            }
            do {
                TimedAction polled = (TimedAction) this.queue.poll();
                if (polled != null) {
                    polled.action.call();
                }
            } while (this.wip.decrementAndGet() > 0);
            return Subscriptions.unsubscribed();
        }

        public final void unsubscribe() {
            this.innerSubscription.unsubscribe();
        }

        public final boolean isUnsubscribed() {
            return this.innerSubscription.isUnsubscribed();
        }

        public final Subscription schedule(Action0 action) {
            return enqueue(action, System.currentTimeMillis());
        }

        public final Subscription schedule(Action0 action, long delayTime, TimeUnit unit) {
            long execTime = System.currentTimeMillis() + unit.toMillis(delayTime);
            return enqueue(new SleepingAction(action, this, execTime), execTime);
        }
    }

    private static final class TimedAction implements Comparable<TimedAction> {
        final Action0 action;
        final int count;
        final Long execTime;

        public final /* bridge */ /* synthetic */ int compareTo(Object x0) {
            TimedAction timedAction = (TimedAction) x0;
            int compareTo = this.execTime.compareTo(timedAction.execTime);
            return compareTo == 0 ? TrampolineScheduler.access$300(this.count, timedAction.count) : compareTo;
        }

        private TimedAction(Action0 action, Long execTime, int count) {
            this.action = action;
            this.execTime = execTime;
            this.count = count;
        }
    }

    public final Worker createWorker() {
        return new InnerCurrentThreadScheduler();
    }

    TrampolineScheduler() {
    }

    static /* synthetic */ int access$300(int x0, int x1) {
        if (x0 < x1) {
            return -1;
        }
        return x0 == x1 ? 0 : 1;
    }
}
