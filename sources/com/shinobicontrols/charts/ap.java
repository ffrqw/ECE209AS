package com.shinobicontrols.charts;

import com.shinobicontrols.charts.Series.Orientation;

class ap {
    static DataPoint<?, ?> a(InternalDataPoint internalDataPoint, CartesianSeries<?> cartesianSeries) {
        if (internalDataPoint.iX) {
            return a(internalDataPoint.x, internalDataPoint, (CartesianSeries) cartesianSeries);
        }
        return a(internalDataPoint.x, internalDataPoint.y, internalDataPoint.iU, cartesianSeries);
    }

    static DataPoint<?, ?> b(InternalDataPoint internalDataPoint, CartesianSeries<?> cartesianSeries) {
        return a(internalDataPoint.iP, internalDataPoint.iQ, internalDataPoint.iU, cartesianSeries);
    }

    static DataPoint<?, ?> a(du duVar, InternalDataPoint internalDataPoint, CartesianSeries<?> cartesianSeries) {
        return a(duVar.x, duVar.y, internalDataPoint.iU, cartesianSeries);
    }

    private static DataPoint<?, ?> a(double d, double d2, boolean z, CartesianSeries<?> cartesianSeries) {
        return new DataPoint(cartesianSeries.getXAxis().transformInternalValueToUser(d), cartesianSeries.getYAxis().transformInternalValueToUser(d2), z);
    }

    private static DataPoint<?, ?> a(double d, InternalDataPoint internalDataPoint, CartesianSeries<?> cartesianSeries) {
        Axis b = b(cartesianSeries);
        Axis c = c(cartesianSeries);
        Comparable transformInternalValueToUser = b.transformInternalValueToUser(d);
        Comparable transformInternalValueToUser2 = c.transformInternalValueToUser(((Double) internalDataPoint.iW.get("Low")).doubleValue());
        Comparable transformInternalValueToUser3 = c.transformInternalValueToUser(((Double) internalDataPoint.iW.get("High")).doubleValue());
        Double d2 = (Double) internalDataPoint.iW.get("Open");
        Double d3 = (Double) internalDataPoint.iW.get("Close");
        if (d2 == null || d3 == null) {
            return new MultiValueDataPoint(transformInternalValueToUser, transformInternalValueToUser2, transformInternalValueToUser3, internalDataPoint.iU);
        }
        return new MultiValueDataPoint(transformInternalValueToUser, transformInternalValueToUser2, transformInternalValueToUser3, c.transformInternalValueToUser(d2.doubleValue()), c.transformInternalValueToUser(d3.doubleValue()), internalDataPoint.iU);
    }

    private static Axis<?, ?> b(CartesianSeries<?> cartesianSeries) {
        return d(cartesianSeries) ? cartesianSeries.getYAxis() : cartesianSeries.getXAxis();
    }

    private static Axis<?, ?> c(CartesianSeries<?> cartesianSeries) {
        return d(cartesianSeries) ? cartesianSeries.getXAxis() : cartesianSeries.getYAxis();
    }

    private static boolean d(CartesianSeries<?> cartesianSeries) {
        return cartesianSeries.dR == Orientation.VERTICAL;
    }
}
