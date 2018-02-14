package android.support.v4.animation;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.TargetApi;
import android.view.View;

@TargetApi(12)
final class HoneycombMr1AnimatorCompatProvider implements AnimatorProvider {
    private TimeInterpolator mDefaultInterpolator;

    static class AnimatorListenerCompatWrapper implements AnimatorListener {
        final ValueAnimatorCompat mValueAnimatorCompat;
        final AnimatorListenerCompat mWrapped;

        public AnimatorListenerCompatWrapper(AnimatorListenerCompat wrapped, ValueAnimatorCompat valueAnimatorCompat) {
            this.mWrapped = wrapped;
            this.mValueAnimatorCompat = valueAnimatorCompat;
        }

        public final void onAnimationStart(Animator animation) {
            this.mWrapped.onAnimationStart(this.mValueAnimatorCompat);
        }

        public final void onAnimationEnd(Animator animation) {
            this.mWrapped.onAnimationEnd(this.mValueAnimatorCompat);
        }

        public final void onAnimationCancel(Animator animation) {
            this.mWrapped.onAnimationCancel(this.mValueAnimatorCompat);
        }

        public final void onAnimationRepeat(Animator animation) {
            this.mWrapped.onAnimationRepeat(this.mValueAnimatorCompat);
        }
    }

    static class HoneycombValueAnimatorCompat implements ValueAnimatorCompat {
        final Animator mWrapped;

        public HoneycombValueAnimatorCompat(Animator wrapped) {
            this.mWrapped = wrapped;
        }

        public final void setTarget(View view) {
            this.mWrapped.setTarget(view);
        }

        public final void addListener(AnimatorListenerCompat listener) {
            this.mWrapped.addListener(new AnimatorListenerCompatWrapper(listener, this));
        }

        public final void setDuration(long duration) {
            this.mWrapped.setDuration(duration);
        }

        public final void start() {
            this.mWrapped.start();
        }

        public final void cancel() {
            this.mWrapped.cancel();
        }

        public final void addUpdateListener(final AnimatorUpdateListenerCompat animatorUpdateListener) {
            if (this.mWrapped instanceof ValueAnimator) {
                ((ValueAnimator) this.mWrapped).addUpdateListener(new AnimatorUpdateListener() {
                    public final void onAnimationUpdate(ValueAnimator animation) {
                        animatorUpdateListener.onAnimationUpdate(HoneycombValueAnimatorCompat.this);
                    }
                });
            }
        }

        public final float getAnimatedFraction() {
            return ((ValueAnimator) this.mWrapped).getAnimatedFraction();
        }
    }

    HoneycombMr1AnimatorCompatProvider() {
    }

    public final ValueAnimatorCompat emptyValueAnimator() {
        return new HoneycombValueAnimatorCompat(ValueAnimator.ofFloat(new float[]{0.0f, 1.0f}));
    }

    public final void clearInterpolator(View view) {
        if (this.mDefaultInterpolator == null) {
            this.mDefaultInterpolator = new ValueAnimator().getInterpolator();
        }
        view.animate().setInterpolator(this.mDefaultInterpolator);
    }
}
