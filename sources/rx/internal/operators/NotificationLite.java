package rx.internal.operators;

import java.io.Serializable;
import rx.Observer;

public final class NotificationLite<T> {
    private static final NotificationLite INSTANCE = new NotificationLite();
    private static final Object ON_COMPLETED_SENTINEL = new Serializable() {
        public final String toString() {
            return "Notification=>Completed";
        }
    };
    private static final Object ON_NEXT_NULL_SENTINEL = new Serializable() {
        public final String toString() {
            return "Notification=>NULL";
        }
    };

    private static class OnErrorSentinel implements Serializable {
        private final Throwable e;

        public OnErrorSentinel(Throwable e) {
            this.e = e;
        }

        public String toString() {
            return "Notification=>Error:" + this.e;
        }
    }

    private NotificationLite() {
    }

    public static <T> NotificationLite<T> instance() {
        return INSTANCE;
    }

    public static Object next(T t) {
        if (t == null) {
            return ON_NEXT_NULL_SENTINEL;
        }
        return t;
    }

    public static Object completed() {
        return ON_COMPLETED_SENTINEL;
    }

    public static Object error(Throwable e) {
        return new OnErrorSentinel(e);
    }

    public static boolean accept(Observer<? super T> o, Object n) {
        if (n == ON_COMPLETED_SENTINEL) {
            o.onCompleted();
            return true;
        } else if (n == ON_NEXT_NULL_SENTINEL) {
            o.onNext(null);
            return false;
        } else if (n == null) {
            throw new IllegalArgumentException("The lite notification can not be null");
        } else if (n.getClass() == OnErrorSentinel.class) {
            o.onError(((OnErrorSentinel) n).e);
            return true;
        } else {
            o.onNext(n);
            return false;
        }
    }

    public static T getValue(Object n) {
        return n == ON_NEXT_NULL_SENTINEL ? null : n;
    }
}
