package android.support.v4.widget;

import android.annotation.TargetApi;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.CompoundButton;
import java.lang.reflect.Field;

@TargetApi(9)
final class CompoundButtonCompatGingerbread {
    private static Field sButtonDrawableField;
    private static boolean sButtonDrawableFieldFetched;

    static Drawable getButtonDrawable(CompoundButton button) {
        if (!sButtonDrawableFieldFetched) {
            try {
                Field declaredField = CompoundButton.class.getDeclaredField("mButtonDrawable");
                sButtonDrawableField = declaredField;
                declaredField.setAccessible(true);
            } catch (NoSuchFieldException e) {
                Log.i("CompoundButtonCompatGingerbread", "Failed to retrieve mButtonDrawable field", e);
            }
            sButtonDrawableFieldFetched = true;
        }
        if (sButtonDrawableField != null) {
            try {
                return (Drawable) sButtonDrawableField.get(button);
            } catch (IllegalAccessException e2) {
                Log.i("CompoundButtonCompatGingerbread", "Failed to get button drawable via reflection", e2);
                sButtonDrawableField = null;
            }
        }
        return null;
    }
}
