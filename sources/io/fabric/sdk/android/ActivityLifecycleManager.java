package io.fabric.sdk.android;

import android.app.Activity;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Bundle;
import java.util.HashSet;
import java.util.Set;

public final class ActivityLifecycleManager {
    private final Application application;
    private ActivityLifecycleCallbacksWrapper callbacksWrapper;

    public static abstract class Callbacks {
        public void onActivityCreated$9bb446d(Activity activity) {
        }

        public void onActivityStarted(Activity activity) {
        }

        public void onActivityResumed(Activity activity) {
        }

        public void onActivityPaused(Activity activity) {
        }

        public void onActivityStopped(Activity activity) {
        }
    }

    private static class ActivityLifecycleCallbacksWrapper {
        private final Application application;
        private final Set<ActivityLifecycleCallbacks> registeredCallbacks = new HashSet();

        ActivityLifecycleCallbacksWrapper(Application application) {
            this.application = application;
        }

        static /* synthetic */ boolean access$000(ActivityLifecycleCallbacksWrapper x0, final Callbacks x1) {
            if (x0.application == null) {
                return false;
            }
            ActivityLifecycleCallbacks anonymousClass1 = new ActivityLifecycleCallbacks() {
                public final void onActivityCreated(Activity activity, Bundle bundle) {
                    x1.onActivityCreated$9bb446d(activity);
                }

                public final void onActivityStarted(Activity activity) {
                    x1.onActivityStarted(activity);
                }

                public final void onActivityResumed(Activity activity) {
                    x1.onActivityResumed(activity);
                }

                public final void onActivityPaused(Activity activity) {
                    x1.onActivityPaused(activity);
                }

                public final void onActivityStopped(Activity activity) {
                    x1.onActivityStopped(activity);
                }

                public final void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
                }

                public final void onActivityDestroyed(Activity activity) {
                }
            };
            x0.application.registerActivityLifecycleCallbacks(anonymousClass1);
            x0.registeredCallbacks.add(anonymousClass1);
            return true;
        }

        static /* synthetic */ void access$100(ActivityLifecycleCallbacksWrapper x0) {
            for (ActivityLifecycleCallbacks unregisterActivityLifecycleCallbacks : x0.registeredCallbacks) {
                x0.application.unregisterActivityLifecycleCallbacks(unregisterActivityLifecycleCallbacks);
            }
        }
    }

    public ActivityLifecycleManager(Context context) {
        this.application = (Application) context.getApplicationContext();
        if (VERSION.SDK_INT >= 14) {
            this.callbacksWrapper = new ActivityLifecycleCallbacksWrapper(this.application);
        }
    }

    public final boolean registerCallbacks(Callbacks callbacks) {
        return this.callbacksWrapper != null && ActivityLifecycleCallbacksWrapper.access$000(this.callbacksWrapper, callbacks);
    }

    public final void resetCallbacks() {
        if (this.callbacksWrapper != null) {
            ActivityLifecycleCallbacksWrapper.access$100(this.callbacksWrapper);
        }
    }
}
