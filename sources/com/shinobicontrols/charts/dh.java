package com.shinobicontrols.charts;

class dh implements am {
    private final f cX = new f();

    dh() {
    }

    public boolean a(Series<?> series) {
        return this.cX.a(series);
    }

    public boolean a(Data<?, ?> data, Series<?> series) {
        return c(series.getXAxis(), data) || d(series.getYAxis(), data);
    }

    private static boolean c(Axis<?, ?> axis, Data<?, ?> data) {
        return axis.isUserDataPointWithinASkipRange(data.getX());
    }

    private static boolean d(Axis<?, ?> axis, Data<?, ?> data) {
        return axis.isUserDataPointWithinASkipRange(((MultiValueData) data).getOpen()) || axis.isUserDataPointWithinASkipRange(((MultiValueData) data).getHigh()) || axis.isUserDataPointWithinASkipRange(((MultiValueData) data).getLow()) || axis.isUserDataPointWithinASkipRange(((MultiValueData) data).getClose());
    }
}
