package com.shinobicontrols.charts;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.view.View;

class a {
    @SuppressLint({"NewApi"})
    static void a(View view, Drawable drawable) {
        if (VERSION.SDK_INT >= 16) {
            view.setBackground(drawable);
        } else {
            view.setBackgroundDrawable(drawable);
        }
    }

    static dr a(Context context, af afVar) {
        if (VERSION.SDK_INT >= 14) {
            return new ds(context, afVar);
        }
        return new dt(context, afVar);
    }
}
