package com.rachio.iro.utils;

import android.os.Build.VERSION;
import android.view.View;

public class OpacityUtil {
    public static void makeViewLookDisabled(View v) {
        if (VERSION.SDK_INT >= 11) {
            v.setAlpha(0.5f);
        }
    }

    public static void makeViewLookEnabledOrDisabled(View v, boolean enabled) {
        if (!enabled) {
            makeViewLookDisabled(v);
        } else if (VERSION.SDK_INT >= 11) {
            v.setAlpha(1.0f);
        }
    }
}
