package android.support.design.widget;

import android.support.design.widget.CoordinatorLayout.Behavior;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.support.v4.widget.ViewDragHelper.Callback;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;

public class SwipeDismissBehavior<V extends View> extends Behavior<V> {
    float mAlphaEndSwipeDistance = 0.5f;
    float mAlphaStartSwipeDistance = 0.0f;
    private final Callback mDragCallback = new Callback() {
        private int mActivePointerId = -1;
        private int mOriginalCapturedViewLeft;

        public final boolean tryCaptureView(View child, int pointerId) {
            return this.mActivePointerId == -1 && SwipeDismissBehavior.this.canSwipeDismissView(child);
        }

        public final void onViewCaptured(View capturedChild, int activePointerId) {
            this.mActivePointerId = activePointerId;
            this.mOriginalCapturedViewLeft = capturedChild.getLeft();
            ViewParent parent = capturedChild.getParent();
            if (parent != null) {
                parent.requestDisallowInterceptTouchEvent(true);
            }
        }

        public final void onViewDragStateChanged(int state) {
            if (SwipeDismissBehavior.this.mListener != null) {
                SwipeDismissBehavior.this.mListener.onDragStateChanged(state);
            }
        }

        public final void onViewReleased(View child, float xvel, float yvel) {
            int targetLeft;
            Object obj = 1;
            this.mActivePointerId = -1;
            int childWidth = child.getWidth();
            boolean dismiss = false;
            if (xvel != 0.0f) {
                Object obj2;
                if (ViewCompat.getLayoutDirection(child) == 1) {
                    obj2 = 1;
                } else {
                    obj2 = null;
                }
                if (SwipeDismissBehavior.this.mSwipeDirection != 2) {
                    if (SwipeDismissBehavior.this.mSwipeDirection == 0) {
                        if (obj2 != null) {
                            if (xvel >= 0.0f) {
                                obj = null;
                            }
                        } else if (xvel <= 0.0f) {
                            obj = null;
                        }
                    } else if (SwipeDismissBehavior.this.mSwipeDirection != 1) {
                        obj = null;
                    } else if (obj2 != null) {
                        if (xvel <= 0.0f) {
                            obj = null;
                        }
                    } else if (xvel >= 0.0f) {
                        obj = null;
                    }
                }
            } else {
                if (Math.abs(child.getLeft() - this.mOriginalCapturedViewLeft) < Math.round(((float) child.getWidth()) * SwipeDismissBehavior.this.mDragDismissThreshold)) {
                    obj = null;
                }
            }
            if (obj != null) {
                targetLeft = child.getLeft() < this.mOriginalCapturedViewLeft ? this.mOriginalCapturedViewLeft - childWidth : this.mOriginalCapturedViewLeft + childWidth;
                dismiss = true;
            } else {
                targetLeft = this.mOriginalCapturedViewLeft;
            }
            if (SwipeDismissBehavior.this.mViewDragHelper.settleCapturedViewAt(targetLeft, child.getTop())) {
                ViewCompat.postOnAnimation(child, new SettleRunnable(child, dismiss));
            } else if (dismiss && SwipeDismissBehavior.this.mListener != null) {
                SwipeDismissBehavior.this.mListener.onDismiss(child);
            }
        }

        public final int getViewHorizontalDragRange(View child) {
            return child.getWidth();
        }

        public final int clampViewPositionHorizontal$17e143b0(View child, int left) {
            int min;
            int max;
            boolean isRtl = ViewCompat.getLayoutDirection(child) == 1;
            if (SwipeDismissBehavior.this.mSwipeDirection == 0) {
                if (isRtl) {
                    min = this.mOriginalCapturedViewLeft - child.getWidth();
                    max = this.mOriginalCapturedViewLeft;
                } else {
                    min = this.mOriginalCapturedViewLeft;
                    max = this.mOriginalCapturedViewLeft + child.getWidth();
                }
            } else if (SwipeDismissBehavior.this.mSwipeDirection != 1) {
                min = this.mOriginalCapturedViewLeft - child.getWidth();
                max = this.mOriginalCapturedViewLeft + child.getWidth();
            } else if (isRtl) {
                min = this.mOriginalCapturedViewLeft;
                max = this.mOriginalCapturedViewLeft + child.getWidth();
            } else {
                min = this.mOriginalCapturedViewLeft - child.getWidth();
                max = this.mOriginalCapturedViewLeft;
            }
            return SwipeDismissBehavior.clamp(min, left, max);
        }

        public final int clampViewPositionVertical$17e143b0(View child, int top) {
            return child.getTop();
        }

        public final void onViewPositionChanged$5b6f797d(View child, int left, int top) {
            float startAlphaDistance = ((float) this.mOriginalCapturedViewLeft) + (((float) child.getWidth()) * SwipeDismissBehavior.this.mAlphaStartSwipeDistance);
            float endAlphaDistance = ((float) this.mOriginalCapturedViewLeft) + (((float) child.getWidth()) * SwipeDismissBehavior.this.mAlphaEndSwipeDistance);
            if (((float) left) <= startAlphaDistance) {
                ViewCompat.setAlpha(child, 1.0f);
            } else if (((float) left) >= endAlphaDistance) {
                ViewCompat.setAlpha(child, 0.0f);
            } else {
                ViewCompat.setAlpha(child, SwipeDismissBehavior.clamp(0.0f, 1.0f - SwipeDismissBehavior.fraction(startAlphaDistance, endAlphaDistance, (float) left), 1.0f));
            }
        }
    };
    float mDragDismissThreshold = 0.5f;
    private boolean mInterceptingEvents;
    OnDismissListener mListener;
    private float mSensitivity = 0.0f;
    int mSwipeDirection = 2;
    ViewDragHelper mViewDragHelper;

