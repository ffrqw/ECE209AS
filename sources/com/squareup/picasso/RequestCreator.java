package com.squareup.picasso;

import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ImageView;
import com.squareup.picasso.Picasso.LoadedFrom;
import com.squareup.picasso.Picasso.Priority;
import com.squareup.picasso.Request.Builder;
import java.util.concurrent.atomic.AtomicInteger;

public final class RequestCreator {
    private static final AtomicInteger nextId = new AtomicInteger();
    private final Builder data;
    private boolean deferred;
    private final Picasso picasso;
    private boolean setPlaceholder;
    private Object tag;

    RequestCreator(Picasso picasso, Uri uri, int resourceId) {
        this.setPlaceholder = true;
        boolean z = picasso.shutdown;
        this.picasso = picasso;
        this.data = new Builder(uri, 0, picasso.defaultBitmapConfig);
    }

    RequestCreator() {
        this.setPlaceholder = true;
        this.picasso = null;
        this.data = new Builder(null, 0, null);
    }

    public final RequestCreator fit() {
        this.deferred = true;
        return this;
    }

    final RequestCreator unfit() {
        this.deferred = false;
        return this;
    }

    final RequestCreator clearTag() {
        this.tag = null;
        return this;
    }

    public final RequestCreator resize(int targetWidth, int targetHeight) {
        this.data.resize(targetWidth, targetHeight);
        return this;
    }

    public final RequestCreator centerInside() {
        this.data.centerInside();
        return this;
    }

    public final void fetch() {
        long nanoTime = System.nanoTime();
        if (this.deferred) {
            throw new IllegalStateException("Fit cannot be used with fetch.");
        } else if (this.data.hasImage()) {
            if (!this.data.hasPriority()) {
                this.data.priority$8880afd(Priority.LOW$159b5429);
            }
            Request createRequest = createRequest(nanoTime);
            String createKey = Utils.createKey(createRequest, new StringBuilder());
            if (!MemoryPolicy.shouldReadFromMemoryCache(0) || this.picasso.quickMemoryCacheCheck(createKey) == null) {
                this.picasso.submit(new FetchAction(this.picasso, createRequest, 0, 0, null, createKey, null));
            } else if (this.picasso.loggingEnabled) {
                Utils.log("Main", "completed", createRequest.plainId(), "from " + LoadedFrom.MEMORY);
            }
        }
    }

    public final void into(ImageView target) {
        into(target, null);
    }

    public final void into(ImageView target, Callback callback) {
        long started = System.nanoTime();
        Utils.checkMain();
        if (target == null) {
            throw new IllegalArgumentException("Target must not be null.");
        } else if (this.data.hasImage()) {
            if (this.deferred) {
                if (this.data.hasSize()) {
                    throw new IllegalStateException("Fit cannot be used with resize.");
                }
                int width = target.getWidth();
                int height = target.getHeight();
                if (width == 0 || height == 0 || target.isLayoutRequested()) {
                    if (this.setPlaceholder) {
                        PicassoDrawable.setPlaceholder(target, null);
                    }
                    Picasso picasso = this.picasso;
                    DeferredRequestCreator deferredRequestCreator = new DeferredRequestCreator(this, target, callback);
                    if (picasso.targetToDeferredRequestCreator.containsKey(target)) {
                        picasso.cancelExistingRequest(target);
                    }
                    picasso.targetToDeferredRequestCreator.put(target, deferredRequestCreator);
                    return;
                }
                this.data.resize(width, height);
            }
            Request request = createRequest(started);
            String requestKey = Utils.createKey(request);
            if (MemoryPolicy.shouldReadFromMemoryCache(0)) {
                Bitmap bitmap = this.picasso.quickMemoryCacheCheck(requestKey);
                if (bitmap != null) {
                    this.picasso.cancelRequest(target);
                    PicassoDrawable.setBitmap(target, this.picasso.context, bitmap, LoadedFrom.MEMORY, false, this.picasso.indicatorsEnabled);
                    if (this.picasso.loggingEnabled) {
                        Utils.log("Main", "completed", request.plainId(), "from " + LoadedFrom.MEMORY);
                        return;
                    }
                    return;
                }
            }
            if (this.setPlaceholder) {
                PicassoDrawable.setPlaceholder(target, null);
            }
            this.picasso.enqueueAndSubmit(new ImageViewAction(this.picasso, target, request, 0, 0, 0, null, requestKey, null, callback, false));
        } else {
            this.picasso.cancelRequest(target);
            if (this.setPlaceholder) {
                PicassoDrawable.setPlaceholder(target, null);
            }
        }
    }

    private Request createRequest(long started) {
        int id = nextId.getAndIncrement();
        Request request = this.data.build();
        request.id = id;
        request.started = started;
        boolean loggingEnabled = this.picasso.loggingEnabled;
        if (loggingEnabled) {
            Utils.log("Main", "created", request.plainId(), request.toString());
        }
        Request transformed = this.picasso.transformRequest(request);
        if (transformed != request) {
            transformed.id = id;
            transformed.started = started;
            if (loggingEnabled) {
                Utils.log("Main", "changed", transformed.logId(), "into " + transformed);
            }
        }
        return transformed;
    }
}
