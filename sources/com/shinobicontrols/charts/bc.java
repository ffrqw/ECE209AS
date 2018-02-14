package com.shinobicontrols.charts;

import android.graphics.Color;

class bc extends Color {
    static final bc hi = new bc(0);
    final float alpha;
    final float hj;
    final float hk;
    final float hl;

    private static float m(int i) {
        return ((float) i) * 0.003921569f;
    }

    bc(int i) {
        this(Color.red(i), Color.green(i), Color.blue(i), Color.alpha(i));
    }

    bc(int i, int i2, int i3, int i4) {
        this(m(i), m(i2), m(i3), m(i4));
    }

    bc(float f, float f2, float f3, float f4) {
        this.hj = f;
        this.hk = f2;
        this.hl = f3;
        this.alpha = f4;
    }
}
