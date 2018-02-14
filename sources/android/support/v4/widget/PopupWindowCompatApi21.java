package android.support.v4.widget;

import android.annotation.TargetApi;
import android.util.Log;
import android.widget.PopupWindow;
import java.lang.reflect.Field;

@TargetApi(21)
final class PopupWindowCompatApi21 {
    private static Field sOverlapAnchorField;

    static {
        try {
            Field declaredField = PopupWindow.class.getDeclaredField("mOverlapAnchor");
            sOverlapAnchorField = declaredField;
            declaredField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            Log.i("PopupWindowCompatApi21", "Could not fetch mOverlapAnchor field from PopupWindow", e);
        }
    }

    static void setOverlapAnchor(PopupWindow popupWindow, boolean overlapAnchor) {
        if (sOverlapAnchorField != null) {
            try {
                sOverlapAnchorField.set(popupWindow, Boolean.valueOf(overlapAnchor));
            } catch (IllegalAccessException e) {
                Log.i("PopupWindowCompatApi21", "Could not set overlap anchor field in PopupWindow", e);
            }
        }
    }

    static boolean getOverlapAnchor(PopupWindow popupWindow) {
        if (sOverlapAnchorField != null) {
            try {
                return ((Boolean) sOverlapAnchorField.get(popupWindow)).booleanValue();
            } catch (IllegalAccessException e) {
                Log.i("PopupWindowCompatApi21", "Could not get overlap anchor field in PopupWindow", e);
            }
        }
        return false;
    }
}
