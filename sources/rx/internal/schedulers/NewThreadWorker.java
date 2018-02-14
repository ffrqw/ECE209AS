package rx.internal.schedulers;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import rx.Scheduler.Worker;
import rx.Subscription;
import rx.exceptions.Exceptions;
import rx.functions.Action0;
import rx.internal.util.RxThreadFactory;
import rx.internal.util.SubscriptionList;
import rx.plugins.RxJavaPlugins;
import rx.plugins.RxJavaSchedulersHook;
import rx.subscriptions.CompositeSubscription;
import rx.subscriptions.Subscriptions;

public class NewThreadWorker extends Worker implements Subscription {
    private static final ConcurrentHashMap<ScheduledThreadPoolExecutor, ScheduledThreadPoolExecutor> EXECUTORS = new ConcurrentHashMap();
    private static final AtomicReference<ScheduledExecutorService> PURGE = new AtomicReference();
    private static final boolean PURGE_FORCE = Boolean.getBoolean("rx.scheduler.jdk6.purge-force");
    public static final int PURGE_FREQUENCY = Integer.getInteger("rx.scheduler.jdk6.purge-frequency-millis", 1000).intValue();
    private final ScheduledExecutorService executor;
    volatile boolean isUnsubscribed;
    private final RxJavaSchedulersHook schedulersHook;

    static void purgeExecutors() {
        try {
            Iterator<ScheduledThreadPoolExecutor> it = EXECUTORS.keySet().iterator();
            while (it.hasNext()) {
                ScheduledThreadPoolExecutor exec = (ScheduledThreadPoolExecutor) it.next();
                if (exec.isShutdown()) {
                    it.remove();
                } else {
                    exec.purge();
                }
            }
        } catch (Throwable t) {
            Exceptions.throwIfFatal(t);
            RxJavaPlugins.getInstance().getErrorHandler();
        }
    }

    private static boolean tryEnableCancelPolicy(ScheduledExecutorService exec) {
        if (!PURGE_FORCE) {
            for (Method m : exec.getClass().getMethods()) {
                if (m.getName().equals("setRemoveOnCancelPolicy") && m.getParameterTypes().length == 1 && m.getParameterTypes()[0] == Boolean.TYPE) {
                    try {
                        m.invoke(exec, new Object[]{Boolean.valueOf(true)});
                        return true;
                    } catch (Exception e) {
                        RxJavaPlugins.getInstance().getErrorHandler();
                    }
                }
            }
        }
        return false;
    }

    public NewThreadWorker(ThreadFactory threadFactory) {
        ScheduledExecutorService exec = Executors.newScheduledThreadPool(1, threadFactory);
        if (!tryEnableCancelPolicy(exec) && (exec instanceof ScheduledThreadPoolExecutor)) {
            ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = (ScheduledThreadPoolExecutor) exec;
            while (((ScheduledExecutorService) PURGE.get()) == null) {
                ScheduledExecutorService newScheduledThreadPool = Executors.newScheduledThreadPool(1, new RxThreadFactory("RxSchedulerPurge-"));
                if (PURGE.compareAndSet(null, newScheduledThreadPool)) {
                    newScheduledThreadPool.scheduleAtFixedRate(new Runnable() {
                        public final void run() {
                            NewThreadWorker.purgeExecutors();
                        }
                    }, (long) PURGE_FREQUENCY, (long) PURGE_FREQUENCY, TimeUnit.MILLISECONDS);
                    break;
                }
            }
            EXECUTORS.putIfAbsent(scheduledThreadPoolExecutor, scheduledThreadPoolExecutor);
        }
        this.schedulersHook = RxJavaPlugins.getInstance().getSchedulersHook();
        this.executor = exec;
    }

    public final Subscription schedule(Action0 action) {
        return schedule(action, 0, null);
    }

    public final Subscription schedule(Action0 action, long delayTime, TimeUnit unit) {
        if (this.isUnsubscribed) {
            return Subscriptions.unsubscribed();
        }
        return scheduleActual(action, delayTime, unit);
    }

    public final ScheduledAction scheduleActual(Action0 action, long delayTime, TimeUnit unit) {
        Future f;
        ScheduledAction run = new ScheduledAction(RxJavaSchedulersHook.onSchedule(action));
        if (delayTime <= 0) {
            f = this.executor.submit(run);
        } else {
            f = this.executor.schedule(run, delayTime, unit);
        }
        run.add(f);
        return run;
    }

    public final ScheduledAction scheduleActual(Action0 action, long delayTime, TimeUnit unit, CompositeSubscription parent) {
        Future f;
        ScheduledAction run = new ScheduledAction(RxJavaSchedulersHook.onSchedule(action), parent);
        parent.add(run);
        if (delayTime <= 0) {
            f = this.executor.submit(run);
        } else {
            f = this.executor.schedule(run, delayTime, unit);
        }
        run.add(f);
        return run;
    }

    public final ScheduledAction scheduleActual(Action0 action, long delayTime, TimeUnit unit, SubscriptionList parent) {
        Future f;
        ScheduledAction run = new ScheduledAction(RxJavaSchedulersHook.onSchedule(action), parent);
        parent.add(run);
        if (0 <= 0) {
            f = this.executor.submit(run);
        } else {
            f = this.executor.schedule(run, 0, null);
        }
        run.add(f);
        return run;
    }

    public final void unsubscribe() {
        this.isUnsubscribed = true;
        this.executor.shutdownNow();
        EXECUTORS.remove(this.executor);
    }

    public final boolean isUnsubscribed() {
        return this.isUnsubscribed;
    }
}
