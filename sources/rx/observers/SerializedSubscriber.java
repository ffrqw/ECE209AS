package rx.observers;

import rx.Observer;
import rx.Subscriber;

public final class SerializedSubscriber<T> extends Subscriber<T> {
    private final Observer<T> s;

    public SerializedSubscriber(Subscriber<? super T> s) {
        this(s, true);
    }

    private SerializedSubscriber(Subscriber<? super T> s, boolean shareSubscriptions) {
        super(s, true);
        this.s = new SerializedObserver(s);
    }

    public final void onCompleted() {
        this.s.onCompleted();
    }

    public final void onError(Throwable e) {
        this.s.onError(e);
    }

    public final void onNext(T t) {
        this.s.onNext(t);
    }
}
