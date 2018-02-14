package com.shinobicontrols.charts;

import com.shinobicontrols.charts.SeriesStyle.FillStyle;
import java.util.Arrays;

class n extends ac<BarColumnSeries<?>> {
    float[] bZ;
    int cG = 0;
    private int cH;
    private int cI;
    private int cJ;
    private int cK;
    private int cL;
    private int cM;
    private boolean cN;
    private FillStyle cO;
    private float cP;
    private float ct;
    private int[] cy = new int[2];
    private final int orientation;
    float[] points;

    n(BarColumnSeries<?> barColumnSeries) {
        super(barColumnSeries);
        this.orientation = barColumnSeries.dR.eo();
    }

    void i(int i) {
        if (this.points == null || this.points.length != i) {
            this.points = new float[i];
        }
    }

    void j(int i) {
        if (this.bZ == null || this.bZ.length != i) {
            this.bZ = new float[i];
        }
    }

    void a(SChartGLDrawer sChartGLDrawer, float[] fArr, float f) {
        if (((BarColumnSeries) this.cZ).eh()) {
            a((BarColumnSeries) this.cZ, (double) f);
            boolean z = !(this.cH == 0 && this.cK == 0) && this.cN;
            Object obj = this.cO != FillStyle.NONE ? 1 : null;
            if (this.cO != FillStyle.GRADIENT) {
                this.cJ = this.cI;
                this.cM = this.cL;
            }
            if (z && obj == null) {
                this.cI = 0;
                this.cL = this.cI;
                this.cJ = this.cI;
                this.cM = this.cI;
                obj = 1;
            }
            if (obj != null) {
                sChartGLDrawer.drawBarColumnFill(((BarColumnSeries) this.cZ).ej(), this.points, this.cZ, this.bZ, this.cy, this.cG, this.cI, this.cL, this.cJ, this.cM, this.cP, this.dJ, this.orientation, z, fArr);
            }
            if (z) {
                sChartGLDrawer.drawBarColumnLine(((BarColumnSeries) this.cZ).ej(), this.points, this.cZ, this.bZ, this.cy, this.cG, this.cH, this.cK, this.cP, this.ct, this.dJ, this.orientation, fArr);
            }
            if (this.bZ != null) {
                Arrays.fill(this.bZ, 0.0f);
            }
        }
    }

    private void a(BarColumnSeries<?> barColumnSeries, double d) {
        BarColumnSeriesStyle barColumnSeriesStyle = (BarColumnSeriesStyle) (barColumnSeries.dM ? barColumnSeries.ov : barColumnSeries.ou);
        this.cP = barColumnSeries.ao();
        this.db = barColumnSeries.db;
        this.cH = barColumnSeriesStyle.getLineColor();
        this.ct = barColumnSeriesStyle.getLineWidth();
        this.cI = barColumnSeriesStyle.getAreaColor();
        this.cJ = barColumnSeriesStyle.getAreaColorGradient();
        this.cK = barColumnSeriesStyle.getLineColorBelowBaseline();
        this.cL = barColumnSeriesStyle.getAreaColorBelowBaseline();
        this.cM = barColumnSeriesStyle.getAreaColorGradientBelowBaseline();
        this.cN = barColumnSeriesStyle.isLineShown();
        this.cO = barColumnSeriesStyle.getFillStyle();
        this.ct = (float) (((double) this.ct) * d);
        this.ct *= 2.0f;
    }
}
