package com.shinobicontrols.charts;

import android.graphics.Typeface;

public class CrosshairStyle {
    final fh<Integer> bP = new fh(Integer.valueOf(-16777216));
    final fh<Float> bQ = new fh(Float.valueOf(0.0f));
    final fh<Float> ge = new fh(Float.valueOf(0.0f));
    final fh<Typeface> gf = new fh(null);
    final fh<Float> gg = new fh(Float.valueOf(12.0f));
    final fh<Integer> gh = new fh(Integer.valueOf(-16777216));
    final fh<Integer> gi = new fh(Integer.valueOf(0));
    final fh<Integer> gj = new fh(Integer.valueOf(0));
    final fh<Float> gk = new fh(Float.valueOf(0.0f));
    final fh<Float> gl = new fh(Float.valueOf(0.0f));
    final fh<Integer> gm = new fh(Integer.valueOf(0));

    public float getLineWidth() {
        return ((Float) this.bQ.sU).floatValue();
    }

    public void setLineWidth(float lineWidth) {
        this.bQ.b(Float.valueOf(lineWidth));
    }

    public int getLineColor() {
        return ((Integer) this.bP.sU).intValue();
    }

    public void setLineColor(int lineColor) {
        this.bP.b(Integer.valueOf(lineColor));
    }

    public float getTooltipPadding() {
        return ((Float) this.ge.sU).floatValue();
    }

    public void setTooltipPadding(float tooltipPadding) {
        this.ge.b(Float.valueOf(tooltipPadding));
    }

    public Typeface getTooltipTypeface() {
        return (Typeface) this.gf.sU;
    }

    public void setTooltipTypeface(Typeface tooltipTypeface) {
        this.gf.b(tooltipTypeface);
    }

    public float getTooltipTextSize() {
        return ((Float) this.gg.sU).floatValue();
    }

    public void setTooltipTextSize(float tooltipTextSize) {
        this.gg.b(Float.valueOf(tooltipTextSize));
    }

    public int getTooltipTextColor() {
        return ((Integer) this.gh.sU).intValue();
    }

    public void setTooltipTextColor(int tooltipTextColor) {
        this.gh.b(Integer.valueOf(tooltipTextColor));
    }

    public int getTooltipLabelBackgroundColor() {
        return ((Integer) this.gi.sU).intValue();
    }

    public void setTooltipLabelBackgroundColor(int tooltipLabelBackgroundColor) {
        this.gi.b(Integer.valueOf(tooltipLabelBackgroundColor));
    }

    public int getTooltipBackgroundColor() {
        return ((Integer) this.gj.sU).intValue();
    }

    public void setTooltipBackgroundColor(int tooltipBackgroundColor) {
        this.gj.b(Integer.valueOf(tooltipBackgroundColor));
    }

    public float getTooltipCornerRadius() {
        return ((Float) this.gk.sU).floatValue();
    }

    public void setTooltipCornerRadius(float tooltipCornerRadius) {
        this.gk.b(Float.valueOf(tooltipCornerRadius));
    }

    public float getTooltipBorderWidth() {
        return ((Float) this.gl.sU).floatValue();
    }

    public void setTooltipBorderWidth(float tooltipBorderWidth) {
        this.gl.b(Float.valueOf(tooltipBorderWidth));
    }

    public int getTooltipBorderColor() {
        return ((Integer) this.gm.sU).intValue();
    }

    public void setTooltipBorderColor(int tooltipBorderColor) {
        this.gm.b(Integer.valueOf(tooltipBorderColor));
    }

    void a(CrosshairStyle crosshairStyle) {
        if (crosshairStyle != null) {
            this.bQ.c(crosshairStyle.bQ.sU);
            this.bP.c(crosshairStyle.bP.sU);
            this.ge.c(crosshairStyle.ge.sU);
            this.gf.c(crosshairStyle.gf.sU);
            this.gg.c(crosshairStyle.gg.sU);
            this.gh.c(crosshairStyle.gh.sU);
            this.gi.c(crosshairStyle.gi.sU);
            this.gj.c(crosshairStyle.gj.sU);
            this.gk.c(crosshairStyle.gk.sU);
            this.gl.c(crosshairStyle.gl.sU);
            this.gm.c(crosshairStyle.gm.sU);
        }
    }
}
