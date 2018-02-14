package com.crashlytics.android.answers;

import android.app.Activity;
import io.fabric.sdk.android.ActivityLifecycleManager.Callbacks;
import java.util.concurrent.ScheduledFuture;

final class AnswersLifecycleCallbacks extends Callbacks {
    private final SessionAnalyticsManager analyticsManager;
    private final BackgroundManager backgroundManager;

    public AnswersLifecycleCallbacks(SessionAnalyticsManager analyticsManager, BackgroundManager backgroundManager) {
        this.analyticsManager = analyticsManager;
        this.backgroundManager = backgroundManager;
    }

    public final void onActivityCreated$9bb446d(Activity activity) {
    }

    public final void onActivityStarted(Activity activity) {
        this.analyticsManager.onLifecycle(activity, Type.START);
    }

    public final void onActivityResumed(Activity activity) {
        this.analyticsManager.onLifecycle(activity, Type.RESUME);
        BackgroundManager backgroundManager = this.backgroundManager;
        backgroundManager.inBackground = false;
        ScheduledFuture scheduledFuture = (ScheduledFuture) backgroundManager.backgroundFutureRef.getAndSet(null);
        if (scheduledFuture != null) {
            scheduledFuture.cancel(false);
        }
    }

    public final void onActivityPaused(Activity activity) {
        this.analyticsManager.onLifecycle(activity, Type.PAUSE);
        this.backgroundManager.onActivityPaused();
    }

    public final void onActivityStopped(Activity activity) {
        this.analyticsManager.onLifecycle(activity, Type.STOP);
    }
}
