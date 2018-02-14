package android.support.v4.view;

import android.os.Build.VERSION;
import android.view.ViewGroup.MarginLayoutParams;

public final class MarginLayoutParamsCompat {
    static final MarginLayoutParamsCompatImpl IMPL;

    interface MarginLayoutParamsCompatImpl {
        int getMarginEnd(MarginLayoutParams marginLayoutParams);

        int getMarginStart(MarginLayoutParams marginLayoutParams);
    }

    static class MarginLayoutParamsCompatImplBase implements MarginLayoutParamsCompatImpl {
        MarginLayoutParamsCompatImplBase() {
        }

        public final int getMarginStart(MarginLayoutParams lp) {
            return lp.leftMargin;
        }

        public final int getMarginEnd(MarginLayoutParams lp) {
            return lp.rightMargin;
        }
    }

    static class MarginLayoutParamsCompatImplJbMr1 implements MarginLayoutParamsCompatImpl {
        MarginLayoutParamsCompatImplJbMr1() {
        }

        public final int getMarginStart(MarginLayoutParams lp) {
            return lp.getMarginStart();
        }

        public final int getMarginEnd(MarginLayoutParams lp) {
            return lp.getMarginEnd();
        }
    }

    static {
        if (VERSION.SDK_INT >= 17) {
            IMPL = new MarginLayoutParamsCompatImplJbMr1();
        } else {
            IMPL = new MarginLayoutParamsCompatImplBase();
        }
    }

    public static int getMarginStart(MarginLayoutParams lp) {
        return IMPL.getMarginStart(lp);
    }

    public static int getMarginEnd(MarginLayoutParams lp) {
        return IMPL.getMarginEnd(lp);
    }
}
