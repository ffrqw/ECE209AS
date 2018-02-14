package android.support.v4.view;

import android.os.Build.VERSION;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;

public final class ViewParentCompat {
    static final ViewParentCompatImpl IMPL;

    interface ViewParentCompatImpl {
        boolean onNestedFling(ViewParent viewParent, View view, float f, float f2, boolean z);

        boolean onNestedPreFling(ViewParent viewParent, View view, float f, float f2);

        void onNestedPreScroll(ViewParent viewParent, View view, int i, int i2, int[] iArr);

        void onNestedScroll(ViewParent viewParent, View view, int i, int i2, int i3, int i4);

        void onNestedScrollAccepted(ViewParent viewParent, View view, View view2, int i);

        boolean onStartNestedScroll(ViewParent viewParent, View view, View view2, int i);

        void onStopNestedScroll(ViewParent viewParent, View view);
    }

    static class ViewParentCompatStubImpl implements ViewParentCompatImpl {
        ViewParentCompatStubImpl() {
        }

        public boolean onStartNestedScroll(ViewParent parent, View child, View target, int nestedScrollAxes) {
            if (parent instanceof NestedScrollingParent) {
                return ((NestedScrollingParent) parent).onStartNestedScroll(child, target, nestedScrollAxes);
            }
            return false;
        }

        public void onNestedScrollAccepted(ViewParent parent, View child, View target, int nestedScrollAxes) {
            if (parent instanceof NestedScrollingParent) {
                ((NestedScrollingParent) parent).onNestedScrollAccepted(child, target, nestedScrollAxes);
            }
        }

        public void onStopNestedScroll(ViewParent parent, View target) {
            if (parent instanceof NestedScrollingParent) {
                ((NestedScrollingParent) parent).onStopNestedScroll(target);
            }
        }

        public void onNestedScroll(ViewParent parent, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
            if (parent instanceof NestedScrollingParent) {
                ((NestedScrollingParent) parent).onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
            }
        }

        public void onNestedPreScroll(ViewParent parent, View target, int dx, int dy, int[] consumed) {
            if (parent instanceof NestedScrollingParent) {
                ((NestedScrollingParent) parent).onNestedPreScroll(target, dx, dy, consumed);
            }
        }

        public boolean onNestedFling(ViewParent parent, View target, float velocityX, float velocityY, boolean consumed) {
            if (parent instanceof NestedScrollingParent) {
                return ((NestedScrollingParent) parent).onNestedFling(target, velocityX, velocityY, consumed);
            }
            return false;
        }

        public boolean onNestedPreFling(ViewParent parent, View target, float velocityX, float velocityY) {
            if (parent instanceof NestedScrollingParent) {
                return ((NestedScrollingParent) parent).onNestedPreFling(target, velocityX, velocityY);
            }
            return false;
        }
    }

    static class ViewParentCompatICSImpl extends ViewParentCompatStubImpl {
        ViewParentCompatICSImpl() {
        }
    }

    static class ViewParentCompatKitKatImpl extends ViewParentCompatICSImpl {
        ViewParentCompatKitKatImpl() {
        }
    }

    static class ViewParentCompatLollipopImpl extends ViewParentCompatKitKatImpl {
        ViewParentCompatLollipopImpl() {
        }

        public final boolean onStartNestedScroll(ViewParent parent, View child, View target, int nestedScrollAxes) {
            return ViewParentCompatLollipop.onStartNestedScroll(parent, child, target, nestedScrollAxes);
        }

        public final boolean onNestedFling(ViewParent parent, View target, float velocityX, float velocityY, boolean consumed) {
            return ViewParentCompatLollipop.onNestedFling(parent, target, velocityX, velocityY, consumed);
        }

        public final boolean onNestedPreFling(ViewParent parent, View target, float velocityX, float velocityY) {
            return ViewParentCompatLollipop.onNestedPreFling(parent, target, velocityX, velocityY);
        }

        public final void onNestedScrollAccepted(ViewParent parent, View child, View target, int nestedScrollAxes) {
            try {
                parent.onNestedScrollAccepted(child, target, nestedScrollAxes);
            } catch (Throwable e) {
                Log.e("ViewParentCompat", "ViewParent " + parent + " does not implement interface method onNestedScrollAccepted", e);
            }
        }

        public final void onStopNestedScroll(ViewParent parent, View target) {
            try {
                parent.onStopNestedScroll(target);
            } catch (Throwable e) {
                Log.e("ViewParentCompat", "ViewParent " + parent + " does not implement interface method onStopNestedScroll", e);
            }
        }

        public final void onNestedScroll(ViewParent parent, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
            try {
                parent.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
            } catch (Throwable e) {
                Log.e("ViewParentCompat", "ViewParent " + parent + " does not implement interface method onNestedScroll", e);
            }
        }

        public final void onNestedPreScroll(ViewParent parent, View target, int dx, int dy, int[] consumed) {
            try {
                parent.onNestedPreScroll(target, dx, dy, consumed);
            } catch (Throwable e) {
                Log.e("ViewParentCompat", "ViewParent " + parent + " does not implement interface method onNestedPreScroll", e);
            }
        }
    }

    static {
        int version = VERSION.SDK_INT;
        if (version >= 21) {
            IMPL = new ViewParentCompatLollipopImpl();
        } else if (version >= 19) {
            IMPL = new ViewParentCompatKitKatImpl();
        } else if (version >= 14) {
            IMPL = new ViewParentCompatICSImpl();
        } else {
            IMPL = new ViewParentCompatStubImpl();
        }
    }

    public static boolean onStartNestedScroll(ViewParent parent, View child, View target, int nestedScrollAxes) {
        return IMPL.onStartNestedScroll(parent, child, target, nestedScrollAxes);
    }

    public static void onNestedScrollAccepted(ViewParent parent, View child, View target, int nestedScrollAxes) {
        IMPL.onNestedScrollAccepted(parent, child, target, nestedScrollAxes);
    }

    public static void onStopNestedScroll(ViewParent parent, View target) {
        IMPL.onStopNestedScroll(parent, target);
    }

    public static void onNestedScroll(ViewParent parent, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        IMPL.onNestedScroll(parent, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
    }

    public static void onNestedPreScroll(ViewParent parent, View target, int dx, int dy, int[] consumed) {
        IMPL.onNestedPreScroll(parent, target, dx, dy, consumed);
    }

    public static boolean onNestedFling(ViewParent parent, View target, float velocityX, float velocityY, boolean consumed) {
        return IMPL.onNestedFling(parent, target, velocityX, velocityY, consumed);
    }

    public static boolean onNestedPreFling(ViewParent parent, View target, float velocityX, float velocityY) {
        return IMPL.onNestedPreFling(parent, target, velocityX, velocityY);
    }
}
