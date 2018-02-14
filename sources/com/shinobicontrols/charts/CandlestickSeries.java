package com.shinobicontrols.charts;

import android.graphics.drawable.Drawable;
import com.shinobicontrols.charts.Series.Orientation;

public class CandlestickSeries extends BarColumnSeries<CandlestickSeriesStyle> {
    /* synthetic */ SeriesStyle an() {
        return ay();
    }

    /* synthetic */ SeriesStyle b(fb fbVar, int i, boolean z) {
        return d(fbVar, i, z);
    }

    public CandlestickSeries() {
        this(new de());
    }

    CandlestickSeries(ao dataLoadHelperFactory) {
        super(Orientation.HORIZONTAL, dataLoadHelperFactory);
        this.ot = new v(this);
        this.dU = new u(this);
        setStyle(ay());
        setSelectedStyle(ay());
        this.oA = SeriesAnimation.createGrowVerticalAnimation();
        this.oB = SeriesAnimation.createGrowVerticalAnimation();
    }

    CandlestickSeriesStyle ay() {
        return new CandlestickSeriesStyle();
    }

    CandlestickSeriesStyle d(fb fbVar, int i, boolean z) {
        return fbVar.b(i, z);
    }

    Drawable c(float f) {
        CandlestickSeriesStyle candlestickSeriesStyle = (!isSelected() || this.ov == null) ? (CandlestickSeriesStyle) this.ou : (CandlestickSeriesStyle) this.ov;
        if (candlestickSeriesStyle.eC()) {
            return new ci();
        }
        return new ch(candlestickSeriesStyle.getRisingColor(), candlestickSeriesStyle.getOutlineColor(), f);
    }

    NumberRange g(Axis<?, ?> axis) {
        return axis.h() ? a(axis.j(), aM()) : c(aN());
    }

    NumberRange c(NumberRange numberRange) {
        if (Range.h(numberRange)) {
            return numberRange;
        }
        return new NumberRange(Double.valueOf(numberRange.nv * 1.01d), Double.valueOf(numberRange.nw * 1.01d));
    }

    a aq() {
        return a.HORIZONTAL;
    }

    a ar() {
        return a.HORIZONTAL;
    }

    public void setBaseline(Object baseline) {
        ev.g(this.J != null ? this.J.getContext().getString(R.string.CandlestickSeriesBaselineNotApplicable) : "Baseline not applicable for CandlestickSeries.");
    }

    ak am() {
        return new d();
    }

    double al() {
        if (this.oz.pb != null) {
            return (getYAxis().ai.nv * (1.0d - ((double) this.oz.pb.floatValue()))) + (getYAxis().ai.nw * ((double) this.oz.pb.floatValue()));
        }
        return this.oy.h(this);
    }
}
