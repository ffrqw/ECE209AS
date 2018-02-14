package android.support.design.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build.VERSION;
import android.support.v4.view.ViewCompat;

@TargetApi(14)
class FloatingActionButtonIcs extends FloatingActionButtonGingerbread {
    private float mRotation = this.mView.getRotation();

    FloatingActionButtonIcs(VisibilityAwareImageButton view, ShadowViewDelegate shadowViewDelegate, Creator animatorCreator) {
        super(view, shadowViewDelegate, animatorCreator);
    }

    boolean requirePreDrawListener() {
        return true;
    }

    final void onPreDraw() {
        float rotation = this.mView.getRotation();
        if (this.mRotation != rotation) {
            this.mRotation = rotation;
            if (VERSION.SDK_INT != 19) {
                return;
            }
            if (this.mRotation % 90.0f != 0.0f) {
                if (this.mView.getLayerType() != 1) {
                    this.mView.setLayerType(1, null);
                }
            } else if (this.mView.getLayerType() != 0) {
                this.mView.setLayerType(0, null);
            }
        }
    }

    final void hide(final InternalVisibilityChangedListener listener, final boolean fromUser) {
        if (!isOrWillBeHidden()) {
            this.mView.animate().cancel();
            if (shouldAnimateVisibilityChange()) {
                this.mAnimState = 1;
                this.mView.animate().scaleX(0.0f).scaleY(0.0f).alpha(0.0f).setDuration(200).setInterpolator(AnimationUtils.FAST_OUT_LINEAR_IN_INTERPOLATOR).setListener(new AnimatorListenerAdapter() {
                    private boolean mCancelled;

                    public final void onAnimationStart(Animator animation) {
                        FloatingActionButtonIcs.this.mView.internalSetVisibility(0, fromUser);
                        this.mCancelled = false;
                    }

                    public final void onAnimationCancel(Animator animation) {
                        this.mCancelled = true;
                    }

                    public final void onAnimationEnd(Animator animation) {
                        FloatingActionButtonIcs.this.mAnimState = 0;
                        if (!this.mCancelled) {
                            FloatingActionButtonIcs.this.mView.internalSetVisibility(fromUser ? 8 : 4, fromUser);
                        }
                    }
                });
                return;
            }
            this.mView.internalSetVisibility(fromUser ? 8 : 4, fromUser);
        }
    }

    final void show(final InternalVisibilityChangedListener listener, final boolean fromUser) {
        if (!isOrWillBeShown()) {
            this.mView.animate().cancel();
            if (shouldAnimateVisibilityChange()) {
                this.mAnimState = 2;
                if (this.mView.getVisibility() != 0) {
                    this.mView.setAlpha(0.0f);
                    this.mView.setScaleY(0.0f);
                    this.mView.setScaleX(0.0f);
                }
                this.mView.animate().scaleX(1.0f).scaleY(1.0f).alpha(1.0f).setDuration(200).setInterpolator(AnimationUtils.LINEAR_OUT_SLOW_IN_INTERPOLATOR).setListener(new AnimatorListenerAdapter() {
                    public final void onAnimationStart(Animator animation) {
                        FloatingActionButtonIcs.this.mView.internalSetVisibility(0, fromUser);
                    }

                    public final void onAnimationEnd(Animator animation) {
                        FloatingActionButtonIcs.this.mAnimState = 0;
                    }
                });
                return;
            }
            this.mView.internalSetVisibility(0, fromUser);
            this.mView.setAlpha(1.0f);
            this.mView.setScaleY(1.0f);
            this.mView.setScaleX(1.0f);
        }
    }

    private boolean shouldAnimateVisibilityChange() {
        return ViewCompat.isLaidOut(this.mView) && !this.mView.isInEditMode();
    }
}
