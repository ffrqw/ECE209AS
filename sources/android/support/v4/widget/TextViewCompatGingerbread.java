package android.support.v4.widget;

import android.annotation.TargetApi;
import android.util.Log;
import android.widget.TextView;
import java.lang.reflect.Field;

@TargetApi(9)
final class TextViewCompatGingerbread {
    private static Field sMaxModeField;
    private static boolean sMaxModeFieldFetched;
    private static Field sMaximumField;
    private static boolean sMaximumFieldFetched;

    static int getMaxLines(TextView textView) {
        if (!sMaxModeFieldFetched) {
            sMaxModeField = retrieveField("mMaxMode");
            sMaxModeFieldFetched = true;
        }
        if (sMaxModeField != null && retrieveIntFromField(sMaxModeField, textView) == 1) {
            if (!sMaximumFieldFetched) {
                sMaximumField = retrieveField("mMaximum");
                sMaximumFieldFetched = true;
            }
            if (sMaximumField != null) {
                return retrieveIntFromField(sMaximumField, textView);
            }
        }
        return -1;
    }

    private static Field retrieveField(String fieldName) {
        Field field = null;
        try {
            field = TextView.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            Log.e("TextViewCompatGingerbread", "Could not retrieve " + fieldName + " field.");
            return field;
        }
    }

    private static int retrieveIntFromField(Field field, TextView textView) {
        try {
            return field.getInt(textView);
        } catch (IllegalAccessException e) {
            Log.d("TextViewCompatGingerbread", "Could not retrieve value of " + field.getName() + " field.");
            return -1;
        }
    }
}
