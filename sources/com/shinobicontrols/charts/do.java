package com.shinobicontrols.charts;

class do implements bs {
    do() {
    }

    public InternalDataPoint a(Data<?, ?> data, Series<?> series, int i) {
        double d = (double) i;
        double doubleValue = ((Number) data.getY()).doubleValue();
        if (doubleValue < 0.0d) {
            throw new IllegalArgumentException(series.J.getContext().getString(R.string.PieDonutSeriesYDataMustBePositive));
        }
        InternalDataPoint a = a(d, doubleValue, data.getX().toString());
        if (data instanceof SelectableData) {
            a.iU = ((SelectableData) data).getSelected();
            a.mI = a.iU ? ((PieDonutSeriesStyle) series.ov).getProtrusion() : ((PieDonutSeriesStyle) series.ou).getProtrusion();
        }
        a.iV = i;
        return a;
    }

    private PieDonutSlice a(double d, double d2, String str) {
        PieDonutSlice pieDonutSlice = new PieDonutSlice(d, d2);
        pieDonutSlice.mE = str;
        return pieDonutSlice;
    }
}
