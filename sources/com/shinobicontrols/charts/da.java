package com.shinobicontrols.charts;

import com.shinobicontrols.charts.InternalDataSeriesUpdater.PreUpdateCallback;

class da implements PreUpdateCallback {
    da() {
    }

    public void preAction(Series<?> series) {
        Axis xAxis = series.getXAxis();
        Axis yAxis = series.getYAxis();
        if (xAxis != null) {
            xAxis.T();
        }
        if (yAxis != null) {
            yAxis.T();
        }
    }
}
