package com.shinobicontrols.charts;

import android.graphics.drawable.Drawable;
import com.shinobicontrols.charts.Series.Orientation;

public final class ColumnSeries extends BarColumnSeries<ColumnSeriesStyle> {
    final /* synthetic */ SeriesStyle an() {
        return bM();
    }

    final /* synthetic */ SeriesStyle b(fb fbVar, int i, boolean z) {
        return e(fbVar, i, z);
    }

    public ColumnSeries() {
        this(new y());
    }

    ColumnSeries(ao dataLoadHelperFactory) {
        super(Orientation.HORIZONTAL, dataLoadHelperFactory);
        this.da = true;
        this.ot = new n(this);
        this.dU = new m(this);
        setStyle(bM());
        setSelectedStyle(bM());
        this.oA = SeriesAnimation.createGrowVerticalAnimation();
        this.oB = SeriesAnimation.createGrowVerticalAnimation();
    }

    final ColumnSeriesStyle bM() {
        return new ColumnSeriesStyle();
    }

    final ColumnSeriesStyle e(fb fbVar, int i, boolean z) {
        return fbVar.d(i, z);
    }

    final NumberRange g(Axis<?, ?> axis) {
        return axis.h() ? a(axis.j(), aM()) : c(aN());
    }

    final Drawable c(float f) {
        if (((ColumnSeriesStyle) this.ou).eC()) {
            return null;
        }
        return e(f);
    }

    final void a(a aVar, du duVar, boolean z, b bVar) {
        ej.b((BarColumnSeries) this, aVar, duVar, z, a(bVar));
    }

    final a aq() {
        return a.HORIZONTAL;
    }

    final a ar() {
        return a.HORIZONTAL;
    }

    final double al() {
        if (this.oz.pb != null) {
            return (getYAxis().ai.nv * (1.0d - ((double) this.oz.pb.floatValue()))) + (getYAxis().ai.nw * ((double) this.oz.pb.floatValue()));
        }
        return this.oy.h(this);
    }

    final ak am() {
        return new b();
    }
}
