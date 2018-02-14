package com.instabug.library.interaction;

import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import com.instabug.library.Instabug;

public class InstabugSwipeDelegate {
    private static final int DIRECTION_DOWN = 2;
    private static final int DIRECTION_LEFT = 3;
    private static final int DIRECTION_RIGHT = 4;
    private static final int DIRECTION_UP = 1;
    private int mDirectionNeeded;
    private final a mForwarder;
    private boolean mGoodCycle = false;
    private int mPointerCountNeeded = 0;

    public interface a {
        void onInstabugGestureEvent(MotionEvent motionEvent);
    }

    public enum GestureConfig {
        ThreeSwipeUp,
        TwoSwipeLeft
    }

    public class GestureListener extends SimpleOnGestureListener {
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
            if (InstabugSwipeDelegate.this.isDirectionValid(motionEvent, motionEvent2) && InstabugSwipeDelegate.this.mGoodCycle) {
                Instabug.invoke();
            }
            InstabugSwipeDelegate.this.mGoodCycle = false;
            return false;
        }
    }

    public InstabugSwipeDelegate(a aVar, GestureConfig gestureConfig) {
        this.mForwarder = aVar;
        if (gestureConfig == null) {
            gestureConfig = GestureConfig.ThreeSwipeUp;
        }
        switch (gestureConfig) {
            case ThreeSwipeUp:
                this.mDirectionNeeded = 1;
                this.mPointerCountNeeded = 3;
                return;
            case TwoSwipeLeft:
                this.mDirectionNeeded = 3;
                this.mPointerCountNeeded = 2;
                return;
            default:
                return;
        }
    }

    public void onTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getPointerCount() >= this.mPointerCountNeeded) {
            this.mGoodCycle = true;
        }
        switch (motionEvent.getAction() & 255) {
            case 2:
                if (motionEvent.getPointerCount() < this.mPointerCountNeeded) {
                    return;
                }
                break;
        }
        this.mForwarder.onInstabugGestureEvent(motionEvent);
    }

    private boolean isDirectionValid(MotionEvent motionEvent, MotionEvent motionEvent2) {
        if (motionEvent == null || motionEvent2 == null) {
            return false;
        }
        switch (this.mDirectionNeeded) {
            case 1:
                if (motionEvent.getY() <= motionEvent2.getY()) {
                    return false;
                }
                return true;
            case 3:
                if (motionEvent.getX() <= motionEvent2.getX() || motionEvent.getX() - motionEvent2.getX() < Math.abs(motionEvent.getY() - motionEvent2.getY())) {
                    return false;
                }
                return true;
            default:
                return false;
        }
    }
}
