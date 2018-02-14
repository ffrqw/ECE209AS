package com.squareup.picasso;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.widget.ImageView;
import com.squareup.picasso.Picasso.LoadedFrom;

final class PicassoDrawable extends BitmapDrawable {
    private static final Paint DEBUG_PAINT = new Paint();
    int alpha = 255;
    boolean animating;
    private final boolean debugging;
    private final float density;
    private final LoadedFrom loadedFrom;
    Drawable placeholder;
    long startTimeMillis;

    static void setBitmap(ImageView target, Context context, Bitmap bitmap, LoadedFrom loadedFrom, boolean noFade, boolean debugging) {
        Drawable placeholder = target.getDrawable();
        if (placeholder instanceof AnimationDrawable) {
            ((AnimationDrawable) placeholder).stop();
        }
        target.setImageDrawable(new PicassoDrawable(context, bitmap, placeholder, loadedFrom, noFade, debugging));
    }

    static void setPlaceholder(ImageView target, Drawable placeholderDrawable) {
        target.setImageDrawable(placeholderDrawable);
        if (target.getDrawable() instanceof AnimationDrawable) {
            ((AnimationDrawable) target.getDrawable()).start();
        }
    }

    private PicassoDrawable(Context context, Bitmap bitmap, Drawable placeholder, LoadedFrom loadedFrom, boolean noFade, boolean debugging) {
        super(context.getResources(), bitmap);
        this.debugging = debugging;
        this.density = context.getResources().getDisplayMetrics().density;
        this.loadedFrom = loadedFrom;
        boolean fade = (loadedFrom == LoadedFrom.MEMORY || noFade) ? false : true;
        if (fade) {
            this.placeholder = placeholder;
            this.animating = true;
            this.startTimeMillis = SystemClock.uptimeMillis();
        }
    }

    public final void draw(Canvas canvas) {
        if (this.animating) {
            float normalized = ((float) (SystemClock.uptimeMillis() - this.startTimeMillis)) / 200.0f;
            if (normalized >= 1.0f) {
                this.animating = false;
                this.placeholder = null;
                super.draw(canvas);
            } else {
                if (this.placeholder != null) {
                    this.placeholder.draw(canvas);
                }
                super.setAlpha((int) (((float) this.alpha) * normalized));
                super.draw(canvas);
                super.setAlpha(this.alpha);
            }
        } else {
            super.draw(canvas);
        }
        if (this.debugging) {
            DEBUG_PAINT.setColor(-1);
            canvas.drawPath(getTrianglePath(0, 0, (int) (16.0f * this.density)), DEBUG_PAINT);
            DEBUG_PAINT.setColor(this.loadedFrom.debugColor);
            canvas.drawPath(getTrianglePath(0, 0, (int) (15.0f * this.density)), DEBUG_PAINT);
        }
    }

    public final void setAlpha(int alpha) {
        this.alpha = alpha;
        if (this.placeholder != null) {
            this.placeholder.setAlpha(alpha);
        }
        super.setAlpha(alpha);
    }

    public final void setColorFilter(ColorFilter cf) {
        if (this.placeholder != null) {
            this.placeholder.setColorFilter(cf);
        }
        super.setColorFilter(cf);
    }

    protected final void onBoundsChange(Rect bounds) {
        if (this.placeholder != null) {
            this.placeholder.setBounds(bounds);
        }
        super.onBoundsChange(bounds);
    }

    private static Path getTrianglePath(int x1, int y1, int width) {
        Path path = new Path();
        path.moveTo(0.0f, 0.0f);
        path.lineTo((float) (width + 0), 0.0f);
        path.lineTo(0.0f, (float) (width + 0));
        return path;
    }
}
