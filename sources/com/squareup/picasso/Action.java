package com.squareup.picasso;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import com.squareup.picasso.Picasso.LoadedFrom;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

abstract class Action<T> {
    boolean cancelled;
    final Drawable errorDrawable;
    final int errorResId;
    final String key;
    final int memoryPolicy;
    final int networkPolicy;
    final boolean noFade;
    final Picasso picasso;
    final Request request;
    final Object tag;
    final WeakReference<T> target;
    boolean willReplay;

    static class RequestWeakReference<M> extends WeakReference<M> {
        final Action action;

        public RequestWeakReference(Action action, M referent, ReferenceQueue<? super M> q) {
            super(referent, q);
            this.action = action;
        }
    }

    abstract void complete(Bitmap bitmap, LoadedFrom loadedFrom);

    abstract void error(Exception exception);

    Action(Picasso picasso, T target, Request request, int memoryPolicy, int networkPolicy, int errorResId, Drawable errorDrawable, String key, Object tag, boolean noFade) {
        this.picasso = picasso;
        this.request = request;
        this.target = target == null ? null : new RequestWeakReference(this, target, picasso.referenceQueue);
        this.memoryPolicy = memoryPolicy;
        this.networkPolicy = networkPolicy;
        this.noFade = noFade;
        this.errorResId = errorResId;
        this.errorDrawable = errorDrawable;
        this.key = key;
        if (tag == null) {
            Action tag2 = this;
        }
        this.tag = tag;
    }

    void cancel() {
        this.cancelled = true;
    }

    T getTarget() {
        return this.target == null ? null : this.target.get();
    }
}
