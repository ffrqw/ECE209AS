package com.shinobicontrols.charts;

import android.graphics.drawable.Drawable;

public final class PointStyle {
    final fh<Float> lZ = new fh(Float.valueOf(3.0f));
    final fh<Integer> nd = new fh(Integer.valueOf(-16777216));
    final fh<Integer> ne = new fh(Integer.valueOf(-16777216));
    final fh<Float> nf = new fh(Float.valueOf(0.0f));
    final fh<Integer> ng = new fh(Integer.valueOf(-16777216));
    final fh<Integer> nh = new fh(Integer.valueOf(-16777216));
    final fh<Float> ni = new fh(Float.valueOf(5.0f));
    final fh<Boolean> nj = new fh(Boolean.valueOf(false));
    final fh<Drawable> nk = new fh(null);
    SeriesStyle nl = null;

    PointStyle(SeriesStyle parentStyle) {
        this.nl = parentStyle;
    }

    final void a(PointStyle pointStyle) {
        synchronized (ah.lock) {
            if (pointStyle == null) {
                return;
            }
            this.nd.c(Integer.valueOf(pointStyle.getColor()));
            this.ne.c(Integer.valueOf(pointStyle.getColorBelowBaseline()));
            this.nf.c(Float.valueOf(pointStyle.dw()));
            this.ng.c(Integer.valueOf(pointStyle.getInnerColor()));
            this.nh.c(Integer.valueOf(pointStyle.getInnerColorBelowBaseline()));
            this.lZ.c(Float.valueOf(pointStyle.getInnerRadius()));
            this.ni.c(Float.valueOf(pointStyle.getRadius()));
            this.nj.c(Boolean.valueOf(pointStyle.arePointsShown()));
            this.nk.c(pointStyle.dx());
        }
    }

    public final int getColor() {
        return ((Integer) this.nd.sU).intValue();
    }

    public final void setColor(int color) {
        synchronized (ah.lock) {
            this.nd.b(Integer.valueOf(color));
            ag();
        }
    }

    public final int getColorBelowBaseline() {
        return ((Integer) this.ne.sU).intValue();
    }

    public final void setColorBelowBaseline(int colorBelowBaseline) {
        synchronized (ah.lock) {
            this.ne.b(Integer.valueOf(colorBelowBaseline));
            ag();
        }
    }

    final float dw() {
        return ((Float) this.nf.sU).floatValue();
    }

    final void m(float f) {
        synchronized (ah.lock) {
            this.nf.b(Float.valueOf(f));
            ag();
        }
    }

    public final int getInnerColor() {
        return ((Integer) this.ng.sU).intValue();
    }

    public final void setInnerColor(int innerColor) {
        synchronized (ah.lock) {
            this.ng.b(Integer.valueOf(innerColor));
            ag();
        }
    }

    public final int getInnerColorBelowBaseline() {
        return ((Integer) this.nh.sU).intValue();
    }

    public final void setInnerColorBelowBaseline(int innerColorBelowBaseline) {
        synchronized (ah.lock) {
            this.nh.b(Integer.valueOf(innerColorBelowBaseline));
            ag();
        }
    }

    public final float getInnerRadius() {
        return ((Float) this.lZ.sU).floatValue();
    }

    public final void setInnerRadius(float innerRadius) {
        synchronized (ah.lock) {
            this.lZ.b(Float.valueOf(innerRadius));
            ag();
        }
    }

    public final float getRadius() {
        return ((Float) this.ni.sU).floatValue();
    }

    public final void setRadius(float radius) {
        synchronized (ah.lock) {
            this.ni.b(Float.valueOf(radius));
            ag();
        }
    }

    public final boolean arePointsShown() {
        return ((Boolean) this.nj.sU).booleanValue();
    }

    public final void setPointsShown(boolean showPoints) {
        synchronized (ah.lock) {
            this.nj.b(Boolean.valueOf(showPoints));
            ag();
        }
    }

    final Drawable dx() {
        return (Drawable) this.nk.sU;
    }

    final void ag() {
        if (this.nl != null) {
            this.nl.ag();
        }
    }
}
