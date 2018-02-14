package com.shinobicontrols.charts;

import java.util.ArrayList;
import java.util.List;

class cs implements bu {
    private final List<InternalDataPoint> gT = new ArrayList();
    private final al kB;

    cs(al alVar) {
        this.kB = alVar;
    }

    public void a(Series<?> series, List<? extends Data<?, ?>> list) {
        DataValueInterpolator dh = ((LineSeries) series).dh();
        this.gT.clear();
        this.gT.addAll(this.kB.a(a(dh, (List) list), series.oL, series.db, series.oM, series));
        series.db.m(this.gT);
    }

    private <Tx, Ty> List<? extends Data<?, ?>> a(DataValueInterpolator<?, ?> dataValueInterpolator, List<? extends Data<?, ?>> list) {
        return dataValueInterpolator.getDataValuesForDisplay(list);
    }
}
