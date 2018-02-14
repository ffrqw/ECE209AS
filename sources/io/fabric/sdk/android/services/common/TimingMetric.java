package io.fabric.sdk.android.services.common;

import android.os.SystemClock;
import android.util.Log;

public final class TimingMetric {
    private final boolean disabled;
    private long duration;
    private final String eventName;
    private long start;
    private final String tag;

    public TimingMetric(String eventName, String tag) {
        this.eventName = eventName;
        this.tag = tag;
        this.disabled = !Log.isLoggable(tag, 2);
    }

    public final synchronized void startMeasuring() {
        if (!this.disabled) {
            this.start = SystemClock.elapsedRealtime();
            this.duration = 0;
        }
    }

    public final synchronized void stopMeasuring() {
        if (!this.disabled) {
            if (this.duration == 0) {
                this.duration = SystemClock.elapsedRealtime() - this.start;
                Log.v(this.tag, this.eventName + ": " + this.duration + "ms");
            }
        }
    }
}
