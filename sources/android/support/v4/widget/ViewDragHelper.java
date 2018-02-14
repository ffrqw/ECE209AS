package android.support.v4.widget;

import android.content.Context;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import java.util.Arrays;

public final class ViewDragHelper {
    private static final Interpolator sInterpolator = new Interpolator() {
        public final float getInterpolation(float t) {
            t -= 1.0f;
            return ((((t * t) * t) * t) * t) + 1.0f;
        }
    };
    private int mActivePointerId = -1;
    private final Callback mCallback;
    private View mCapturedView;
    private int mDragState;
    private int[] mEdgeDragsInProgress;
    private int[] mEdgeDragsLocked;
    private int mEdgeSize;
    private int[] mInitialEdgesTouched;
    private float[] mInitialMotionX;
    private float[] mInitialMotionY;
    private float[] mLastMotionX;
    private float[] mLastMotionY;
    private float mMaxVelocity;
    private float mMinVelocity;
    private final ViewGroup mParentView;
    private int mPointersDown;
    private boolean mReleaseInProgress;
    private ScrollerCompat mScroller;
    private final Runnable mSetIdleRunnable = new Runnable() {
        public final void run() {
            ViewDragHelper.this.setDragState(0);
        }
    };
    private int mTouchSlop;
    private int mTrackingEdges;
    private VelocityTracker mVelocityTracker;

    public static abstract class Callback {
        public abstract boolean tryCaptureView(View view, int i);

        public void onViewDragStateChanged(int state) {
        }

        public void onViewPositionChanged$5b6f797d(View changedView, int left, int top) {
        }

        public void onViewCaptured(View capturedChild, int activePointerId) {
        }

        public void onViewReleased(View releasedChild, float xvel, float yvel) {
        }

        public void onEdgeTouched$255f295() {
        }

        public void onEdgeDragStarted(int edgeFlags, int pointerId) {
        }

        public int getViewHorizontalDragRange(View child) {
            return 0;
        }

        public int getViewVerticalDragRange$3c7ec8d0() {
            return 0;
        }

        public int clampViewPositionHorizontal$17e143b0(View child, int left) {
            return 0;
        }

        public int clampViewPositionVertical$17e143b0(View child, int top) {
            return 0;
        }
    }

    public static ViewDragHelper create(ViewGroup forParent, Callback cb) {
        return new ViewDragHelper(forParent.getContext(), forParent, cb);
    }

    public static ViewDragHelper create(ViewGroup forParent, float sensitivity, Callback cb) {
        ViewDragHelper helper = create(forParent, cb);
        helper.mTouchSlop = (int) (((float) helper.mTouchSlop) * (1.0f / sensitivity));
        return helper;
    }

    private ViewDragHelper(Context context, ViewGroup forParent, Callback cb) {
        if (forParent == null) {
            throw new IllegalArgumentException("Parent view may not be null");
        } else if (cb == null) {
            throw new IllegalArgumentException("Callback may not be null");
        } else {
            this.mParentView = forParent;
            this.mCallback = cb;
            ViewConfiguration vc = ViewConfiguration.get(context);
            this.mEdgeSize = (int) ((20.0f * context.getResources().getDisplayMetrics().density) + 0.5f);
            this.mTouchSlop = vc.getScaledTouchSlop();
            this.mMaxVelocity = (float) vc.getScaledMaximumFlingVelocity();
            this.mMinVelocity = (float) vc.getScaledMinimumFlingVelocity();
            this.mScroller = ScrollerCompat.create(context, sInterpolator);
        }
    }

    public final void setMinVelocity(float minVel) {
        this.mMinVelocity = minVel;
    }

    public final int getViewDragState() {
        return this.mDragState;
    }

    public final void setEdgeTrackingEnabled(int edgeFlags) {
        this.mTrackingEdges = edgeFlags;
    }

    public final int getEdgeSize() {
        return this.mEdgeSize;
    }

    public final void captureChildView(View childView, int activePointerId) {
        if (childView.getParent() != this.mParentView) {
            throw new IllegalArgumentException("captureChildView: parameter must be a descendant of the ViewDragHelper's tracked parent view (" + this.mParentView + ")");
        }
        this.mCapturedView = childView;
        this.mActivePointerId = activePointerId;
        this.mCallback.onViewCaptured(childView, activePointerId);
        setDragState(1);
    }

    public final View getCapturedView() {
        return this.mCapturedView;
    }

    public final int getTouchSlop() {
        return this.mTouchSlop;
    }

    public final void cancel() {
        this.mActivePointerId = -1;
        if (this.mInitialMotionX != null) {
            Arrays.fill(this.mInitialMotionX, 0.0f);
            Arrays.fill(this.mInitialMotionY, 0.0f);
            Arrays.fill(this.mLastMotionX, 0.0f);
            Arrays.fill(this.mLastMotionY, 0.0f);
            Arrays.fill(this.mInitialEdgesTouched, 0);
            Arrays.fill(this.mEdgeDragsInProgress, 0);
            Arrays.fill(this.mEdgeDragsLocked, 0);
            this.mPointersDown = 0;
        }
        if (this.mVelocityTracker != null) {
            this.mVelocityTracker.recycle();
            this.mVelocityTracker = null;
        }
    }

    public final boolean smoothSlideViewTo(View child, int finalLeft, int finalTop) {
        this.mCapturedView = child;
        this.mActivePointerId = -1;
        boolean continueSliding = forceSettleCapturedViewAt(finalLeft, finalTop, 0, 0);
        if (!(continueSliding || this.mDragState != 0 || this.mCapturedView == null)) {
            this.mCapturedView = null;
        }
        return continueSliding;
    }

