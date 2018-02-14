package com.crashlytics.android.core;

final class MiddleOutFallbackStrategy implements StackTraceTrimmingStrategy {
    private final int maximumStackSize = 1024;
    private final MiddleOutStrategy middleOutStrategy;
    private final StackTraceTrimmingStrategy[] trimmingStrategies;

    public MiddleOutFallbackStrategy(int maximumStackSize, StackTraceTrimmingStrategy... strategies) {
        this.trimmingStrategies = strategies;
        this.middleOutStrategy = new MiddleOutStrategy(1024);
    }

    public final StackTraceElement[] getTrimmedStackTrace(StackTraceElement[] stacktrace) {
        if (stacktrace.length <= this.maximumStackSize) {
            return stacktrace;
        }
        StackTraceElement[] trimmed = stacktrace;
        for (StackTraceTrimmingStrategy strategy : this.trimmingStrategies) {
            if (trimmed.length <= this.maximumStackSize) {
                break;
            }
            trimmed = strategy.getTrimmedStackTrace(stacktrace);
        }
        if (trimmed.length > this.maximumStackSize) {
            trimmed = this.middleOutStrategy.getTrimmedStackTrace(trimmed);
        }
        return trimmed;
    }
}
