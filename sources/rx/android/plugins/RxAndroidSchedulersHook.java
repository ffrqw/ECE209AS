package rx.android.plugins;

import rx.Scheduler;
import rx.functions.Action0;

public final class RxAndroidSchedulersHook {
    private static final RxAndroidSchedulersHook DEFAULT_INSTANCE = new RxAndroidSchedulersHook();

    public static RxAndroidSchedulersHook getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    public static Scheduler getMainThreadScheduler() {
        return null;
    }

    public static Action0 onSchedule(Action0 action) {
        return action;
    }
}
