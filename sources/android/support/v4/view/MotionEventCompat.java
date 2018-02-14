package android.support.v4.view;

import android.os.Build.VERSION;
import android.view.MotionEvent;

public final class MotionEventCompat {
    static final MotionEventVersionImpl IMPL;

    interface MotionEventVersionImpl {
        float getAxisValue(MotionEvent motionEvent, int i);
    }

    static class BaseMotionEventVersionImpl implements MotionEventVersionImpl {
        BaseMotionEventVersionImpl() {
        }

        public float getAxisValue(MotionEvent event, int axis) {
            return 0.0f;
        }
    }

    static class HoneycombMr1MotionEventVersionImpl extends BaseMotionEventVersionImpl {
        HoneycombMr1MotionEventVersionImpl() {
        }

        public final float getAxisValue(MotionEvent event, int axis) {
            return event.getAxisValue(axis);
        }
    }

    private static class ICSMotionEventVersionImpl extends HoneycombMr1MotionEventVersionImpl {
        ICSMotionEventVersionImpl() {
        }
    }

    static {
        if (VERSION.SDK_INT >= 14) {
            IMPL = new ICSMotionEventVersionImpl();
        } else if (VERSION.SDK_INT >= 12) {
            IMPL = new HoneycombMr1MotionEventVersionImpl();
        } else {
            IMPL = new BaseMotionEventVersionImpl();
        }
    }

    public static int getActionMasked(MotionEvent event) {
        return event.getAction() & 255;
    }

    public static int getActionIndex(MotionEvent event) {
        return (event.getAction() >> 8) & 255;
    }

    @Deprecated
    public static int findPointerIndex(MotionEvent event, int pointerId) {
        return event.findPointerIndex(pointerId);
    }

    @Deprecated
    public static int getPointerId(MotionEvent event, int pointerIndex) {
        return event.getPointerId(pointerIndex);
    }

    @Deprecated
    public static float getX(MotionEvent event, int pointerIndex) {
        return event.getX(pointerIndex);
    }

    public static float getAxisValue(MotionEvent event, int axis) {
        return IMPL.getAxisValue(event, axis);
    }
}
