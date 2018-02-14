package com.instabug.library.util;

import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import com.instabug.library.Instabug;

public final class c {
    public static Drawable a(Drawable drawable) {
        drawable.setColorFilter(Instabug.getPrimaryColor(), Mode.SRC_IN);
        return drawable;
    }

    public static void a(ImageView imageView) {
        imageView.setColorFilter(new PorterDuffColorFilter(Instabug.getPrimaryColor(), Mode.SRC_IN));
    }
}
