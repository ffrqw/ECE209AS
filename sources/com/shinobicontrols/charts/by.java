package com.shinobicontrols.charts;

import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.PointF;
import android.graphics.Typeface;

class by {
    private final Paint fE = new Paint();
    private float jl;
    private float jm;
    private String jn;
    private float jo;
    private Typeface jp;

    by() {
    }

    void a(PointF pointF, String str, float f, Typeface typeface, af afVar) {
        if (str.equals(this.jn) && f == this.jo && typeface.equals(this.jp)) {
            pointF.x = this.jl;
            pointF.y = this.jm;
            return;
        }
        this.fE.setTextSize(afVar.getResources().getDisplayMetrics().scaledDensity * f);
        this.fE.setTypeface(typeface);
        FontMetrics fontMetrics = this.fE.getFontMetrics();
        pointF.x = a(str, this.fE);
        pointF.y = (float) Math.ceil((double) (fontMetrics.bottom - fontMetrics.top));
        this.jn = str;
        this.jo = f;
        this.jp = typeface;
        this.jl = pointF.x;
        this.jm = pointF.y;
    }

    void b(PointF pointF, String str, float f, Typeface typeface, af afVar) {
        a(pointF, str, f, typeface, afVar);
        pointF.y *= (float) e(str);
    }

    private float a(String str, Paint paint) {
        float f;
        if (str.contains("\n")) {
            f = 0.0f;
            for (String str2 : str.split("\n")) {
                if (paint.measureText(str2) > f) {
                    f = paint.measureText(str2);
                }
            }
        } else {
            f = paint.measureText(str);
        }
        return (float) Math.ceil((double) (f + 2.0f));
    }

    private int e(String str) {
        if (str == null || str.length() <= 0) {
            return 0;
        }
        return str.split("\n", -1).length;
    }
}
