package com.shinobicontrols.charts;

import android.annotation.SuppressLint;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class cb {
    private final af J;
    private final float density;

    cb(ShinobiChart shinobiChart) {
        if (shinobiChart instanceof af) {
            this.J = (af) shinobiChart;
            this.density = this.J.getResources().getDisplayMetrics().density;
            return;
        }
        throw new IllegalStateException("Unable to retrieve LegendItems from Chart");
    }

    List<cc> a(LegendStyle legendStyle) {
        if (this.J.bn()) {
            return b(legendStyle);
        }
        return c(legendStyle);
    }

    private List<cc> b(LegendStyle legendStyle) {
        int i = 0;
        List arrayList = new ArrayList();
        if (this.J.getSeries().size() > 0) {
            Series series = (Series) this.J.getSeries().get(0);
            if (series.isShownInLegend() && (series instanceof PieDonutSeries)) {
                PieDonutSeries pieDonutSeries = (PieDonutSeries) series;
                int length = pieDonutSeries.db.je.length;
                while (i < length) {
                    arrayList.add(new cc(a(pieDonutSeries.C(i), s(i)), new cf(this.J.getContext(), pieDonutSeries.b(i, this.density)), pieDonutSeries, i));
                    i++;
                }
            }
        }
        return Collections.unmodifiableList(arrayList);
    }

    private List<cc> c(LegendStyle legendStyle) {
        List arrayList = new ArrayList();
        List series = this.J.getSeries();
        for (int i = 0; i < series.size(); i++) {
            Series series2 = (Series) series.get(i);
            if (series2.isShownInLegend()) {
                arrayList.add(new cc(a(series2.getTitle(), r(i)), new cf(this.J.getContext(), series2.c(this.density)), series2, -1));
            }
        }
        return Collections.unmodifiableList(arrayList);
    }

    @SuppressLint({"DefaultLocale"})
    private String r(int i) {
        return String.format("%s %d", new Object[]{"Series", Integer.valueOf(i + 1)});
    }

    @SuppressLint({"DefaultLocale"})
    private String s(int i) {
        return String.format("%s %d", new Object[]{"Slice", Integer.valueOf(i + 1)});
    }

    private TextView a(String str, String str2) {
        TextView textView = new TextView(this.J.getContext());
        textView.setText(b(str, str2));
        int c = ca.c(this.density, 5.0f);
        textView.setPadding(c, c, c, c);
        LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, -2);
        layoutParams.gravity = 1;
        textView.setLayoutParams(layoutParams);
        return textView;
    }

    private String b(String str, String str2) {
        return Axis.a(str) ? str2 : str;
    }
}
