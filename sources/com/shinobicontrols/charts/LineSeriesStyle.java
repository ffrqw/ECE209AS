package com.shinobicontrols.charts;

import com.shinobicontrols.charts.SeriesStyle.FillStyle;

public final class LineSeriesStyle extends SeriesStyle {
    final fh<Integer> bP = new fh(Integer.valueOf(-16777216));
    final fh<Float> bQ = new fh(Float.valueOf(1.0f));
    private PointStyle cE = new PointStyle(this);
    private PointStyle cF = new PointStyle(this);
    final fh<Integer> cQ = new fh(Integer.valueOf(0));
    final fh<Integer> cR = new fh(Integer.valueOf(0));
    final fh<Integer> cS = new fh(Integer.valueOf(0));
    final fh<Integer> cT = new fh(Integer.valueOf(0));
    final fh<Integer> cU = new fh(Integer.valueOf(-16777216));
    final fh<Boolean> cV = new fh(Boolean.valueOf(true));
    final fh<FillStyle> cW = new fh(FillStyle.NONE);
    final fh<Integer> kW = new fh(Integer.valueOf(-16777216));
    final fh<Integer> kX = new fh(Integer.valueOf(0));
    final fh<Float> kY = new fh(Float.valueOf(1.0f));
    fh<a> kZ = new fh(a.HORIZONTAL);

    enum a {
        HORIZONTAL,
        VERTICAL
    }

    final void a(SeriesStyle seriesStyle) {
        synchronized (ah.lock) {
            super.a(seriesStyle);
            LineSeriesStyle lineSeriesStyle = (LineSeriesStyle) seriesStyle;
            this.cQ.c(Integer.valueOf(lineSeriesStyle.getAreaColor()));
            this.cR.c(Integer.valueOf(lineSeriesStyle.getAreaColorBelowBaseline()));
            this.cS.c(Integer.valueOf(lineSeriesStyle.getAreaColorGradientBelowBaseline()));
            this.cT.c(Integer.valueOf(lineSeriesStyle.getAreaColorGradient()));
            this.kW.c(Integer.valueOf(lineSeriesStyle.getAreaLineColor()));
            this.kX.c(Integer.valueOf(lineSeriesStyle.getAreaLineColorBelowBaseline()));
            this.cW.c(lineSeriesStyle.getFillStyle());
            this.bP.c(Integer.valueOf(lineSeriesStyle.getLineColor()));
            this.cU.c(Integer.valueOf(lineSeriesStyle.getLineColorBelowBaseline()));
            this.bQ.c(Float.valueOf(lineSeriesStyle.getLineWidth()));
            this.cV.c(Boolean.valueOf(lineSeriesStyle.isLineShown()));
            this.cE.a(lineSeriesStyle.getPointStyle());
            this.cF.a(lineSeriesStyle.getSelectedPointStyle());
        }
    }

    public final int getAreaColor() {
        return ((Integer) this.cQ.sU).intValue();
    }

    public final void setAreaColor(int areaColor) {
        synchronized (ah.lock) {
            this.cQ.b(Integer.valueOf(areaColor));
            ag();
        }
    }

    public final int getAreaColorBelowBaseline() {
        return ((Integer) this.cR.sU).intValue();
    }

    public final void setAreaColorBelowBaseline(int areaColorBelowBaseline) {
        synchronized (ah.lock) {
            this.cR.b(Integer.valueOf(areaColorBelowBaseline));
            ag();
        }
    }

    public final int getAreaColorGradient() {
        return ((Integer) this.cT.sU).intValue();
    }

    public final void setAreaColorGradient(int areaColorGradient) {
        synchronized (ah.lock) {
            this.cT.b(Integer.valueOf(areaColorGradient));
            ag();
        }
    }

    public final int getAreaColorGradientBelowBaseline() {
        return ((Integer) this.cS.sU).intValue();
    }

    public final void setAreaColorGradientBelowBaseline(int areaColorGradientBelowBaseline) {
        synchronized (ah.lock) {
            this.cS.b(Integer.valueOf(areaColorGradientBelowBaseline));
            ag();
        }
    }

    public final int getAreaLineColor() {
        return ((Integer) this.kW.sU).intValue();
    }

    public final void setAreaLineColor(int areaLineColor) {
        synchronized (ah.lock) {
            this.kW.b(Integer.valueOf(areaLineColor));
            ag();
        }
    }

    public final int getAreaLineColorBelowBaseline() {
        return ((Integer) this.kX.sU).intValue();
    }

    public final void setAreaLineColorBelowBaseline(int areaLineColorBelowBaseline) {
        synchronized (ah.lock) {
            this.kX.b(Integer.valueOf(areaLineColorBelowBaseline));
            ag();
        }
    }

    public final float getAreaLineWidth() {
        return ((Float) this.kY.sU).floatValue();
    }

    public final void setAreaLineWidth(float areaLineWidth) {
        synchronized (ah.lock) {
            this.kY.b(Float.valueOf(areaLineWidth));
            ag();
        }
    }

    public final FillStyle getFillStyle() {
        return (FillStyle) this.cW.sU;
    }

    public final void setFillStyle(FillStyle fillStyle) {
        synchronized (ah.lock) {
            this.cW.b(fillStyle);
            ag();
        }
    }

    public final int getLineColor() {
        return ((Integer) this.bP.sU).intValue();
    }

    public final void setLineColor(int lineColor) {
        synchronized (ah.lock) {
            this.bP.b(Integer.valueOf(lineColor));
            ag();
        }
    }

    public final int getLineColorBelowBaseline() {
        return ((Integer) this.cU.sU).intValue();
    }

    public final void setLineColorBelowBaseline(int lineColorBelowBaseline) {
        synchronized (ah.lock) {
            this.cU.b(Integer.valueOf(lineColorBelowBaseline));
            ag();
        }
    }

    public final float getLineWidth() {
        return ((Float) this.bQ.sU).floatValue();
    }

    public final void setLineWidth(float lineWidth) {
        synchronized (ah.lock) {
            this.bQ.b(Float.valueOf(lineWidth));
            ag();
        }
    }

    public final PointStyle getPointStyle() {
        return this.cE;
    }

    public final void setPointStyle(PointStyle style) {
        if (style == null) {
            throw new IllegalArgumentException("Styles may not be null");
        }
        synchronized (ah.lock) {
            this.cE.nl = null;
            this.cE = style;
            this.cE.nl = this;
            ag();
        }
    }

    public final PointStyle getSelectedPointStyle() {
        return this.cF;
    }

    public final void setSelectedPointStyle(PointStyle style) {
        if (style == null) {
            throw new IllegalArgumentException("Styles may not be null");
        }
        synchronized (ah.lock) {
            this.cF.nl = null;
            this.cF = style;
            this.cF.nl = this;
            ag();
        }
    }

    public final boolean isLineShown() {
        return ((Boolean) this.cV.sU).booleanValue();
    }

    public final void setLineShown(boolean lineShown) {
        synchronized (ah.lock) {
            this.cV.b(Boolean.valueOf(lineShown));
            ag();
        }
    }
}
