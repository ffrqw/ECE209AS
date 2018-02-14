package android.support.design.widget;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.rachio.iro.R;

class FloatingActionButtonGingerbread extends FloatingActionButtonImpl {
    ShadowDrawableWrapper mShadowDrawable;
    private final StateListAnimator mStateListAnimator = new StateListAnimator();

    private abstract class ShadowAnimatorImpl extends AnimatorListenerAdapter implements AnimatorUpdateListener {
        private float mShadowSizeEnd;
        private float mShadowSizeStart;
        private boolean mValidValues;

        protected abstract float getTargetShadowSize();

        private ShadowAnimatorImpl() {
        }

        public final void onAnimationUpdate(ValueAnimatorCompat animator) {
            if (!this.mValidValues) {
                this.mShadowSizeStart = FloatingActionButtonGingerbread.this.mShadowDrawable.mRawShadowSize;
                this.mShadowSizeEnd = getTargetShadowSize();
                this.mValidValues = true;
            }
            FloatingActionButtonGingerbread.this.mShadowDrawable.setShadowSize(this.mShadowSizeStart + ((this.mShadowSizeEnd - this.mShadowSizeStart) * animator.getAnimatedFraction()));
        }

        public final void onAnimationEnd(ValueAnimatorCompat animator) {
            FloatingActionButtonGingerbread.this.mShadowDrawable.setShadowSize(this.mShadowSizeEnd);
            this.mValidValues = false;
        }
    }

    private class DisabledElevationAnimation extends ShadowAnimatorImpl {
        DisabledElevationAnimation() {
            super();
        }

        protected final float getTargetShadowSize() {
            return 0.0f;
        }
    }

    private class ElevateToTranslationZAnimation extends ShadowAnimatorImpl {
        ElevateToTranslationZAnimation() {
            super();
        }

        protected final float getTargetShadowSize() {
            float f = FloatingActionButtonGingerbread.this.mElevation;
            f = FloatingActionButtonGingerbread.this.mPressedTranslationZ;
            return 0.0f;
        }
    }

    private class ResetElevationAnimation extends ShadowAnimatorImpl {
        ResetElevationAnimation() {
            super();
        }

        protected final float getTargetShadowSize() {
            return FloatingActionButtonGingerbread.this.mElevation;
        }
    }

    FloatingActionButtonGingerbread(VisibilityAwareImageButton view, ShadowViewDelegate shadowViewDelegate, Creator animatorCreator) {
        super(view, shadowViewDelegate, animatorCreator);
        this.mStateListAnimator.addState(PRESSED_ENABLED_STATE_SET, createAnimator(new ElevateToTranslationZAnimation()));
        this.mStateListAnimator.addState(FOCUSED_ENABLED_STATE_SET, createAnimator(new ElevateToTranslationZAnimation()));
        this.mStateListAnimator.addState(ENABLED_STATE_SET, createAnimator(new ResetElevationAnimation()));
        this.mStateListAnimator.addState(EMPTY_STATE_SET, createAnimator(new DisabledElevationAnimation()));
    }

    final void setBackgroundTintList(ColorStateList tint) {
    }

    final void setBackgroundTintMode(Mode tintMode) {
    }

    void onDrawableStateChanged(int[] state) {
        this.mStateListAnimator.setState(state);
    }

    void jumpDrawableToCurrentState() {
        StateListAnimator stateListAnimator = this.mStateListAnimator;
        if (stateListAnimator.mRunningAnimator != null) {
            stateListAnimator.mRunningAnimator.end();
            stateListAnimator.mRunningAnimator = null;
        }
    }

    void hide(final InternalVisibilityChangedListener listener, final boolean fromUser) {
        if (!isOrWillBeHidden()) {
            this.mAnimState = 1;
            Animation anim = AnimationUtils.loadAnimation(this.mView.getContext(), R.anim.design_fab_out);
            anim.setInterpolator(AnimationUtils.FAST_OUT_LINEAR_IN_INTERPOLATOR);
            anim.setDuration(200);
            anim.setAnimationListener(new AnimationListenerAdapter() {
                public final void onAnimationEnd(Animation animation) {
                    FloatingActionButtonGingerbread.this.mAnimState = 0;
                    FloatingActionButtonGingerbread.this.mView.internalSetVisibility(fromUser ? 8 : 4, fromUser);
                }
            });
            this.mView.startAnimation(anim);
        }
    }

    void show(final InternalVisibilityChangedListener listener, boolean fromUser) {
        if (!isOrWillBeShown()) {
            this.mAnimState = 2;
            this.mView.internalSetVisibility(0, fromUser);
            Animation anim = AnimationUtils.loadAnimation(this.mView.getContext(), R.anim.design_fab_in);
            anim.setDuration(200);
            anim.setInterpolator(AnimationUtils.LINEAR_OUT_SLOW_IN_INTERPOLATOR);
            anim.setAnimationListener(new AnimationListenerAdapter() {
                public final void onAnimationEnd(Animation animation) {
                    FloatingActionButtonGingerbread.this.mAnimState = 0;
                }
            });
            this.mView.startAnimation(anim);
        }
    }

    void getPadding(Rect rect) {
        ShadowDrawableWrapper shadowDrawableWrapper = null;
        shadowDrawableWrapper.getPadding(rect);
    }

    private ValueAnimatorCompat createAnimator(ShadowAnimatorImpl impl) {
        ValueAnimatorCompat animator = this.mAnimatorCreator.createAnimator();
        animator.setInterpolator(ANIM_INTERPOLATOR);
        animator.setDuration(100);
        animator.addListener(impl);
        animator.addUpdateListener(impl);
        animator.setFloatValues(0.0f, 1.0f);
        return animator;
    }
}
