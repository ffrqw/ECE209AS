package com.shinobicontrols.charts;

class bq extends q {
    bq() {
    }

    double a(Data<?, ?> data, Axis<?, ?> axis, Axis<?, ?> axis2) {
        return axis.convertPoint(data.getX());
    }

    double a(MultiValueData<?> multiValueData, Axis<?, ?> axis, Axis<?, ?> axis2) {
        return axis2.convertPoint(multiValueData.getHigh());
    }

    double b(MultiValueData<?> multiValueData, Axis<?, ?> axis, Axis<?, ?> axis2) {
        return axis2.convertPoint(multiValueData.getLow());
    }
}
