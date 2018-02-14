package android.support.v4.app;

import android.view.View;
import android.view.View.OnAttachStateChangeListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;

final class OneShotPreDrawListener implements OnAttachStateChangeListener, OnPreDrawListener {
    private final Runnable mRunnable;
    private final View mView;
    private ViewTreeObserver mViewTreeObserver;

    private OneShotPreDrawListener(View view, Runnable runnable) {
        this.mView = view;
        this.mViewTreeObserver = view.getViewTreeObserver();
        this.mRunnable = runnable;
    }

    public static OneShotPreDrawListener add(View view, Runnable runnable) {
        OneShotPreDrawListener listener = new OneShotPreDrawListener(view, runnable);
        view.getViewTreeObserver().addOnPreDrawListener(listener);
        view.addOnAttachStateChangeListener(listener);
        return listener;
    }

    public final boolean onPreDraw() {
        removeListener();
        this.mRunnable.run();
        return true;
    }

    private void removeListener() {
        if (this.mViewTreeObserver.isAlive()) {
            this.mViewTreeObserver.removeOnPreDrawListener(this);
        } else {
            this.mView.getViewTreeObserver().removeOnPreDrawListener(this);
        }
        this.mView.removeOnAttachStateChangeListener(this);
    }

    public final void onViewAttachedToWindow(View v) {
        this.mViewTreeObserver = v.getViewTreeObserver();
    }

    public final void onViewDetachedFromWindow(View v) {
        removeListener();
    }
}
