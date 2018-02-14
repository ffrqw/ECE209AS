package com.shinobicontrols.charts;

class ab implements bs {
    ab() {
    }

    public InternalDataPoint a(Data<?, ?> data, Series<?> series, int i) {
        Axis xAxis = series.getXAxis();
        Axis yAxis = series.getYAxis();
        if (xAxis == null || yAxis == null) {
            throw new IllegalStateException("Axes cannot be null when converting data points.");
        }
        InternalDataPoint internalDataPoint = new InternalDataPoint(xAxis.convertPoint(data.getX()), yAxis.convertPoint(data.getY()));
        if (data instanceof SelectableData) {
            internalDataPoint.iU = ((SelectableData) data).getSelected();
        }
        internalDataPoint.iV = i;
        return internalDataPoint;
    }
}
