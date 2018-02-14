package android.support.v4.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.FillType;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;
import java.util.ArrayList;

final class MaterialProgressDrawable extends Drawable implements Animatable {
    private static final int[] COLORS = new int[]{-16777216};
    private static final Interpolator LINEAR_INTERPOLATOR = new LinearInterpolator();
    static final Interpolator MATERIAL_INTERPOLATOR = new FastOutSlowInInterpolator();
    private Animation mAnimation;
    private final ArrayList<Animation> mAnimators = new ArrayList();
    private final Callback mCallback = new Callback() {
        public final void invalidateDrawable(Drawable d) {
            MaterialProgressDrawable.this.invalidateSelf();
        }

        public final void scheduleDrawable(Drawable d, Runnable what, long when) {
            MaterialProgressDrawable.this.scheduleSelf(what, when);
        }

        public final void unscheduleDrawable(Drawable d, Runnable what) {
            MaterialProgressDrawable.this.unscheduleSelf(what);
        }
    };
    boolean mFinishing;
    private double mHeight;
    private View mParent;
    private Resources mResources;
    private final Ring mRing;
    private float mRotation;
    float mRotationCount;
    private double mWidth;

    private static class Ring {
        private int mAlpha;
        private Path mArrow;
        private int mArrowHeight;
        private final Paint mArrowPaint = new Paint();
        private float mArrowScale;
        private int mArrowWidth;
        private int mBackgroundColor;
        private final Callback mCallback;
        private final Paint mCirclePaint = new Paint(1);
        private int mColorIndex;
        private int[] mColors;
        private int mCurrentColor;
        private float mEndTrim = 0.0f;
        private final Paint mPaint = new Paint();
        private double mRingCenterRadius;
        private float mRotation = 0.0f;
        private boolean mShowArrow;
        private float mStartTrim = 0.0f;
        private float mStartingEndTrim;
        private float mStartingRotation;
        private float mStartingStartTrim;
        private float mStrokeInset = 2.5f;
        private float mStrokeWidth = 5.0f;
        private final RectF mTempBounds = new RectF();

        Ring(Callback callback) {
            this.mCallback = callback;
            this.mPaint.setStrokeCap(Cap.SQUARE);
            this.mPaint.setAntiAlias(true);
            this.mPaint.setStyle(Style.STROKE);
            this.mArrowPaint.setStyle(Style.FILL);
            this.mArrowPaint.setAntiAlias(true);
        }

        public final void setBackgroundColor(int color) {
            this.mBackgroundColor = color;
        }

        public final void setArrowDimensions(float width, float height) {
            this.mArrowWidth = (int) width;
            this.mArrowHeight = (int) height;
        }

        public final void draw(Canvas c, Rect bounds) {
            RectF arcBounds = this.mTempBounds;
            arcBounds.set(bounds);
            arcBounds.inset(this.mStrokeInset, this.mStrokeInset);
            float startAngle = (this.mStartTrim + this.mRotation) * 360.0f;
            float sweepAngle = ((this.mEndTrim + this.mRotation) * 360.0f) - startAngle;
            this.mPaint.setColor(this.mCurrentColor);
            c.drawArc(arcBounds, startAngle, sweepAngle, false, this.mPaint);
            if (this.mShowArrow) {
                if (this.mArrow == null) {
                    this.mArrow = new Path();
                    this.mArrow.setFillType(FillType.EVEN_ODD);
                } else {
                    this.mArrow.reset();
                }
                float f = ((float) (((int) this.mStrokeInset) / 2)) * this.mArrowScale;
                float cos = (float) ((this.mRingCenterRadius * Math.cos(0.0d)) + ((double) bounds.exactCenterX()));
                float sin = (float) ((this.mRingCenterRadius * Math.sin(0.0d)) + ((double) bounds.exactCenterY()));
                this.mArrow.moveTo(0.0f, 0.0f);
                this.mArrow.lineTo(((float) this.mArrowWidth) * this.mArrowScale, 0.0f);
                this.mArrow.lineTo((((float) this.mArrowWidth) * this.mArrowScale) / 2.0f, ((float) this.mArrowHeight) * this.mArrowScale);
                this.mArrow.offset(cos - f, sin);
                this.mArrow.close();
                this.mArrowPaint.setColor(this.mCurrentColor);
                c.rotate((startAngle + sweepAngle) - 5.0f, bounds.exactCenterX(), bounds.exactCenterY());
                c.drawPath(this.mArrow, this.mArrowPaint);
            }
            if (this.mAlpha < 255) {
                this.mCirclePaint.setColor(this.mBackgroundColor);
                this.mCirclePaint.setAlpha(255 - this.mAlpha);
                c.drawCircle(bounds.exactCenterX(), bounds.exactCenterY(), (float) (bounds.width() / 2), this.mCirclePaint);
            }
        }

