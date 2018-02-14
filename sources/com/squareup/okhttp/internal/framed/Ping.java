package com.squareup.okhttp.internal.framed;

import java.util.concurrent.CountDownLatch;

public final class Ping {
    private final CountDownLatch latch;
    private long received;
    private long sent;

    final void send() {
        if (this.sent != -1) {
            throw new IllegalStateException();
        }
        this.sent = System.nanoTime();
    }

    final void receive() {
        if (this.received != -1 || this.sent == -1) {
            throw new IllegalStateException();
        }
        this.received = System.nanoTime();
        this.latch.countDown();
    }
}
