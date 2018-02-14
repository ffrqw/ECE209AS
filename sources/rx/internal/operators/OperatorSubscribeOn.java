package rx.internal.operators;

import rx.Observable;
import rx.Observable.Operator;
import rx.Producer;
import rx.Scheduler;
import rx.Subscriber;
import rx.functions.Action0;

public final class OperatorSubscribeOn<T> implements Operator<T, Observable<T>> {
    private final Scheduler scheduler;

    public final /* bridge */ /* synthetic */ Object call(Object x0) {
        final Subscriber subscriber = (Subscriber) x0;
        final Object createWorker = this.scheduler.createWorker();
        subscriber.add(createWorker);
        return new Subscriber<Observable<T>>(subscriber) {
            public final /* bridge */ /* synthetic */ void onNext(Object x0) {
                final Observable observable = (Observable) x0;
                createWorker.schedule(new Action0() {
                    public final void call() {
                        final Thread t = Thread.currentThread();
                        observable.unsafeSubscribe(new Subscriber<T>(subscriber) {
                            public final void onCompleted() {
                                subscriber.onCompleted();
                            }

                            public final void onError(Throwable e) {
                                subscriber.onError(e);
                            }

                            public final void onNext(T t) {
                                subscriber.onNext(t);
                            }

                            public final void setProducer(final Producer producer) {
                                subscriber.setProducer(new Producer() {
                                    public final void request(final long n) {
                                        if (Thread.currentThread() == t) {
                                            producer.request(n);
                                        } else {
                                            createWorker.schedule(new Action0() {
                                                public final void call() {
                                                    producer.request(n);
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        });
                    }
                });
            }

            public final void onCompleted() {
            }

            public final void onError(Throwable e) {
                subscriber.onError(e);
            }
        };
    }

    public OperatorSubscribeOn(Scheduler scheduler) {
        this.scheduler = scheduler;
    }
}
