package android.support.v4.widget;

import android.content.res.Resources;
import android.os.SystemClock;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

public abstract class AutoScrollHelper implements OnTouchListener {
    private static final int DEFAULT_ACTIVATION_DELAY = ViewConfiguration.getTapTimeout();
    private int mActivationDelay;
    private boolean mAlreadyDelayed;
    boolean mAnimating;
    private final Interpolator mEdgeInterpolator = new AccelerateInterpolator();
    private int mEdgeType;
    private boolean mEnabled;
    private float[] mMaximumEdges = new float[]{Float.MAX_VALUE, Float.MAX_VALUE};
    private float[] mMaximumVelocity = new float[]{Float.MAX_VALUE, Float.MAX_VALUE};
    private float[] mMinimumVelocity = new float[]{0.0f, 0.0f};
    boolean mNeedsCancel;
    boolean mNeedsReset;
    private float[] mRelativeEdges = new float[]{0.0f, 0.0f};
    private float[] mRelativeVelocity = new float[]{0.0f, 0.0f};
    private Runnable mRunnable;
    final ClampedScroller mScroller = new ClampedScroller();
    final View mTarget;

    private static class ClampedScroller {
        private long mDeltaTime = 0;
        private int mDeltaX = 0;
        private int mDeltaY = 0;
        private int mEffectiveRampDown;
        private int mRampDownDuration;
        private int mRampUpDuration;
        private long mStartTime = Long.MIN_VALUE;
        private long mStopTime = -1;
        private float mStopValue;
        private float mTargetVelocityX;
        private float mTargetVelocityY;

        ClampedScroller() {
        }

        public final void setRampUpDuration(int durationMillis) {
            this.mRampUpDuration = durationMillis;
        }

        public final void setRampDownDuration(int durationMillis) {
            this.mRampDownDuration = durationMillis;
        }

        public final void start() {
            this.mStartTime = AnimationUtils.currentAnimationTimeMillis();
            this.mStopTime = -1;
            this.mDeltaTime = this.mStartTime;
            this.mStopValue = 0.5f;
            this.mDeltaX = 0;
            this.mDeltaY = 0;
        }

        public final void requestStop() {
            long currentTime = AnimationUtils.currentAnimationTimeMillis();
            this.mEffectiveRampDown = AutoScrollHelper.constrain((int) (currentTime - this.mStartTime), 0, this.mRampDownDuration);
            this.mStopValue = getValueAt(currentTime);
            this.mStopTime = currentTime;
        }

        public final boolean isFinished() {
            return this.mStopTime > 0 && AnimationUtils.currentAnimationTimeMillis() > this.mStopTime + ((long) this.mEffectiveRampDown);
        }

        private float getValueAt(long currentTime) {
            if (currentTime < this.mStartTime) {
                return 0.0f;
            }
            if (this.mStopTime < 0 || currentTime < this.mStopTime) {
                return AutoScrollHelper.constrain(((float) (currentTime - this.mStartTime)) / ((float) this.mRampUpDuration), 0.0f, 1.0f) * 0.5f;
            }
            long elapsedSinceEnd = currentTime - this.mStopTime;
            return (AutoScrollHelper.constrain(((float) elapsedSinceEnd) / ((float) this.mEffectiveRampDown), 0.0f, 1.0f) * this.mStopValue) + (1.0f - this.mStopValue);
        }

        public final void computeScrollDelta() {
            if (this.mDeltaTime == 0) {
                throw new RuntimeException("Cannot compute scroll delta before calling start()");
            }
            long currentTime = AnimationUtils.currentAnimationTimeMillis();
            float value = getValueAt(currentTime);
            float scale = ((-4.0f * value) * value) + (4.0f * value);
            long elapsedSinceDelta = currentTime - this.mDeltaTime;
            this.mDeltaTime = currentTime;
            this.mDeltaX = (int) ((((float) elapsedSinceDelta) * scale) * this.mTargetVelocityX);
            this.mDeltaY = (int) ((((float) elapsedSinceDelta) * scale) * this.mTargetVelocityY);
        }

