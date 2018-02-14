package com.crashlytics.android.answers;

import io.fabric.sdk.android.Fabric;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

final class BackgroundManager {
    final AtomicReference<ScheduledFuture<?>> backgroundFutureRef = new AtomicReference();
    private final ScheduledExecutorService executorService;
    private volatile boolean flushOnBackground = true;
    boolean inBackground = true;
    private final List<Listener> listeners = new ArrayList();

    public interface Listener {
        void onBackground();
    }

    public BackgroundManager(ScheduledExecutorService executorService) {
        this.executorService = executorService;
    }

    public final void setFlushOnBackground(boolean flushOnBackground) {
        this.flushOnBackground = flushOnBackground;
    }

    public final void registerListener(Listener listener) {
        this.listeners.add(listener);
    }

    public final void onActivityPaused() {
        if (this.flushOnBackground && !this.inBackground) {
            this.inBackground = true;
            try {
                this.backgroundFutureRef.compareAndSet(null, this.executorService.schedule(new Runnable() {
                    public final void run() {
                        BackgroundManager.this.backgroundFutureRef.set(null);
                        BackgroundManager.access$000(BackgroundManager.this);
                    }
                }, 5000, TimeUnit.MILLISECONDS));
            } catch (RejectedExecutionException e) {
                Fabric.getLogger().d("Answers", "Failed to schedule background detector", e);
            }
        }
    }

    static /* synthetic */ void access$000(BackgroundManager x0) {
        for (Listener onBackground : x0.listeners) {
            onBackground.onBackground();
        }
    }
}
