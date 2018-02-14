package android.support.design.widget;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.animation.Interpolator;

abstract class FloatingActionButtonImpl {
    static final Interpolator ANIM_INTERPOLATOR = AnimationUtils.FAST_OUT_LINEAR_IN_INTERPOLATOR;
    static final int[] EMPTY_STATE_SET = new int[0];
    static final int[] ENABLED_STATE_SET = new int[]{16842910};
    static final int[] FOCUSED_ENABLED_STATE_SET = new int[]{16842908, 16842910};
    static final int[] PRESSED_ENABLED_STATE_SET = new int[]{16842919, 16842910};
    int mAnimState = 0;
    final Creator mAnimatorCreator;
    float mElevation;
    private OnPreDrawListener mPreDrawListener;
    float mPressedTranslationZ;
    final ShadowViewDelegate mShadowViewDelegate;
    private final Rect mTmpRect = new Rect();
    final VisibilityAwareImageButton mView;

    interface InternalVisibilityChangedListener {
    }

    abstract void getPadding(Rect rect);

    abstract void hide(InternalVisibilityChangedListener internalVisibilityChangedListener, boolean z);

    abstract void jumpDrawableToCurrentState();

    abstract void onDrawableStateChanged(int[] iArr);

    abstract void setBackgroundTintList(ColorStateList colorStateList);

    abstract void setBackgroundTintMode(Mode mode);

    abstract void show(InternalVisibilityChangedListener internalVisibilityChangedListener, boolean z);

    FloatingActionButtonImpl(VisibilityAwareImageButton view, ShadowViewDelegate shadowViewDelegate, Creator animatorCreator) {
        this.mView = view;
        this.mShadowViewDelegate = shadowViewDelegate;
        this.mAnimatorCreator = animatorCreator;
    }

    final void updatePadding() {
        Rect rect = this.mTmpRect;
        getPadding(rect);
        onPaddingUpdated(rect);
        this.mShadowViewDelegate.setShadowPadding(rect.left, rect.top, rect.right, rect.bottom);
    }

    void onPaddingUpdated(Rect padding) {
    }

    final void onAttachedToWindow() {
        if (requirePreDrawListener()) {
            if (this.mPreDrawListener == null) {
                this.mPreDrawListener = new OnPreDrawListener() {
                    public final boolean onPreDraw() {
                        FloatingActionButtonImpl.this.onPreDraw();
                        return true;
                    }
                };
            }
            this.mView.getViewTreeObserver().addOnPreDrawListener(this.mPreDrawListener);
        }
    }

    final void onDetachedFromWindow() {
        if (this.mPreDrawListener != null) {
            this.mView.getViewTreeObserver().removeOnPreDrawListener(this.mPreDrawListener);
            this.mPreDrawListener = null;
        }
    }

    boolean requirePreDrawListener() {
        return false;
    }

    void onPreDraw() {
    }

    final boolean isOrWillBeShown() {
        if (this.mView.getVisibility() != 0) {
            if (this.mAnimState == 2) {
                return true;
            }
            return false;
        } else if (this.mAnimState == 1) {
            return false;
        } else {
            return true;
        }
    }

    final boolean isOrWillBeHidden() {
        if (this.mView.getVisibility() == 0) {
            if (this.mAnimState == 1) {
                return true;
            }
            return false;
        } else if (this.mAnimState == 2) {
            return false;
        } else {
            return true;
        }
    }
}
