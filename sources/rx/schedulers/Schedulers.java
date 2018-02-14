package rx.schedulers;

import rx.Scheduler;
import rx.internal.schedulers.EventLoopsScheduler;
import rx.plugins.RxJavaPlugins;
import rx.plugins.RxJavaSchedulersHook;

public final class Schedulers {
    private static final Schedulers INSTANCE = new Schedulers();
    private final Scheduler computationScheduler = new EventLoopsScheduler();
    private final Scheduler ioScheduler;
    private final Scheduler newThreadScheduler;

    private Schedulers() {
        RxJavaPlugins.getInstance().getSchedulersHook();
        RxJavaSchedulersHook.getComputationScheduler();
        RxJavaPlugins.getInstance().getSchedulersHook();
        RxJavaSchedulersHook.getIOScheduler();
        this.ioScheduler = new CachedThreadScheduler();
        RxJavaPlugins.getInstance().getSchedulersHook();
        RxJavaSchedulersHook.getNewThreadScheduler();
        this.newThreadScheduler = NewThreadScheduler.instance();
    }

    public static Scheduler computation() {
        return INSTANCE.computationScheduler;
    }

    public static Scheduler io() {
        return INSTANCE.ioScheduler;
    }
}
