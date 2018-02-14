package io.fabric.sdk.android.services.concurrency.internal;

public final class ExponentialBackoff implements Backoff {
    private final long baseTimeMillis = 1000;
    private final int power = 8;

    public ExponentialBackoff(long baseTimeMillis, int power) {
    }

    public final long getDelayMillis(int retries) {
        return (long) (((double) this.baseTimeMillis) * Math.pow((double) this.power, (double) retries));
    }
}
