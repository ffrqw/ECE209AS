package com.instabug.library.internal.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.ImageView;

public class AnimatedImageView extends ImageView {
    private a[] a;
    private Drawable b = null;
    private final Handler c = new Handler();
    private boolean d = false;
    private Thread e;
    private final Runnable f = new Runnable(this) {
        final /* synthetic */ AnimatedImageView a;

        {
            this.a = r1;
        }

        public final void run() {
            if (this.a.b != null) {
                this.a.setImageDrawable(this.a.b);
            }
        }
    };

    public static class a {
        public int a;
        public int b;

        public a(int i, int i2) {
            this.b = i2;
            this.a = i;
        }
    }

    private class b implements Runnable {
        final /* synthetic */ AnimatedImageView a;

        private b(AnimatedImageView animatedImageView) {
            this.a = animatedImageView;
        }

        public final void run() {
            int length = this.a.a.length;
            do {
                for (int i = 0; i < length; i++) {
                    Drawable a = this.a.b;
                    this.a.b = this.a.getResources().getDrawable(this.a.a[i].a);
                    if (a != null) {
                        a.setCallback(null);
                    }
                    try {
                        Thread.sleep((long) this.a.a[i].b);
                        this.a.c.post(this.a.f);
                    } catch (InterruptedException e) {
                    }
                }
            } while (this.a.d);
        }
    }

    public AnimatedImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public AnimatedImageView(Context context) {
        super(context);
    }

    public final void setFrames(a[] aVarArr) {
        this.a = aVarArr;
        try {
            this.b = getResources().getDrawable(aVarArr[0].a);
            setImageDrawable(this.b);
            a();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            this.a = null;
        }
    }

    public final void a() {
        boolean z = true;
        this.d = true;
        if (!(this.d && this.a != null && this.e == null)) {
            z = false;
        }
        if (z) {
            this.e = new Thread(new b());
            this.e.start();
        }
    }

    public final void b() {
        this.d = false;
        if (this.e != null) {
            this.e.interrupt();
            this.e = null;
        }
    }
}
