package rx.schedulers;

import rx.Scheduler.Worker;
import rx.functions.Action0;

final class SleepingAction implements Action0 {
    private final long execTime;
    private final Worker innerScheduler;
    private final Action0 underlying;

    public SleepingAction(Action0 underlying, Worker scheduler, long execTime) {
        this.underlying = underlying;
        this.innerScheduler = scheduler;
        this.execTime = execTime;
    }

    public final void call() {
        if (!this.innerScheduler.isUnsubscribed()) {
            if (this.execTime > System.currentTimeMillis()) {
                long delay = this.execTime - System.currentTimeMillis();
                if (delay > 0) {
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException(e);
                    }
                }
            }
            if (!this.innerScheduler.isUnsubscribed()) {
                this.underlying.call();
            }
        }
    }
}
