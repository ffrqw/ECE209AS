package okio;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public final class ForwardingTimeout extends Timeout {
    private Timeout delegate;

    public ForwardingTimeout(Timeout delegate) {
        if (delegate == null) {
            throw new IllegalArgumentException("delegate == null");
        }
        this.delegate = delegate;
    }

    public final Timeout delegate() {
        return this.delegate;
    }

    public final ForwardingTimeout setDelegate(Timeout delegate) {
        if (delegate == null) {
            throw new IllegalArgumentException("delegate == null");
        }
        this.delegate = delegate;
        return this;
    }

    public final Timeout timeout(long timeout, TimeUnit unit) {
        return this.delegate.timeout(timeout, unit);
    }

    public final long timeoutNanos() {
        return this.delegate.timeoutNanos();
    }

    public final boolean hasDeadline() {
        return this.delegate.hasDeadline();
    }

    public final long deadlineNanoTime() {
        return this.delegate.deadlineNanoTime();
    }

    public final Timeout deadlineNanoTime(long deadlineNanoTime) {
        return this.delegate.deadlineNanoTime(deadlineNanoTime);
    }

    public final Timeout clearTimeout() {
        return this.delegate.clearTimeout();
    }

    public final Timeout clearDeadline() {
        return this.delegate.clearDeadline();
    }

    public final void throwIfReached() throws IOException {
        this.delegate.throwIfReached();
    }
}
