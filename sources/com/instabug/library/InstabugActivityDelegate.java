package com.instabug.library;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.FragmentManager.BackStackEntry;
import android.app.FragmentManager.OnBackStackChangedListener;
import android.content.Context;
import android.os.Build.VERSION;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GestureDetectorCompat;
import android.view.MotionEvent;
import com.instabug.library.Feature.State;
import com.instabug.library.interaction.InstabugSwipeDelegate;
import com.instabug.library.interaction.InstabugSwipeDelegate.GestureConfig;
import com.instabug.library.interaction.InstabugSwipeDelegate.GestureListener;
import com.instabug.library.util.TouchEventDispatcher;

public class InstabugActivityDelegate implements com.instabug.library.interaction.InstabugSwipeDelegate.a {
    private final Activity mActivity;
    private GestureDetectorCompat mDetector;
    private InstabugSwipeDelegate mSwipeDelegate;
    private TouchEventDispatcher mTouchEventDispatcher = new TouchEventDispatcher(Instabug.iG().b());

    @TargetApi(11)
    private class a implements OnBackStackChangedListener {
        final /* synthetic */ InstabugActivityDelegate a;

        private a(InstabugActivityDelegate instabugActivityDelegate) {
            this.a = instabugActivityDelegate;
        }

        public final void onBackStackChanged() {
            int backStackEntryCount = this.a.mActivity.getFragmentManager().getBackStackEntryCount();
            if (backStackEntryCount > 0) {
                BackStackEntry backStackEntryAt = this.a.mActivity.getFragmentManager().getBackStackEntryAt(backStackEntryCount - 1);
                if (VERSION.SDK_INT >= 14) {
                    Instabug.iG().b().a(backStackEntryAt.getName(), 2567);
                    return;
                }
                return;
            }
            Instabug.iG().b().a("", 2577);
        }
    }

    private class b implements FragmentManager.OnBackStackChangedListener {
        final /* synthetic */ InstabugActivityDelegate a;

        private b(InstabugActivityDelegate instabugActivityDelegate) {
            this.a = instabugActivityDelegate;
        }

        public final void onBackStackChanged() {
            int backStackEntryCount = ((FragmentActivity) this.a.mActivity).getSupportFragmentManager().getBackStackEntryCount();
            if (backStackEntryCount > 0) {
                Instabug.iG().b().a(((FragmentActivity) this.a.mActivity).getSupportFragmentManager().getBackStackEntryAt(backStackEntryCount - 1).getName(), 2567);
                return;
            }
            Instabug.iG().b().a("", 2577);
        }
    }

    public InstabugActivityDelegate(Activity activity) {
        this.mActivity = activity;
        if (VERSION.SDK_INT >= 11) {
            this.mActivity.getFragmentManager().addOnBackStackChangedListener(new a());
        }
        if (this.mActivity instanceof FragmentActivity) {
            ((FragmentActivity) this.mActivity).getSupportFragmentManager().addOnBackStackChangedListener(new b());
        }
    }

    public void onStart() {
        if (VERSION.SDK_INT < 14) {
            Instabug.notifyActivityStarted(this.mActivity);
        }
    }

    public void onResume() {
        Instabug.iG().a.delegate;
        if (q.a().e() == IBGInvocationEvent.IBGInvocationEventTwoFingersSwipeLeft) {
            this.mSwipeDelegate = new InstabugSwipeDelegate(this, GestureConfig.TwoSwipeLeft);
            Context context = this.mActivity;
            InstabugSwipeDelegate instabugSwipeDelegate = this.mSwipeDelegate;
            instabugSwipeDelegate.getClass();
            this.mDetector = new GestureDetectorCompat(context, new GestureListener());
        } else {
            this.mSwipeDelegate = null;
            this.mDetector = null;
        }
        if (VERSION.SDK_INT < 14) {
            Instabug.notifyActivityResumed(this.mActivity);
        }
    }

    public void onPause() {
        this.mSwipeDelegate = null;
        this.mDetector = null;
        if (VERSION.SDK_INT < 14) {
            Instabug.notifyActivityPaused(this.mActivity);
        }
    }

    public void onStop() {
        if (VERSION.SDK_INT < 14) {
            Instabug.notifyActivityStopped(this.mActivity);
        }
    }

    public void onDestroy() {
        Instabug.notifyActivityDestroyed(this.mActivity);
    }

    public void dispatchTouchEvent(MotionEvent motionEvent) {
        if (this.mSwipeDelegate != null) {
            this.mSwipeDelegate.onTouchEvent(motionEvent);
        }
        if (InstabugFeaturesManager.getInstance().getFeatureState(Feature.TRACK_USER_STEPS) == State.ENABLED) {
            this.mTouchEventDispatcher.dispatchTouchEvent(this.mActivity, motionEvent);
        }
    }

    public void onInstabugGestureEvent(MotionEvent motionEvent) {
        this.mDetector.onTouchEvent(motionEvent);
    }
}
