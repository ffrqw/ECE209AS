package rx.internal.util;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Scheduler;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.internal.schedulers.EventLoopsScheduler;

public final class ScalarSynchronousObservable<T> extends Observable<T> {
    private final T t;

    /* renamed from: rx.internal.util.ScalarSynchronousObservable$1 */
    class AnonymousClass1 implements OnSubscribe<T> {
        final /* synthetic */ Object val$t;

        AnonymousClass1(Object obj) {
            this.val$t = obj;
        }

        public final /* bridge */ /* synthetic */ void call(Object x0) {
            Subscriber subscriber = (Subscriber) x0;
            subscriber.onNext(this.val$t);
            subscriber.onCompleted();
        }
    }

    static final class DirectScheduledEmission<T> implements OnSubscribe<T> {
        private final EventLoopsScheduler es;
        private final T value;

        public final /* bridge */ /* synthetic */ void call(Object x0) {
            Subscriber subscriber = (Subscriber) x0;
            subscriber.add(this.es.scheduleDirect(new ScalarSynchronousAction(subscriber, this.value)));
        }

        DirectScheduledEmission(EventLoopsScheduler es, T value) {
            this.es = es;
            this.value = value;
        }
    }

    static final class NormalScheduledEmission<T> implements OnSubscribe<T> {
        private final Scheduler scheduler;
        private final T value;

        public final /* bridge */ /* synthetic */ void call(Object x0) {
            Subscriber subscriber = (Subscriber) x0;
            Object createWorker = this.scheduler.createWorker();
            subscriber.add(createWorker);
            createWorker.schedule(new ScalarSynchronousAction(subscriber, this.value));
        }

        NormalScheduledEmission(Scheduler scheduler, T value) {
            this.scheduler = scheduler;
            this.value = value;
        }
    }

    static final class ScalarSynchronousAction<T> implements Action0 {
        private final Subscriber<? super T> subscriber;
        private final T value;

        private ScalarSynchronousAction(Subscriber<? super T> subscriber, T value) {
            this.subscriber = subscriber;
            this.value = value;
        }

        public final void call() {
            try {
                this.subscriber.onNext(this.value);
                this.subscriber.onCompleted();
            } catch (Throwable t) {
                this.subscriber.onError(t);
            }
        }
    }

    public static final <T> ScalarSynchronousObservable<T> create(T t) {
        return new ScalarSynchronousObservable(t);
    }

    private ScalarSynchronousObservable(T t) {
        super(new AnonymousClass1(t));
        this.t = t;
    }

    public final T get() {
        return this.t;
    }

    public final Observable<T> scalarScheduleOn(Scheduler scheduler) {
        if (scheduler instanceof EventLoopsScheduler) {
            return Observable.create(new DirectScheduledEmission((EventLoopsScheduler) scheduler, this.t));
        }
        return Observable.create(new NormalScheduledEmission(scheduler, this.t));
    }

    public final <R> Observable<R> scalarFlatMap(final Func1<? super T, ? extends Observable<? extends R>> func) {
        return Observable.create(new OnSubscribe<R>() {
            public final /* bridge */ /* synthetic */ void call(Object x0) {
                final Subscriber subscriber = (Subscriber) x0;
                Observable observable = (Observable) func.call(ScalarSynchronousObservable.this.t);
                if (observable.getClass() == ScalarSynchronousObservable.class) {
                    subscriber.onNext(((ScalarSynchronousObservable) observable).t);
                    subscriber.onCompleted();
                    return;
                }
                observable.unsafeSubscribe(new Subscriber<R>(subscriber) {
                    public final void onNext(R v) {
                        subscriber.onNext(v);
                    }

                    public final void onError(Throwable e) {
                        subscriber.onError(e);
                    }

                    public final void onCompleted() {
                        subscriber.onCompleted();
                    }
                });
            }
        });
    }
}
