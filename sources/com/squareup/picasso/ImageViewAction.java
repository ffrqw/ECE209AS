package com.squareup.picasso;

import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import com.squareup.picasso.Picasso.LoadedFrom;

final class ImageViewAction extends Action<ImageView> {
    Callback callback;

    ImageViewAction(Picasso picasso, ImageView imageView, Request data, int memoryPolicy, int networkPolicy, int errorResId, Drawable errorDrawable, String key, Object tag, Callback callback, boolean noFade) {
        super(picasso, imageView, data, memoryPolicy, networkPolicy, errorResId, errorDrawable, key, tag, noFade);
        this.callback = callback;
    }

    public final void complete(Bitmap result, LoadedFrom from) {
        if (result == null) {
            throw new AssertionError(String.format("Attempted to complete action with no result!\n%s", new Object[]{this}));
        }
        ImageView target = (ImageView) this.target.get();
        if (target != null) {
            Bitmap bitmap = result;
            LoadedFrom loadedFrom = from;
            PicassoDrawable.setBitmap(target, this.picasso.context, bitmap, loadedFrom, this.noFade, this.picasso.indicatorsEnabled);
        }
    }

    public final void error(Exception e) {
        ImageView target = (ImageView) this.target.get();
        if (target != null) {
            Drawable placeholder = target.getDrawable();
            if (placeholder instanceof AnimationDrawable) {
                ((AnimationDrawable) placeholder).stop();
            }
            if (this.errorResId != 0) {
                target.setImageResource(this.errorResId);
            } else if (this.errorDrawable != null) {
                target.setImageDrawable(this.errorDrawable);
            }
        }
    }

    final void cancel() {
        super.cancel();
        if (this.callback != null) {
            this.callback = null;
        }
    }
}
