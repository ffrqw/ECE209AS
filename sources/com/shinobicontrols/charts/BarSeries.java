package com.shinobicontrols.charts;

import android.graphics.drawable.Drawable;
import com.shinobicontrols.charts.Series.Orientation;

public final class BarSeries extends BarColumnSeries<BarSeriesStyle> {
    final /* synthetic */ SeriesStyle an() {
        return ap();
    }

    final /* synthetic */ SeriesStyle b(fb fbVar, int i, boolean z) {
        return c(fbVar, i, z);
    }

    public BarSeries() {
        this(new y());
    }

    BarSeries(ao dataLoadHelperFactory) {
        super(Orientation.VERTICAL, dataLoadHelperFactory);
        this.da = true;
        this.ot = new n(this);
        this.dU = new m(this);
        setStyle(ap());
        setSelectedStyle(ap());
        this.oA = SeriesAnimation.createGrowHorizontalAnimation();
        this.oB = SeriesAnimation.createGrowHorizontalAnimation();
    }

    final BarSeriesStyle ap() {
        return new BarSeriesStyle();
    }

    final BarSeriesStyle c(fb fbVar, int i, boolean z) {
        return fbVar.e(i, z);
    }

    final NumberRange g(Axis<?, ?> axis) {
        return axis.h() ? c(aM()) : a(axis.j(), aN());
    }

    final Drawable c(float f) {
        if (((BarSeriesStyle) this.ou).eC()) {
            return null;
        }
        return e(f);
    }

    final void a(a aVar, du duVar, boolean z, b bVar) {
        ej.a((BarColumnSeries) this, aVar, duVar, z, a(bVar));
    }

    final a aq() {
        return a.VERTICAL;
    }

    final a ar() {
        return a.VERTICAL;
    }

    final ak am() {
        return new a();
    }

    final double as() {
        if (this.oz.pa != null) {
            return (getXAxis().ai.nv * (1.0d - ((double) this.oz.pa.floatValue()))) + (getXAxis().ai.nw * ((double) this.oz.pa.floatValue()));
        }
        return this.oy.h(this);
    }
}
