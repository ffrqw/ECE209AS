package com.shinobicontrols.charts;

import android.graphics.drawable.Drawable;
import com.shinobicontrols.charts.Series.Orientation;

public class BandSeries extends CartesianSeries<BandSeriesStyle> {
    /* synthetic */ SeriesStyle an() {
        return ak();
    }

    /* synthetic */ SeriesStyle b(fb fbVar, int i, boolean z) {
        return a(fbVar, i, z);
    }

    public BandSeries() {
        this(Orientation.HORIZONTAL);
    }

    public BandSeries(Orientation orientation) {
        this(orientation, b(orientation));
    }

    BandSeries(Orientation orientation, ao dataLoadHelperFactory) {
        super(orientation, dataLoadHelperFactory);
        this.ot = new l(this);
        this.dU = new k(this);
        setStyle(ak());
        setSelectedStyle(ak());
        this.oA = SeriesAnimation.createTelevisionAnimation();
        this.oB = SeriesAnimation.createTelevisionAnimation();
    }

    private static ao b(Orientation orientation) {
        return orientation == Orientation.HORIZONTAL ? new bn() : new fj();
    }

    BandSeriesStyle ak() {
        return new BandSeriesStyle();
    }

    BandSeriesStyle a(fb fbVar, int i, boolean z) {
        return fbVar.a(i, z);
    }

    Drawable c(float f) {
        BandSeriesStyle bandSeriesStyle = (!isSelected() || this.ov == null) ? (BandSeriesStyle) this.ou : (BandSeriesStyle) this.ov;
        if (bandSeriesStyle.eC()) {
            return new cg();
        }
        return new ch(bandSeriesStyle.getAreaColorNormal(), bandSeriesStyle.getLineColorHigh(), f);
    }

    double al() {
        if (this.oz.pb != null) {
            return (getYAxis().ai.nv * (1.0d - ((double) this.oz.pb.floatValue()))) + (getYAxis().ai.nw * ((double) this.oz.pb.floatValue()));
        }
        return this.oy.h(this);
    }

    public void setBaseline(Object baseline) {
        ev.g(this.J != null ? this.J.getContext().getString(R.string.BandSeriesBaselineNotApplicable) : "Baseline not applicable for BandSeries.");
    }

    ak am() {
        return new d();
    }
}
