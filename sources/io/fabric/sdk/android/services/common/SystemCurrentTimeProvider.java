package io.fabric.sdk.android.services.common;

public final class SystemCurrentTimeProvider implements CurrentTimeProvider {
    public final long getCurrentTimeMillis() {
        return System.currentTimeMillis();
    }
}
