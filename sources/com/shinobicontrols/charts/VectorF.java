package com.shinobicontrols.charts;

import android.graphics.PointF;

class VectorF extends PointF {
    VectorF(float x, float y) {
        super(x, y);
    }

    static VectorF f(PointF pointF, PointF pointF2) {
        return new VectorF(pointF2.x - pointF.x, pointF2.y - pointF.y);
    }

    float fd() {
        return Math.max(Math.abs(this.x), Math.abs(this.y));
    }

    VectorF fe() {
        return new VectorF(Math.abs(this.x), Math.abs(this.y));
    }
}