    public final boolean settleCapturedViewAt(int finalLeft, int finalTop) {
        if (this.mReleaseInProgress) {
            return forceSettleCapturedViewAt(finalLeft, finalTop, (int) VelocityTrackerCompat.getXVelocity(this.mVelocityTracker, this.mActivePointerId), (int) VelocityTrackerCompat.getYVelocity(this.mVelocityTracker, this.mActivePointerId));
        }
        throw new IllegalStateException("Cannot settleCapturedViewAt outside of a call to Callback#onViewReleased");
    }

    private boolean forceSettleCapturedViewAt(int finalLeft, int finalTop, int xvel, int yvel) {
        int startLeft = this.mCapturedView.getLeft();
        int startTop = this.mCapturedView.getTop();
        int dx = finalLeft - startLeft;
        int dy = finalTop - startTop;
        if (dx == 0 && dy == 0) {
            this.mScroller.mScroller.abortAnimation();
            setDragState(0);
            return false;
        }
        View view = this.mCapturedView;
        int clampMag = clampMag(xvel, (int) this.mMinVelocity, (int) this.mMaxVelocity);
        int clampMag2 = clampMag(yvel, (int) this.mMinVelocity, (int) this.mMaxVelocity);
        int abs = Math.abs(dx);
        int abs2 = Math.abs(dy);
        int abs3 = Math.abs(clampMag);
        int abs4 = Math.abs(clampMag2);
        int i = abs3 + abs4;
        int i2 = abs + abs2;
        this.mScroller.startScroll(startLeft, startTop, dx, dy, (int) (((clampMag2 != 0 ? ((float) abs4) / ((float) i) : ((float) abs2) / ((float) i2)) * ((float) computeAxisDuration(dy, clampMag2, this.mCallback.getViewVerticalDragRange$3c7ec8d0()))) + ((clampMag != 0 ? ((float) abs3) / ((float) i) : ((float) abs) / ((float) i2)) * ((float) computeAxisDuration(dx, clampMag, this.mCallback.getViewHorizontalDragRange(view))))));
        setDragState(2);
        return true;
    }

    private int computeAxisDuration(int delta, int velocity, int motionRange) {
        if (delta == 0) {
            return 0;
        }
        int duration;
        int width = this.mParentView.getWidth();
        int halfWidth = width / 2;
        float distance = ((float) halfWidth) + (((float) halfWidth) * ((float) Math.sin((double) ((float) (((double) (Math.min(1.0f, ((float) Math.abs(delta)) / ((float) width)) - 0.5f)) * 0.4712389167638204d)))));
        velocity = Math.abs(velocity);
        if (velocity > 0) {
            duration = Math.round(1000.0f * Math.abs(distance / ((float) velocity))) * 4;
        } else {
            duration = (int) ((1.0f + (((float) Math.abs(delta)) / ((float) motionRange))) * 256.0f);
        }
        return Math.min(duration, 600);
    }

    private static int clampMag(int value, int absMin, int absMax) {
        int absValue = Math.abs(value);
        if (absValue < absMin) {
            return 0;
        }
        if (absValue <= absMax) {
            return value;
        }
        if (value <= 0) {
            return -absMax;
        }
        return absMax;
    }

    private static float clampMag(float value, float absMin, float absMax) {
        float absValue = Math.abs(value);
        if (absValue < absMin) {
            return 0.0f;
        }
        if (absValue <= absMax) {
            return value;
        }
        if (value <= 0.0f) {
            return -absMax;
        }
        return absMax;
    }

    public final boolean continueSettling(boolean deferCallbacks) {
        if (this.mDragState == 2) {
            boolean keepGoing = this.mScroller.mScroller.computeScrollOffset();
            int x = this.mScroller.mScroller.getCurrX();
            int y = this.mScroller.mScroller.getCurrY();
            int dx = x - this.mCapturedView.getLeft();
            int dy = y - this.mCapturedView.getTop();
            if (dx != 0) {
                ViewCompat.offsetLeftAndRight(this.mCapturedView, dx);
            }
            if (dy != 0) {
                ViewCompat.offsetTopAndBottom(this.mCapturedView, dy);
            }
            if (!(dx == 0 && dy == 0)) {
                this.mCallback.onViewPositionChanged$5b6f797d(this.mCapturedView, x, y);
            }
            if (keepGoing && x == this.mScroller.mScroller.getFinalX() && y == this.mScroller.mScroller.getFinalY()) {
                this.mScroller.mScroller.abortAnimation();
                keepGoing = false;
            }
            if (!keepGoing) {
                this.mParentView.post(this.mSetIdleRunnable);
            }
        }
        return this.mDragState == 2;
    }

    private void dispatchViewReleased(float xvel, float yvel) {
        this.mReleaseInProgress = true;
        this.mCallback.onViewReleased(this.mCapturedView, xvel, yvel);
        this.mReleaseInProgress = false;
        if (this.mDragState == 1) {
            setDragState(0);
        }
    }

