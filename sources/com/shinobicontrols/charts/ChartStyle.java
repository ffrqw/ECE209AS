package com.shinobicontrols.charts;

public final class ChartStyle {
    final fh<Integer> F = new fh(Integer.valueOf(-1));
    final fh<Integer> fm = new fh(Integer.valueOf(-1));
    final fh<Integer> fn = new fh(Integer.valueOf(-1));
    final fh<Float> fo = new fh(Float.valueOf(0.0f));
    final fh<Integer> fp = new fh(Integer.valueOf(-1));
    final fh<Integer> fq = new fh(Integer.valueOf(-1));
    final fh<Integer> fr = new fh(Integer.valueOf(-1));
    final fh<Float> fs = new fh(Float.valueOf(0.0f));

    final void b(ChartStyle chartStyle) {
        if (chartStyle != null) {
            this.F.c(Integer.valueOf(chartStyle.getBackgroundColor()));
            this.fm.c(Integer.valueOf(chartStyle.bI()));
            this.fn.c(Integer.valueOf(chartStyle.getBorderColor()));
            this.fo.c(Float.valueOf(chartStyle.getBorderWidth()));
            this.fp.c(Integer.valueOf(chartStyle.getCanvasBackgroundColor()));
            this.fq.c(Integer.valueOf(chartStyle.getPlotAreaBackgroundColor()));
            this.fr.c(Integer.valueOf(chartStyle.bJ()));
            this.fs.c(Float.valueOf(chartStyle.bK()));
        }
    }

    public final int getBackgroundColor() {
        return ((Integer) this.F.sU).intValue();
    }

    public final void setBackgroundColor(int backgroundColor) {
        this.F.b(Integer.valueOf(backgroundColor));
    }

    final int bI() {
        return ((Integer) this.fm.sU).intValue();
    }

    final int getBorderColor() {
        return ((Integer) this.fn.sU).intValue();
    }

    final float getBorderWidth() {
        return ((Float) this.fo.sU).floatValue();
    }

    public final int getCanvasBackgroundColor() {
        return ((Integer) this.fp.sU).intValue();
    }

    public final void setCanvasBackgroundColor(int canvasBackgroundColor) {
        this.fp.b(Integer.valueOf(canvasBackgroundColor));
    }

    public final int getPlotAreaBackgroundColor() {
        return ((Integer) this.fq.sU).intValue();
    }

    public final void setPlotAreaBackgroundColor(int plotAreaBackgroundColor) {
        this.fq.b(Integer.valueOf(plotAreaBackgroundColor));
    }

    final int bJ() {
        return ((Integer) this.fr.sU).intValue();
    }

    final float bK() {
        return ((Float) this.fs.sU).floatValue();
    }
}
