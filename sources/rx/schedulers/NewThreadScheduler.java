package rx.schedulers;

import rx.Scheduler;
import rx.Scheduler.Worker;
import rx.internal.schedulers.NewThreadWorker;
import rx.internal.util.RxThreadFactory;

public final class NewThreadScheduler extends Scheduler {
    private static final NewThreadScheduler INSTANCE = new NewThreadScheduler();
    private static final RxThreadFactory THREAD_FACTORY = new RxThreadFactory("RxNewThreadScheduler-");

    static NewThreadScheduler instance() {
        return INSTANCE;
    }

    private NewThreadScheduler() {
    }

    public final Worker createWorker() {
        return new NewThreadWorker(THREAD_FACTORY);
    }
}
