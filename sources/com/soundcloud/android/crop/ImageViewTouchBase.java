package com.soundcloud.android.crop;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

abstract class ImageViewTouchBase extends ImageView {
    protected Matrix baseMatrix;
    protected final RotateBitmap bitmapDisplayed;
    private final Matrix displayMatrix;
    protected Handler handler;
    private final float[] matrixValues;
    float maxZoom;
    private Runnable onLayoutRunnable;
    private Recycler recycler;
    protected Matrix suppMatrix;
    int thisHeight;
    int thisWidth;

    public interface Recycler {
        void recycle(Bitmap bitmap);
    }

    public ImageViewTouchBase(Context context) {
        super(context);
        this.baseMatrix = new Matrix();
        this.suppMatrix = new Matrix();
        this.displayMatrix = new Matrix();
        this.matrixValues = new float[9];
        this.bitmapDisplayed = new RotateBitmap(null, 0);
        this.thisWidth = -1;
        this.thisHeight = -1;
        this.handler = new Handler();
        setScaleType(ScaleType.MATRIX);
    }

    public ImageViewTouchBase(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.baseMatrix = new Matrix();
        this.suppMatrix = new Matrix();
        this.displayMatrix = new Matrix();
        this.matrixValues = new float[9];
        this.bitmapDisplayed = new RotateBitmap(null, 0);
        this.thisWidth = -1;
        this.thisHeight = -1;
        this.handler = new Handler();
        setScaleType(ScaleType.MATRIX);
    }

    public ImageViewTouchBase(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.baseMatrix = new Matrix();
        this.suppMatrix = new Matrix();
        this.displayMatrix = new Matrix();
        this.matrixValues = new float[9];
        this.bitmapDisplayed = new RotateBitmap(null, 0);
        this.thisWidth = -1;
        this.thisHeight = -1;
        this.handler = new Handler();
        setScaleType(ScaleType.MATRIX);
    }

