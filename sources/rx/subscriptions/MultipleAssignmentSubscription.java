package rx.subscriptions;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import rx.Subscription;

public final class MultipleAssignmentSubscription implements Subscription {
    static final AtomicReferenceFieldUpdater<MultipleAssignmentSubscription, State> STATE_UPDATER = AtomicReferenceFieldUpdater.newUpdater(MultipleAssignmentSubscription.class, State.class, "state");
    volatile State state = new State(false, Subscriptions.empty());

    private static final class State {
        final boolean isUnsubscribed;
        final Subscription subscription;

        State(boolean u, Subscription s) {
            this.isUnsubscribed = u;
            this.subscription = s;
        }
    }

    public final boolean isUnsubscribed() {
        return this.state.isUnsubscribed;
    }

    public final void unsubscribe() {
        State oldState;
        do {
            oldState = this.state;
            if (!oldState.isUnsubscribed) {
            } else {
                return;
            }
        } while (!STATE_UPDATER.compareAndSet(this, oldState, new State(true, oldState.subscription)));
        oldState.subscription.unsubscribe();
    }

    public final void set(Subscription s) {
        if (s == null) {
            throw new IllegalArgumentException("Subscription can not be null");
        }
        State oldState;
        do {
            oldState = this.state;
            if (oldState.isUnsubscribed) {
                s.unsubscribe();
                return;
            }
        } while (!STATE_UPDATER.compareAndSet(this, oldState, new State(oldState.isUnsubscribed, s)));
    }
}
