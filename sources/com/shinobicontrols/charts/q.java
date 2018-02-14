package com.shinobicontrols.charts;

abstract class q implements bs {
    abstract double a(Data<?, ?> data, Axis<?, ?> axis, Axis<?, ?> axis2);

    abstract double a(MultiValueData<?> multiValueData, Axis<?, ?> axis, Axis<?, ?> axis2);

    abstract double b(MultiValueData<?> multiValueData, Axis<?, ?> axis, Axis<?, ?> axis2);

    q() {
    }

    public InternalDataPoint a(Data<?, ?> data, Series<?> series, int i) {
        Axis xAxis = series.getXAxis();
        Axis yAxis = series.getYAxis();
        if (xAxis == null || yAxis == null) {
            throw new IllegalStateException("Axes cannot be null when creating internal data points.");
        }
        MultiValueData multiValueData = (MultiValueData) data;
        InternalDataPoint internalDataPoint = new InternalDataPoint();
        double a = a((Data) data, xAxis, yAxis);
        internalDataPoint.x = a;
        internalDataPoint.iP = a;
        internalDataPoint.b(b(multiValueData, xAxis, yAxis), a(multiValueData, xAxis, yAxis));
        if (data instanceof SelectableData) {
            internalDataPoint.iU = ((SelectableData) data).getSelected();
        }
        internalDataPoint.iV = i;
        return internalDataPoint;
    }
}
