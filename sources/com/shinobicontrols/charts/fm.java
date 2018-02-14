package com.shinobicontrols.charts;

class fm extends q {
    fm() {
    }

    double a(Data<?, ?> data, Axis<?, ?> axis, Axis<?, ?> axis2) {
        return axis2.convertPoint(data.getX());
    }

    double a(MultiValueData<?> multiValueData, Axis<?, ?> axis, Axis<?, ?> axis2) {
        return axis.convertPoint(multiValueData.getHigh());
    }

    double b(MultiValueData<?> multiValueData, Axis<?, ?> axis, Axis<?, ?> axis2) {
        return axis.convertPoint(multiValueData.getLow());
    }
}
