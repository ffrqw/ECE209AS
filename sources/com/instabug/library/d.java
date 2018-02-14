package com.instabug.library;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application.ActivityLifecycleCallbacks;
import android.os.Bundle;
import com.instabug.library.util.InstabugSDKLogger;

@TargetApi(14)
public final class d implements ActivityLifecycleCallbacks {
    public final void onActivityCreated(Activity activity, Bundle bundle) {
        InstabugSDKLogger.d(this, activity.getClass().getSimpleName() + " created");
    }

    public final void onActivityStarted(Activity activity) {
        InstabugSDKLogger.d(this, activity.getClass().getSimpleName() + " started");
        Instabug.notifyDelegateActivityStarted(activity);
    }

    public final void onActivityResumed(Activity activity) {
        InstabugSDKLogger.d(this, activity.getClass().getSimpleName() + " resumed");
        if (!(activity instanceof _InstabugFeedbackActivity)) {
            Instabug.notifyDelegateActivityResumed(activity);
        }
    }

    public final void onActivityPaused(Activity activity) {
        InstabugSDKLogger.d(this, activity.getClass().getSimpleName() + " paused");
        if (!(activity instanceof _InstabugFeedbackActivity)) {
            Instabug.notifyDelegateActivityPaused(activity);
        }
    }

    public final void onActivityStopped(Activity activity) {
        InstabugSDKLogger.d(this, activity.getClass().getSimpleName() + " stopped");
        Instabug.notifyDelegateActivityStopped(activity);
    }

    public final void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
        InstabugSDKLogger.d(this, activity.getClass().getSimpleName() + " SaveInstanceState");
    }

    public final void onActivityDestroyed(Activity activity) {
        InstabugSDKLogger.d(this, activity.getClass().getSimpleName() + " destroyed");
        if (!(activity instanceof _InstabugFeedbackActivity)) {
            Instabug.notifyDelegateActivityDestroyed(activity);
        }
    }
}
