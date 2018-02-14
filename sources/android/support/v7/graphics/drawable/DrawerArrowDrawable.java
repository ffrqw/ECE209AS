package android.support.v7.graphics.drawable;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.appcompat.R;

public final class DrawerArrowDrawable extends Drawable {
    private static final float ARROW_HEAD_ANGLE = ((float) Math.toRadians(45.0d));
    private float mArrowHeadLength;
    private float mArrowShaftLength;
    private float mBarGap;
    private float mBarLength;
    private int mDirection = 2;
    private float mMaxCutForBarSize;
    private final Paint mPaint = new Paint();
    private final Path mPath = new Path();
    private float mProgress;
    private final int mSize;
    private boolean mSpin;
    private boolean mVerticalMirror = false;

    public DrawerArrowDrawable(Context context) {
        this.mPaint.setStyle(Style.STROKE);
        this.mPaint.setStrokeJoin(Join.MITER);
        this.mPaint.setStrokeCap(Cap.BUTT);
        this.mPaint.setAntiAlias(true);
        TypedArray a = context.getTheme().obtainStyledAttributes(null, R.styleable.DrawerArrowToggle, com.rachio.iro.R.attr.drawerArrowStyle, com.rachio.iro.R.style.Base.Widget.AppCompat.DrawerArrowToggle);
        int color = a.getColor(R.styleable.DrawerArrowToggle_color, 0);
        if (color != this.mPaint.getColor()) {
            this.mPaint.setColor(color);
            invalidateSelf();
        }
        float dimension = a.getDimension(R.styleable.DrawerArrowToggle_thickness, 0.0f);
        if (this.mPaint.getStrokeWidth() != dimension) {
            this.mPaint.setStrokeWidth(dimension);
            this.mMaxCutForBarSize = (float) (((double) (dimension / 2.0f)) * Math.cos((double) ARROW_HEAD_ANGLE));
            invalidateSelf();
        }
        boolean z = a.getBoolean(R.styleable.DrawerArrowToggle_spinBars, true);
        if (this.mSpin != z) {
            this.mSpin = z;
            invalidateSelf();
        }
        dimension = (float) Math.round(a.getDimension(R.styleable.DrawerArrowToggle_gapBetweenBars, 0.0f));
        if (dimension != this.mBarGap) {
            this.mBarGap = dimension;
            invalidateSelf();
        }
        this.mSize = a.getDimensionPixelSize(R.styleable.DrawerArrowToggle_drawableSize, 0);
        this.mBarLength = (float) Math.round(a.getDimension(R.styleable.DrawerArrowToggle_barLength, 0.0f));
        this.mArrowHeadLength = (float) Math.round(a.getDimension(R.styleable.DrawerArrowToggle_arrowHeadLength, 0.0f));
        this.mArrowShaftLength = a.getDimension(R.styleable.DrawerArrowToggle_arrowShaftLength, 0.0f);
        a.recycle();
    }

    public final void setVerticalMirror(boolean verticalMirror) {
        if (this.mVerticalMirror != verticalMirror) {
            this.mVerticalMirror = verticalMirror;
            invalidateSelf();
        }
    }

    public final void draw(Canvas canvas) {
        boolean flipToPointRight;
        float f;
        Rect bounds = getBounds();
        switch (this.mDirection) {
            case 0:
                flipToPointRight = false;
                break;
            case 1:
                flipToPointRight = true;
                break;
            case 3:
                flipToPointRight = DrawableCompat.getLayoutDirection(this) == 0;
                break;
            default:
                if (DrawableCompat.getLayoutDirection(this) != 1) {
                    flipToPointRight = false;
                    break;
                } else {
                    flipToPointRight = true;
                    break;
                }
        }
        float arrowHeadBarLength = (float) Math.sqrt((double) ((this.mArrowHeadLength * this.mArrowHeadLength) * 2.0f));
        float f2 = this.mBarLength;
        arrowHeadBarLength = f2 + (this.mProgress * (arrowHeadBarLength - f2));
        f2 = this.mBarLength;
        float arrowShaftLength = f2 + ((this.mArrowShaftLength - f2) * this.mProgress);
        float arrowShaftCut = (float) Math.round(0.0f + ((this.mMaxCutForBarSize - 0.0f) * this.mProgress));
        float rotation = 0.0f + ((ARROW_HEAD_ANGLE - 0.0f) * this.mProgress);
        if (flipToPointRight) {
            f = 0.0f;
        } else {
            f = -180.0f;
        }
        if (flipToPointRight) {
            f2 = 180.0f;
        } else {
            f2 = 0.0f;
        }
        float canvasRotate = f + ((f2 - f) * this.mProgress);
        float arrowWidth = (float) Math.round(((double) arrowHeadBarLength) * Math.cos((double) rotation));
        float arrowHeight = (float) Math.round(((double) arrowHeadBarLength) * Math.sin((double) rotation));
        this.mPath.rewind();
        f2 = this.mBarGap + this.mPaint.getStrokeWidth();
        float topBottomBarOffset = f2 + (((-this.mMaxCutForBarSize) - f2) * this.mProgress);
        float arrowEdge = (-arrowShaftLength) / 2.0f;
        this.mPath.moveTo(arrowEdge + arrowShaftCut, 0.0f);
        this.mPath.rLineTo(arrowShaftLength - (2.0f * arrowShaftCut), 0.0f);
        this.mPath.moveTo(arrowEdge, topBottomBarOffset);
        this.mPath.rLineTo(arrowWidth, arrowHeight);
        this.mPath.moveTo(arrowEdge, -topBottomBarOffset);
        this.mPath.rLineTo(arrowWidth, -arrowHeight);
        this.mPath.close();
        canvas.save();
        float barThickness = this.mPaint.getStrokeWidth();
        canvas.translate((float) bounds.centerX(), (float) (((double) ((float) ((((int) ((((float) bounds.height()) - (3.0f * barThickness)) - (this.mBarGap * 2.0f))) / 4) << 1))) + ((((double) barThickness) * 1.5d) + ((double) this.mBarGap))));
        if (this.mSpin) {
            canvas.rotate(((float) ((this.mVerticalMirror ^ flipToPointRight) != 0 ? -1 : 1)) * canvasRotate);
        } else if (flipToPointRight) {
            canvas.rotate(180.0f);
        }
        canvas.drawPath(this.mPath, this.mPaint);
        canvas.restore();
    }

    public final void setAlpha(int alpha) {
        if (alpha != this.mPaint.getAlpha()) {
            this.mPaint.setAlpha(alpha);
            invalidateSelf();
        }
    }

    public final void setColorFilter(ColorFilter colorFilter) {
        this.mPaint.setColorFilter(colorFilter);
        invalidateSelf();
    }

    public final int getIntrinsicHeight() {
        return this.mSize;
    }

    public final int getIntrinsicWidth() {
        return this.mSize;
    }

    public final int getOpacity() {
        return -3;
    }

    public final void setProgress(float progress) {
        if (this.mProgress != progress) {
            this.mProgress = progress;
            invalidateSelf();
        }
    }
}