        public final void setTargetVelocity(float x, float y) {
            this.mTargetVelocityX = x;
            this.mTargetVelocityY = y;
        }

        public final int getHorizontalDirection() {
            return (int) (this.mTargetVelocityX / Math.abs(this.mTargetVelocityX));
        }

        public final int getVerticalDirection() {
            return (int) (this.mTargetVelocityY / Math.abs(this.mTargetVelocityY));
        }

        public final int getDeltaY() {
            return this.mDeltaY;
        }
    }

    private class ScrollAnimationRunnable implements Runnable {
        ScrollAnimationRunnable() {
        }

        public final void run() {
            if (AutoScrollHelper.this.mAnimating) {
                if (AutoScrollHelper.this.mNeedsReset) {
                    AutoScrollHelper.this.mNeedsReset = false;
                    AutoScrollHelper.this.mScroller.start();
                }
                ClampedScroller scroller = AutoScrollHelper.this.mScroller;
                if (scroller.isFinished() || !AutoScrollHelper.this.shouldAnimate()) {
                    AutoScrollHelper.this.mAnimating = false;
                    return;
                }
                if (AutoScrollHelper.this.mNeedsCancel) {
                    AutoScrollHelper.this.mNeedsCancel = false;
                    AutoScrollHelper autoScrollHelper = AutoScrollHelper.this;
                    long uptimeMillis = SystemClock.uptimeMillis();
                    MotionEvent obtain = MotionEvent.obtain(uptimeMillis, uptimeMillis, 3, 0.0f, 0.0f, 0);
                    autoScrollHelper.mTarget.onTouchEvent(obtain);
                    obtain.recycle();
                }
                scroller.computeScrollDelta();
                AutoScrollHelper.this.scrollTargetBy$255f295(scroller.getDeltaY());
                ViewCompat.postOnAnimation(AutoScrollHelper.this.mTarget, this);
            }
        }
    }

    public abstract boolean canTargetScrollVertically(int i);

    public abstract void scrollTargetBy$255f295(int i);

    public AutoScrollHelper(View target) {
        this.mTarget = target;
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        int maxVelocity = (int) ((1575.0f * metrics.density) + 0.5f);
        int minVelocity = (int) ((315.0f * metrics.density) + 0.5f);
        float f = (float) maxVelocity;
        this.mMaximumVelocity[0] = ((float) maxVelocity) / 1000.0f;
        this.mMaximumVelocity[1] = f / 1000.0f;
        f = (float) minVelocity;
        this.mMinimumVelocity[0] = ((float) minVelocity) / 1000.0f;
        this.mMinimumVelocity[1] = f / 1000.0f;
        this.mEdgeType = 1;
        this.mMaximumEdges[0] = Float.MAX_VALUE;
        this.mMaximumEdges[1] = Float.MAX_VALUE;
        this.mRelativeEdges[0] = 0.2f;
        this.mRelativeEdges[1] = 0.2f;
        this.mRelativeVelocity[0] = 0.001f;
        this.mRelativeVelocity[1] = 0.001f;
        this.mActivationDelay = DEFAULT_ACTIVATION_DELAY;
        this.mScroller.setRampUpDuration(500);
        this.mScroller.setRampDownDuration(500);
    }

    public final AutoScrollHelper setEnabled(boolean enabled) {
        if (this.mEnabled && !enabled) {
            requestStop();
        }
        this.mEnabled = enabled;
        return this;
    }

