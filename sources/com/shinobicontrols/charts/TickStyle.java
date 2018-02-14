package com.shinobicontrols.charts;

import android.graphics.Typeface;
import com.shinobicontrols.charts.TickMark.Orientation;

public final class TickStyle {
    final fh<Integer> bP = new fh(Integer.valueOf(-16777216));
    final fh<Float> bQ = new fh(Float.valueOf(1.0f));
    final fh<Typeface> mA = new fh(Typeface.DEFAULT);
    final fh<Float> mB = new fh(Float.valueOf(10.0f));
    final fh<Integer> sq = new fh(Integer.valueOf(-16777216));
    final fh<Integer> sr = new fh(Integer.valueOf(-1));
    final fh<Float> ss = new fh(Float.valueOf(1.0f));
    final fh<Boolean> st = new fh(Boolean.valueOf(true));
    final fh<Boolean> su = new fh(Boolean.valueOf(true));
    final fh<Boolean> sv = new fh(Boolean.valueOf(false));
    final fh<Float> sw = new fh(Float.valueOf(1.0f));
    final fh<Orientation> sx = new fh(Orientation.HORIZONTAL);

    final void a(TickStyle tickStyle) {
        if (tickStyle != null) {
            this.sq.c(tickStyle.sq.sU);
            this.mA.c(tickStyle.mA.sU);
            this.mB.c(tickStyle.mB.sU);
            this.sr.c(tickStyle.sr.sU);
            this.bP.c(tickStyle.bP.sU);
            this.ss.c(tickStyle.ss.sU);
            this.bQ.c(tickStyle.bQ.sU);
            this.st.c(tickStyle.st.sU);
            this.su.c(tickStyle.su.sU);
            this.sv.c(tickStyle.sv.sU);
            this.sw.c(tickStyle.sw.sU);
            this.sx.c(tickStyle.sx.sU);
        }
    }

    public final int getLabelColor() {
        return ((Integer) this.sq.sU).intValue();
    }

    public final Typeface getLabelTypeface() {
        return (Typeface) this.mA.sU;
    }

    public final float getLabelTextSize() {
        return ((Float) this.mB.sU).floatValue();
    }

    public final int getLabelTextShadowColor() {
        return ((Integer) this.sr.sU).intValue();
    }

    public final int getLineColor() {
        return ((Integer) this.bP.sU).intValue();
    }

    public final float getLineLength() {
        return ((Float) this.ss.sU).floatValue();
    }

    public final float getLineWidth() {
        return ((Float) this.bQ.sU).floatValue();
    }

    public final float getTickGap() {
        return ((Float) this.sw.sU).floatValue();
    }

    public final Orientation getLabelOrientation() {
        return (Orientation) this.sx.sU;
    }

    public final boolean areLabelsShown() {
        return ((Boolean) this.st.sU).booleanValue();
    }

    public final boolean areMajorTicksShown() {
        return ((Boolean) this.su.sU).booleanValue();
    }

    public final boolean areMinorTicksShown() {
        return ((Boolean) this.sv.sU).booleanValue();
    }

    public final void setLabelColor(int labelColor) {
        this.sq.b(Integer.valueOf(labelColor));
    }

    public final void setLabelTypeface(Typeface labelTypeface) {
        this.mA.b(labelTypeface);
    }

    public final void setLabelTextSize(float labelTextSize) {
        this.mB.b(Float.valueOf(labelTextSize));
    }

    public final void setLabelTextShadowColor(int labelTextShadowColor) {
        this.sr.b(Integer.valueOf(labelTextShadowColor));
    }

    public final void setLineColor(int lineColor) {
        this.bP.b(Integer.valueOf(lineColor));
    }

    public final void setLineLength(float lineLength) {
        this.ss.b(Float.valueOf(lineLength));
    }

    public final void setLineWidth(float lineWidth) {
        this.bQ.b(Float.valueOf(lineWidth));
    }

    public final void setLabelsShown(boolean showLabels) {
        this.st.b(Boolean.valueOf(showLabels));
    }

    public final void setMajorTicksShown(boolean showTicks) {
        this.su.b(Boolean.valueOf(showTicks));
    }

    public final void setMinorTicksShown(boolean showTicks) {
        this.sv.b(Boolean.valueOf(showTicks));
    }

    public final void setTickGap(float tickGap) {
        this.sw.b(Float.valueOf(tickGap));
    }

    public final void setLabelOrientation(Orientation tickLabelOrientation) {
        this.sx.b(tickLabelOrientation);
    }
}
