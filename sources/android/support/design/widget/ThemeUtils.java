package android.support.design.widget;

import android.content.Context;
import android.content.res.TypedArray;
import com.rachio.iro.R;

final class ThemeUtils {
    private static final int[] APPCOMPAT_CHECK_ATTRS = new int[]{R.attr.colorPrimary};

    static void checkAppCompatTheme(Context context) {
        boolean failed = false;
        TypedArray a = context.obtainStyledAttributes(APPCOMPAT_CHECK_ATTRS);
        if (!a.hasValue(0)) {
            failed = true;
        }
        a.recycle();
        if (failed) {
            throw new IllegalArgumentException("You need to use a Theme.AppCompat theme (or descendant) with the design library.");
        }
    }
}
