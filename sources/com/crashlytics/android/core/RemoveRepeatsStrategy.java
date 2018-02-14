package com.crashlytics.android.core;

import java.util.HashMap;
import java.util.Map;

final class RemoveRepeatsStrategy implements StackTraceTrimmingStrategy {
    private final int maxRepetitions;

    public RemoveRepeatsStrategy() {
        this(1);
    }

    public RemoveRepeatsStrategy(int maxRepetitions) {
        this.maxRepetitions = maxRepetitions;
    }

    public final StackTraceElement[] getTrimmedStackTrace(StackTraceElement[] stacktrace) {
        int i = this.maxRepetitions;
        Map hashMap = new HashMap();
        Object obj = new StackTraceElement[stacktrace.length];
        int i2 = 0;
        int i3 = 1;
        int i4 = 0;
        while (i2 < stacktrace.length) {
            int i5;
            Object obj2 = stacktrace[i2];
            Integer num = (Integer) hashMap.get(obj2);
            if (num == null || !isRepeatingSequence(stacktrace, num.intValue(), i2)) {
                obj[i4] = stacktrace[i2];
                i4++;
                i5 = i2;
                i3 = 1;
            } else {
                i5 = i2 - num.intValue();
                if (i3 < i) {
                    System.arraycopy(stacktrace, i2, obj, i4, i5);
                    i4 += i5;
                    i3++;
                }
                i5 = (i5 - 1) + i2;
            }
            hashMap.put(obj2, Integer.valueOf(i2));
            i2 = i5 + 1;
        }
        StackTraceElement[] trimmed = new StackTraceElement[i4];
        System.arraycopy(obj, 0, trimmed, 0, trimmed.length);
        return trimmed.length < stacktrace.length ? trimmed : stacktrace;
    }

    private static boolean isRepeatingSequence(StackTraceElement[] stacktrace, int prevIndex, int currentIndex) {
        int windowSize = currentIndex - prevIndex;
        if (currentIndex + windowSize > stacktrace.length) {
            return false;
        }
        for (int i = 0; i < windowSize; i++) {
            if (!stacktrace[prevIndex + i].equals(stacktrace[currentIndex + i])) {
                return false;
            }
        }
        return true;
    }
}
