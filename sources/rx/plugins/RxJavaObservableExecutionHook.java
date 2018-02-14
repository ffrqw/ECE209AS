package rx.plugins;

import rx.Observable.OnSubscribe;
import rx.Observable.Operator;
import rx.Subscription;

public abstract class RxJavaObservableExecutionHook {
    public static <T> OnSubscribe<T> onCreate(OnSubscribe<T> f) {
        return f;
    }

    public static <T> OnSubscribe<T> onSubscribeStart$15005f2c(OnSubscribe<T> onSubscribe) {
        return onSubscribe;
    }

    public static <T> Subscription onSubscribeReturn(Subscription subscription) {
        return subscription;
    }

    public static <T> Throwable onSubscribeError(Throwable e) {
        return e;
    }

    public static <T, R> Operator<? extends R, ? super T> onLift(Operator<? extends R, ? super T> lift) {
        return lift;
    }
}
