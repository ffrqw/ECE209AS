package com.shinobicontrols.charts;

class aa implements am {
    private final f cX = new f();

    aa() {
    }

    public boolean a(Series<?> series) {
        return this.cX.a(series);
    }

    public boolean a(Data<?, ?> data, Series<?> series) {
        Axis xAxis = series.getXAxis();
        Axis yAxis = series.getYAxis();
        if (xAxis == null || yAxis == null) {
            return false;
        }
        if (xAxis.isUserDataPointWithinASkipRange(data.getX()) || yAxis.isUserDataPointWithinASkipRange(data.getY())) {
            return true;
        }
        return false;
    }
}
