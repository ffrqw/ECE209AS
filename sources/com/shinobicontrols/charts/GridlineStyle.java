package com.shinobicontrols.charts;

public class GridlineStyle {
    final fh<Integer> bP = new fh(Integer.valueOf(-12303292));
    private final fh<Float> bQ = new fh(Float.valueOf(1.0f));
    final fh<Boolean> iG = new fh(Boolean.valueOf(false));
    final fh<Boolean> iH = new fh(Boolean.valueOf(false));
    final fh<float[]> iI = new fh(new float[]{1.0f, 1.0f});

    void a(GridlineStyle gridlineStyle) {
        if (gridlineStyle != null) {
            this.iG.c(Boolean.valueOf(gridlineStyle.areGridlinesShown()));
            this.iH.c(Boolean.valueOf(gridlineStyle.areGridlinesDashed()));
            this.bP.c(Integer.valueOf(gridlineStyle.getLineColor()));
            this.bQ.c(Float.valueOf(gridlineStyle.getLineWidth()));
            this.iI.c(gridlineStyle.getDashStyle());
        }
    }

    public boolean areGridlinesShown() {
        return ((Boolean) this.iG.sU).booleanValue();
    }

    public void setGridlinesShown(boolean showGridlines) {
        this.iG.b(Boolean.valueOf(showGridlines));
    }

    public boolean areGridlinesDashed() {
        return ((Boolean) this.iH.sU).booleanValue();
    }

    public void setGridlinesDashed(boolean dashedGridlines) {
        this.iH.b(Boolean.valueOf(dashedGridlines));
    }

    public int getLineColor() {
        return ((Integer) this.bP.sU).intValue();
    }

    public void setLineColor(int lineColor) {
        this.bP.b(Integer.valueOf(lineColor));
    }

    public float getLineWidth() {
        return ((Float) this.bQ.sU).floatValue();
    }

    public void setLineWidth(float lineWidth) {
        this.bQ.b(Float.valueOf(lineWidth));
    }

    public float[] getDashStyle() {
        return (float[]) this.iI.sU;
    }

    public void setDashStyle(float[] dashStyle) {
        this.iI.b(dashStyle);
    }
}
