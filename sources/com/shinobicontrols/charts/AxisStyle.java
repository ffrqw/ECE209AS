package com.shinobicontrols.charts;

public final class AxisStyle {
    final fh<Float> bN = new fh(Float.valueOf(0.0f));
    final fh<Float> bO = new fh(Float.valueOf(0.0f));
    final fh<Integer> bP = new fh(Integer.valueOf(-16777216));
    final fh<Float> bQ = new fh(Float.valueOf(1.0f));
    GridStripeStyle bR = new GridStripeStyle();
    GridlineStyle bS = new GridlineStyle();
    TickStyle bT = new TickStyle();
    AxisTitleStyle bU = new AxisTitleStyle();
    private final bg u = new bg();

    final void a(AxisStyle axisStyle) {
        if (axisStyle != null) {
            this.bN.c(Float.valueOf(axisStyle.getInterSeriesPadding()));
            this.bO.c(Float.valueOf(axisStyle.getInterSeriesSetPadding()));
            this.bP.c(Integer.valueOf(axisStyle.getLineColor()));
            this.bQ.c(Float.valueOf(axisStyle.getLineWidth()));
            this.bR.a(axisStyle.bR);
            this.bS.a(axisStyle.bS);
            this.bT.a(axisStyle.bT);
            this.bU.a(axisStyle.bU);
        }
    }

    public final GridStripeStyle getGridStripeStyle() {
        return this.bR;
    }

    public final void setGridStripeStyle(GridStripeStyle gridStripeStyle) {
        this.bR = gridStripeStyle;
    }

    public final float getInterSeriesPadding() {
        return ((Float) this.bN.sU).floatValue();
    }

    public final void setInterSeriesPadding(float interSeriesPadding) {
        if (interSeriesPadding < 0.0f || interSeriesPadding >= 1.0f) {
            interSeriesPadding = 0.0f;
        }
        synchronized (ah.lock) {
            this.bN.b(Float.valueOf(interSeriesPadding));
            ag();
        }
    }

    public final float getInterSeriesSetPadding() {
        return ((Float) this.bO.sU).floatValue();
    }

    public final void setInterSeriesSetPadding(float interSeriesSetPadding) {
        if (interSeriesSetPadding < 0.0f || interSeriesSetPadding >= 1.0f) {
            interSeriesSetPadding = 0.0f;
        }
        synchronized (ah.lock) {
            this.bO.b(Float.valueOf(interSeriesSetPadding));
            ag();
        }
    }

    public final int getLineColor() {
        return ((Integer) this.bP.sU).intValue();
    }

    public final void setLineColor(int lineColor) {
        this.bP.b(Integer.valueOf(lineColor));
    }

    public final float getLineWidth() {
        return ((Float) this.bQ.sU).floatValue();
    }

    public final void setLineWidth(float lineWidth) {
        this.bQ.b(Float.valueOf(lineWidth));
    }

    public final GridlineStyle getGridlineStyle() {
        return this.bS;
    }

    public final void setGridlineStyle(GridlineStyle gridlineStyle) {
        this.bS = gridlineStyle;
    }

    public final TickStyle getTickStyle() {
        return this.bT;
    }

    public final void setTickStyle(TickStyle tickStyle) {
        this.bT = tickStyle;
    }

    public final AxisTitleStyle getTitleStyle() {
        return this.bU;
    }

    public final void setTitleStyle(AxisTitleStyle titleStyle) {
        this.bU = titleStyle;
    }

    private final void ag() {
        this.u.a(new ea());
    }

    final bh a(a aVar) {
        return this.u.a(ea.A, (a) aVar);
    }
}
