package rx.android.schedulers;

import android.os.Handler;
import android.os.Looper;
import rx.Scheduler;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;

public final class AndroidSchedulers {
    private static final Scheduler MAIN_THREAD_SCHEDULER = new HandlerScheduler(new Handler(Looper.getMainLooper()));

    public static Scheduler mainThread() {
        RxAndroidPlugins.getInstance().getSchedulersHook();
        RxAndroidSchedulersHook.getMainThreadScheduler();
        return MAIN_THREAD_SCHEDULER;
    }
}
