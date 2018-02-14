package android.support.v4.animation;

import android.annotation.TargetApi;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

@TargetApi(9)
final class GingerbreadAnimatorCompatProvider implements AnimatorProvider {

    private static class GingerbreadFloatValueAnimator implements ValueAnimatorCompat {
        private long mDuration = 200;
        private boolean mEnded = false;
        private float mFraction = 0.0f;
        List<AnimatorListenerCompat> mListeners = new ArrayList();
        private Runnable mLoopRunnable = new Runnable() {
            public final void run() {
                float fraction = ((float) (GingerbreadFloatValueAnimator.this.mTarget.getDrawingTime() - GingerbreadFloatValueAnimator.this.mStartTime)) / ((float) GingerbreadFloatValueAnimator.this.mDuration);
                if (fraction > 1.0f || GingerbreadFloatValueAnimator.this.mTarget.getParent() == null) {
                    fraction = 1.0f;
                }
                GingerbreadFloatValueAnimator.this.mFraction = fraction;
                GingerbreadFloatValueAnimator.access$400(GingerbreadFloatValueAnimator.this);
                if (GingerbreadFloatValueAnimator.this.mFraction >= 1.0f) {
                    GingerbreadFloatValueAnimator.this.dispatchEnd();
                } else {
                    GingerbreadFloatValueAnimator.this.mTarget.postDelayed(GingerbreadFloatValueAnimator.this.mLoopRunnable, 16);
                }
            }
        };
        private long mStartTime;
        private boolean mStarted = false;
        View mTarget;
        List<AnimatorUpdateListenerCompat> mUpdateListeners = new ArrayList();

        public final void setTarget(View view) {
            this.mTarget = view;
        }

        public final void addListener(AnimatorListenerCompat listener) {
            this.mListeners.add(listener);
        }

        public final void setDuration(long duration) {
            if (!this.mStarted) {
                this.mDuration = duration;
            }
        }

        public final void start() {
            if (!this.mStarted) {
                this.mStarted = true;
                for (int size = this.mListeners.size() - 1; size >= 0; size--) {
                    ((AnimatorListenerCompat) this.mListeners.get(size)).onAnimationStart(this);
                }
                this.mFraction = 0.0f;
                this.mStartTime = this.mTarget.getDrawingTime();
                this.mTarget.postDelayed(this.mLoopRunnable, 16);
            }
        }

        private void dispatchEnd() {
            for (int i = this.mListeners.size() - 1; i >= 0; i--) {
                ((AnimatorListenerCompat) this.mListeners.get(i)).onAnimationEnd(this);
            }
        }

        public final void cancel() {
            if (!this.mEnded) {
                this.mEnded = true;
                if (this.mStarted) {
                    for (int size = this.mListeners.size() - 1; size >= 0; size--) {
                        ((AnimatorListenerCompat) this.mListeners.get(size)).onAnimationCancel(this);
                    }
                }
                dispatchEnd();
            }
        }

        public final void addUpdateListener(AnimatorUpdateListenerCompat animatorUpdateListener) {
            this.mUpdateListeners.add(animatorUpdateListener);
        }

        public final float getAnimatedFraction() {
            return this.mFraction;
        }

        static /* synthetic */ void access$400(GingerbreadFloatValueAnimator x0) {
            for (int size = x0.mUpdateListeners.size() - 1; size >= 0; size--) {
                ((AnimatorUpdateListenerCompat) x0.mUpdateListeners.get(size)).onAnimationUpdate(x0);
            }
        }
    }

    GingerbreadAnimatorCompatProvider() {
    }

    public final ValueAnimatorCompat emptyValueAnimator() {
        return new GingerbreadFloatValueAnimator();
    }

    public final void clearInterpolator(View view) {
    }
}
