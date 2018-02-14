package com.crashlytics.android.answers;

import io.fabric.sdk.android.services.concurrency.internal.RetryState;

final class RetryManager {
    long lastRetry;
    private RetryState retryState;

    public RetryManager(RetryState retryState) {
        this.retryState = retryState;
    }

    public final boolean canRetry(long timeNanos) {
        return timeNanos - this.lastRetry >= 1000000 * this.retryState.getRetryDelay();
    }

    public final void recordRetry(long timeNanos) {
        this.lastRetry = timeNanos;
        this.retryState = this.retryState.nextRetryState();
    }

    public final void reset() {
        this.lastRetry = 0;
        this.retryState = this.retryState.initialRetryState();
    }
}
