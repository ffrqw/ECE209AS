package com.shinobicontrols.charts;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.view.View;

class cf extends View {
    private final Drawable jZ;

    @SuppressLint({"NewApi", "ViewConstructor"})
    public cf(Context context, Drawable drawable) {
        super(context);
        this.jZ = drawable;
        if (VERSION.SDK_INT >= 16) {
            setBackground(this.jZ);
        } else {
            setBackgroundDrawable(this.jZ);
        }
    }

    Drawable de() {
        return this.jZ;
    }
}
