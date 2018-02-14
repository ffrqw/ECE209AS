package com.shinobicontrols.charts;

import com.shinobicontrols.charts.InternalDataSeriesUpdater.PostUpdateCallback;

class t implements PostUpdateCallback {
    t() {
    }

    public void postAction(Series<?> series) {
        d(series);
    }

    private void d(Series<?> series) {
        float f = 0.0f;
        for (InternalDataPoint internalDataPoint : series.db.je) {
            f = (float) (((double) f) + internalDataPoint.y);
        }
        float f2 = 0.0f;
        for (InternalDataPoint internalDataPoint2 : series.db.je) {
            PieDonutSlice pieDonutSlice = (PieDonutSlice) internalDataPoint2;
            pieDonutSlice.mF = f2;
            pieDonutSlice.mG = (float) (((double) f2) + (((pieDonutSlice.y / ((double) f)) * 3.141592653589793d) * 2.0d));
            f2 = pieDonutSlice.mG;
        }
    }
}
