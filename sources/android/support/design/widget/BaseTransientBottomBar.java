package android.support.design.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.support.design.R;
import android.support.design.widget.SwipeDismissBehavior.OnDismissListener;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

public abstract class BaseTransientBottomBar<B extends BaseTransientBottomBar<B>> {
    static final Handler sHandler = new Handler(Looper.getMainLooper(), new Callback() {
        public final boolean handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    ((BaseTransientBottomBar) message.obj).showView();
                    return true;
                case 1:
                    ((BaseTransientBottomBar) message.obj).hideView(message.arg1);
                    return true;
                default:
                    return false;
            }
        }
    });
    private final AccessibilityManager mAccessibilityManager;
    private final ContentViewCallback mContentViewCallback;
    final Callback mManagerCallback;
    private final ViewGroup mTargetParent;
    final SnackbarBaseLayout mView;

    public interface ContentViewCallback {
        void animateContentIn(int i, int i2);

        void animateContentOut(int i, int i2);
    }

    interface OnAttachStateChangeListener {
        void onViewDetachedFromWindow$3c7ec8c3();
    }

    interface OnLayoutChangeListener {
        void onLayoutChange$5b6f797d();
    }

    final class Behavior extends SwipeDismissBehavior<SnackbarBaseLayout> {
        Behavior() {
        }

        public final /* bridge */ /* synthetic */ boolean onInterceptTouchEvent(CoordinatorLayout coordinatorLayout, View view, MotionEvent motionEvent) {
            SnackbarBaseLayout snackbarBaseLayout = (SnackbarBaseLayout) view;
            switch (motionEvent.getActionMasked()) {
                case 0:
                    if (coordinatorLayout.isPointInChildBounds(snackbarBaseLayout, (int) motionEvent.getX(), (int) motionEvent.getY())) {
                        SnackbarManager.getInstance().pauseTimeout(BaseTransientBottomBar.this.mManagerCallback);
                        break;
                    }
                    break;
                case 1:
                case 3:
                    SnackbarManager.getInstance().restoreTimeoutIfPaused(BaseTransientBottomBar.this.mManagerCallback);
                    break;
            }
            return super.onInterceptTouchEvent(coordinatorLayout, snackbarBaseLayout, motionEvent);
        }

        public final boolean canSwipeDismissView(View child) {
            return child instanceof SnackbarBaseLayout;
        }
    }

    static class SnackbarBaseLayout extends FrameLayout {
        private OnAttachStateChangeListener mOnAttachStateChangeListener;
        private OnLayoutChangeListener mOnLayoutChangeListener;

        SnackbarBaseLayout(Context context) {
            this(context, null);
        }

        SnackbarBaseLayout(Context context, AttributeSet attrs) {
            super(context, attrs);
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SnackbarLayout);
            if (a.hasValue(R.styleable.SnackbarLayout_elevation)) {
                ViewCompat.setElevation(this, (float) a.getDimensionPixelSize(R.styleable.SnackbarLayout_elevation, 0));
            }
            a.recycle();
            setClickable(true);
        }

        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            super.onLayout(changed, l, t, r, b);
            if (this.mOnLayoutChangeListener != null) {
                this.mOnLayoutChangeListener.onLayoutChange$5b6f797d();
            }
        }

        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            ViewCompat.requestApplyInsets(this);
        }

        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            if (this.mOnAttachStateChangeListener != null) {
                this.mOnAttachStateChangeListener.onViewDetachedFromWindow$3c7ec8c3();
            }
        }

        final void setOnLayoutChangeListener(OnLayoutChangeListener onLayoutChangeListener) {
            this.mOnLayoutChangeListener = onLayoutChangeListener;
        }

        final void setOnAttachStateChangeListener(OnAttachStateChangeListener listener) {
            this.mOnAttachStateChangeListener = listener;
        }
    }

    final void showView() {
        if (this.mView.getParent() == null) {
            LayoutParams lp = this.mView.getLayoutParams();
            if (lp instanceof CoordinatorLayout.LayoutParams) {
                CoordinatorLayout.LayoutParams clp = (CoordinatorLayout.LayoutParams) lp;
                Behavior behavior = new Behavior();
                behavior.mAlphaStartSwipeDistance = SwipeDismissBehavior.clamp(0.0f, 0.1f, 1.0f);
                behavior.mAlphaEndSwipeDistance = SwipeDismissBehavior.clamp(0.0f, 0.6f, 1.0f);
                behavior.mSwipeDirection = 0;
                behavior.mListener = new OnDismissListener() {
                    public final void onDismiss(View view) {
                        view.setVisibility(8);
                        SnackbarManager.getInstance().dismiss(BaseTransientBottomBar.this.mManagerCallback, 0);
                    }

                    public final void onDragStateChanged(int state) {
                        switch (state) {
                            case 0:
                                SnackbarManager.getInstance().restoreTimeoutIfPaused(BaseTransientBottomBar.this.mManagerCallback);
                                return;
                            case 1:
                            case 2:
                                SnackbarManager.getInstance().pauseTimeout(BaseTransientBottomBar.this.mManagerCallback);
                                return;
                            default:
                                return;
                        }
                    }
                };
                clp.setBehavior(behavior);
                clp.insetEdge = 80;
            }
            this.mTargetParent.addView(this.mView);
        }
        this.mView.setOnAttachStateChangeListener(new OnAttachStateChangeListener() {
            public final void onViewDetachedFromWindow$3c7ec8c3() {
                if (SnackbarManager.getInstance().isCurrentOrNext(BaseTransientBottomBar.this.mManagerCallback)) {
                    BaseTransientBottomBar.sHandler.post(new Runnable() {
                        public final void run() {
                            BaseTransientBottomBar.this.onViewHidden(3);
                        }
                    });
                }
            }
        });
        if (!ViewCompat.isLaidOut(this.mView)) {
            this.mView.setOnLayoutChangeListener(new OnLayoutChangeListener() {
                public final void onLayoutChange$5b6f797d() {
                    BaseTransientBottomBar.this.mView.setOnLayoutChangeListener(null);
                    if (BaseTransientBottomBar.this.shouldAnimate()) {
                        BaseTransientBottomBar.this.animateViewIn();
                    } else {
                        BaseTransientBottomBar.this.onViewShown();
                    }
                }
            });
        } else if (shouldAnimate()) {
            animateViewIn();
        } else {
            onViewShown();
        }
    }

    final void animateViewIn() {
        if (VERSION.SDK_INT >= 14) {
            ViewCompat.setTranslationY(this.mView, (float) this.mView.getHeight());
            ViewCompat.animate(this.mView).translationY(0.0f).setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR).setDuration(250).setListener(new ViewPropertyAnimatorListenerAdapter() {
                public final void onAnimationStart(View view) {
                    BaseTransientBottomBar.this.mContentViewCallback.animateContentIn(70, 180);
                }

                public final void onAnimationEnd(View view) {
                    BaseTransientBottomBar.this.onViewShown();
                }
            }).start();
            return;
        }
        Animation anim = AnimationUtils.loadAnimation(this.mView.getContext(), com.rachio.iro.R.anim.design_snackbar_in);
        anim.setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);
        anim.setDuration(250);
        anim.setAnimationListener(new AnimationListener() {
            public final void onAnimationEnd(Animation animation) {
                BaseTransientBottomBar.this.onViewShown();
            }

            public final void onAnimationStart(Animation animation) {
            }

            public final void onAnimationRepeat(Animation animation) {
            }
        });
        this.mView.startAnimation(anim);
    }

    final void hideView(final int event) {
        if (!shouldAnimate() || this.mView.getVisibility() != 0) {
            onViewHidden(event);
        } else if (VERSION.SDK_INT >= 14) {
            ViewCompat.animate(this.mView).translationY((float) this.mView.getHeight()).setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR).setDuration(250).setListener(new ViewPropertyAnimatorListenerAdapter() {
                public final void onAnimationStart(View view) {
                    BaseTransientBottomBar.this.mContentViewCallback.animateContentOut(0, 180);
                }

                public final void onAnimationEnd(View view) {
                    BaseTransientBottomBar.this.onViewHidden(event);
                }
            }).start();
        } else {
            Animation loadAnimation = AnimationUtils.loadAnimation(this.mView.getContext(), com.rachio.iro.R.anim.design_snackbar_out);
            loadAnimation.setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);
            loadAnimation.setDuration(250);
            loadAnimation.setAnimationListener(new AnimationListener() {
                public final void onAnimationEnd(Animation animation) {
                    BaseTransientBottomBar.this.onViewHidden(event);
                }

                public final void onAnimationStart(Animation animation) {
                }

                public final void onAnimationRepeat(Animation animation) {
                }
            });
            this.mView.startAnimation(loadAnimation);
        }
    }

    final void onViewShown() {
        SnackbarManager.getInstance().onShown(this.mManagerCallback);
    }

    final void onViewHidden(int event) {
        SnackbarManager.getInstance().onDismissed(this.mManagerCallback);
        if (VERSION.SDK_INT < 11) {
            this.mView.setVisibility(8);
        }
        ViewParent parent = this.mView.getParent();
        if (parent instanceof ViewGroup) {
            ((ViewGroup) parent).removeView(this.mView);
        }
    }

    final boolean shouldAnimate() {
        return !this.mAccessibilityManager.isEnabled();
    }
}