        public final void setColors(int[] colors) {
            this.mColors = colors;
            setColorIndex(0);
        }

        public final void setColor(int color) {
            this.mCurrentColor = color;
        }

        public final void setColorIndex(int index) {
            this.mColorIndex = index;
            this.mCurrentColor = this.mColors[this.mColorIndex];
        }

        public final int getNextColor() {
            return this.mColors[getNextColorIndex()];
        }

        private int getNextColorIndex() {
            return (this.mColorIndex + 1) % this.mColors.length;
        }

        public final void goToNextColor() {
            setColorIndex(getNextColorIndex());
        }

        public final void setColorFilter(ColorFilter filter) {
            this.mPaint.setColorFilter(filter);
            invalidateSelf();
        }

        public final void setAlpha(int alpha) {
            this.mAlpha = alpha;
        }

        public final int getAlpha() {
            return this.mAlpha;
        }

        public final void setStrokeWidth(float strokeWidth) {
            this.mStrokeWidth = strokeWidth;
            this.mPaint.setStrokeWidth(strokeWidth);
            invalidateSelf();
        }

        public final float getStrokeWidth() {
            return this.mStrokeWidth;
        }

        public final void setStartTrim(float startTrim) {
            this.mStartTrim = startTrim;
            invalidateSelf();
        }

        public final float getStartTrim() {
            return this.mStartTrim;
        }

        public final float getStartingStartTrim() {
            return this.mStartingStartTrim;
        }

        public final float getStartingEndTrim() {
            return this.mStartingEndTrim;
        }

        public final int getStartingColor() {
            return this.mColors[this.mColorIndex];
        }

        public final void setEndTrim(float endTrim) {
            this.mEndTrim = endTrim;
            invalidateSelf();
        }

        public final float getEndTrim() {
            return this.mEndTrim;
        }

        public final void setRotation(float rotation) {
            this.mRotation = rotation;
            invalidateSelf();
        }

        public final void setInsets(int width, int height) {
            float insets;
            float minEdge = (float) Math.min(width, height);
            if (this.mRingCenterRadius <= 0.0d || minEdge < 0.0f) {
                insets = (float) Math.ceil((double) (this.mStrokeWidth / 2.0f));
            } else {
                insets = (float) (((double) (minEdge / 2.0f)) - this.mRingCenterRadius);
            }
            this.mStrokeInset = insets;
        }

        public final void setCenterRadius(double centerRadius) {
            this.mRingCenterRadius = centerRadius;
        }

        public final double getCenterRadius() {
            return this.mRingCenterRadius;
        }

        public final void setShowArrow(boolean show) {
            if (this.mShowArrow != show) {
                this.mShowArrow = show;
                invalidateSelf();
            }
        }

        public final void setArrowScale(float scale) {
            if (scale != this.mArrowScale) {
                this.mArrowScale = scale;
                invalidateSelf();
            }
        }

        public final float getStartingRotation() {
            return this.mStartingRotation;
        }

        public final void storeOriginals() {
            this.mStartingStartTrim = this.mStartTrim;
            this.mStartingEndTrim = this.mEndTrim;
            this.mStartingRotation = this.mRotation;
        }

