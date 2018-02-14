package com.shinobicontrols.charts;

class f implements am {
    f() {
    }

    public boolean a(Series<?> series) {
        Axis xAxis = series.getXAxis();
        Axis yAxis = series.getYAxis();
        return (xAxis == null || yAxis == null || (!xAxis.ab() && !yAxis.ab())) ? false : true;
    }

    public boolean a(Data<?, ?> data, Series<?> series) {
        return false;
    }
}
