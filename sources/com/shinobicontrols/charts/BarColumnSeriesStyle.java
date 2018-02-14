package com.shinobicontrols.charts;

import com.shinobicontrols.charts.SeriesStyle.FillStyle;

abstract class BarColumnSeriesStyle extends SeriesStyle {
    final fh<Integer> bP = new fh(Integer.valueOf(-16777216));
    final fh<Float> bQ = new fh(Float.valueOf(1.0f));
    final fh<Integer> cQ = new fh(Integer.valueOf(-16777216));
    final fh<Integer> cR = new fh(Integer.valueOf(-16777216));
    final fh<Integer> cS = new fh(Integer.valueOf(-16777216));
    final fh<Integer> cT = new fh(Integer.valueOf(-16777216));
    final fh<Integer> cU = new fh(Integer.valueOf(-16777216));
    final fh<Boolean> cV = new fh(Boolean.valueOf(false));
    final fh<FillStyle> cW = new fh(FillStyle.GRADIENT);

    BarColumnSeriesStyle() {
    }

    void a(SeriesStyle seriesStyle) {
        synchronized (ah.lock) {
            super.a(seriesStyle);
            BarColumnSeriesStyle barColumnSeriesStyle = (BarColumnSeriesStyle) seriesStyle;
            this.cQ.c(Integer.valueOf(barColumnSeriesStyle.getAreaColor()));
            this.cR.c(Integer.valueOf(barColumnSeriesStyle.getAreaColorBelowBaseline()));
            this.cS.c(Integer.valueOf(barColumnSeriesStyle.getAreaColorGradientBelowBaseline()));
            this.cT.c(Integer.valueOf(barColumnSeriesStyle.getAreaColorGradient()));
            this.bP.c(Integer.valueOf(barColumnSeriesStyle.getLineColor()));
            this.cU.c(Integer.valueOf(barColumnSeriesStyle.getLineColorBelowBaseline()));
            this.bQ.c(Float.valueOf(barColumnSeriesStyle.getLineWidth()));
            this.cW.c(barColumnSeriesStyle.getFillStyle());
            this.cV.c(Boolean.valueOf(barColumnSeriesStyle.isLineShown()));
        }
    }

    public int getAreaColor() {
        return ((Integer) this.cQ.sU).intValue();
    }

    public void setAreaColor(int areaColor) {
        synchronized (ah.lock) {
            this.cQ.b(Integer.valueOf(areaColor));
            ag();
        }
    }

    public int getAreaColorBelowBaseline() {
        return ((Integer) this.cR.sU).intValue();
    }

    public void setAreaColorBelowBaseline(int areaColorBelowBaseline) {
        synchronized (ah.lock) {
            this.cR.b(Integer.valueOf(areaColorBelowBaseline));
            ag();
        }
    }

    public int getAreaColorGradient() {
        return ((Integer) this.cT.sU).intValue();
    }

    public void setAreaColorGradient(int areaColorGradient) {
        synchronized (ah.lock) {
            this.cT.b(Integer.valueOf(areaColorGradient));
            ag();
        }
    }

    public int getAreaColorGradientBelowBaseline() {
        return ((Integer) this.cS.sU).intValue();
    }

    public void setAreaColorGradientBelowBaseline(int areaColorGradientBelowBaseline) {
        synchronized (ah.lock) {
            this.cS.b(Integer.valueOf(areaColorGradientBelowBaseline));
            ag();
        }
    }

    public int getLineColor() {
        return ((Integer) this.bP.sU).intValue();
    }

    public void setLineColor(int lineColor) {
        synchronized (ah.lock) {
            this.bP.b(Integer.valueOf(lineColor));
            ag();
        }
    }

    public int getLineColorBelowBaseline() {
        return ((Integer) this.cU.sU).intValue();
    }

    public void setLineColorBelowBaseline(int lineColorBelowBaseline) {
        synchronized (ah.lock) {
            this.cU.b(Integer.valueOf(lineColorBelowBaseline));
            ag();
        }
    }

    public float getLineWidth() {
        return ((Float) this.bQ.sU).floatValue();
    }

    public void setLineWidth(float lineWidth) {
        synchronized (ah.lock) {
            this.bQ.b(Float.valueOf(lineWidth));
            ag();
        }
    }

    public boolean isLineShown() {
        return ((Boolean) this.cV.sU).booleanValue();
    }

    public void setLineShown(boolean showLine) {
        synchronized (ah.lock) {
            this.cV.b(Boolean.valueOf(showLine));
            ag();
        }
    }

    public FillStyle getFillStyle() {
        return (FillStyle) this.cW.sU;
    }

    public void setFillStyle(FillStyle fillStyle) {
        synchronized (ah.lock) {
            this.cW.b(fillStyle);
            ag();
        }
    }
}