        public final void resetOriginals() {
            this.mStartingStartTrim = 0.0f;
            this.mStartingEndTrim = 0.0f;
            this.mStartingRotation = 0.0f;
            setStartTrim(0.0f);
            setEndTrim(0.0f);
            setRotation(0.0f);
        }

        private void invalidateSelf() {
            this.mCallback.invalidateDrawable(null);
        }
    }

    MaterialProgressDrawable(Context context, View parent) {
        this.mParent = parent;
        this.mResources = context.getResources();
        this.mRing = new Ring(this.mCallback);
        this.mRing.setColors(COLORS);
        Ring ring = this.mRing;
        float f = this.mResources.getDisplayMetrics().density;
        this.mWidth = ((double) f) * 40.0d;
        this.mHeight = ((double) f) * 40.0d;
        ring.setStrokeWidth(((float) 2.5d) * f);
        ring.setCenterRadius(8.75d * ((double) f));
        ring.setColorIndex(0);
        ring.setArrowDimensions(10.0f * f, 5.0f * f);
        ring.setInsets((int) this.mWidth, (int) this.mHeight);
        final Ring ring2 = this.mRing;
        Animation anonymousClass1 = new Animation() {
            public final void applyTransformation(float interpolatedTime, Transformation t) {
                if (MaterialProgressDrawable.this.mFinishing) {
                    MaterialProgressDrawable.this.applyFinishTranslation(interpolatedTime, ring2);
                    return;
                }
                float minProgressArc = MaterialProgressDrawable.getMinProgressArc(ring2);
                float startingEndTrim = ring2.getStartingEndTrim();
                float startingTrim = ring2.getStartingStartTrim();
                float startingRotation = ring2.getStartingRotation();
                MaterialProgressDrawable.this.updateRingColor(interpolatedTime, ring2);
                if (interpolatedTime <= 0.5f) {
                    ring2.setStartTrim(startingTrim + ((0.8f - minProgressArc) * MaterialProgressDrawable.MATERIAL_INTERPOLATOR.getInterpolation(interpolatedTime / 0.5f)));
                }
                if (interpolatedTime > 0.5f) {
                    ring2.setEndTrim(startingEndTrim + (MaterialProgressDrawable.MATERIAL_INTERPOLATOR.getInterpolation((interpolatedTime - 0.5f) / 0.5f) * (0.8f - minProgressArc)));
                }
                ring2.setRotation(startingRotation + (0.25f * interpolatedTime));
                MaterialProgressDrawable.this.setRotation((216.0f * interpolatedTime) + (1080.0f * (MaterialProgressDrawable.this.mRotationCount / 5.0f)));
            }
        };
        anonymousClass1.setRepeatCount(-1);
        anonymousClass1.setRepeatMode(1);
        anonymousClass1.setInterpolator(LINEAR_INTERPOLATOR);
        anonymousClass1.setAnimationListener(new AnimationListener() {
            public final void onAnimationStart(Animation animation) {
                MaterialProgressDrawable.this.mRotationCount = 0.0f;
            }

            public final void onAnimationEnd(Animation animation) {
            }

            public final void onAnimationRepeat(Animation animation) {
                ring2.storeOriginals();
                ring2.goToNextColor();
                ring2.setStartTrim(ring2.getEndTrim());
                if (MaterialProgressDrawable.this.mFinishing) {
                    MaterialProgressDrawable.this.mFinishing = false;
                    animation.setDuration(1332);
                    ring2.setShowArrow(false);
                    return;
                }
                MaterialProgressDrawable.this.mRotationCount = (MaterialProgressDrawable.this.mRotationCount + 1.0f) % 5.0f;
            }
        });
        this.mAnimation = anonymousClass1;
    }

    public final void showArrow(boolean show) {
        this.mRing.setShowArrow(show);
    }

    public final void setArrowScale(float scale) {
        this.mRing.setArrowScale(scale);
    }

    public final void setStartEndTrim(float startAngle, float endAngle) {
        this.mRing.setStartTrim(0.0f);
        this.mRing.setEndTrim(endAngle);
    }

