package rx;

import java.util.concurrent.TimeUnit;
import rx.functions.Action0;
import rx.subscriptions.MultipleAssignmentSubscription;

public abstract class Scheduler {

    public static abstract class Worker implements Subscription {
        public abstract Subscription schedule(Action0 action0);

        public abstract Subscription schedule(Action0 action0, long j, TimeUnit timeUnit);

        public final Subscription schedulePeriodically(Action0 action, long initialDelay, long period, TimeUnit unit) {
            final long periodInNanos = unit.toNanos(period);
            final long startInNanos = TimeUnit.MILLISECONDS.toNanos(System.currentTimeMillis()) + unit.toNanos(initialDelay);
            final MultipleAssignmentSubscription mas = new MultipleAssignmentSubscription();
            final Action0 action0 = action;
            Action0 recursiveAction = new Action0() {
                long count = 0;

                public final void call() {
                    if (!mas.isUnsubscribed()) {
                        action0.call();
                        long j = startInNanos;
                        long j2 = this.count + 1;
                        this.count = j2;
                        mas.set(Worker.this.schedule(this, (j + (j2 * periodInNanos)) - TimeUnit.MILLISECONDS.toNanos(System.currentTimeMillis()), TimeUnit.NANOSECONDS));
                    }
                }
            };
            MultipleAssignmentSubscription s = new MultipleAssignmentSubscription();
            mas.set(s);
            s.set(schedule(recursiveAction, initialDelay, unit));
            return mas;
        }
    }

    public abstract Worker createWorker();
}
