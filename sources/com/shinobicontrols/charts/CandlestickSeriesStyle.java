package com.shinobicontrols.charts;

public class CandlestickSeriesStyle extends SeriesStyle {
    final fh<Float> dA = new fh(Float.valueOf(2.0f));
    final fh<Integer> dt = new fh(Integer.valueOf(-16777216));
    final fh<Integer> du = new fh(Integer.valueOf(0));
    final fh<Integer> dv = new fh(Integer.valueOf(-16777216));
    final fh<Integer> dw = new fh(Integer.valueOf(0));
    final fh<Integer> dx = new fh(Integer.valueOf(-16777216));
    final fh<Integer> dy = new fh(Integer.valueOf(-16777216));
    final fh<Float> dz = new fh(Float.valueOf(2.0f));

    void a(SeriesStyle seriesStyle) {
        synchronized (ah.lock) {
            super.a(seriesStyle);
            CandlestickSeriesStyle candlestickSeriesStyle = (CandlestickSeriesStyle) seriesStyle;
            this.dv.c(Integer.valueOf(candlestickSeriesStyle.getFallingColor()));
            this.dw.c(Integer.valueOf(candlestickSeriesStyle.getFallingColorGradient()));
            this.dt.c(Integer.valueOf(candlestickSeriesStyle.getRisingColor()));
            this.du.c(Integer.valueOf(candlestickSeriesStyle.getRisingColorGradient()));
            this.dx.c(Integer.valueOf(candlestickSeriesStyle.getOutlineColor()));
            this.dA.c(Float.valueOf(candlestickSeriesStyle.getOutlineWidth()));
            this.dy.c(Integer.valueOf(candlestickSeriesStyle.getStickColor()));
            this.dz.c(Float.valueOf(candlestickSeriesStyle.getStickWidth()));
        }
    }

    public int getRisingColor() {
        return ((Integer) this.dt.sU).intValue();
    }

    public void setRisingColor(int color) {
        synchronized (ah.lock) {
            this.dt.b(Integer.valueOf(color));
            ag();
        }
    }

    public int getRisingColorGradient() {
        return ((Integer) this.du.sU).intValue();
    }

    public void setRisingColorGradient(int color) {
        synchronized (ah.lock) {
            this.du.b(Integer.valueOf(color));
            ag();
        }
    }

    public int getFallingColor() {
        return ((Integer) this.dv.sU).intValue();
    }

    public void setFallingColor(int color) {
        synchronized (ah.lock) {
            this.dv.b(Integer.valueOf(color));
            ag();
        }
    }

    public int getFallingColorGradient() {
        return ((Integer) this.dw.sU).intValue();
    }

    public void setFallingColorGradient(int color) {
        synchronized (ah.lock) {
            this.dw.b(Integer.valueOf(color));
            ag();
        }
    }

    public int getOutlineColor() {
        return ((Integer) this.dx.sU).intValue();
    }

    public void setOutlineColor(int color) {
        synchronized (ah.lock) {
            this.dx.b(Integer.valueOf(color));
            ag();
        }
    }

    public int getStickColor() {
        return ((Integer) this.dy.sU).intValue();
    }

    public void setStickColor(int color) {
        synchronized (ah.lock) {
            this.dy.b(Integer.valueOf(color));
            ag();
        }
    }

    public float getStickWidth() {
        return ((Float) this.dz.sU).floatValue();
    }

    public void setStickWidth(float width) {
        synchronized (ah.lock) {
            this.dz.b(Float.valueOf(width));
            ag();
        }
    }

    public float getOutlineWidth() {
        return ((Float) this.dA.sU).floatValue();
    }

    public void setOutlineWidth(float width) {
        synchronized (ah.lock) {
            this.dA.b(Float.valueOf(width));
            ag();
        }
    }
}
