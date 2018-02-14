package com.shinobicontrols.charts;

import java.util.ArrayList;
import java.util.List;

class av implements al {
    private final List<InternalDataPoint> gT = new ArrayList();

    av() {
    }

    public List<InternalDataPoint> a(List<? extends Data<?, ?>> list, bs bsVar, bt btVar, aq aqVar, Series<?> series) {
        this.gT.clear();
        Object[] toArray = list.toArray();
        int length = toArray.length;
        int i = 0;
        while (i < length) {
            try {
                InternalDataPoint a = bsVar.a((Data) toArray[i], series, i);
                this.gT.add(a);
                aqVar.a(btVar, a);
                i++;
            } catch (IllegalArgumentException e) {
                ev.g(series.J.getContext().getString(R.string.SeriesCannotAddPoint) + " " + e.getMessage());
                throw e;
            }
        }
        return this.gT;
    }
}
