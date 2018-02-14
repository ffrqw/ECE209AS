package com.shinobicontrols.charts;

import com.shinobicontrols.charts.InternalDataSeriesUpdater.PostUpdateCallback;
import com.shinobicontrols.charts.InternalDataSeriesUpdater.PreUpdateCallback;
import java.util.List;

class ax implements InternalDataSeriesUpdater {
    private final an gU;

    ax(an anVar) {
        this.gU = anVar;
    }

    public void a(Series<?> series, PreUpdateCallback preUpdateCallback, PostUpdateCallback postUpdateCallback) {
        preUpdateCallback.preAction(series);
        series.db.cV();
        List dataPointsForDisplay = series.oo.getDataPointsForDisplay();
        this.gU.a(dataPointsForDisplay, series.oI, series);
        for (bu a : series.oJ) {
            a.a(series, dataPointsForDisplay);
        }
        postUpdateCallback.postAction(series);
    }
}