    public boolean onTouch(View v, MotionEvent event) {
        if (this.mEnabled) {
            switch (MotionEventCompat.getActionMasked(event)) {
                case 0:
                    this.mNeedsCancel = true;
                    this.mAlreadyDelayed = false;
                    break;
                case 1:
                case 3:
                    requestStop();
                    break;
                case 2:
                    break;
                default:
                    break;
            }
            this.mScroller.setTargetVelocity(computeTargetVelocity(0, event.getX(), (float) v.getWidth(), (float) this.mTarget.getWidth()), computeTargetVelocity(1, event.getY(), (float) v.getHeight(), (float) this.mTarget.getHeight()));
            if (!this.mAnimating && shouldAnimate()) {
                if (this.mRunnable == null) {
                    this.mRunnable = new ScrollAnimationRunnable();
                }
                this.mAnimating = true;
                this.mNeedsReset = true;
                if (this.mAlreadyDelayed || this.mActivationDelay <= 0) {
                    this.mRunnable.run();
                } else {
                    ViewCompat.postOnAnimationDelayed(this.mTarget, this.mRunnable, (long) this.mActivationDelay);
                }
                this.mAlreadyDelayed = true;
            }
        }
        return false;
    }

    final boolean shouldAnimate() {
        ClampedScroller scroller = this.mScroller;
        int verticalDirection = scroller.getVerticalDirection();
        int horizontalDirection = scroller.getHorizontalDirection();
        if (verticalDirection != 0 && canTargetScrollVertically(verticalDirection)) {
            return true;
        }
        if (horizontalDirection != 0) {
        }
        return false;
    }

    private void requestStop() {
        if (this.mNeedsReset) {
            this.mAnimating = false;
        } else {
            this.mScroller.requestStop();
        }
    }

    private float computeTargetVelocity(int direction, float coordinate, float srcSize, float dstSize) {
        float value;
        float minimumVelocity;
        float maximumVelocity;
        float targetVelocity;
        float relativeEdge = this.mRelativeEdges[direction];
        float constrain = constrain(relativeEdge * srcSize, 0.0f, this.mMaximumEdges[direction]);
        constrain = constrainEdgeValue(srcSize - coordinate, constrain) - constrainEdgeValue(coordinate, constrain);
        if (constrain < 0.0f) {
            constrain = -this.mEdgeInterpolator.getInterpolation(-constrain);
        } else if (constrain > 0.0f) {
            constrain = this.mEdgeInterpolator.getInterpolation(constrain);
        } else {
            value = 0.0f;
            if (value == 0.0f) {
                return 0.0f;
            }
            float relativeVelocity = this.mRelativeVelocity[direction];
            minimumVelocity = this.mMinimumVelocity[direction];
            maximumVelocity = this.mMaximumVelocity[direction];
            targetVelocity = relativeVelocity * dstSize;
            if (value <= 0.0f) {
                return constrain(value * targetVelocity, minimumVelocity, maximumVelocity);
            }
            return -constrain((-value) * targetVelocity, minimumVelocity, maximumVelocity);
        }
        value = constrain(constrain, -1.0f, 1.0f);
        if (value == 0.0f) {
            return 0.0f;
        }
        float relativeVelocity2 = this.mRelativeVelocity[direction];
        minimumVelocity = this.mMinimumVelocity[direction];
        maximumVelocity = this.mMaximumVelocity[direction];
        targetVelocity = relativeVelocity2 * dstSize;
        if (value <= 0.0f) {
            return -constrain((-value) * targetVelocity, minimumVelocity, maximumVelocity);
        }
        return constrain(value * targetVelocity, minimumVelocity, maximumVelocity);
    }

    private float constrainEdgeValue(float current, float leading) {
        if (leading == 0.0f) {
            return 0.0f;
        }
        switch (this.mEdgeType) {
            case 0:
            case 1:
                if (current >= leading) {
                    return 0.0f;
                }
                if (current >= 0.0f) {
                    return 1.0f - (current / leading);
                }
                if (this.mAnimating && this.mEdgeType == 1) {
                    return 1.0f;
                }
                return 0.0f;
            case 2:
                if (current < 0.0f) {
                    return current / (-leading);
                }
                return 0.0f;
            default:
                return 0.0f;
        }
    }

    static int constrain(int value, int min, int max) {
        if (value > max) {
            return max;
        }
        return value < 0 ? 0 : value;
    }

    static float constrain(float value, float min, float max) {
        if (value > max) {
            return max;
        }
        if (value < min) {
            return min;
        }
        return value;
    }
}
