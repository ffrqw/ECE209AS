package com.instabug.library.c;

import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;

public final class d {
    private com.instabug.library.internal.c.a a;
    private Handler b = new Handler();
    private a c;
    private int d = 0;
    private SensorManager e;
    private Runnable f = new Runnable(this) {
        final /* synthetic */ d a;

        {
            this.a = r1;
        }

        public final void run() {
            this.a.d = 0;
        }
    };
    private com.instabug.library.internal.c.a.a g = new com.instabug.library.internal.c.a.a(this) {
        final /* synthetic */ d a;

        {
            this.a = r1;
        }

        public final void a() {
            this.a.d = this.a.d + 1;
            if (this.a.d == 2) {
                this.a.b.postDelayed(this.a.f, 3000);
                this.a.c.a();
            }
        }
    };

    public interface a {
        void a();
    }

    public d(com.instabug.library.internal.c.a aVar, a aVar2, SensorManager sensorManager) {
        this.a = aVar;
        this.c = aVar2;
        this.e = sensorManager;
        aVar.a(this.g);
    }

    public final void a(boolean z) {
        SensorEventListener sensorEventListener;
        SensorManager sensorManager;
        if (z) {
            sensorEventListener = this.a;
            sensorManager = this.e;
            if (sensorManager != null) {
                sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(1), 2);
                return;
            }
            return;
        }
        sensorEventListener = this.a;
        sensorManager = this.e;
        if (sensorManager != null) {
            sensorManager.unregisterListener(sensorEventListener);
        }
    }

    public final void a(float f) {
        this.a.a(f);
    }
}
