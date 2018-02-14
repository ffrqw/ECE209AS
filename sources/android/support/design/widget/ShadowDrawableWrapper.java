package android.support.design.widget;

import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.FillType;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.support.v7.graphics.drawable.DrawableWrapper;

final class ShadowDrawableWrapper extends DrawableWrapper {
    static final double COS_45 = Math.cos(Math.toRadians(45.0d));
    final RectF mContentBounds;
    final Paint mCornerShadowPaint;
    Path mCornerShadowPath;
    private boolean mDirty;
    final Paint mEdgeShadowPaint;
    float mMaxShadowSize;
    private boolean mPrintedShadowClipWarning;
    float mRawMaxShadowSize;
    float mRawShadowSize;
    private float mRotation;
    private final int mShadowEndColor;
    private final int mShadowMiddleColor;
    float mShadowSize;
    private final int mShadowStartColor;

    private static int toEven(float value) {
        int i = Math.round(value);
        return i % 2 == 1 ? i - 1 : i;
    }

    public final void setAlpha(int alpha) {
        super.setAlpha(alpha);
        this.mCornerShadowPaint.setAlpha(alpha);
        this.mEdgeShadowPaint.setAlpha(alpha);
    }

    protected final void onBoundsChange(Rect bounds) {
        this.mDirty = true;
    }

    public final boolean getPadding(Rect padding) {
        int vOffset = (int) Math.ceil((double) calculateVerticalPadding(this.mRawMaxShadowSize, 0.0f, false));
        int hOffset = (int) Math.ceil((double) calculateHorizontalPadding(this.mRawMaxShadowSize, 0.0f, false));
        padding.set(hOffset, vOffset, hOffset, vOffset);
        return true;
    }

    public static float calculateVerticalPadding(float maxShadowSize, float cornerRadius, boolean addPaddingForCorners) {
        if (addPaddingForCorners) {
            return (float) (((double) (1.5f * maxShadowSize)) + ((1.0d - COS_45) * ((double) cornerRadius)));
        }
        return 1.5f * maxShadowSize;
    }

    public static float calculateHorizontalPadding(float maxShadowSize, float cornerRadius, boolean addPaddingForCorners) {
        if (addPaddingForCorners) {
            return (float) (((double) maxShadowSize) + ((1.0d - COS_45) * ((double) cornerRadius)));
        }
        return maxShadowSize;
    }

    public final int getOpacity() {
        return -3;
    }

