package rx.android.schedulers;

import android.os.Handler;
import java.util.concurrent.TimeUnit;
import rx.Scheduler;
import rx.Scheduler.Worker;
import rx.Subscription;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.functions.Action0;
import rx.internal.schedulers.ScheduledAction;
import rx.subscriptions.CompositeSubscription;
import rx.subscriptions.Subscriptions;

public final class HandlerScheduler extends Scheduler {
    private final Handler handler;

    static class HandlerWorker extends Worker {
        private final CompositeSubscription compositeSubscription = new CompositeSubscription();
        private final Handler handler;

        HandlerWorker(Handler handler) {
            this.handler = handler;
        }

        public final void unsubscribe() {
            this.compositeSubscription.unsubscribe();
        }

        public final boolean isUnsubscribed() {
            return this.compositeSubscription.isUnsubscribed();
        }

        public final Subscription schedule(Action0 action, long delayTime, TimeUnit unit) {
            if (this.compositeSubscription.isUnsubscribed()) {
                return Subscriptions.unsubscribed();
            }
            RxAndroidPlugins.getInstance().getSchedulersHook();
            final Subscription scheduledAction = new ScheduledAction(RxAndroidSchedulersHook.onSchedule(action));
            scheduledAction.addParent(this.compositeSubscription);
            this.compositeSubscription.add(scheduledAction);
            this.handler.postDelayed(scheduledAction, unit.toMillis(delayTime));
            scheduledAction.add(Subscriptions.create(new Action0() {
                public final void call() {
                    HandlerWorker.this.handler.removeCallbacks(scheduledAction);
                }
            }));
            return scheduledAction;
        }

        public final Subscription schedule(Action0 action) {
            return schedule(action, 0, TimeUnit.MILLISECONDS);
        }
    }

    HandlerScheduler(Handler handler) {
        this.handler = handler;
    }

    public final Worker createWorker() {
        return new HandlerWorker(this.handler);
    }
}
