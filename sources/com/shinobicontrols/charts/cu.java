package com.shinobicontrols.charts;

import java.util.Locale;

class cu {
    final double ld;
    final double le;

    cu(double d, double d2) {
        this.ld = d;
        this.le = d2;
    }

    double g(double d) {
        return (this.ld * d) + this.le;
    }

    public String toString() {
        return String.format(Locale.US, "f(x) = %fx + %f", new Object[]{Double.valueOf(this.ld), Double.valueOf(this.le)});
    }
}
