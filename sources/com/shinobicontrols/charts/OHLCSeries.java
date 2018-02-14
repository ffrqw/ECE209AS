package com.shinobicontrols.charts;

import android.graphics.drawable.Drawable;
import com.shinobicontrols.charts.Series.Orientation;

public class OHLCSeries extends BarColumnSeries<OHLCSeriesStyle> {
    /* synthetic */ SeriesStyle an() {
        return dl();
    }

    /* synthetic */ SeriesStyle b(fb fbVar, int i, boolean z) {
        return h(fbVar, i, z);
    }

    public OHLCSeries() {
        this(new de());
    }

    OHLCSeries(ao dataLoadHelperFactory) {
        super(Orientation.HORIZONTAL, dataLoadHelperFactory);
        this.ot = new dj(this);
        this.dU = new dg(this);
        setStyle(dl());
        setSelectedStyle(dl());
        this.oA = SeriesAnimation.createGrowVerticalAnimation();
        this.oB = SeriesAnimation.createGrowVerticalAnimation();
    }

    OHLCSeriesStyle dl() {
        return new OHLCSeriesStyle();
    }

    OHLCSeriesStyle h(fb fbVar, int i, boolean z) {
        return fbVar.c(i, z);
    }

    Drawable c(float f) {
        OHLCSeriesStyle oHLCSeriesStyle = (!isSelected() || this.ov == null) ? (OHLCSeriesStyle) this.ou : (OHLCSeriesStyle) this.ov;
        if (oHLCSeriesStyle.eC()) {
            return new ck();
        }
        return new ch(oHLCSeriesStyle.getRisingColor(), oHLCSeriesStyle.getRisingColor(), f);
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
        ev.g(this.J != null ? this.J.getContext().getString(R.string.OHLCSeriesBaselineNotApplicable) : "Baseline not applicable for OHLCSeries.");
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
