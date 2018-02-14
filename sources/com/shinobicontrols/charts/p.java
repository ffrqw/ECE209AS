package com.shinobicontrols.charts;

abstract class p implements am {
    private final f cX = new f();

    abstract Axis<?, ?> a(Axis<?, ?> axis, Axis<?, ?> axis2);

    abstract Axis<?, ?> b(Axis<?, ?> axis, Axis<?, ?> axis2);

    p() {
    }

    public boolean a(Series<?> series) {
        return this.cX.a(series);
    }

    public boolean a(Data<?, ?> data, Series<?> series) {
        Axis xAxis = series.getXAxis();
        Axis yAxis = series.getYAxis();
        return a(a(xAxis, yAxis), (Data) data) || b(b(xAxis, yAxis), (Data) data);
    }

    boolean a(Axis<?, ?> axis, Data<?, ?> data) {
        return axis.isUserDataPointWithinASkipRange(data.getX());
    }

    boolean b(Axis<?, ?> axis, Data<?, ?> data) {
        return axis.isUserDataPointWithinASkipRange(((MultiValueData) data).getHigh()) || axis.isUserDataPointWithinASkipRange(((MultiValueData) data).getLow());
    }
}
