package com.instabug.library.util;

import android.content.Context;
import android.util.TypedValue;

public final class a {
    public static int a(Context context, int i) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(i, typedValue, true);
        return typedValue.resourceId;
    }
}
