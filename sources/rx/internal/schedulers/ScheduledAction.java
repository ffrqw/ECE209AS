package rx.internal.schedulers;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import rx.Subscription;
import rx.exceptions.OnErrorNotImplementedException;
import rx.functions.Action0;
import rx.internal.util.SubscriptionList;
import rx.plugins.RxJavaPlugins;
import rx.subscriptions.CompositeSubscription;

public final class ScheduledAction extends AtomicReference<Thread> implements Runnable, Subscription {
    final Action0 action;
    final SubscriptionList cancel;

    private final class FutureCompleter implements Subscription {
        private final Future<?> f;

        private FutureCompleter(Future<?> f) {
            this.f = f;
        }

        public final void unsubscribe() {
            if (ScheduledAction.this.get() != Thread.currentThread()) {
                this.f.cancel(true);
            } else {
                this.f.cancel(false);
            }
        }

        public final boolean isUnsubscribed() {
            return this.f.isCancelled();
        }
    }

    private static final class Remover2 extends AtomicBoolean implements Subscription {
        final SubscriptionList parent;
        final ScheduledAction s;

        public Remover2(ScheduledAction s, SubscriptionList parent) {
            this.s = s;
            this.parent = parent;
        }

        public final boolean isUnsubscribed() {
            return this.s.isUnsubscribed();
        }

        public final void unsubscribe() {
            if (compareAndSet(false, true)) {
                this.parent.remove(this.s);
            }
        }
    }

    private static final class Remover extends AtomicBoolean implements Subscription {
        final CompositeSubscription parent;
        final ScheduledAction s;

        public Remover(ScheduledAction s, CompositeSubscription parent) {
            this.s = s;
            this.parent = parent;
        }

        public final boolean isUnsubscribed() {
            return this.s.isUnsubscribed();
        }

        public final void unsubscribe() {
            if (compareAndSet(false, true)) {
                this.parent.remove(this.s);
            }
        }
    }

    public ScheduledAction(Action0 action) {
        this.action = action;
        this.cancel = new SubscriptionList();
    }

    public ScheduledAction(Action0 action, CompositeSubscription parent) {
        this.action = action;
        this.cancel = new SubscriptionList(new Remover(this, parent));
    }

    public ScheduledAction(Action0 action, SubscriptionList parent) {
        this.action = action;
        this.cancel = new SubscriptionList(new Remover2(this, parent));
    }

    public final void run() {
        try {
            lazySet(Thread.currentThread());
            this.action.call();
        } catch (Throwable e) {
            IllegalStateException ie;
            if (e instanceof OnErrorNotImplementedException) {
                ie = new IllegalStateException("Exception thrown on Scheduler.Worker thread. Add `onError` handling.", e);
            } else {
                ie = new IllegalStateException("Fatal Exception thrown on Scheduler.Worker thread.", e);
            }
            RxJavaPlugins.getInstance().getErrorHandler();
            Thread thread = Thread.currentThread();
            thread.getUncaughtExceptionHandler().uncaughtException(thread, ie);
        } finally {
            unsubscribe();
        }
    }

    public final boolean isUnsubscribed() {
        return this.cancel.isUnsubscribed();
    }

    public final void unsubscribe() {
        if (!this.cancel.isUnsubscribed()) {
            this.cancel.unsubscribe();
        }
    }

    public final void add(Subscription s) {
        this.cancel.add(s);
    }

    public final void add(Future<?> f) {
        this.cancel.add(new FutureCompleter(f));
    }

    public final void addParent(CompositeSubscription parent) {
        this.cancel.add(new Remover(this, parent));
    }
}
