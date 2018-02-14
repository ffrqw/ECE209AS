package com.squareup.picasso;

import android.view.View;
import android.view.View.OnAttachStateChangeListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.ImageView;
import java.lang.ref.WeakReference;

final class DeferredRequestCreator implements OnAttachStateChangeListener, OnPreDrawListener {
    Callback callback;
    private final RequestCreator creator;
    final WeakReference<ImageView> target;

    DeferredRequestCreator(RequestCreator creator, ImageView target, Callback callback) {
        this.creator = creator;
        this.target = new WeakReference(target);
        this.callback = callback;
        target.addOnAttachStateChangeListener(this);
        if (target.getWindowToken() != null) {
            onViewAttachedToWindow(target);
        }
    }

    public final void onViewAttachedToWindow(View view) {
        view.getViewTreeObserver().addOnPreDrawListener(this);
    }

    public final void onViewDetachedFromWindow(View view) {
        view.getViewTreeObserver().removeOnPreDrawListener(this);
    }

    public final boolean onPreDraw() {
        ImageView target = (ImageView) this.target.get();
        if (target != null) {
            ViewTreeObserver vto = target.getViewTreeObserver();
            if (vto.isAlive()) {
                int width = target.getWidth();
                int height = target.getHeight();
                if (width > 0 && height > 0 && !target.isLayoutRequested()) {
                    target.removeOnAttachStateChangeListener(this);
                    vto.removeOnPreDrawListener(this);
                    this.target.clear();
                    this.creator.unfit().resize(width, height).into(target, this.callback);
                }
            }
        }
        return true;
    }

    final void cancel() {
        this.creator.clearTag();
        this.callback = null;
        ImageView target = (ImageView) this.target.get();
        if (target != null) {
            this.target.clear();
            target.removeOnAttachStateChangeListener(this);
            ViewTreeObserver vto = target.getViewTreeObserver();
            if (vto.isAlive()) {
                vto.removeOnPreDrawListener(this);
            }
        }
    }
}
