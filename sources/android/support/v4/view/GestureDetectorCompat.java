package android.support.v4.view;

import android.content.Context;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;

public final class GestureDetectorCompat {
    private final GestureDetectorCompatImpl mImpl;

    interface GestureDetectorCompatImpl {
        boolean onTouchEvent(MotionEvent motionEvent);
    }

    static class GestureDetectorCompatImplBase implements GestureDetectorCompatImpl {
        private static final int DOUBLE_TAP_TIMEOUT = ViewConfiguration.getDoubleTapTimeout();
        private static final int LONGPRESS_TIMEOUT = ViewConfiguration.getLongPressTimeout();
        private static final int TAP_TIMEOUT = ViewConfiguration.getTapTimeout();
        private boolean mAlwaysInBiggerTapRegion;
        private boolean mAlwaysInTapRegion;
        MotionEvent mCurrentDownEvent;
        boolean mDeferConfirmSingleTap;
        OnDoubleTapListener mDoubleTapListener;
        private int mDoubleTapSlopSquare;
        private float mDownFocusX;
        private float mDownFocusY;
        private final Handler mHandler;
        private boolean mInLongPress;
        private boolean mIsDoubleTapping;
        private boolean mIsLongpressEnabled;
        private float mLastFocusX;
        private float mLastFocusY;
        final OnGestureListener mListener;
        private int mMaximumFlingVelocity;
        private int mMinimumFlingVelocity;
        private MotionEvent mPreviousUpEvent;
        boolean mStillDown;
        private int mTouchSlopSquare;
        private VelocityTracker mVelocityTracker;

        private class GestureHandler extends Handler {
            GestureHandler() {
            }

            GestureHandler(Handler handler) {
                super(handler.getLooper());
            }

