package com.shinobicontrols.charts;

import android.graphics.PointF;
import android.os.SystemClock;

class dx {
    final PointF ls;
    final long time = SystemClock.uptimeMillis();

    dx(float f, float f2) {
        this.ls = new PointF(f, f2);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof dx)) {
            return false;
        }
        dx dxVar = (dx) o;
        if (this.time == dxVar.time && this.ls.equals(dxVar)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return ((((int) (this.time ^ (this.time >>> 32))) + 527) * 31) + this.ls.hashCode();
    }

    long b(dx dxVar) {
        return dxVar.time - this.time;
    }
}
