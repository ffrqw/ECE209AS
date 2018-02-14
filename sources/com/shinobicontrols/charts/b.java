package com.shinobicontrols.charts;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;

class b implements Runnable {
    private Animation f;
    private a g;
    private final Handler h = new Handler(Looper.getMainLooper());
    private long i;
    private boolean j = false;

    interface a {
        void b(Animation animation);

        void c();

        void onAnimationEnd();

        void onAnimationStart();
    }

    public void run() {
        if (this.j && this.g != null) {
            long uptimeMillis = SystemClock.uptimeMillis();
            float f = ((float) (uptimeMillis - this.i)) / 1000.0f;
            this.i = uptimeMillis;
            this.f.a(f);
            this.g.b(this.f);
            if (this.f.isFinished()) {
                this.j = false;
                this.g.onAnimationEnd();
                return;
            }
            this.h.post(this);
        }
    }

    void a(Animation animation) {
        this.f = animation;
    }

    void a(a aVar) {
        this.g = aVar;
    }

    void start() {
        if (this.g != null) {
            this.i = SystemClock.uptimeMillis() - 17;
            this.g.onAnimationStart();
            this.j = true;
            run();
        }
    }

    void cancel() {
        this.h.removeCallbacks(this);
        if (this.j) {
            this.j = false;
            this.g.c();
        }
    }
}