            public final void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        GestureDetectorCompatImplBase.this.mListener.onShowPress(GestureDetectorCompatImplBase.this.mCurrentDownEvent);
                        return;
                    case 2:
                        GestureDetectorCompatImplBase.this.dispatchLongPress();
                        return;
                    case 3:
                        if (GestureDetectorCompatImplBase.this.mDoubleTapListener == null) {
                            return;
                        }
                        if (GestureDetectorCompatImplBase.this.mStillDown) {
                            GestureDetectorCompatImplBase.this.mDeferConfirmSingleTap = true;
                            return;
                        } else {
                            GestureDetectorCompatImplBase.this.mDoubleTapListener.onSingleTapConfirmed(GestureDetectorCompatImplBase.this.mCurrentDownEvent);
                            return;
                        }
                    default:
                        throw new RuntimeException("Unknown message " + msg);
                }
            }
        }

        public GestureDetectorCompatImplBase(Context context, OnGestureListener listener, Handler handler) {
            if (handler != null) {
                this.mHandler = new GestureHandler(handler);
            } else {
                this.mHandler = new GestureHandler();
            }
            this.mListener = listener;
            if (listener instanceof OnDoubleTapListener) {
                this.mDoubleTapListener = (OnDoubleTapListener) listener;
            }
            if (context == null) {
                throw new IllegalArgumentException("Context must not be null");
            } else if (this.mListener == null) {
                throw new IllegalArgumentException("OnGestureListener must not be null");
            } else {
                this.mIsLongpressEnabled = true;
                ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
                int scaledTouchSlop = viewConfiguration.getScaledTouchSlop();
                int scaledDoubleTapSlop = viewConfiguration.getScaledDoubleTapSlop();
                this.mMinimumFlingVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
                this.mMaximumFlingVelocity = viewConfiguration.getScaledMaximumFlingVelocity();
                this.mTouchSlopSquare = scaledTouchSlop * scaledTouchSlop;
                this.mDoubleTapSlopSquare = scaledDoubleTapSlop * scaledDoubleTapSlop;
            }
        }

        public final boolean onTouchEvent(MotionEvent ev) {
            int i;
            int div;
            int action = ev.getAction();
            if (this.mVelocityTracker == null) {
                this.mVelocityTracker = VelocityTracker.obtain();
            }
            this.mVelocityTracker.addMovement(ev);
            boolean pointerUp = (action & 255) == 6;
            int skipIndex = pointerUp ? MotionEventCompat.getActionIndex(ev) : -1;
            float sumX = 0.0f;
            float sumY = 0.0f;
            int count = ev.getPointerCount();
            for (i = 0; i < count; i++) {
                if (skipIndex != i) {
                    sumX += ev.getX(i);
                    sumY += ev.getY(i);
                }
            }
            if (pointerUp) {
                div = count - 1;
            } else {
                div = count;
            }
            float focusX = sumX / ((float) div);
            float focusY = sumY / ((float) div);
            boolean handled = false;
            switch (action & 255) {
                case 0:
                    if (this.mDoubleTapListener != null) {
                        boolean hadTapMessage = this.mHandler.hasMessages(3);
                        if (hadTapMessage) {
                            this.mHandler.removeMessages(3);
                        }
                        if (!(this.mCurrentDownEvent == null || this.mPreviousUpEvent == null || !hadTapMessage)) {
                            Object obj;
                            MotionEvent motionEvent = this.mCurrentDownEvent;
                            MotionEvent motionEvent2 = this.mPreviousUpEvent;
                            if (this.mAlwaysInBiggerTapRegion && ev.getEventTime() - motionEvent2.getEventTime() <= ((long) DOUBLE_TAP_TIMEOUT)) {
                                int x = ((int) motionEvent.getX()) - ((int) ev.getX());
                                int y = ((int) motionEvent.getY()) - ((int) ev.getY());
                                if ((y * y) + (x * x) < this.mDoubleTapSlopSquare) {
                                    obj = 1;
                                    if (obj != null) {
                                        this.mIsDoubleTapping = true;
                                        handled = (this.mDoubleTapListener.onDoubleTap(this.mCurrentDownEvent) | 0) | this.mDoubleTapListener.onDoubleTapEvent(ev);
                                    }
                                }
                            }
                            obj = null;
                            if (obj != null) {
                                this.mIsDoubleTapping = true;
                                handled = (this.mDoubleTapListener.onDoubleTap(this.mCurrentDownEvent) | 0) | this.mDoubleTapListener.onDoubleTapEvent(ev);
                            }
                        }
                        this.mHandler.sendEmptyMessageDelayed(3, (long) DOUBLE_TAP_TIMEOUT);
                    }
                    this.mLastFocusX = focusX;
                    this.mDownFocusX = focusX;
                    this.mLastFocusY = focusY;
                    this.mDownFocusY = focusY;
                    if (this.mCurrentDownEvent != null) {
                        this.mCurrentDownEvent.recycle();
                    }
                    this.mCurrentDownEvent = MotionEvent.obtain(ev);
                    this.mAlwaysInTapRegion = true;
                    this.mAlwaysInBiggerTapRegion = true;
                    this.mStillDown = true;
                    this.mInLongPress = false;
                    this.mDeferConfirmSingleTap = false;
                    if (this.mIsLongpressEnabled) {
                        this.mHandler.removeMessages(2);
                        this.mHandler.sendEmptyMessageAtTime(2, (this.mCurrentDownEvent.getDownTime() + ((long) TAP_TIMEOUT)) + ((long) LONGPRESS_TIMEOUT));
                    }
                    this.mHandler.sendEmptyMessageAtTime(1, this.mCurrentDownEvent.getDownTime() + ((long) TAP_TIMEOUT));
                    return handled | this.mListener.onDown(ev);
                case 1:
                    this.mStillDown = false;
                    MotionEvent currentUpEvent = MotionEvent.obtain(ev);
                    if (this.mIsDoubleTapping) {
                        handled = this.mDoubleTapListener.onDoubleTapEvent(ev) | 0;
                    } else if (this.mInLongPress) {
                        this.mHandler.removeMessages(3);
                        this.mInLongPress = false;
                    } else if (this.mAlwaysInTapRegion) {
                        handled = this.mListener.onSingleTapUp(ev);
                        if (this.mDeferConfirmSingleTap && this.mDoubleTapListener != null) {
                            this.mDoubleTapListener.onSingleTapConfirmed(ev);
                        }
                    } else {
                        VelocityTracker velocityTracker = this.mVelocityTracker;
                        int pointerId = ev.getPointerId(0);
                        velocityTracker.computeCurrentVelocity(1000, (float) this.mMaximumFlingVelocity);
                        float velocityY = VelocityTrackerCompat.getYVelocity(velocityTracker, pointerId);
                        float velocityX = VelocityTrackerCompat.getXVelocity(velocityTracker, pointerId);
                        if (Math.abs(velocityY) > ((float) this.mMinimumFlingVelocity) || Math.abs(velocityX) > ((float) this.mMinimumFlingVelocity)) {
                            handled = this.mListener.onFling(this.mCurrentDownEvent, ev, velocityX, velocityY);
                        }
                    }
                    if (this.mPreviousUpEvent != null) {
                        this.mPreviousUpEvent.recycle();
                    }
                    this.mPreviousUpEvent = currentUpEvent;
                    if (this.mVelocityTracker != null) {
                        this.mVelocityTracker.recycle();
                        this.mVelocityTracker = null;
                    }
                    this.mIsDoubleTapping = false;
                    this.mDeferConfirmSingleTap = false;
                    this.mHandler.removeMessages(1);
                    this.mHandler.removeMessages(2);
                    return handled;
                case 2:
                    if (this.mInLongPress) {
                        return false;
                    }
                    float scrollX = this.mLastFocusX - focusX;
                    float scrollY = this.mLastFocusY - focusY;
                    if (this.mIsDoubleTapping) {
                        return this.mDoubleTapListener.onDoubleTapEvent(ev) | 0;
                    }
                    if (this.mAlwaysInTapRegion) {
                        int deltaX = (int) (focusX - this.mDownFocusX);
                        int deltaY = (int) (focusY - this.mDownFocusY);
                        int distance = (deltaX * deltaX) + (deltaY * deltaY);
                        if (distance > this.mTouchSlopSquare) {
                            handled = this.mListener.onScroll(this.mCurrentDownEvent, ev, scrollX, scrollY);
                            this.mLastFocusX = focusX;
                            this.mLastFocusY = focusY;
                            this.mAlwaysInTapRegion = false;
                            this.mHandler.removeMessages(3);
                            this.mHandler.removeMessages(1);
                            this.mHandler.removeMessages(2);
                        }
                        if (distance <= this.mTouchSlopSquare) {
                            return handled;
                        }
                        this.mAlwaysInBiggerTapRegion = false;
                        return handled;
                    } else if (Math.abs(scrollX) < 1.0f && Math.abs(scrollY) < 1.0f) {
                        return false;
                    } else {
                        handled = this.mListener.onScroll(this.mCurrentDownEvent, ev, scrollX, scrollY);
                        this.mLastFocusX = focusX;
                        this.mLastFocusY = focusY;
                        return handled;
                    }
                case 3:
                    this.mHandler.removeMessages(1);
                    this.mHandler.removeMessages(2);
                    this.mHandler.removeMessages(3);
                    this.mVelocityTracker.recycle();
                    this.mVelocityTracker = null;
                    this.mIsDoubleTapping = false;
                    this.mStillDown = false;
                    this.mAlwaysInTapRegion = false;
                    this.mAlwaysInBiggerTapRegion = false;
                    this.mDeferConfirmSingleTap = false;
                    if (!this.mInLongPress) {
                        return false;
                    }
                    this.mInLongPress = false;
                    return false;
                case 5:
                    this.mLastFocusX = focusX;
                    this.mDownFocusX = focusX;
                    this.mLastFocusY = focusY;
                    this.mDownFocusY = focusY;
                    this.mHandler.removeMessages(1);
                    this.mHandler.removeMessages(2);
                    this.mHandler.removeMessages(3);
                    this.mIsDoubleTapping = false;
                    this.mAlwaysInTapRegion = false;
                    this.mAlwaysInBiggerTapRegion = false;
                    this.mDeferConfirmSingleTap = false;
                    if (!this.mInLongPress) {
                        return false;
                    }
                    this.mInLongPress = false;
                    return false;
                case 6:
                    this.mLastFocusX = focusX;
                    this.mDownFocusX = focusX;
                    this.mLastFocusY = focusY;
                    this.mDownFocusY = focusY;
                    this.mVelocityTracker.computeCurrentVelocity(1000, (float) this.mMaximumFlingVelocity);
                    int upIndex = MotionEventCompat.getActionIndex(ev);
                    int id1 = ev.getPointerId(upIndex);
                    float x1 = VelocityTrackerCompat.getXVelocity(this.mVelocityTracker, id1);
                    float y1 = VelocityTrackerCompat.getYVelocity(this.mVelocityTracker, id1);
                    for (i = 0; i < count; i++) {
                        if (i != upIndex) {
                            int id2 = ev.getPointerId(i);
                            if ((x1 * VelocityTrackerCompat.getXVelocity(this.mVelocityTracker, id2)) + (y1 * VelocityTrackerCompat.getYVelocity(this.mVelocityTracker, id2)) < 0.0f) {
                                this.mVelocityTracker.clear();
                                return false;
                            }
                        }
                    }
                    return false;
                default:
                    return false;
            }
        }

        final void dispatchLongPress() {
            this.mHandler.removeMessages(3);
            this.mDeferConfirmSingleTap = false;
            this.mInLongPress = true;
            this.mListener.onLongPress(this.mCurrentDownEvent);
        }
    }

    static class GestureDetectorCompatImplJellybeanMr2 implements GestureDetectorCompatImpl {
        private final GestureDetector mDetector;

        public GestureDetectorCompatImplJellybeanMr2(Context context, OnGestureListener listener, Handler handler) {
            this.mDetector = new GestureDetector(context, listener, handler);
        }

        public final boolean onTouchEvent(MotionEvent ev) {
            return this.mDetector.onTouchEvent(ev);
        }
    }

    public GestureDetectorCompat(Context context, OnGestureListener listener) {
        this(context, listener, null);
    }

    private GestureDetectorCompat(Context context, OnGestureListener listener, Handler handler) {
        if (VERSION.SDK_INT > 17) {
            this.mImpl = new GestureDetectorCompatImplJellybeanMr2(context, listener, null);
        } else {
            this.mImpl = new GestureDetectorCompatImplBase(context, listener, null);
        }
    }

    public final boolean onTouchEvent(MotionEvent event) {
        return this.mImpl.onTouchEvent(event);
    }
}