    public final void draw(Canvas canvas) {
        float f;
        Object obj;
        if (this.mDirty) {
            Rect bounds = getBounds();
            float f2 = this.mRawMaxShadowSize * 1.5f;
            this.mContentBounds.set(((float) bounds.left) + this.mRawMaxShadowSize, ((float) bounds.top) + f2, ((float) bounds.right) - this.mRawMaxShadowSize, ((float) bounds.bottom) - f2);
            getWrappedDrawable().setBounds((int) this.mContentBounds.left, (int) this.mContentBounds.top, (int) this.mContentBounds.right, (int) this.mContentBounds.bottom);
            RectF rectF = new RectF(-0.0f, -0.0f, 0.0f, 0.0f);
            RectF rectF2 = new RectF(rectF);
            rectF2.inset(-this.mShadowSize, -this.mShadowSize);
            if (this.mCornerShadowPath == null) {
                this.mCornerShadowPath = new Path();
            } else {
                this.mCornerShadowPath.reset();
            }
            this.mCornerShadowPath.setFillType(FillType.EVEN_ODD);
            this.mCornerShadowPath.moveTo(-0.0f, 0.0f);
            this.mCornerShadowPath.rLineTo(-this.mShadowSize, 0.0f);
            this.mCornerShadowPath.arcTo(rectF2, 180.0f, 90.0f, false);
            this.mCornerShadowPath.arcTo(rectF, 270.0f, -90.0f, false);
            this.mCornerShadowPath.close();
            float f3 = -rectF2.top;
            if (f3 > 0.0f) {
                float f4 = 0.0f / f3;
                f = f4 + ((1.0f - f4) / 2.0f);
                this.mCornerShadowPaint.setShader(new RadialGradient(0.0f, 0.0f, f3, new int[]{0, this.mShadowStartColor, this.mShadowMiddleColor, this.mShadowEndColor}, new float[]{0.0f, f4, f, 1.0f}, TileMode.CLAMP));
            }
            this.mEdgeShadowPaint.setShader(new LinearGradient(0.0f, rectF.top, 0.0f, rectF2.top, new int[]{this.mShadowStartColor, this.mShadowMiddleColor, this.mShadowEndColor}, new float[]{0.0f, 0.5f, 1.0f}, TileMode.CLAMP));
            this.mEdgeShadowPaint.setAntiAlias(false);
            this.mDirty = false;
        }
        int save = canvas.save();
        canvas.rotate(this.mRotation, this.mContentBounds.centerX(), this.mContentBounds.centerY());
        float f5 = -0.0f - this.mShadowSize;
        Object obj2 = this.mContentBounds.width() > 0.0f ? 1 : null;
        if (this.mContentBounds.height() > 0.0f) {
            obj = 1;
        } else {
            obj = null;
        }
        f = 0.0f / ((this.mRawShadowSize - (this.mRawShadowSize * 0.5f)) + 0.0f);
        float f6 = 0.0f / ((this.mRawShadowSize - (this.mRawShadowSize * 0.25f)) + 0.0f);
        float f7 = 0.0f / (0.0f + (this.mRawShadowSize - this.mRawShadowSize));
        int save2 = canvas.save();
        canvas.translate(this.mContentBounds.left + 0.0f, this.mContentBounds.top + 0.0f);
        canvas.scale(f, f6);
        canvas.drawPath(this.mCornerShadowPath, this.mCornerShadowPaint);
        if (obj2 != null) {
            canvas.scale(1.0f / f, 1.0f);
            canvas.drawRect(0.0f, f5, this.mContentBounds.width(), -0.0f, this.mEdgeShadowPaint);
        }
        canvas.restoreToCount(save2);
        save2 = canvas.save();
        canvas.translate(this.mContentBounds.right, this.mContentBounds.bottom);
        canvas.scale(f, f7);
        canvas.rotate(180.0f);
        canvas.drawPath(this.mCornerShadowPath, this.mCornerShadowPaint);
        if (obj2 != null) {
            canvas.scale(1.0f / f, 1.0f);
            canvas.drawRect(0.0f, f5, this.mContentBounds.width(), this.mShadowSize - -0.0f, this.mEdgeShadowPaint);
        }
        canvas.restoreToCount(save2);
        int save3 = canvas.save();
        canvas.translate(this.mContentBounds.left + 0.0f, this.mContentBounds.bottom);
        canvas.scale(f, f7);
        canvas.rotate(270.0f);
        canvas.drawPath(this.mCornerShadowPath, this.mCornerShadowPaint);
        if (obj != null) {
            canvas.scale(1.0f / f7, 1.0f);
            canvas.drawRect(0.0f, f5, this.mContentBounds.height(), -0.0f, this.mEdgeShadowPaint);
        }
        canvas.restoreToCount(save3);
        save3 = canvas.save();
        canvas.translate(this.mContentBounds.right, this.mContentBounds.top + 0.0f);
        canvas.scale(f, f6);
        canvas.rotate(90.0f);
        canvas.drawPath(this.mCornerShadowPath, this.mCornerShadowPaint);
        if (obj != null) {
            canvas.scale(1.0f / f6, 1.0f);
            canvas.drawRect(0.0f, f5, this.mContentBounds.height(), -0.0f, this.mEdgeShadowPaint);
        }
        canvas.restoreToCount(save3);
        canvas.restoreToCount(save);
        super.draw(canvas);
    }

    public final void setShadowSize(float size) {
        float f = this.mRawMaxShadowSize;
        if (size < 0.0f || f < 0.0f) {
            throw new IllegalArgumentException("invalid shadow size");
        }
        float toEven = (float) toEven(size);
        f = (float) toEven(f);
        if (toEven > f) {
            if (!this.mPrintedShadowClipWarning) {
                this.mPrintedShadowClipWarning = true;
            }
            toEven = f;
        }
        if (this.mRawShadowSize != toEven || this.mRawMaxShadowSize != f) {
            this.mRawShadowSize = toEven;
            this.mRawMaxShadowSize = f;
            this.mShadowSize = (float) Math.round(toEven * 1.5f);
            this.mMaxShadowSize = f;
            this.mDirty = true;
            invalidateSelf();
        }
    }
}
