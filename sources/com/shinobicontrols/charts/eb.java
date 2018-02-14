package com.shinobicontrols.charts;

import com.shinobicontrols.charts.PieDonutSeries.DrawDirection;

abstract class eb {
    static eb nt = new eb() {
        final float d(float f, float f2) {
            return cv.j(f + f2);
        }

        final float e(float f, float f2) {
            return cv.j(f + f2);
        }

        final float d(double d, double d2) {
            return e(d, d2);
        }

        final float o(float f) {
            return f;
        }
    };
    static eb nu = new eb() {
        final float d(float f, float f2) {
            return cv.j(f2 - f);
        }

        final float e(float f, float f2) {
            return cv.j(f2 - f);
        }

        final float d(double d, double d2) {
            return 6.2831855f - e(d, d2);
        }

        final float o(float f) {
            return (float) (6.283185307179586d - ((double) f));
        }
    };

    abstract float d(double d, double d2);

    abstract float d(float f, float f2);

    abstract float e(float f, float f2);

    abstract float o(float f);

    eb() {
    }

    float e(double d, double d2) {
        return cv.j((float) (Math.atan2(d2, d) + 1.5707963267948966d));
    }

    static eb a(DrawDirection drawDirection) {
        if (drawDirection == DrawDirection.ANTICLOCKWISE) {
            return nt;
        }
        return nu;
    }
}