    private void clearMotionHistory(int pointerId) {
        if (this.mInitialMotionX != null && isPointerDown(pointerId)) {
            this.mInitialMotionX[pointerId] = 0.0f;
            this.mInitialMotionY[pointerId] = 0.0f;
            this.mLastMotionX[pointerId] = 0.0f;
            this.mLastMotionY[pointerId] = 0.0f;
            this.mInitialEdgesTouched[pointerId] = 0;
            this.mEdgeDragsInProgress[pointerId] = 0;
            this.mEdgeDragsLocked[pointerId] = 0;
            this.mPointersDown &= (1 << pointerId) ^ -1;
        }
    }

    private void saveInitialMotion(float x, float y, int pointerId) {
        int i = 0;
        if (this.mInitialMotionX == null || this.mInitialMotionX.length <= pointerId) {
            Object obj = new float[(pointerId + 1)];
            Object obj2 = new float[(pointerId + 1)];
            Object obj3 = new float[(pointerId + 1)];
            Object obj4 = new float[(pointerId + 1)];
            Object obj5 = new int[(pointerId + 1)];
            Object obj6 = new int[(pointerId + 1)];
            Object obj7 = new int[(pointerId + 1)];
            if (this.mInitialMotionX != null) {
                System.arraycopy(this.mInitialMotionX, 0, obj, 0, this.mInitialMotionX.length);
                System.arraycopy(this.mInitialMotionY, 0, obj2, 0, this.mInitialMotionY.length);
                System.arraycopy(this.mLastMotionX, 0, obj3, 0, this.mLastMotionX.length);
                System.arraycopy(this.mLastMotionY, 0, obj4, 0, this.mLastMotionY.length);
                System.arraycopy(this.mInitialEdgesTouched, 0, obj5, 0, this.mInitialEdgesTouched.length);
                System.arraycopy(this.mEdgeDragsInProgress, 0, obj6, 0, this.mEdgeDragsInProgress.length);
                System.arraycopy(this.mEdgeDragsLocked, 0, obj7, 0, this.mEdgeDragsLocked.length);
            }
            this.mInitialMotionX = obj;
            this.mInitialMotionY = obj2;
            this.mLastMotionX = obj3;
            this.mLastMotionY = obj4;
            this.mInitialEdgesTouched = obj5;
            this.mEdgeDragsInProgress = obj6;
            this.mEdgeDragsLocked = obj7;
        }
        float[] fArr = this.mInitialMotionX;
        this.mLastMotionX[pointerId] = x;
        fArr[pointerId] = x;
        fArr = this.mInitialMotionY;
        this.mLastMotionY[pointerId] = y;
        fArr[pointerId] = y;
        int[] iArr = this.mInitialEdgesTouched;
        int i2 = (int) x;
        int i3 = (int) y;
        if (i2 < this.mParentView.getLeft() + this.mEdgeSize) {
            i = 1;
        }
        if (i3 < this.mParentView.getTop() + this.mEdgeSize) {
            i |= 4;
        }
        if (i2 > this.mParentView.getRight() - this.mEdgeSize) {
            i |= 2;
        }
        if (i3 > this.mParentView.getBottom() - this.mEdgeSize) {
            i |= 8;
        }
        iArr[pointerId] = i;
        this.mPointersDown |= 1 << pointerId;
    }

    private void saveLastMotion(MotionEvent ev) {
        int pointerCount = ev.getPointerCount();
        for (int i = 0; i < pointerCount; i++) {
            int pointerId = ev.getPointerId(i);
            if (isValidPointerForActionMove(pointerId)) {
                float x = ev.getX(i);
                float y = ev.getY(i);
                this.mLastMotionX[pointerId] = x;
                this.mLastMotionY[pointerId] = y;
            }
        }
    }

    private boolean isPointerDown(int pointerId) {
        return (this.mPointersDown & (1 << pointerId)) != 0;
    }

    final void setDragState(int state) {
        this.mParentView.removeCallbacks(this.mSetIdleRunnable);
        if (this.mDragState != state) {
            this.mDragState = state;
            this.mCallback.onViewDragStateChanged(state);
            if (this.mDragState == 0) {
                this.mCapturedView = null;
            }
        }
    }

