package com.shinobicontrols.charts;

import java.util.ArrayList;
import java.util.List;

class dz implements bu {
    private final List<InternalDataPoint> gT = new ArrayList();
    private final al kB;

    dz(al alVar) {
        this.kB = alVar;
    }

    public void a(Series<?> series, List<? extends Data<?, ?>> list) {
        this.gT.clear();
        this.gT.addAll(this.kB.a(list, series.oL, series.db, series.oM, series));
        series.db.n(this.gT.size());
        series.db.je = (InternalDataPoint[]) this.gT.toArray(series.db.je);
    }
}
