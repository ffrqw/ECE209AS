package com.instabug.library.internal.c;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public final class a implements SensorEventListener {
    private a a;
    private b[] b = new b[150];
    private float c = 10.0f;
    private int d = 2;
    private int[][] e = new int[][]{new int[]{0, 0}, new int[]{0, 0}, new int[]{0, 0}};
    private int[] f = new int[]{0, 0, 0};
    private int g = 0;

    public interface a {
        void a();
    }

    private class b {
        float a;
        float b;
        float c;
        long d;
        final /* synthetic */ a e;

        public b(a aVar, float f, float f2, float f3, long j) {
            this.e = aVar;
            this.a = f;
            this.b = f2;
            this.c = f3;
            this.d = j;
        }

        public final String toString() {
            return "SensorBundle{mXAcc=" + this.a + ", mYAcc=" + this.b + ", mZAcc=" + this.c + ", mTimestamp=" + this.d + '}';
        }
    }

    public final void a(a aVar) {
        this.a = aVar;
    }

    public final void a(float f) {
        this.c = 100.0f * f;
    }

    public final synchronized void onSensorChanged(SensorEvent sensorEvent) {
        if (this.b[0] == null || this.g <= 0 || sensorEvent.timestamp - this.b[this.g - 1].d > 500) {
            int i;
            b bVar = this.b[this.g];
            if (bVar == null) {
                bVar = new b(this, sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2], sensorEvent.timestamp);
            }
            bVar.a = sensorEvent.values[0];
            bVar.b = sensorEvent.values[1];
            bVar.c = sensorEvent.values[2];
            bVar.d = sensorEvent.timestamp;
            this.b[this.g] = bVar;
            this.g++;
            if (this.g == this.b.length) {
                this.g = 0;
            }
            this.e[0][0] = 0;
            this.e[0][1] = 0;
            this.e[1][0] = 0;
            this.e[1][1] = 0;
            this.e[2][0] = 0;
            this.e[2][1] = 0;
            for (b bVar2 : this.b) {
                if (bVar2 != null) {
                    int[] iArr;
                    if (bVar2.a > this.c && this.f[0] <= 0) {
                        this.f[0] = 1;
                        iArr = this.e[0];
                        iArr[0] = iArr[0] + 1;
                    }
                    if (bVar2.a < (-this.c) && this.f[0] >= 0) {
                        this.f[0] = -1;
                        iArr = this.e[0];
                        iArr[1] = iArr[1] + 1;
                    }
                    if (bVar2.b > this.c && this.f[1] <= 0) {
                        this.f[1] = 1;
                        iArr = this.e[1];
                        iArr[0] = iArr[0] + 1;
                    }
                    if (bVar2.b < (-this.c) && this.f[1] >= 0) {
                        this.f[1] = -1;
                        iArr = this.e[1];
                        iArr[1] = iArr[1] + 1;
                    }
                    if (bVar2.c > this.c && this.f[2] <= 0) {
                        this.f[2] = 1;
                        iArr = this.e[2];
                        iArr[0] = iArr[0] + 1;
                    }
                    if (bVar2.c < (-this.c) && this.f[2] >= 0) {
                        this.f[2] = -1;
                        int[] iArr2 = this.e[2];
                        iArr2[1] = iArr2[1] + 1;
                    }
                }
            }
            Object obj = null;
            for (int[] iArr3 : this.e) {
                for (int i2 : r3[r2]) {
                    if (i2 >= this.d) {
                        if (obj != null) {
                            if (this.a != null) {
                                this.a.a();
                            }
                            this.b = new b[150];
                            this.g = 0;
                        } else {
                            i = 1;
                        }
                    }
                }
            }
        }
    }

    public final void onAccuracyChanged(Sensor sensor, int i) {
    }
}
