package com.shinobicontrols.charts;

class di implements bs {
    di() {
    }

    public InternalDataPoint a(Data<?, ?> data, Series<?> series, int i) {
        Axis xAxis = series.getXAxis();
        Axis yAxis = series.getYAxis();
        if (xAxis == null || yAxis == null) {
            throw new IllegalStateException("Axes cannot be null when converting data points.");
        }
        InternalDataPoint internalDataPoint = new InternalDataPoint();
        double convertPoint = xAxis.convertPoint(data.getX());
        internalDataPoint.x = convertPoint;
        internalDataPoint.iP = convertPoint;
        MultiValueData multiValueData = (MultiValueData) data;
        internalDataPoint.a(yAxis.convertPoint(multiValueData.getOpen()), yAxis.convertPoint(multiValueData.getHigh()), yAxis.convertPoint(multiValueData.getLow()), yAxis.convertPoint(multiValueData.getClose()));
        if (data instanceof SelectableData) {
            internalDataPoint.iU = ((SelectableData) data).getSelected();
        }
        internalDataPoint.iV = i;
        return internalDataPoint;
    }
}
