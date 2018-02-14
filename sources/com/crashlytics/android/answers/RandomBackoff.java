package com.crashlytics.android.answers;

import io.fabric.sdk.android.services.concurrency.internal.Backoff;
import java.util.Random;

final class RandomBackoff implements Backoff {
    final Backoff backoff;
    final double jitterPercent;
    final Random random;

    public RandomBackoff(Backoff backoff, double jitterPercent) {
        this(backoff, 0.1d, new Random());
    }

    private RandomBackoff(Backoff backoff, double jitterPercent, Random random) {
        if (jitterPercent < 0.0d || jitterPercent > 1.0d) {
            throw new IllegalArgumentException("jitterPercent must be between 0.0 and 1.0");
        } else if (backoff == null) {
            throw new NullPointerException("backoff must not be null");
        } else {
            this.backoff = backoff;
            this.jitterPercent = jitterPercent;
            this.random = random;
        }
    }

    public final long getDelayMillis(int retries) {
        double d = 1.0d - this.jitterPercent;
        return (long) ((d + (((this.jitterPercent + 1.0d) - d) * this.random.nextDouble())) * ((double) this.backoff.getDelayMillis(retries)));
    }
}