    public final void setProgressRotation(float rotation) {
        this.mRing.setRotation(rotation);
    }

    public final void setBackgroundColor(int color) {
        this.mRing.setBackgroundColor(-328966);
    }

    public final int getIntrinsicHeight() {
        return (int) this.mHeight;
    }

    public final int getIntrinsicWidth() {
        return (int) this.mWidth;
    }

    public final void draw(Canvas c) {
        Rect bounds = getBounds();
        int saveCount = c.save();
        c.rotate(this.mRotation, bounds.exactCenterX(), bounds.exactCenterY());
        this.mRing.draw(c, bounds);
        c.restoreToCount(saveCount);
    }

    public final void setAlpha(int alpha) {
        this.mRing.setAlpha(alpha);
    }

    public final int getAlpha() {
        return this.mRing.getAlpha();
    }

    public final void setColorFilter(ColorFilter colorFilter) {
        this.mRing.setColorFilter(colorFilter);
    }

    final void setRotation(float rotation) {
        this.mRotation = rotation;
        invalidateSelf();
    }

    public final int getOpacity() {
        return -3;
    }

    public final boolean isRunning() {
        ArrayList<Animation> animators = this.mAnimators;
        int N = animators.size();
        for (int i = 0; i < N; i++) {
            Animation animator = (Animation) animators.get(i);
            if (animator.hasStarted() && !animator.hasEnded()) {
                return true;
            }
        }
        return false;
    }

    public final void start() {
        this.mAnimation.reset();
        this.mRing.storeOriginals();
        if (this.mRing.getEndTrim() != this.mRing.getStartTrim()) {
            this.mFinishing = true;
            this.mAnimation.setDuration(666);
            this.mParent.startAnimation(this.mAnimation);
            return;
        }
        this.mRing.setColorIndex(0);
        this.mRing.resetOriginals();
        this.mAnimation.setDuration(1332);
        this.mParent.startAnimation(this.mAnimation);
    }

    public final void stop() {
        this.mParent.clearAnimation();
        setRotation(0.0f);
        this.mRing.setShowArrow(false);
        this.mRing.setColorIndex(0);
        this.mRing.resetOriginals();
    }

    static float getMinProgressArc(Ring ring) {
        return (float) Math.toRadians(((double) ring.getStrokeWidth()) / (6.283185307179586d * ring.getCenterRadius()));
    }

    final void updateRingColor(float interpolatedTime, Ring ring) {
        if (interpolatedTime > 0.75f) {
            float f = (interpolatedTime - 0.75f) / 0.25f;
            int startingColor = ring.getStartingColor();
            int nextColor = ring.getNextColor();
            startingColor = Integer.valueOf(startingColor).intValue();
            int i = startingColor >>> 24;
            int i2 = (startingColor >> 16) & 255;
            int i3 = (startingColor >> 8) & 255;
            startingColor &= 255;
            nextColor = Integer.valueOf(nextColor).intValue();
            int i4 = ((int) (f * ((float) ((nextColor & 255) - startingColor)))) + startingColor;
            ring.setColor(i4 | ((((i + ((int) (((float) ((nextColor >>> 24) - i)) * f))) << 24) | ((i2 + ((int) (((float) (((nextColor >> 16) & 255) - i2)) * f))) << 16)) | ((((int) (((float) (((nextColor >> 8) & 255) - i3)) * f)) + i3) << 8)));
        }
    }

    final void applyFinishTranslation(float interpolatedTime, Ring ring) {
        updateRingColor(interpolatedTime, ring);
        float targetRotation = (float) (Math.floor((double) (ring.getStartingRotation() / 0.8f)) + 1.0d);
        ring.setStartTrim(ring.getStartingStartTrim() + (((ring.getStartingEndTrim() - getMinProgressArc(ring)) - ring.getStartingStartTrim()) * interpolatedTime));
        ring.setEndTrim(ring.getStartingEndTrim());
        ring.setRotation(ring.getStartingRotation() + ((targetRotation - ring.getStartingRotation()) * interpolatedTime));
    }
}
