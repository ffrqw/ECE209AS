package com.shinobicontrols.charts;

public class OHLCSeriesStyle extends SeriesStyle {
    final fh<Integer> dt = new fh(Integer.valueOf(-16777216));
    final fh<Integer> du = new fh(Integer.valueOf(0));
    final fh<Integer> dv = new fh(Integer.valueOf(-16777216));
    final fh<Integer> dw = new fh(Integer.valueOf(0));
    final fh<Float> lM = new fh(Float.valueOf(2.0f));
    final fh<Float> lN = new fh(Float.valueOf(2.0f));

    void a(SeriesStyle seriesStyle) {
        synchronized (ah.lock) {
            super.a(seriesStyle);
            OHLCSeriesStyle oHLCSeriesStyle = (OHLCSeriesStyle) seriesStyle;
            this.lN.c(Float.valueOf(oHLCSeriesStyle.getArmWidth()));
            this.dv.c(Integer.valueOf(oHLCSeriesStyle.getFallingColor()));
            this.dw.c(Integer.valueOf(oHLCSeriesStyle.getFallingColorGradient()));
            this.dt.c(Integer.valueOf(oHLCSeriesStyle.getRisingColor()));
            this.du.c(Integer.valueOf(oHLCSeriesStyle.getRisingColorGradient()));
            this.lM.c(Float.valueOf(oHLCSeriesStyle.getTrunkWidth()));
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

    public float getTrunkWidth() {
        return ((Float) this.lM.sU).floatValue();
    }

    public void setTrunkWidth(float width) {
        synchronized (ah.lock) {
            this.lM.b(Float.valueOf(width));
            ag();
        }
    }

    public float getArmWidth() {
        return ((Float) this.lN.sU).floatValue();
    }

    public void setArmWidth(float width) {
        synchronized (ah.lock) {
            this.lN.b(Float.valueOf(width));
            ag();
        }
    }
}
