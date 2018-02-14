package com.shinobicontrols.charts;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;

class ca {
    static int a(View view) {
        if (view == null || view.getVisibility() == 8) {
            return 0;
        }
        MarginLayoutParams marginLayoutParams = (MarginLayoutParams) view.getLayoutParams();
        return marginLayoutParams.bottomMargin + (view.getMeasuredHeight() + marginLayoutParams.topMargin);
    }

    static int b(View view) {
        if (view == null || view.getVisibility() == 8) {
            return 0;
        }
        MarginLayoutParams marginLayoutParams = (MarginLayoutParams) view.getLayoutParams();
        return marginLayoutParams.rightMargin + (view.getMeasuredWidth() + marginLayoutParams.leftMargin);
    }

    static void a(View view, Rect rect) {
        MarginLayoutParams marginLayoutParams = (MarginLayoutParams) view.getLayoutParams();
        rect.left += marginLayoutParams.leftMargin;
        rect.top += marginLayoutParams.topMargin;
        rect.right -= marginLayoutParams.rightMargin;
        rect.bottom -= marginLayoutParams.bottomMargin;
    }

    static void b(View view, Rect rect) {
        view.layout(rect.left, rect.top, rect.right, rect.bottom);
    }

    static void a(View view, ed edVar) {
        view.layout((int) Math.round(edVar.oc), (int) Math.round(edVar.od), (int) Math.round(edVar.oe), (int) Math.round(edVar.of));
    }

    static int a(float f, int i, float f2) {
        return (int) ((((float) i) + (f2 * f)) + 0.51f);
    }

    static int c(float f, float f2) {
        return (int) ((f2 * f) + 0.51f);
    }
}
