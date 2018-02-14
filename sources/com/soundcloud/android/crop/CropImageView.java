package com.soundcloud.android.crop;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import com.soundcloud.android.crop.ImageViewTouchBase.Recycler;
import java.util.ArrayList;
import java.util.Iterator;

public class CropImageView extends ImageViewTouchBase {
    Context context;
    ArrayList<HighlightView> highlightViews = new ArrayList();
    private float lastX;
    private float lastY;
    private int motionEdge;
    HighlightView motionHighlightView;
    private int validPointerId;

    public final /* bridge */ /* synthetic */ void clear() {
        super.clear();
    }

    public final /* bridge */ /* synthetic */ Matrix getUnrotatedMatrix() {
        return super.getUnrotatedMatrix();
    }

    public /* bridge */ /* synthetic */ boolean onKeyDown(int i, KeyEvent keyEvent) {
        return super.onKeyDown(i, keyEvent);
    }

    public /* bridge */ /* synthetic */ boolean onKeyUp(int i, KeyEvent keyEvent) {
        return super.onKeyUp(i, keyEvent);
    }

    public /* bridge */ /* synthetic */ void setImageBitmap(Bitmap bitmap) {
        super.setImageBitmap(bitmap);
    }

    public final /* bridge */ /* synthetic */ void setImageBitmapResetBase(Bitmap bitmap, boolean z) {
        super.setImageBitmapResetBase(bitmap, z);
    }

    public final /* bridge */ /* synthetic */ void setImageRotateBitmapResetBase(RotateBitmap rotateBitmap, boolean z) {
        super.setImageRotateBitmapResetBase(rotateBitmap, z);
    }

    public final /* bridge */ /* synthetic */ void setRecycler(Recycler recycler) {
        super.setRecycler(recycler);
    }

    public CropImageView(Context context) {
        super(context);
    }

    public CropImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CropImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (this.bitmapDisplayed.getBitmap() != null) {
            Iterator it = this.highlightViews.iterator();
            while (it.hasNext()) {
                HighlightView hv = (HighlightView) it.next();
                hv.matrix.set(super.getUnrotatedMatrix());
                hv.invalidate();
                if (hv.hasFocus()) {
                    centerBasedOnHighlightView(hv);
                }
            }
        }
    }

    protected final void zoomTo(float scale, float centerX, float centerY) {
        super.zoomTo(scale, centerX, centerY);
        Iterator it = this.highlightViews.iterator();
        while (it.hasNext()) {
            HighlightView hv = (HighlightView) it.next();
            hv.matrix.set(super.getUnrotatedMatrix());
            hv.invalidate();
        }
    }

    protected final void postTranslate(float deltaX, float deltaY) {
        super.postTranslate(deltaX, deltaY);
        Iterator it = this.highlightViews.iterator();
        while (it.hasNext()) {
            HighlightView hv = (HighlightView) it.next();
            hv.matrix.postTranslate(deltaX, deltaY);
            hv.invalidate();
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (this.context.isSaving()) {
            return false;
        }
        switch (event.getAction()) {
            case 0:
                Iterator it = this.highlightViews.iterator();
                while (it.hasNext()) {
                    HighlightView hv = (HighlightView) it.next();
                    int edge = hv.getHit(event.getX(), event.getY());
                    if (edge != 1) {
                        this.motionEdge = edge;
                        this.motionHighlightView = hv;
                        this.lastX = event.getX();
                        this.lastY = event.getY();
                        this.validPointerId = event.getPointerId(event.getActionIndex());
                        this.motionHighlightView.setMode$27ea16d8(edge == 32 ? ModifyMode.Move$899a623 : ModifyMode.Grow$899a623);
                        break;
                    }
                }
                break;
            case 1:
                if (this.motionHighlightView != null) {
                    centerBasedOnHighlightView(this.motionHighlightView);
                    this.motionHighlightView.setMode$27ea16d8(ModifyMode.None$899a623);
                }
                this.motionHighlightView = null;
                center();
                break;
            case 2:
                if (this.motionHighlightView != null && event.getPointerId(event.getActionIndex()) == this.validPointerId) {
                    this.motionHighlightView.handleMotion(this.motionEdge, event.getX() - this.lastX, event.getY() - this.lastY);
                    this.lastX = event.getX();
                    this.lastY = event.getY();
                }
                if (getScale() == 1.0f) {
                    center();
                    break;
                }
                break;
        }
        return true;
    }

    private void centerBasedOnHighlightView(HighlightView hv) {
        Rect drawRect = hv.drawRect;
        float thisWidth = (float) getWidth();
        float thisHeight = (float) getHeight();
        float zoom = Math.max(1.0f, Math.min((thisWidth / ((float) drawRect.width())) * 0.6f, (thisHeight / ((float) drawRect.height())) * 0.6f) * getScale());
        if (((double) (Math.abs(zoom - getScale()) / zoom)) > 0.1d) {
            float[] coordinates = new float[]{hv.cropRect.centerX(), hv.cropRect.centerY()};
            super.getUnrotatedMatrix().mapPoints(coordinates);
            zoomTo(zoom, coordinates[0], coordinates[1], 300.0f);
        }
        Rect rect = hv.drawRect;
        int max = Math.max(0, getLeft() - rect.left);
        int min = Math.min(0, getRight() - rect.right);
        int max2 = Math.max(0, getTop() - rect.top);
        int min2 = Math.min(0, getBottom() - rect.bottom);
        if (max == 0) {
            max = min;
        }
        if (max2 == 0) {
            max2 = min2;
        }
        if (max != 0 || max2 != 0) {
            panBy((float) max, (float) max2);
        }
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Iterator it = this.highlightViews.iterator();
        while (it.hasNext()) {
            ((HighlightView) it.next()).draw(canvas);
        }
    }
}
