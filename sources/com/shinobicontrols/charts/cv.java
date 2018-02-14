package com.shinobicontrols.charts;

final class cv {
    static float a(float f, float f2, float f3) {
        float f4 = f;
        while (f4 < f2) {
            f4 += 6.2831855f;
        }
        while (f4 > f3) {
            f4 -= 6.2831855f;
        }
        return f4;
    }

    static float j(float f) {
        return a(f, 0.0f, 6.2831855f);
    }

    static float k(float f) {
        return a(f, -3.1415927f, 3.1415927f);
    }
}