    private boolean tryCaptureViewForDrag(View toCapture, int pointerId) {
        if (toCapture == this.mCapturedView && this.mActivePointerId == pointerId) {
            return true;
        }
        if (toCapture == null || !this.mCallback.tryCaptureView(toCapture, pointerId)) {
            return false;
        }
        this.mActivePointerId = pointerId;
        captureChildView(toCapture, pointerId);
        return true;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final boolean shouldInterceptTouchEvent(android.view.MotionEvent r27) {
        /*
        r26 = this;
        r4 = android.support.v4.view.MotionEventCompat.getActionMasked(r27);
        r5 = android.support.v4.view.MotionEventCompat.getActionIndex(r27);
        if (r4 != 0) goto L_0x000d;
    L_0x000a:
        r26.cancel();
    L_0x000d:
        r0 = r26;
        r0 = r0.mVelocityTracker;
        r24 = r0;
        if (r24 != 0) goto L_0x001f;
    L_0x0015:
        r24 = android.view.VelocityTracker.obtain();
        r0 = r24;
        r1 = r26;
        r1.mVelocityTracker = r0;
    L_0x001f:
        r0 = r26;
        r0 = r0.mVelocityTracker;
        r24 = r0;
        r0 = r24;
        r1 = r27;
        r0.addMovement(r1);
        switch(r4) {
            case 0: goto L_0x0040;
            case 1: goto L_0x022a;
            case 2: goto L_0x012b;
            case 3: goto L_0x022a;
            case 4: goto L_0x002f;
            case 5: goto L_0x00b0;
            case 6: goto L_0x021b;
            default: goto L_0x002f;
        };
    L_0x002f:
        r0 = r26;
        r0 = r0.mDragState;
        r24 = r0;
        r25 = 1;
        r0 = r24;
        r1 = r25;
        if (r0 != r1) goto L_0x022f;
    L_0x003d:
        r24 = 1;
    L_0x003f:
        return r24;
    L_0x0040:
        r22 = r27.getX();
        r23 = r27.getY();
        r24 = 0;
        r0 = r27;
        r1 = r24;
        r17 = r0.getPointerId(r1);
        r0 = r26;
        r1 = r22;
        r2 = r23;
        r3 = r17;
        r0.saveInitialMotion(r1, r2, r3);
        r0 = r22;
        r0 = (int) r0;
        r24 = r0;
        r0 = r23;
        r0 = (int) r0;
        r25 = r0;
        r0 = r26;
        r1 = r24;
        r2 = r25;
        r20 = r0.findTopChildUnder(r1, r2);
        r0 = r26;
        r0 = r0.mCapturedView;
        r24 = r0;
        r0 = r20;
        r1 = r24;
        if (r0 != r1) goto L_0x0094;
    L_0x007d:
        r0 = r26;
        r0 = r0.mDragState;
        r24 = r0;
        r25 = 2;
        r0 = r24;
        r1 = r25;
        if (r0 != r1) goto L_0x0094;
    L_0x008b:
        r0 = r26;
        r1 = r20;
        r2 = r17;
        r0.tryCaptureViewForDrag(r1, r2);
    L_0x0094:
        r0 = r26;
        r0 = r0.mInitialEdgesTouched;
        r24 = r0;
        r8 = r24[r17];
        r0 = r26;
        r0 = r0.mTrackingEdges;
        r24 = r0;
        r24 = r24 & r8;
        if (r24 == 0) goto L_0x002f;
    L_0x00a6:
        r0 = r26;
        r0 = r0.mCallback;
        r24 = r0;
        r24.onEdgeTouched$255f295();
        goto L_0x002f;
    L_0x00b0:
        r0 = r27;
        r17 = r0.getPointerId(r5);
        r0 = r27;
        r22 = r0.getX(r5);
        r0 = r27;
        r23 = r0.getY(r5);
        r0 = r26;
        r1 = r22;
        r2 = r23;
        r3 = r17;
        r0.saveInitialMotion(r1, r2, r3);
        r0 = r26;
        r0 = r0.mDragState;
        r24 = r0;
        if (r24 != 0) goto L_0x00f2;
    L_0x00d5:
        r0 = r26;
        r0 = r0.mInitialEdgesTouched;
        r24 = r0;
        r8 = r24[r17];
        r0 = r26;
        r0 = r0.mTrackingEdges;
        r24 = r0;
        r24 = r24 & r8;
        if (r24 == 0) goto L_0x002f;
    L_0x00e7:
        r0 = r26;
        r0 = r0.mCallback;
        r24 = r0;
        r24.onEdgeTouched$255f295();
        goto L_0x002f;
    L_0x00f2:
        r0 = r26;
        r0 = r0.mDragState;
        r24 = r0;
        r25 = 2;
        r0 = r24;
        r1 = r25;
        if (r0 != r1) goto L_0x002f;
    L_0x0100:
        r0 = r22;
        r0 = (int) r0;
        r24 = r0;
        r0 = r23;
        r0 = (int) r0;
        r25 = r0;
        r0 = r26;
        r1 = r24;
        r2 = r25;
        r20 = r0.findTopChildUnder(r1, r2);
        r0 = r26;
        r0 = r0.mCapturedView;
        r24 = r0;
        r0 = r20;
        r1 = r24;
        if (r0 != r1) goto L_0x002f;
    L_0x0120:
        r0 = r26;
        r1 = r20;
        r2 = r17;
        r0.tryCaptureViewForDrag(r1, r2);
        goto L_0x002f;
    L_0x012b:
        r0 = r26;
        r0 = r0.mInitialMotionX;
        r24 = r0;
        if (r24 == 0) goto L_0x002f;
    L_0x0133:
        r0 = r26;
        r0 = r0.mInitialMotionY;
        r24 = r0;
        if (r24 == 0) goto L_0x002f;
    L_0x013b:
        r16 = r27.getPointerCount();
        r10 = 0;
    L_0x0140:
        r0 = r16;
        if (r10 >= r0) goto L_0x0216;
    L_0x0144:
        r0 = r27;
        r17 = r0.getPointerId(r10);
        r0 = r26;
        r1 = r17;
        r24 = r0.isValidPointerForActionMove(r1);
        if (r24 == 0) goto L_0x0210;
    L_0x0154:
        r0 = r27;
        r22 = r0.getX(r10);
        r0 = r27;
        r23 = r0.getY(r10);
        r0 = r26;
        r0 = r0.mInitialMotionX;
        r24 = r0;
        r24 = r24[r17];
        r6 = r22 - r24;
        r0 = r26;
        r0 = r0.mInitialMotionY;
        r24 = r0;
        r24 = r24[r17];
        r7 = r23 - r24;
        r0 = r22;
        r0 = (int) r0;
        r24 = r0;
        r0 = r23;
        r0 = (int) r0;
        r25 = r0;
        r0 = r26;
        r1 = r24;
        r2 = r25;
        r20 = r0.findTopChildUnder(r1, r2);
        if (r20 == 0) goto L_0x0214;
    L_0x018a:
        r0 = r26;
        r1 = r20;
        r24 = r0.checkTouchSlop(r1, r6, r7);
        if (r24 == 0) goto L_0x0214;
    L_0x0194:
        r15 = 1;
    L_0x0195:
        if (r15 == 0) goto L_0x01ed;
    L_0x0197:
        r13 = r20.getLeft();
        r0 = (int) r6;
        r24 = r0;
        r18 = r13 + r24;
        r0 = r26;
        r0 = r0.mCallback;
        r24 = r0;
        r0 = r24;
        r1 = r20;
        r2 = r18;
        r11 = r0.clampViewPositionHorizontal$17e143b0(r1, r2);
        r14 = r20.getTop();
        r0 = (int) r7;
        r24 = r0;
        r19 = r14 + r24;
        r0 = r26;
        r0 = r0.mCallback;
        r24 = r0;
        r0 = r24;
        r1 = r20;
        r2 = r19;
        r12 = r0.clampViewPositionVertical$17e143b0(r1, r2);
        r0 = r26;
        r0 = r0.mCallback;
        r24 = r0;
        r0 = r24;
        r1 = r20;
        r9 = r0.getViewHorizontalDragRange(r1);
        r0 = r26;
        r0 = r0.mCallback;
        r24 = r0;
        r21 = r24.getViewVerticalDragRange$3c7ec8d0();
        if (r9 == 0) goto L_0x01e7;
    L_0x01e3:
        if (r9 <= 0) goto L_0x01ed;
    L_0x01e5:
        if (r11 != r13) goto L_0x01ed;
    L_0x01e7:
        if (r21 == 0) goto L_0x0216;
    L_0x01e9:
        if (r21 <= 0) goto L_0x01ed;
    L_0x01eb:
        if (r12 == r14) goto L_0x0216;
    L_0x01ed:
        r0 = r26;
        r1 = r17;
        r0.reportNewEdgeDrags(r6, r7, r1);
        r0 = r26;
        r0 = r0.mDragState;
        r24 = r0;
        r25 = 1;
        r0 = r24;
        r1 = r25;
        if (r0 == r1) goto L_0x0216;
    L_0x0202:
        if (r15 == 0) goto L_0x0210;
    L_0x0204:
        r0 = r26;
        r1 = r20;
        r2 = r17;
        r24 = r0.tryCaptureViewForDrag(r1, r2);
        if (r24 != 0) goto L_0x0216;
    L_0x0210:
        r10 = r10 + 1;
        goto L_0x0140;
    L_0x0214:
        r15 = 0;
        goto L_0x0195;
    L_0x0216:
        r26.saveLastMotion(r27);
        goto L_0x002f;
    L_0x021b:
        r0 = r27;
        r17 = r0.getPointerId(r5);
        r0 = r26;
        r1 = r17;
        r0.clearMotionHistory(r1);
        goto L_0x002f;
    L_0x022a:
        r26.cancel();
        goto L_0x002f;
    L_0x022f:
        r24 = 0;
        goto L_0x003f;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.widget.ViewDragHelper.shouldInterceptTouchEvent(android.view.MotionEvent):boolean");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void processTouchEvent(android.view.MotionEvent r27) {
        /*
        r26 = this;
        r4 = android.support.v4.view.MotionEventCompat.getActionMasked(r27);
        r5 = android.support.v4.view.MotionEventCompat.getActionIndex(r27);
        if (r4 != 0) goto L_0x000d;
    L_0x000a:
        r26.cancel();
    L_0x000d:
        r0 = r26;
        r0 = r0.mVelocityTracker;
        r20 = r0;
        if (r20 != 0) goto L_0x001f;
    L_0x0015:
        r20 = android.view.VelocityTracker.obtain();
        r0 = r20;
        r1 = r26;
        r1.mVelocityTracker = r0;
    L_0x001f:
        r0 = r26;
        r0 = r0.mVelocityTracker;
        r20 = r0;
        r0 = r20;
        r1 = r27;
        r0.addMovement(r1);
        switch(r4) {
            case 0: goto L_0x0030;
            case 1: goto L_0x0336;
            case 2: goto L_0x0137;
            case 3: goto L_0x034c;
            case 4: goto L_0x002f;
            case 5: goto L_0x0086;
            case 6: goto L_0x02a9;
            default: goto L_0x002f;
        };
    L_0x002f:
        return;
    L_0x0030:
        r18 = r27.getX();
        r19 = r27.getY();
        r20 = 0;
        r0 = r27;
        r1 = r20;
        r16 = r0.getPointerId(r1);
        r0 = r18;
        r0 = (int) r0;
        r20 = r0;
        r0 = r19;
        r0 = (int) r0;
        r21 = r0;
        r0 = r26;
        r1 = r20;
        r2 = r21;
        r17 = r0.findTopChildUnder(r1, r2);
        r0 = r26;
        r1 = r18;
        r2 = r19;
        r3 = r16;
        r0.saveInitialMotion(r1, r2, r3);
        r0 = r26;
        r1 = r17;
        r2 = r16;
        r0.tryCaptureViewForDrag(r1, r2);
        r0 = r26;
        r0 = r0.mInitialEdgesTouched;
        r20 = r0;
        r8 = r20[r16];
        r0 = r26;
        r0 = r0.mTrackingEdges;
        r20 = r0;
        r20 = r20 & r8;
        if (r20 == 0) goto L_0x002f;
    L_0x007c:
        r0 = r26;
        r0 = r0.mCallback;
        r20 = r0;
        r20.onEdgeTouched$255f295();
        goto L_0x002f;
    L_0x0086:
        r0 = r27;
        r16 = r0.getPointerId(r5);
        r0 = r27;
        r18 = r0.getX(r5);
        r0 = r27;
        r19 = r0.getY(r5);
        r0 = r26;
        r1 = r18;
        r2 = r19;
        r3 = r16;
        r0.saveInitialMotion(r1, r2, r3);
        r0 = r26;
        r0 = r0.mDragState;
        r20 = r0;
        if (r20 != 0) goto L_0x00e5;
    L_0x00ab:
        r0 = r18;
        r0 = (int) r0;
        r20 = r0;
        r0 = r19;
        r0 = (int) r0;
        r21 = r0;
        r0 = r26;
        r1 = r20;
        r2 = r21;
        r17 = r0.findTopChildUnder(r1, r2);
        r0 = r26;
        r1 = r17;
        r2 = r16;
        r0.tryCaptureViewForDrag(r1, r2);
        r0 = r26;
        r0 = r0.mInitialEdgesTouched;
        r20 = r0;
        r8 = r20[r16];
        r0 = r26;
        r0 = r0.mTrackingEdges;
        r20 = r0;
        r20 = r20 & r8;
        if (r20 == 0) goto L_0x002f;
    L_0x00da:
        r0 = r26;
        r0 = r0.mCallback;
        r20 = r0;
        r20.onEdgeTouched$255f295();
        goto L_0x002f;
    L_0x00e5:
        r0 = r18;
        r0 = (int) r0;
        r20 = r0;
        r0 = r19;
        r0 = (int) r0;
        r21 = r0;
        r0 = r26;
        r0 = r0.mCapturedView;
        r22 = r0;
        if (r22 == 0) goto L_0x0134;
    L_0x00f7:
        r23 = r22.getLeft();
        r0 = r20;
        r1 = r23;
        if (r0 < r1) goto L_0x0134;
    L_0x0101:
        r23 = r22.getRight();
        r0 = r20;
        r1 = r23;
        if (r0 >= r1) goto L_0x0134;
    L_0x010b:
        r20 = r22.getTop();
        r0 = r21;
        r1 = r20;
        if (r0 < r1) goto L_0x0134;
    L_0x0115:
        r20 = r22.getBottom();
        r0 = r21;
        r1 = r20;
        if (r0 >= r1) goto L_0x0134;
    L_0x011f:
        r20 = 1;
    L_0x0121:
        if (r20 == 0) goto L_0x002f;
    L_0x0123:
        r0 = r26;
        r0 = r0.mCapturedView;
        r20 = r0;
        r0 = r26;
        r1 = r20;
        r2 = r16;
        r0.tryCaptureViewForDrag(r1, r2);
        goto L_0x002f;
    L_0x0134:
        r20 = 0;
        goto L_0x0121;
    L_0x0137:
        r0 = r26;
        r0 = r0.mDragState;
        r20 = r0;
        r21 = 1;
        r0 = r20;
        r1 = r21;
        if (r0 != r1) goto L_0x022b;
    L_0x0145:
        r0 = r26;
        r0 = r0.mActivePointerId;
        r20 = r0;
        r0 = r26;
        r1 = r20;
        r20 = r0.isValidPointerForActionMove(r1);
        if (r20 == 0) goto L_0x002f;
    L_0x0155:
        r0 = r26;
        r0 = r0.mActivePointerId;
        r20 = r0;
        r0 = r27;
        r1 = r20;
        r13 = r0.findPointerIndex(r1);
        r0 = r27;
        r18 = r0.getX(r13);
        r0 = r27;
        r19 = r0.getY(r13);
        r0 = r26;
        r0 = r0.mLastMotionX;
        r20 = r0;
        r0 = r26;
        r0 = r0.mActivePointerId;
        r21 = r0;
        r20 = r20[r21];
        r20 = r18 - r20;
        r0 = r20;
        r11 = (int) r0;
        r0 = r26;
        r0 = r0.mLastMotionY;
        r20 = r0;
        r0 = r26;
        r0 = r0.mActivePointerId;
        r21 = r0;
        r20 = r20[r21];
        r20 = r19 - r20;
        r0 = r20;
        r12 = (int) r0;
        r0 = r26;
        r0 = r0.mCapturedView;
        r20 = r0;
        r20 = r20.getLeft();
        r21 = r20 + r11;
        r0 = r26;
        r0 = r0.mCapturedView;
        r20 = r0;
        r20 = r20.getTop();
        r20 = r20 + r12;
        r0 = r26;
        r0 = r0.mCapturedView;
        r22 = r0;
        r22 = r22.getLeft();
        r0 = r26;
        r0 = r0.mCapturedView;
        r23 = r0;
        r23 = r23.getTop();
        if (r11 == 0) goto L_0x01e8;
    L_0x01c3:
        r0 = r26;
        r0 = r0.mCallback;
        r24 = r0;
        r0 = r26;
        r0 = r0.mCapturedView;
        r25 = r0;
        r0 = r24;
        r1 = r25;
        r2 = r21;
        r21 = r0.clampViewPositionHorizontal$17e143b0(r1, r2);
        r0 = r26;
        r0 = r0.mCapturedView;
        r24 = r0;
        r22 = r21 - r22;
        r0 = r24;
        r1 = r22;
        android.support.v4.view.ViewCompat.offsetLeftAndRight(r0, r1);
    L_0x01e8:
        if (r12 == 0) goto L_0x020b;
    L_0x01ea:
        r0 = r26;
        r0 = r0.mCallback;
        r22 = r0;
        r0 = r26;
        r0 = r0.mCapturedView;
        r24 = r0;
        r0 = r22;
        r1 = r24;
        r2 = r20;
        r20 = r0.clampViewPositionVertical$17e143b0(r1, r2);
        r0 = r26;
        r0 = r0.mCapturedView;
        r22 = r0;
        r23 = r20 - r23;
        android.support.v4.view.ViewCompat.offsetTopAndBottom(r22, r23);
    L_0x020b:
        if (r11 != 0) goto L_0x020f;
    L_0x020d:
        if (r12 == 0) goto L_0x0226;
    L_0x020f:
        r0 = r26;
        r0 = r0.mCallback;
        r22 = r0;
        r0 = r26;
        r0 = r0.mCapturedView;
        r23 = r0;
        r0 = r22;
        r1 = r23;
        r2 = r21;
        r3 = r20;
        r0.onViewPositionChanged$5b6f797d(r1, r2, r3);
    L_0x0226:
        r26.saveLastMotion(r27);
        goto L_0x002f;
    L_0x022b:
        r15 = r27.getPointerCount();
        r9 = 0;
    L_0x0230:
        if (r9 >= r15) goto L_0x02a4;
    L_0x0232:
        r0 = r27;
        r16 = r0.getPointerId(r9);
        r0 = r26;
        r1 = r16;
        r20 = r0.isValidPointerForActionMove(r1);
        if (r20 == 0) goto L_0x02a1;
    L_0x0242:
        r0 = r27;
        r18 = r0.getX(r9);
        r0 = r27;
        r19 = r0.getY(r9);
        r0 = r26;
        r0 = r0.mInitialMotionX;
        r20 = r0;
        r20 = r20[r16];
        r6 = r18 - r20;
        r0 = r26;
        r0 = r0.mInitialMotionY;
        r20 = r0;
        r20 = r20[r16];
        r7 = r19 - r20;
        r0 = r26;
        r1 = r16;
        r0.reportNewEdgeDrags(r6, r7, r1);
        r0 = r26;
        r0 = r0.mDragState;
        r20 = r0;
        r21 = 1;
        r0 = r20;
        r1 = r21;
        if (r0 == r1) goto L_0x02a4;
    L_0x0277:
        r0 = r18;
        r0 = (int) r0;
        r20 = r0;
        r0 = r19;
        r0 = (int) r0;
        r21 = r0;
        r0 = r26;
        r1 = r20;
        r2 = r21;
        r17 = r0.findTopChildUnder(r1, r2);
        r0 = r26;
        r1 = r17;
        r20 = r0.checkTouchSlop(r1, r6, r7);
        if (r20 == 0) goto L_0x02a1;
    L_0x0295:
        r0 = r26;
        r1 = r17;
        r2 = r16;
        r20 = r0.tryCaptureViewForDrag(r1, r2);
        if (r20 != 0) goto L_0x02a4;
    L_0x02a1:
        r9 = r9 + 1;
        goto L_0x0230;
    L_0x02a4:
        r26.saveLastMotion(r27);
        goto L_0x002f;
    L_0x02a9:
        r0 = r27;
        r16 = r0.getPointerId(r5);
        r0 = r26;
        r0 = r0.mDragState;
        r20 = r0;
        r21 = 1;
        r0 = r20;
        r1 = r21;
        if (r0 != r1) goto L_0x032a;
    L_0x02bd:
        r0 = r26;
        r0 = r0.mActivePointerId;
        r20 = r0;
        r0 = r16;
        r1 = r20;
        if (r0 != r1) goto L_0x032a;
    L_0x02c9:
        r14 = -1;
        r15 = r27.getPointerCount();
        r9 = 0;
    L_0x02cf:
        if (r9 >= r15) goto L_0x0321;
    L_0x02d1:
        r0 = r27;
        r10 = r0.getPointerId(r9);
        r0 = r26;
        r0 = r0.mActivePointerId;
        r20 = r0;
        r0 = r20;
        if (r10 == r0) goto L_0x0333;
    L_0x02e1:
        r0 = r27;
        r18 = r0.getX(r9);
        r0 = r27;
        r19 = r0.getY(r9);
        r0 = r18;
        r0 = (int) r0;
        r20 = r0;
        r0 = r19;
        r0 = (int) r0;
        r21 = r0;
        r0 = r26;
        r1 = r20;
        r2 = r21;
        r20 = r0.findTopChildUnder(r1, r2);
        r0 = r26;
        r0 = r0.mCapturedView;
        r21 = r0;
        r0 = r20;
        r1 = r21;
        if (r0 != r1) goto L_0x0333;
    L_0x030d:
        r0 = r26;
        r0 = r0.mCapturedView;
        r20 = r0;
        r0 = r26;
        r1 = r20;
        r20 = r0.tryCaptureViewForDrag(r1, r10);
        if (r20 == 0) goto L_0x0333;
    L_0x031d:
        r0 = r26;
        r14 = r0.mActivePointerId;
    L_0x0321:
        r20 = -1;
        r0 = r20;
        if (r14 != r0) goto L_0x032a;
    L_0x0327:
        r26.releaseViewForPointerUp();
    L_0x032a:
        r0 = r26;
        r1 = r16;
        r0.clearMotionHistory(r1);
        goto L_0x002f;
    L_0x0333:
        r9 = r9 + 1;
        goto L_0x02cf;
    L_0x0336:
        r0 = r26;
        r0 = r0.mDragState;
        r20 = r0;
        r21 = 1;
        r0 = r20;
        r1 = r21;
        if (r0 != r1) goto L_0x0347;
    L_0x0344:
        r26.releaseViewForPointerUp();
    L_0x0347:
        r26.cancel();
        goto L_0x002f;
    L_0x034c:
        r0 = r26;
        r0 = r0.mDragState;
        r20 = r0;
        r21 = 1;
        r0 = r20;
        r1 = r21;
        if (r0 != r1) goto L_0x0367;
    L_0x035a:
        r20 = 0;
        r21 = 0;
        r0 = r26;
        r1 = r20;
        r2 = r21;
        r0.dispatchViewReleased(r1, r2);
    L_0x0367:
        r26.cancel();
        goto L_0x002f;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.widget.ViewDragHelper.processTouchEvent(android.view.MotionEvent):void");
    }

    private void reportNewEdgeDrags(float dx, float dy, int pointerId) {
        int dragsStarted = 0;
        if (checkNewEdgeDrag(dx, dy, pointerId, 1)) {
            dragsStarted = 1;
        }
        if (checkNewEdgeDrag(dy, dx, pointerId, 4)) {
            dragsStarted |= 4;
        }
        if (checkNewEdgeDrag(dx, dy, pointerId, 2)) {
            dragsStarted |= 2;
        }
        if (checkNewEdgeDrag(dy, dx, pointerId, 8)) {
            dragsStarted |= 8;
        }
        if (dragsStarted != 0) {
            int[] iArr = this.mEdgeDragsInProgress;
            iArr[pointerId] = iArr[pointerId] | dragsStarted;
            this.mCallback.onEdgeDragStarted(dragsStarted, pointerId);
        }
    }

    private boolean checkNewEdgeDrag(float delta, float odelta, int pointerId, int edge) {
        float absDelta = Math.abs(delta);
        float absODelta = Math.abs(odelta);
        if ((this.mInitialEdgesTouched[pointerId] & edge) != edge || (this.mTrackingEdges & edge) == 0 || (this.mEdgeDragsLocked[pointerId] & edge) == edge || (this.mEdgeDragsInProgress[pointerId] & edge) == edge) {
            return false;
        }
        if ((absDelta > ((float) this.mTouchSlop) || absODelta > ((float) this.mTouchSlop)) && (this.mEdgeDragsInProgress[pointerId] & edge) == 0 && absDelta > ((float) this.mTouchSlop)) {
            return true;
        }
        return false;
    }

    private boolean checkTouchSlop(View child, float dx, float dy) {
        if (child == null) {
            return false;
        }
        boolean checkHorizontal;
        boolean checkVertical;
        if (this.mCallback.getViewHorizontalDragRange(child) > 0) {
            checkHorizontal = true;
        } else {
            checkHorizontal = false;
        }
        if (this.mCallback.getViewVerticalDragRange$3c7ec8d0() > 0) {
            checkVertical = true;
        } else {
            checkVertical = false;
        }
        if (checkHorizontal && checkVertical) {
            if ((dx * dx) + (dy * dy) > ((float) (this.mTouchSlop * this.mTouchSlop))) {
                return true;
            }
            return false;
        } else if (checkHorizontal) {
            if (Math.abs(dx) > ((float) this.mTouchSlop)) {
                return true;
            }
            return false;
        } else if (!checkVertical || Math.abs(dy) <= ((float) this.mTouchSlop)) {
            return false;
        } else {
            return true;
        }
    }

    public final boolean checkTouchSlop(int directions) {
        int count = this.mInitialMotionX.length;
        for (int i = 0; i < count; i++) {
            boolean z;
            if (isPointerDown(i)) {
                float f = this.mLastMotionX[i] - this.mInitialMotionX[i];
                float f2 = this.mLastMotionY[i] - this.mInitialMotionY[i];
                z = (f * f) + (f2 * f2) > ((float) (this.mTouchSlop * this.mTouchSlop));
            } else {
                z = false;
            }
            if (z) {
                return true;
            }
        }
        return false;
    }

    private void releaseViewForPointerUp() {
        this.mVelocityTracker.computeCurrentVelocity(1000, this.mMaxVelocity);
        dispatchViewReleased(clampMag(VelocityTrackerCompat.getXVelocity(this.mVelocityTracker, this.mActivePointerId), this.mMinVelocity, this.mMaxVelocity), clampMag(VelocityTrackerCompat.getYVelocity(this.mVelocityTracker, this.mActivePointerId), this.mMinVelocity, this.mMaxVelocity));
    }

    public final View findTopChildUnder(int x, int y) {
        for (int i = this.mParentView.getChildCount() - 1; i >= 0; i--) {
            View child = this.mParentView.getChildAt(i);
            if (x >= child.getLeft() && x < child.getRight() && y >= child.getTop() && y < child.getBottom()) {
                return child;
            }
        }
        return null;
    }

    private boolean isValidPointerForActionMove(int pointerId) {
        if (isPointerDown(pointerId)) {
            return true;
        }
        Log.e("ViewDragHelper", "Ignoring pointerId=" + pointerId + " because ACTION_DOWN was not received for this pointer before ACTION_MOVE. It likely happened because " + " ViewDragHelper did not receive all the events in the event stream.");
        return false;
    }
}
