package android.support.v4.view;

import android.annotation.TargetApi;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;

@TargetApi(21)
final class ViewParentCompatLollipop {
    public static boolean onStartNestedScroll(ViewParent parent, View child, View target, int nestedScrollAxes) {
        try {
            return parent.onStartNestedScroll(child, target, nestedScrollAxes);
        } catch (AbstractMethodError e) {
            Log.e("ViewParentCompat", "ViewParent " + parent + " does not implement interface method onStartNestedScroll", e);
            return false;
        }
    }

    public static boolean onNestedFling(ViewParent parent, View target, float velocityX, float velocityY, boolean consumed) {
        try {
            return parent.onNestedFling(target, velocityX, velocityY, consumed);
        } catch (AbstractMethodError e) {
            Log.e("ViewParentCompat", "ViewParent " + parent + " does not implement interface method onNestedFling", e);
            return false;
        }
    }

    public static boolean onNestedPreFling(ViewParent parent, View target, float velocityX, float velocityY) {
        try {
            return parent.onNestedPreFling(target, velocityX, velocityY);
        } catch (AbstractMethodError e) {
            Log.e("ViewParentCompat", "ViewParent " + parent + " does not implement interface method onNestedPreFling", e);
            return false;
        }
    }
}
