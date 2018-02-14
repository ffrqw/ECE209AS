package com.squareup.okhttp;

import java.util.ArrayDeque;
import java.util.Deque;

public final class Dispatcher {
    private final Deque<Call> executedCalls = new ArrayDeque();
    private int maxRequests = 64;
    private int maxRequestsPerHost = 5;
    private final Deque<Object> readyCalls = new ArrayDeque();
    private final Deque<Object> runningCalls = new ArrayDeque();

    final synchronized void executed(Call call) {
        this.executedCalls.add(call);
    }

    final synchronized void finished(Call call) {
        if (!this.executedCalls.remove(call)) {
            throw new AssertionError("Call wasn't in-flight!");
        }
    }
}