    public interface OnDismissListener {
        void onDismiss(View view);

        void onDragStateChanged(int i);
    }

    private class SettleRunnable implements Runnable {
        private final boolean mDismiss;
        private final View mView;

        SettleRunnable(View view, boolean dismiss) {
            this.mView = view;
            this.mDismiss = dismiss;
        }

        public final void run() {
            if (SwipeDismissBehavior.this.mViewDragHelper != null && SwipeDismissBehavior.this.mViewDragHelper.continueSettling(true)) {
                ViewCompat.postOnAnimation(this.mView, this);
            } else if (this.mDismiss && SwipeDismissBehavior.this.mListener != null) {
                SwipeDismissBehavior.this.mListener.onDismiss(this.mView);
            }
        }
    }

    public boolean onInterceptTouchEvent(CoordinatorLayout parent, V child, MotionEvent event) {
        boolean dispatchEventToHelper = this.mInterceptingEvents;
        switch (MotionEventCompat.getActionMasked(event)) {
            case 0:
                this.mInterceptingEvents = parent.isPointInChildBounds(child, (int) event.getX(), (int) event.getY());
                dispatchEventToHelper = this.mInterceptingEvents;
                break;
            case 1:
            case 3:
                this.mInterceptingEvents = false;
                break;
        }
        if (!dispatchEventToHelper) {
            return false;
        }
        if (this.mViewDragHelper == null) {
            this.mViewDragHelper = ViewDragHelper.create(parent, this.mDragCallback);
        }
        return this.mViewDragHelper.shouldInterceptTouchEvent(event);
    }

    public final boolean onTouchEvent(CoordinatorLayout parent, V v, MotionEvent event) {
        if (this.mViewDragHelper == null) {
            return false;
        }
        this.mViewDragHelper.processTouchEvent(event);
        return true;
    }

    public boolean canSwipeDismissView(View view) {
        return true;
    }

    static float clamp(float min, float value, float max) {
        return Math.min(Math.max(0.0f, value), 1.0f);
    }

    static int clamp(int min, int value, int max) {
        return Math.min(Math.max(min, value), max);
    }

    static float fraction(float startValue, float endValue, float value) {
        return (value - startValue) / (endValue - startValue);
    }
}
