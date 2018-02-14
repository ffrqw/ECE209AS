package com.shinobicontrols.charts;

import java.util.List;

class es {
    private final eh pC;

    es(eh ehVar) {
        this.pC = ehVar;
    }

    void a(List<Series<?>> list, boolean z, bd bdVar, SChartGLDrawer sChartGLDrawer) {
        if (list == null) {
            throw new IllegalArgumentException("Cannot draw or render a null list of series (an empty list is acceptable however).");
        }
        this.pC.reset();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            Series series = (Series) list.get(i);
            this.pC.a((long) series.db.je.length);
            if (z) {
                if (!series.oC) {
                    series.ot.a(bdVar, sChartGLDrawer);
                }
                series.ot.ax();
            }
            if (!series.oC) {
                series.ot.b(bdVar, sChartGLDrawer);
            }
        }
    }
}
