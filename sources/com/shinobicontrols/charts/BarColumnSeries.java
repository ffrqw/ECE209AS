package com.shinobicontrols.charts;

import android.graphics.drawable.Drawable;
import com.shinobicontrols.charts.Series.Orientation;
import com.shinobicontrols.charts.SeriesStyle.FillStyle;

abstract class BarColumnSeries<T extends SeriesStyle> extends CartesianSeries<T> {
    float cP = 0.8f;

    BarColumnSeries(Orientation orientation, ao dataLoadHelperFactory) {
        super(orientation, dataLoadHelperFactory);
    }

    float ao() {
        return this.cP;
    }

    void d(float f) {
        synchronized (ah.lock) {
            this.cP = f;
        }
    }

    void f(Axis<?, ?> axis) {
        if (axis.a(this.dR)) {
            for (InternalDataPoint a : this.db.je) {
                a(a, (Axis) axis);
            }
        }
    }

    private void a(InternalDataPoint internalDataPoint, Axis<?, ?> axis) {
        if (axis.P == Axis.Orientation.HORIZONTAL) {
            internalDataPoint.iP = a(internalDataPoint.x, (Axis) axis);
            internalDataPoint.iQ = internalDataPoint.y;
            return;
        }
        internalDataPoint.iQ = a(internalDataPoint.y, (Axis) axis);
        internalDataPoint.iP = internalDataPoint.x;
    }

    private double a(double d, Axis<?, ?> axis) {
        int ex = this.oy.ex();
        int aI = aI();
        double dF = axis.aj.dF();
        if (dF == 0.0d) {
            dF = 1.0d;
        }
        double d2 = axis.aj.nv;
        d2 += ((axis.S - d2) * dF) / dF;
        double floatValue = axis.T * ((double) (1.0f - ((Float) axis.aa.bO.sU).floatValue()));
        dF = ((double) (1.0f - ((Float) axis.aa.bN.sU).floatValue())) * (floatValue / ((double) ex));
        d((float) dF);
        return (((((double) ((Float) axis.aa.bN.sU).floatValue()) * floatValue) / ((double) (ex * 2))) + (((dF / 2.0d) + (d2 + (d - axis.S))) - (floatValue / 2.0d))) + ((((double) aI) * floatValue) / ((double) ex));
    }

    NumberRange a(double d, NumberRange numberRange) {
        return Range.h(numberRange) ? numberRange : new NumberRange(Double.valueOf(numberRange.nv - (d * 0.5d)), Double.valueOf(numberRange.nw + (d * 0.5d)));
    }

    NumberRange c(NumberRange numberRange) {
        if (Range.h(numberRange)) {
            return numberRange;
        }
        NumberRange numberRange2 = new NumberRange(Double.valueOf(numberRange.nv * 1.01d), Double.valueOf(numberRange.nw * 1.01d));
        numberRange2.l(this.oy.h(this));
        return numberRange2;
    }

    Drawable e(float f) {
        int i = 0;
        SeriesStyle seriesStyle = (!isSelected() || this.ov == null) ? this.ou : this.ov;
        BarColumnSeriesStyle barColumnSeriesStyle = (BarColumnSeriesStyle) seriesStyle;
        int areaColor = barColumnSeriesStyle.getFillStyle() == FillStyle.NONE ? 0 : barColumnSeriesStyle.getAreaColor();
        if (barColumnSeriesStyle.isLineShown()) {
            i = barColumnSeriesStyle.getLineColor();
        }
        return new ch(areaColor, i, f);
    }
}
