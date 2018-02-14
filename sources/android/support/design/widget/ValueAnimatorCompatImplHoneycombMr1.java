package android.support.design.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.TargetApi;
import android.view.animation.Interpolator;

@TargetApi(12)
final class ValueAnimatorCompatImplHoneycombMr1 extends Impl {
    private final ValueAnimator mValueAnimator = new ValueAnimator();

    ValueAnimatorCompatImplHoneycombMr1() {
    }

    public final void start() {
        this.mValueAnimator.start();
    }

    public final boolean isRunning() {
        return this.mValueAnimator.isRunning();
    }

    public final void setInterpolator(Interpolator interpolator) {
        this.mValueAnimator.setInterpolator(interpolator);
    }

    public final void addUpdateListener(final AnimatorUpdateListenerProxy updateListener) {
        this.mValueAnimator.addUpdateListener(new AnimatorUpdateListener() {
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                updateListener.onAnimationUpdate();
            }
        });
    }

    public final void addListener(final AnimatorListenerProxy listener) {
        this.mValueAnimator.addListener(new AnimatorListenerAdapter() {
            public final void onAnimationStart(Animator animator) {
            }

            public final void onAnimationEnd(Animator animator) {
                listener.onAnimationEnd();
            }

            public final void onAnimationCancel(Animator animator) {
            }
        });
    }

    public final void setIntValues(int from, int to) {
        this.mValueAnimator.setIntValues(new int[]{from, to});
    }

    public final int getAnimatedIntValue() {
        return ((Integer) this.mValueAnimator.getAnimatedValue()).intValue();
    }

    public final void setFloatValues(float from, float to) {
        this.mValueAnimator.setFloatValues(new float[]{from, to});
    }

    public final void setDuration(long duration) {
        this.mValueAnimator.setDuration(duration);
    }

    public final void cancel() {
        this.mValueAnimator.cancel();
    }

    public final float getAnimatedFraction() {
        return this.mValueAnimator.getAnimatedFraction();
    }

    public final void end() {
        this.mValueAnimator.end();
    }

    public final long getDuration() {
        return this.mValueAnimator.getDuration();
    }
}
