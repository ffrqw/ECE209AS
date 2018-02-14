package rx;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import rx.exceptions.Exceptions;
import rx.exceptions.OnErrorNotImplementedException;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.internal.operators.OnSubscribeFromIterable;
import rx.internal.operators.OperatorDebounceWithTime;
import rx.internal.operators.OperatorMerge;
import rx.internal.operators.OperatorObserveOn;
import rx.internal.operators.OperatorSubscribeOn;
import rx.internal.util.ScalarSynchronousObservable;
import rx.internal.util.UtilityFunctions;
import rx.observers.SafeSubscriber;
import rx.plugins.RxJavaObservableExecutionHook;
import rx.plugins.RxJavaPlugins;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;

public class Observable<T> {
    private static final RxJavaObservableExecutionHook hook = RxJavaPlugins.getInstance().getObservableExecutionHook();
    final OnSubscribe<T> onSubscribe;

    public interface OnSubscribe<T> extends Action1<Subscriber<? super T>> {
    }

    public interface Operator<R, T> extends Func1<Subscriber<? super R>, Subscriber<? super T>> {
    }

    protected Observable(OnSubscribe<T> f) {
        this.onSubscribe = f;
    }

    public static final <T> Observable<T> create(OnSubscribe<T> f) {
        return new Observable(RxJavaObservableExecutionHook.onCreate(f));
    }

    private <R> Observable<R> lift(final Operator<? extends R, ? super T> lift) {
        return new Observable(new OnSubscribe<R>() {
            public final /* bridge */ /* synthetic */ void call(Object x0) {
                OnErrorNotImplementedException onErrorNotImplementedException;
                Subscriber subscriber = (Subscriber) x0;
                Subscriber subscriber2;
                try {
                    Observable.hook;
                    subscriber2 = (Subscriber) RxJavaObservableExecutionHook.onLift(lift).call(subscriber);
                    subscriber2.onStart();
                    Observable.this.onSubscribe.call(subscriber2);
                } catch (Throwable th) {
                    if (th instanceof OnErrorNotImplementedException) {
                        onErrorNotImplementedException = (OnErrorNotImplementedException) th;
                    } else {
                        subscriber.onError(th);
                    }
                }
            }
        });
    }

    private static <T> Observable<T> from(T[] array) {
        return create(new OnSubscribeFromIterable(Arrays.asList(array)));
    }

    public static final <T> Observable<T> merge(Observable<? extends T>[] sequences) {
        Observable from = from(sequences);
        if (from.getClass() == ScalarSynchronousObservable.class) {
            return ((ScalarSynchronousObservable) from).scalarFlatMap(UtilityFunctions.identity());
        }
        return from.lift(OperatorMerge.instance(false));
    }

    public static final <T> Observable<T> merge(Observable<? extends T>[] sequences, int maxConcurrent) {
        Observable from = from(sequences);
        if (from.getClass() == ScalarSynchronousObservable.class) {
            return ((ScalarSynchronousObservable) from).scalarFlatMap(UtilityFunctions.identity());
        }
        return from.lift(OperatorMerge.instance(false, 1));
    }

    public final Observable<T> debounce(long timeout, TimeUnit unit) {
        return lift(new OperatorDebounceWithTime(300, unit, Schedulers.computation()));
    }

    public final Observable<T> observeOn(Scheduler scheduler) {
        if (this instanceof ScalarSynchronousObservable) {
            return ((ScalarSynchronousObservable) this).scalarScheduleOn(scheduler);
        }
        return lift(new OperatorObserveOn(scheduler));
    }

    public final Subscription subscribe(final Observer<? super T> observer) {
        if (observer instanceof Subscriber) {
            return subscribe((Subscriber) observer);
        }
        return subscribe(new Subscriber<T>() {
            public final void onCompleted() {
                observer.onCompleted();
            }

            public final void onError(Throwable e) {
                observer.onError(e);
            }

            public final void onNext(T t) {
                observer.onNext(t);
            }
        });
    }

    public final Subscription unsafeSubscribe(Subscriber<? super T> subscriber) {
        try {
            subscriber.onStart();
            RxJavaObservableExecutionHook.onSubscribeStart$15005f2c(this.onSubscribe).call(subscriber);
            return RxJavaObservableExecutionHook.onSubscribeReturn(subscriber);
        } catch (Throwable e) {
            Exceptions.throwIfFatal(e);
            try {
                subscriber.onError(RxJavaObservableExecutionHook.onSubscribeError(e));
                return Subscriptions.unsubscribed();
            } catch (OnErrorNotImplementedException e2) {
                throw e2;
            } catch (Throwable e22) {
                RuntimeException runtimeException = new RuntimeException("Error occurred attempting to subscribe [" + e.getMessage() + "] and then again while trying to pass to onError.", e22);
            }
        }
    }

    public final Subscription subscribe(Subscriber<? super T> subscriber) {
        if (subscriber == null) {
            throw new IllegalArgumentException("observer can not be null");
        } else if (this.onSubscribe == null) {
            throw new IllegalStateException("onSubscribe function can not be null.");
        } else {
            subscriber.onStart();
            if (!(subscriber instanceof SafeSubscriber)) {
                subscriber = new SafeSubscriber(subscriber);
            }
            try {
                RxJavaObservableExecutionHook.onSubscribeStart$15005f2c(this.onSubscribe).call(subscriber);
                return RxJavaObservableExecutionHook.onSubscribeReturn(subscriber);
            } catch (Throwable e) {
                Exceptions.throwIfFatal(e);
                try {
                    subscriber.onError(RxJavaObservableExecutionHook.onSubscribeError(e));
                    return Subscriptions.unsubscribed();
                } catch (OnErrorNotImplementedException e2) {
                    throw e2;
                } catch (Throwable e22) {
                    RuntimeException runtimeException = new RuntimeException("Error occurred attempting to subscribe [" + e.getMessage() + "] and then again while trying to pass to onError.", e22);
                }
            }
        }
    }

    public final Observable<T> subscribeOn(Scheduler scheduler) {
        if (this instanceof ScalarSynchronousObservable) {
            return ((ScalarSynchronousObservable) this).scalarScheduleOn(scheduler);
        }
        return ScalarSynchronousObservable.create(this).lift(new OperatorSubscribeOn(scheduler));
    }
}
