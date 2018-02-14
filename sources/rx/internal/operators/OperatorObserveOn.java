package rx.internal.operators;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import rx.Observable.Operator;
import rx.Producer;
import rx.Scheduler;
import rx.Scheduler.Worker;
import rx.Subscriber;
import rx.Subscription;
import rx.exceptions.MissingBackpressureException;
import rx.functions.Action0;
import rx.internal.util.RxRingBuffer;
import rx.internal.util.SynchronizedQueue;
import rx.internal.util.unsafe.SpscArrayQueue;
import rx.internal.util.unsafe.UnsafeAccess;
import rx.schedulers.ImmediateScheduler;
import rx.schedulers.TrampolineScheduler;

public final class OperatorObserveOn<T> implements Operator<T, T> {
    private final Scheduler scheduler;

    private static final class ObserveOnSubscriber<T> extends Subscriber<T> {
        static final AtomicLongFieldUpdater<ObserveOnSubscriber> COUNTER_UPDATER = AtomicLongFieldUpdater.newUpdater(ObserveOnSubscriber.class, "counter");
        static final AtomicLongFieldUpdater<ObserveOnSubscriber> REQUESTED = AtomicLongFieldUpdater.newUpdater(ObserveOnSubscriber.class, "requested");
        final Action0 action = new Action0() {
            public final void call() {
                ObserveOnSubscriber.this.pollQueue();
            }
        };
        final Subscriber<? super T> child;
        volatile long counter;
        volatile Throwable error;
        volatile boolean finished = false;
        final NotificationLite<T> on = NotificationLite.instance();
        final Queue<Object> queue;
        final Worker recursiveScheduler;
        volatile long requested = 0;
        final ScheduledUnsubscribe scheduledUnsubscribe;

        public ObserveOnSubscriber(Scheduler scheduler, Subscriber<? super T> child) {
            this.child = child;
            this.recursiveScheduler = scheduler.createWorker();
            if (UnsafeAccess.isUnsafeAvailable()) {
                this.queue = new SpscArrayQueue(RxRingBuffer.SIZE);
            } else {
                this.queue = new SynchronizedQueue(RxRingBuffer.SIZE);
            }
            this.scheduledUnsubscribe = new ScheduledUnsubscribe(this.recursiveScheduler);
        }

        public final void onStart() {
            request((long) RxRingBuffer.SIZE);
        }

        public final void onNext(T t) {
            if (!isUnsubscribed()) {
                if (this.queue.offer(NotificationLite.next(t))) {
                    schedule();
                } else {
                    onError(new MissingBackpressureException());
                }
            }
        }

        public final void onCompleted() {
            if (!isUnsubscribed() && !this.finished) {
                this.finished = true;
                schedule();
            }
        }

        public final void onError(Throwable e) {
            if (!isUnsubscribed() && !this.finished) {
                this.error = e;
                unsubscribe();
                this.finished = true;
                schedule();
            }
        }

        protected final void schedule() {
            if (COUNTER_UPDATER.getAndIncrement(this) == 0) {
                this.recursiveScheduler.schedule(this.action);
            }
        }

        final void pollQueue() {
            int emitted = 0;
            do {
                this.counter = 1;
                long produced = 0;
                long r = this.requested;
                while (!this.child.isUnsubscribed()) {
                    if (this.finished) {
                        Throwable error = this.error;
                        if (error != null) {
                            this.queue.clear();
                            this.child.onError(error);
                            return;
                        } else if (this.queue.isEmpty()) {
                            this.child.onCompleted();
                            return;
                        }
                    }
                    if (r > 0) {
                        Object o = this.queue.poll();
                        if (o != null) {
                            this.child.onNext(NotificationLite.getValue(o));
                            r--;
                            emitted++;
                            produced++;
                        }
                    }
                    if (produced > 0 && this.requested != Long.MAX_VALUE) {
                        REQUESTED.addAndGet(this, -produced);
                    }
                }
                return;
            } while (COUNTER_UPDATER.decrementAndGet(this) > 0);
            if (emitted > 0) {
                request((long) emitted);
            }
        }
    }

    static final class ScheduledUnsubscribe implements Subscription {
        static final AtomicIntegerFieldUpdater<ScheduledUnsubscribe> ONCE_UPDATER = AtomicIntegerFieldUpdater.newUpdater(ScheduledUnsubscribe.class, "once");
        volatile int once;
        volatile boolean unsubscribed = false;
        final Worker worker;

        public ScheduledUnsubscribe(Worker worker) {
            this.worker = worker;
        }

        public final boolean isUnsubscribed() {
            return this.unsubscribed;
        }

        public final void unsubscribe() {
            if (ONCE_UPDATER.getAndSet(this, 1) == 0) {
                this.worker.schedule(new Action0() {
                    public final void call() {
                        ScheduledUnsubscribe.this.worker.unsubscribe();
                        ScheduledUnsubscribe.this.unsubscribed = true;
                    }
                });
            }
        }
    }

    public final /* bridge */ /* synthetic */ Object call(Object x0) {
        Subscriber subscriber = (Subscriber) x0;
        if ((this.scheduler instanceof ImmediateScheduler) || (this.scheduler instanceof TrampolineScheduler)) {
            return subscriber;
        }
        Subscription observeOnSubscriber = new ObserveOnSubscriber(this.scheduler, subscriber);
        observeOnSubscriber.child.add(observeOnSubscriber.scheduledUnsubscribe);
        observeOnSubscriber.child.setProducer(new Producer() {
            public final void request(long n) {
                BackpressureUtils.getAndAddRequest(ObserveOnSubscriber.REQUESTED, ObserveOnSubscriber.this, n);
                ObserveOnSubscriber.this.schedule();
            }
        });
        observeOnSubscriber.child.add(observeOnSubscriber.recursiveScheduler);
        observeOnSubscriber.child.add(observeOnSubscriber);
        return observeOnSubscriber;
    }

    public OperatorObserveOn(Scheduler scheduler) {
        this.scheduler = scheduler;
    }
}
