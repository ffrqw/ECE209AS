package android.support.design.widget;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import java.util.ArrayList;

final class ValueAnimatorCompatImplGingerbread extends Impl {
    private static final Handler sHandler = new Handler(Looper.getMainLooper());
    private float mAnimatedFraction;
    private long mDuration = 200;
    private final float[] mFloatValues = new float[2];
    private final int[] mIntValues = new int[2];
    private Interpolator mInterpolator;
    private boolean mIsRunning;
    private ArrayList<AnimatorListenerProxy> mListeners;
    private final Runnable mRunnable = new Runnable() {
        public final void run() {
            ValueAnimatorCompatImplGingerbread.this.update();
        }
    };
    private long mStartTime;
    private ArrayList<AnimatorUpdateListenerProxy> mUpdateListeners;

    ValueAnimatorCompatImplGingerbread() {
    }

    public final void start() {
        if (!this.mIsRunning) {
            if (this.mInterpolator == null) {
                this.mInterpolator = new AccelerateDecelerateInterpolator();
            }
            this.mIsRunning = true;
            this.mAnimatedFraction = 0.0f;
            this.mStartTime = SystemClock.uptimeMillis();
            dispatchAnimationUpdate();
            dispatchAnimationStart();
            sHandler.postDelayed(this.mRunnable, 10);
        }
    }

    public final boolean isRunning() {
        return this.mIsRunning;
    }

    public final void setInterpolator(Interpolator interpolator) {
        this.mInterpolator = interpolator;
    }

    public final void addListener(AnimatorListenerProxy listener) {
        if (this.mListeners == null) {
            this.mListeners = new ArrayList();
        }
        this.mListeners.add(listener);
    }

    public final void addUpdateListener(AnimatorUpdateListenerProxy updateListener) {
        if (this.mUpdateListeners == null) {
            this.mUpdateListeners = new ArrayList();
        }
        this.mUpdateListeners.add(updateListener);
    }

    public final void setIntValues(int from, int to) {
        this.mIntValues[0] = from;
        this.mIntValues[1] = to;
    }

    public final int getAnimatedIntValue() {
        return AnimationUtils.lerp(this.mIntValues[0], this.mIntValues[1], this.mAnimatedFraction);
    }

    public final void setFloatValues(float from, float to) {
        this.mFloatValues[0] = from;
        this.mFloatValues[1] = to;
    }

    public final void setDuration(long duration) {
        this.mDuration = duration;
    }

    public final void cancel() {
        int i = 0;
        this.mIsRunning = false;
        sHandler.removeCallbacks(this.mRunnable);
        if (this.mListeners != null) {
            int size = this.mListeners.size();
            while (i < size) {
                this.mListeners.get(i);
                i++;
            }
        }
        dispatchAnimationEnd();
    }

    public final float getAnimatedFraction() {
        return this.mAnimatedFraction;
    }

    public final void end() {
        if (this.mIsRunning) {
            this.mIsRunning = false;
            sHandler.removeCallbacks(this.mRunnable);
            this.mAnimatedFraction = 1.0f;
            dispatchAnimationUpdate();
            dispatchAnimationEnd();
        }
    }

    public final long getDuration() {
        return this.mDuration;
    }

    final void update() {
        float linearFraction = 0.0f;
        if (this.mIsRunning) {
            float uptimeMillis = ((float) (SystemClock.uptimeMillis() - this.mStartTime)) / ((float) this.mDuration);
            if (uptimeMillis >= 0.0f) {
                linearFraction = uptimeMillis > 1.0f ? 1.0f : uptimeMillis;
            }
            if (this.mInterpolator != null) {
                linearFraction = this.mInterpolator.getInterpolation(linearFraction);
            }
            this.mAnimatedFraction = linearFraction;
            dispatchAnimationUpdate();
            if (SystemClock.uptimeMillis() >= this.mStartTime + this.mDuration) {
                this.mIsRunning = false;
                dispatchAnimationEnd();
            }
        }
        if (this.mIsRunning) {
            sHandler.postDelayed(this.mRunnable, 10);
        }
    }

    private void dispatchAnimationUpdate() {
        if (this.mUpdateListeners != null) {
            int count = this.mUpdateListeners.size();
            for (int i = 0; i < count; i++) {
                ((AnimatorUpdateListenerProxy) this.mUpdateListeners.get(i)).onAnimationUpdate();
            }
        }
    }

    private void dispatchAnimationStart() {
        if (this.mListeners != null) {
            int count = this.mListeners.size();
            for (int i = 0; i < count; i++) {
                this.mListeners.get(i);
            }
        }
    }

    private void dispatchAnimationEnd() {
        if (this.mListeners != null) {
            int count = this.mListeners.size();
            for (int i = 0; i < count; i++) {
                ((AnimatorListenerProxy) this.mListeners.get(i)).onAnimationEnd();
            }
        }
    }
}