    public void setRecycler(Recycler recycler) {
        this.recycler = recycler;
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        this.thisWidth = right - left;
        this.thisHeight = bottom - top;
        Runnable r = this.onLayoutRunnable;
        if (r != null) {
            this.onLayoutRunnable = null;
            r.run();
        }
        if (this.bitmapDisplayed.getBitmap() != null) {
            getProperBaseMatrix(this.bitmapDisplayed, this.baseMatrix, true);
            setImageMatrix(getImageViewMatrix());
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != 4 || event.getRepeatCount() != 0) {
            return super.onKeyDown(keyCode, event);
        }
        event.startTracking();
        return true;
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode != 4 || !event.isTracking() || event.isCanceled() || getScale() <= 1.0f) {
            return super.onKeyUp(keyCode, event);
        }
        zoomTo(1.0f, ((float) getWidth()) / 2.0f, ((float) getHeight()) / 2.0f);
        return true;
    }

    public void setImageBitmap(Bitmap bitmap) {
        setImageBitmap(bitmap, 0);
    }

    private void setImageBitmap(Bitmap bitmap, int rotation) {
        super.setImageBitmap(bitmap);
        Drawable d = getDrawable();
        if (d != null) {
            d.setDither(true);
        }
        Bitmap old = this.bitmapDisplayed.getBitmap();
        this.bitmapDisplayed.setBitmap(bitmap);
        this.bitmapDisplayed.setRotation(rotation);
        if (old != null && old != bitmap && this.recycler != null) {
            this.recycler.recycle(old);
        }
    }

    public void clear() {
        setImageBitmapResetBase(null, true);
    }

    public void setImageBitmapResetBase(Bitmap bitmap, boolean resetSupp) {
        setImageRotateBitmapResetBase(new RotateBitmap(bitmap, 0), resetSupp);
    }

    public void setImageRotateBitmapResetBase(final RotateBitmap bitmap, final boolean resetSupp) {
        if (getWidth() <= 0) {
            this.onLayoutRunnable = new Runnable() {
                public final void run() {
                    ImageViewTouchBase.this.setImageRotateBitmapResetBase(bitmap, resetSupp);
                }
            };
            return;
        }
        float f;
        if (bitmap.getBitmap() != null) {
            getProperBaseMatrix(bitmap, this.baseMatrix, true);
            setImageBitmap(bitmap.getBitmap(), bitmap.getRotation());
        } else {
            this.baseMatrix.reset();
            setImageBitmap(null);
        }
        if (resetSupp) {
            this.suppMatrix.reset();
        }
        setImageMatrix(getImageViewMatrix());
        if (this.bitmapDisplayed.getBitmap() == null) {
            f = 1.0f;
        } else {
            f = Math.max(((float) this.bitmapDisplayed.getWidth()) / ((float) this.thisWidth), ((float) this.bitmapDisplayed.getHeight()) / ((float) this.thisHeight)) * 4.0f;
        }
        this.maxZoom = f;
    }

    protected final void center() {
        Bitmap bitmap = this.bitmapDisplayed.getBitmap();
        if (bitmap != null) {
            float deltaY;
            Matrix m = getImageViewMatrix();
            RectF rect = new RectF(0.0f, 0.0f, (float) bitmap.getWidth(), (float) bitmap.getHeight());
            m.mapRect(rect);
            float height = rect.height();
            float width = rect.width();
            int height2 = getHeight();
            if (height < ((float) height2)) {
                deltaY = ((((float) height2) - height) / 2.0f) - rect.top;
            } else if (rect.top > 0.0f) {
                deltaY = -rect.top;
            } else if (rect.bottom < ((float) height2)) {
                deltaY = ((float) getHeight()) - rect.bottom;
            } else {
                deltaY = 0.0f;
            }
            height2 = getWidth();
            float deltaX = width < ((float) height2) ? ((((float) height2) - width) / 2.0f) - rect.left : rect.left > 0.0f ? -rect.left : rect.right < ((float) height2) ? ((float) height2) - rect.right : 0.0f;
            postTranslate(deltaX, deltaY);
            setImageMatrix(getImageViewMatrix());
        }
    }

    protected final float getScale() {
        this.suppMatrix.getValues(this.matrixValues);
        return this.matrixValues[0];
    }

    private void getProperBaseMatrix(RotateBitmap bitmap, Matrix matrix, boolean includeRotation) {
        float viewWidth = (float) getWidth();
        float viewHeight = (float) getHeight();
        float w = (float) bitmap.getWidth();
        float h = (float) bitmap.getHeight();
        matrix.reset();
        float scale = Math.min(Math.min(viewWidth / w, 3.0f), Math.min(viewHeight / h, 3.0f));
        if (includeRotation) {
            matrix.postConcat(bitmap.getRotateMatrix());
        }
        matrix.postScale(scale, scale);
        matrix.postTranslate((viewWidth - (w * scale)) / 2.0f, (viewHeight - (h * scale)) / 2.0f);
    }

    private Matrix getImageViewMatrix() {
        this.displayMatrix.set(this.baseMatrix);
        this.displayMatrix.postConcat(this.suppMatrix);
        return this.displayMatrix;
    }

    public Matrix getUnrotatedMatrix() {
        Matrix unrotated = new Matrix();
        getProperBaseMatrix(this.bitmapDisplayed, unrotated, false);
        unrotated.postConcat(this.suppMatrix);
        return unrotated;
    }

    protected void zoomTo(float scale, float centerX, float centerY) {
        if (scale > this.maxZoom) {
            scale = this.maxZoom;
        }
        float deltaScale = scale / getScale();
        this.suppMatrix.postScale(deltaScale, deltaScale, centerX, centerY);
        setImageMatrix(getImageViewMatrix());
        center();
    }

    protected final void zoomTo(float scale, float centerX, float centerY, float durationMs) {
        final float incrementPerMs = (scale - getScale()) / 300.0f;
        final float oldScale = getScale();
        final long startTime = System.currentTimeMillis();
        final float f = centerX;
        final float f2 = centerY;
        this.handler.post(new Runnable(300.0f) {
            public final void run() {
                float currentMs = Math.min(300.0f, (float) (System.currentTimeMillis() - startTime));
                ImageViewTouchBase.this.zoomTo(oldScale + (incrementPerMs * currentMs), f, f2);
                if (currentMs < 300.0f) {
                    ImageViewTouchBase.this.handler.post(this);
                }
            }
        });
    }

    protected void postTranslate(float dx, float dy) {
        this.suppMatrix.postTranslate(dx, dy);
    }

    protected final void panBy(float dx, float dy) {
        postTranslate(dx, dy);
        setImageMatrix(getImageViewMatrix());
    }
}
