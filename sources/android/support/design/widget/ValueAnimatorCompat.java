package android.support.design.widget;

import android.view.animation.Interpolator;

final class ValueAnimatorCompat {
    private final Impl mImpl;

    interface AnimatorUpdateListener {
        void onAnimationUpdate(ValueAnimatorCompat valueAnimatorCompat);
    }

    interface AnimatorListener {
        void onAnimationEnd(ValueAnimatorCompat valueAnimatorCompat);
    }

    static class AnimatorListenerAdapter implements AnimatorListener {
        AnimatorListenerAdapter() {
        }

        public void onAnimationEnd(ValueAnimatorCompat animator) {
        }
    }

    interface Creator {
        ValueAnimatorCompat createAnimator();
    }

    static abstract class Impl {

        interface AnimatorUpdateListenerProxy {
            void onAnimationUpdate();
        }

        interface AnimatorListenerProxy {
            void onAnimationEnd();
        }

        abstract void addListener(AnimatorListenerProxy animatorListenerProxy);

        abstract void addUpdateListener(AnimatorUpdateListenerProxy animatorUpdateListenerProxy);

        abstract void cancel();

        abstract void end();

        abstract float getAnimatedFraction();

        abstract int getAnimatedIntValue();

        abstract long getDuration();

        abstract boolean isRunning();

        abstract void setDuration(long j);

        abstract void setFloatValues(float f, float f2);

        abstract void setIntValues(int i, int i2);

        abstract void setInterpolator(Interpolator interpolator);

        abstract void start();

        Impl() {
        }
    }

    ValueAnimatorCompat(Impl impl) {
        this.mImpl = impl;
    }

    public final void start() {
        this.mImpl.start();
    }

    public final boolean isRunning() {
        return this.mImpl.isRunning();
    }

    public final void setInterpolator(Interpolator interpolator) {
        this.mImpl.setInterpolator(interpolator);
    }

    public final void addUpdateListener(final AnimatorUpdateListener updateListener) {
        if (updateListener != null) {
            this.mImpl.addUpdateListener(new AnimatorUpdateListenerProxy() {
                public final void onAnimationUpdate() {
                    updateListener.onAnimationUpdate(ValueAnimatorCompat.this);
                }
            });
        } else {
            this.mImpl.addUpdateListener(null);
        }
    }

    public final void addListener(final AnimatorListener listener) {
        if (listener != null) {
            this.mImpl.addListener(new AnimatorListenerProxy() {
                public final void onAnimationEnd() {
                    listener.onAnimationEnd(ValueAnimatorCompat.this);
                }
            });
        } else {
            this.mImpl.addListener(null);
        }
    }

    public final void setIntValues(int from, int to) {
        this.mImpl.setIntValues(from, to);
    }

    public final int getAnimatedIntValue() {
        return this.mImpl.getAnimatedIntValue();
    }

    public final void setFloatValues(float from, float to) {
        this.mImpl.setFloatValues(0.0f, 1.0f);
    }

    public final void setDuration(long duration) {
        this.mImpl.setDuration(duration);
    }

    public final void cancel() {
        this.mImpl.cancel();
    }

    public final float getAnimatedFraction() {
        return this.mImpl.getAnimatedFraction();
    }

    public final void end() {
        this.mImpl.end();
    }

    public final long getDuration() {
        return this.mImpl.getDuration();
    }
}
