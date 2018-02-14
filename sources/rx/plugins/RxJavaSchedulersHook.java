package rx.plugins;

import rx.Scheduler;
import rx.functions.Action0;

public class RxJavaSchedulersHook {
    private static final RxJavaSchedulersHook DEFAULT_INSTANCE = new RxJavaSchedulersHook();

    protected RxJavaSchedulersHook() {
    }

    public static Scheduler getComputationScheduler() {
        return null;
    }

    public static Scheduler getIOScheduler() {
        return null;
    }

    public static Scheduler getNewThreadScheduler() {
        return null;
    }

    public static Action0 onSchedule(Action0 action) {
        return action;
    }

    public static RxJavaSchedulersHook getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }
}
