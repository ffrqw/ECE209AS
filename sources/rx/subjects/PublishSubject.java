package rx.subjects;

import java.util.ArrayList;
import java.util.List;
import rx.Observable.OnSubscribe;
import rx.exceptions.Exceptions;
import rx.functions.Action1;
import rx.internal.operators.NotificationLite;

public final class PublishSubject<T> extends Subject<T, T> {
    private final NotificationLite<T> nl = NotificationLite.instance();
    final SubjectSubscriptionManager<T> state;

    public static <T> PublishSubject<T> create() {
        final SubjectSubscriptionManager<T> state = new SubjectSubscriptionManager();
        state.onTerminated = new Action1<SubjectObserver<T>>() {
            public final /* bridge */ /* synthetic */ void call(Object x0) {
                ((SubjectObserver) x0).emitFirst(state.latest, state.nl);
            }
        };
        return new PublishSubject(state, state);
    }

    private PublishSubject(OnSubscribe<T> onSubscribe, SubjectSubscriptionManager<T> state) {
        super(onSubscribe);
        this.state = state;
    }

    public final void onCompleted() {
        if (this.state.active) {
            Object n = NotificationLite.completed();
            for (SubjectObserver<T> bo : this.state.terminate(n)) {
                bo.emitNext(n, this.state.nl);
            }
        }
    }

    public final void onError(Throwable e) {
        if (this.state.active) {
            Object n = NotificationLite.error(e);
            List<Throwable> errors = null;
            for (SubjectObserver<T> bo : this.state.terminate(n)) {
                try {
                    bo.emitNext(n, this.state.nl);
                } catch (Throwable e2) {
                    if (errors == null) {
                        errors = new ArrayList();
                    }
                    errors.add(e2);
                }
            }
            Exceptions.throwIfAny(errors);
        }
    }

    public final void onNext(T v) {
        for (SubjectObserver<T> bo : this.state.state.observers) {
            bo.onNext(v);
        }
    }
}
