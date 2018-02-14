package rx.android.plugins;

import java.util.concurrent.atomic.AtomicReference;

public final class RxAndroidPlugins {
    private static final RxAndroidPlugins INSTANCE = new RxAndroidPlugins();
    private final AtomicReference<RxAndroidSchedulersHook> schedulersHook = new AtomicReference();

    public static RxAndroidPlugins getInstance() {
        return INSTANCE;
    }

    RxAndroidPlugins() {
    }

    public final RxAndroidSchedulersHook getSchedulersHook() {
        if (this.schedulersHook.get() == null) {
            this.schedulersHook.compareAndSet(null, RxAndroidSchedulersHook.getDefaultInstance());
        }
        return (RxAndroidSchedulersHook) this.schedulersHook.get();
    }
}
