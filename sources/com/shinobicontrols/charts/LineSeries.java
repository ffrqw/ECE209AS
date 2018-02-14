package com.shinobicontrols.charts;

import android.graphics.drawable.Drawable;
import com.shinobicontrols.charts.Series.Orientation;
import com.shinobicontrols.charts.Series.SelectionMode;
import com.shinobicontrols.charts.SeriesStyle.FillStyle;
import java.util.ArrayList;
import java.util.List;

public final class LineSeries extends CartesianSeries<LineSeriesStyle> {
    private final dv kS;
    private DataValueInterpolator<?, ?> kT;
    final List<InternalDataPoint> kU;
    final a ko;
    final a kp;

    final /* synthetic */ SeriesStyle an() {
        return dg();
    }

    final /* synthetic */ SeriesStyle b(fb fbVar, int i, boolean z) {
        return g(fbVar, i, z);
    }

    public LineSeries() {
        this(new cp());
    }

    LineSeries(ao dataLoadHelperFactory) {
        super(Orientation.HORIZONTAL, dataLoadHelperFactory);
        this.kS = new dv();
        this.kT = as.gt;
        this.kU = new ArrayList();
        this.ko = new a();
        this.kp = new a();
        this.ot = new ct(this, this.ko, this.kp);
        this.dU = new cq(this, this.ko, this.kp);
        setStyle(dg());
        setSelectedStyle(dg());
        this.oA = SeriesAnimation.createTelevisionAnimation();
        this.oB = SeriesAnimation.createTelevisionAnimation();
    }

    final LineSeriesStyle dg() {
        return new LineSeriesStyle();
    }

    final LineSeriesStyle g(fb fbVar, int i, boolean z) {
        return fbVar.f(i, z);
    }

    final Drawable c(float f) {
        LineSeriesStyle lineSeriesStyle = (!isSelected() || this.ov == null) ? (LineSeriesStyle) this.ou : (LineSeriesStyle) this.ov;
        if (lineSeriesStyle.eC()) {
            return new cj();
        }
        return new ch(lineSeriesStyle.getFillStyle() == FillStyle.NONE ? lineSeriesStyle.getLineColor() : lineSeriesStyle.getAreaColor(), lineSeriesStyle.getFillStyle() == FillStyle.NONE ? lineSeriesStyle.getLineColor() : lineSeriesStyle.getAreaLineColor(), f);
    }

    final void a(a aVar, du duVar, boolean z, b bVar) {
        ej.b((CartesianSeries) this, aVar, duVar, z, a(bVar));
    }

    final InternalDataPoint[] a(b bVar) {
        return (b(bVar) || c(bVar)) ? this.db.cS() : this.db.je;
    }

    private boolean b(b bVar) {
        return bVar == b.CROSSHAIR_ENABLED && this.J != null && this.J.getCrosshair().isLineSeriesInterpolationEnabled();
    }

    private boolean c(b bVar) {
        return bVar == b.SELECTION_MODE_NOT_NONE && this.ox == SelectionMode.SERIES;
    }

    final void a(a aVar, du duVar) {
        if (this.J.ev == null || !this.J.ev.fI) {
            super.a(aVar, duVar);
            return;
        }
        du a = a(aVar.el(), duVar);
        aVar.a(new du(a.x, a.y));
    }

    final a ar() {
        switch ((a) ((LineSeriesStyle) this.ou).kZ.sU) {
            case HORIZONTAL:
                return a.HORIZONTAL;
            case VERTICAL:
                return a.VERTICAL;
            default:
                return a.HORIZONTAL;
        }
    }

    private du a(InternalDataPoint internalDataPoint, du duVar) {
        return this.kS.a(internalDataPoint, duVar, this.db.cS(), ((LineSeriesStyle) this.ou).kZ.sU == a.HORIZONTAL);
    }

    final ak am() {
        return new c();
    }

    final double al() {
        if (this.oz.pb != null) {
            return (getYAxis().ai.nv * (1.0d - ((double) this.oz.pb.floatValue()))) + (getYAxis().ai.nw * ((double) this.oz.pb.floatValue()));
        }
        return this.oy.h(this);
    }

    public final void setLinePathInterpolator(DataValueInterpolator<?, ?> linePathInterpolator) {
        if (linePathInterpolator == null) {
            linePathInterpolator = as.gt;
        }
        this.kT = linePathInterpolator;
        eg();
        fireUpdateHandler();
    }

    public final DataValueInterpolator<?, ?> getLinePathInterpolator() {
        return this.kT == as.gt ? null : this.kT;
    }

    final DataValueInterpolator<?, ?> dh() {
        return this.kT;
    }

    final boolean aO() {
        return true;
    }

    final boolean di() {
        return (this.dL == null || aP()) ? false : true;
    }
}
