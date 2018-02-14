package com.shinobicontrols.charts;

public class BandSeriesStyle extends SeriesStyle {
    final fh<Float> bQ = new fh(Float.valueOf(2.0f));
    final fh<Integer> cA = new fh(Integer.valueOf(-16777216));
    final fh<Integer> cB = new fh(Integer.valueOf(-16777216));
    final fh<Integer> cC = new fh(Integer.valueOf(-16776961));
    final fh<Integer> cD = new fh(Integer.valueOf(0));
    PointStyle cE = new PointStyle(this);
    PointStyle cF = new PointStyle(this);
    final fh<Boolean> cz = new fh(Boolean.valueOf(true));

    void a(SeriesStyle seriesStyle) {
        synchronized (ah.lock) {
            super.a(seriesStyle);
            BandSeriesStyle bandSeriesStyle = (BandSeriesStyle) seriesStyle;
            this.cD.c(Integer.valueOf(bandSeriesStyle.getAreaColorInverted()));
            this.cC.c(Integer.valueOf(bandSeriesStyle.getAreaColorNormal()));
            this.cA.c(Integer.valueOf(bandSeriesStyle.getLineColorHigh()));
            this.cB.c(Integer.valueOf(bandSeriesStyle.getLineColorLow()));
            this.bQ.c(Float.valueOf(bandSeriesStyle.getLineWidth()));
            this.cz.c(Boolean.valueOf(bandSeriesStyle.isFillShown()));
            this.cE.a(bandSeriesStyle.getPointStyle());
            this.cF.a(bandSeriesStyle.getSelectedPointStyle());
        }
    }

    public boolean isFillShown() {
        return ((Boolean) this.cz.sU).booleanValue();
    }

    public void setFillShown(boolean shown) {
        synchronized (ah.lock) {
            this.cz.b(Boolean.valueOf(shown));
            ag();
        }
    }

    public int getLineColorHigh() {
        return ((Integer) this.cA.sU).intValue();
    }

    public void setLineColorHigh(int color) {
        synchronized (ah.lock) {
            this.cA.b(Integer.valueOf(color));
            ag();
        }
    }

    public int getLineColorLow() {
        return ((Integer) this.cB.sU).intValue();
    }

    public void setLineColorLow(int color) {
        synchronized (ah.lock) {
            this.cB.b(Integer.valueOf(color));
            ag();
        }
    }

    public float getLineWidth() {
        return ((Float) this.bQ.sU).floatValue();
    }

    public void setLineWidth(float width) {
        synchronized (ah.lock) {
            this.bQ.b(Float.valueOf(width));
            ag();
        }
    }

    public int getAreaColorNormal() {
        return ((Integer) this.cC.sU).intValue();
    }

    public void setAreaColorNormal(int color) {
        synchronized (ah.lock) {
            this.cC.b(Integer.valueOf(color));
            ag();
        }
    }

    public int getAreaColorInverted() {
        return ((Integer) this.cD.sU).intValue();
    }

    public void setAreaColorInverted(int color) {
        synchronized (ah.lock) {
            this.cD.b(Integer.valueOf(color));
            ag();
        }
    }

    public PointStyle getPointStyle() {
        return this.cE;
    }

    public void setPointStyle(PointStyle style) {
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

    public PointStyle getSelectedPointStyle() {
        return this.cF;
    }

    public void setSelectedPointStyle(PointStyle style) {
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
}
