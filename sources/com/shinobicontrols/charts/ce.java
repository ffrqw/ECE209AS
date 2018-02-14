package com.shinobicontrols.charts;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import java.util.List;

class ce extends LinearLayout {
    private final float density = getResources().getDisplayMetrics().density;

    public ce(Context context) {
        super(context);
        setOrientation(0);
    }

    void a(List<cc> list, int i) {
        removeAllViews();
        if (i != 0) {
            int min = Math.min(list.size(), i);
            t(min);
            for (int i2 = 0; i2 < list.size(); i2++) {
                cc ccVar = (cc) list.get(i2);
                View childAt = getChildAt(i2 % min);
                if (childAt instanceof cd) {
                    ((cd) childAt).a(ccVar);
                }
            }
        }
    }

    private void t(int i) {
        for (int i2 = 0; i2 < i; i2++) {
            View cdVar = new cd(getContext());
            cdVar.setLayoutParams(new LayoutParams(-2, -2));
            addView(cdVar);
        }
    }

    void d(LegendStyle legendStyle) {
        int c = ca.c(this.density, legendStyle.getSymbolLabelGap() / 2.0f);
        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);
            if (childAt instanceof cd) {
                int i2;
                int i3;
                cd cdVar = (cd) childAt;
                cdVar.d(legendStyle);
                if (i > 0) {
                    i2 = c;
                } else {
                    i2 = 0;
                }
                if (i < getChildCount() - 1) {
                    i3 = c;
                } else {
                    i3 = 0;
                }
                a(cdVar, i2, i3);
            }
        }
    }

    private void a(cd cdVar, int i, int i2) {
        LayoutParams layoutParams = (LayoutParams) cdVar.getLayoutParams();
        layoutParams.leftMargin = i;
        layoutParams.rightMargin = i2;
    }
}
