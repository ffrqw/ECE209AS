package rx.subscriptions;

import rx.Subscription;
import rx.functions.Action0;

public final class Subscriptions {
    private static final Unsubscribed UNSUBSCRIBED = new Unsubscribed();

    private static final class Unsubscribed implements Subscription {
        private Unsubscribed() {
        }

        public final void unsubscribe() {
        }

        public final boolean isUnsubscribed() {
            return true;
        }
    }

    public static Subscription empty() {
        return BooleanSubscription.create();
    }

    public static Subscription unsubscribed() {
        return UNSUBSCRIBED;
    }

    public static Subscription create(Action0 unsubscribe) {
        return BooleanSubscription.create(unsubscribe);
    }
}
