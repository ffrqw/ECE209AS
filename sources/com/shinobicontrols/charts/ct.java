package com.shinobicontrols.charts;

import com.shinobicontrols.charts.SeriesStyle.FillStyle;

class ct extends ac<LineSeries> {
    private int cH;
    private int cK;
    private boolean cN;
    private FillStyle cO;
    private int cq;
    private int cr;
    private float ct;
    private float cu;
    private float cv;
    private final int[] cy = new int[4];
    private int fillColor;
    private float kC;
    private float kD;
    private int kE;
    private int kF;
    private int kG;
    private int kH;
    private int kI;
    private int kJ;
    private int kK;
    private int kL;
    private int kM;
    private int kN;
    private int kO;
    private boolean kP;
    private float[] kQ;
    private float[] kR;
    private final a ko;
    private final a kp;
    private int kq;
    private boolean kr;
    private float[] ks;
    private int[] kt;
    private int ku;
    private float kv;
    private float kw;

    ct(LineSeries lineSeries, a aVar, a aVar2) {
        super(lineSeries);
        this.ko = aVar;
        this.kp = aVar2;
    }

    void a(SChartGLDrawer sChartGLDrawer, float[] fArr, float f) {
        if (((LineSeries) this.cZ).eh()) {
            a((LineSeries) this.cZ, (double) f);
            a(this.ko, this.kp);
            df();
            a(sChartGLDrawer, this.kq);
            b(sChartGLDrawer, this.kq);
            a(sChartGLDrawer);
        }
    }

    private void a(a aVar, a aVar2) {
        this.dJ = aVar.dJ;
        this.kq = aVar2.kq;
        this.kQ = aVar2.points;
        this.ks = aVar2.ks;
        this.ku = aVar2.ku;
        this.kv = aVar2.kv;
        this.kw = aVar2.kw;
        this.kr = aVar.kr;
        this.kR = aVar.points;
        this.kt = aVar.kt;
        this.cq = aVar.cq;
        this.cr = aVar.cr;
    }

    private void df() {
        if (((LineSeries) this.cZ).di()) {
            this.db.l(((LineSeries) this.cZ).kU);
        }
    }

    private void a(SChartGLDrawer sChartGLDrawer, int i) {
        if (this.cO == FillStyle.NONE) {
            return;
        }
        if (this.kP) {
            sChartGLDrawer.drawBandSeriesFill(((LineSeries) this.cZ).ej(), this.kQ, this.cZ, this.ks, this.cy, i, this.fillColor, this.kN, ((LineSeries) this.cZ).dR.eo(), this.ku, this.cY);
        } else {
            sChartGLDrawer.drawHorizontalFill(((LineSeries) this.cZ).ej(), this.kQ, this.cZ, this.cy, i, this.fillColor, this.kN, this.kM, this.kO, x(this.kM), x(this.kO), this.dJ, this.kv, this.kw, 1 - ((LineSeries) this.cZ).dR.eo(), this.cO == FillStyle.GRADIENT, this.cY);
        }
    }

    private void b(SChartGLDrawer sChartGLDrawer, int i) {
        if (x(this.cH) && this.cN) {
            sChartGLDrawer.drawLineStrip(((LineSeries) this.cZ).ej(), this.kQ, this.cZ, this.cy, i, this.cH, this.cK, this.ct, this.dJ, 1 - ((LineSeries) this.cZ).dR.eo(), this.cY);
        }
    }

    private void a(SChartGLDrawer sChartGLDrawer) {
        int i;
        Object obj;
        if (this.kr) {
            if (ai()) {
                i = 0;
                for (InternalDataPoint internalDataPoint : this.db.je) {
                    if (!internalDataPoint.iU) {
                        this.kR[i] = (float) internalDataPoint.iP;
                        this.kR[i + 1] = (float) internalDataPoint.iQ;
                        this.kt[i / 2] = 0;
                        i += 2;
                    }
                }
                sChartGLDrawer.drawDataPoints(((LineSeries) this.cZ).ej(), this.kR, this.cZ, this.cy, this.kt, i, this.kE, this.kG, this.cu, this.dJ, 1 - ((LineSeries) this.cZ).dR.eo(), this.cY);
                obj = (this.kC <= 0.0f || this.kF == 0) ? null : 1;
                if (obj != null) {
                    sChartGLDrawer.drawDataPoints(((LineSeries) this.cZ).ej(), this.kR, this.cZ, this.cy, this.kt, i, this.kF, this.kH, this.kC, this.dJ, 1 - ((LineSeries) this.cZ).dR.eo(), this.cY);
                }
            }
            if (aj()) {
                i = 0;
                for (InternalDataPoint internalDataPoint2 : this.db.je) {
                    if (internalDataPoint2.iU) {
                        this.kR[i] = (float) internalDataPoint2.iP;
                        this.kR[i + 1] = (float) internalDataPoint2.iQ;
                        this.kt[i / 2] = 0;
                        i += 2;
                    }
                }
                sChartGLDrawer.drawDataPoints(((LineSeries) this.cZ).ej(), this.kR, this.cZ, this.cy, this.kt, i, this.kI, this.kK, this.cv, this.dJ, 1 - ((LineSeries) this.cZ).dR.eo(), this.cY);
                obj = (this.kD <= 0.0f || this.kJ == 0) ? null : 1;
                if (obj != null) {
                    sChartGLDrawer.drawDataPoints(((LineSeries) this.cZ).ej(), this.kR, this.cZ, this.cy, this.kt, i, this.kJ, this.kL, this.kD, this.dJ, 1 - ((LineSeries) this.cZ).dR.eo(), this.cY);
                }
            }
        } else if (ai()) {
            i = 0;
            for (InternalDataPoint internalDataPoint22 : this.db.je) {
                if (!internalDataPoint22.iU) {
                    this.kR[i] = (float) internalDataPoint22.iP;
                    this.kR[i + 1] = (float) internalDataPoint22.iQ;
                    this.kt[i / 2] = 0;
                    i += 2;
                }
            }
            sChartGLDrawer.drawDataPoints(((LineSeries) this.cZ).ej(), this.kR, this.cZ, this.cy, this.kt, i, this.kE, this.kG, this.cu, this.dJ, 1 - ((LineSeries) this.cZ).dR.eo(), this.cY);
            obj = (this.kC <= 0.0f || this.kF == 0) ? null : 1;
            if (obj != null) {
                sChartGLDrawer.drawDataPoints(((LineSeries) this.cZ).ej(), this.kR, this.cZ, this.cy, this.kt, i, this.kF, this.kH, this.kC, this.dJ, 1 - ((LineSeries) this.cZ).dR.eo(), this.cY);
            }
        }
    }

    private void a(LineSeries lineSeries, double d) {
        LineSeriesStyle lineSeriesStyle = lineSeries.dM ? (LineSeriesStyle) lineSeries.ov : (LineSeriesStyle) lineSeries.ou;
        this.kP = lineSeries.aL();
        this.db = lineSeries.db;
        this.cN = lineSeriesStyle.isLineShown();
        this.cO = lineSeriesStyle.getFillStyle();
        this.fillColor = lineSeriesStyle.getAreaColor();
        this.kM = lineSeriesStyle.getAreaColorGradient();
        this.cH = lineSeriesStyle.getLineColor();
        this.ct = lineSeriesStyle.getLineWidth();
        this.kN = lineSeriesStyle.getAreaColorBelowBaseline();
        this.kO = lineSeriesStyle.getAreaColorGradientBelowBaseline();
        this.cK = lineSeriesStyle.getLineColorBelowBaseline();
        int areaLineColor = lineSeriesStyle.getAreaLineColor();
        float areaLineWidth = lineSeriesStyle.getAreaLineWidth();
        int areaLineColorBelowBaseline = lineSeriesStyle.getAreaLineColorBelowBaseline();
        PointStyle pointStyle = lineSeriesStyle.getPointStyle();
        PointStyle selectedPointStyle = lineSeriesStyle.getSelectedPointStyle();
        this.cu = pointStyle.getRadius();
        this.kC = pointStyle.getInnerRadius();
        this.kE = pointStyle.getColor();
        this.kF = pointStyle.getInnerColor();
        this.kG = pointStyle.getColorBelowBaseline();
        this.kH = pointStyle.getInnerColorBelowBaseline();
        this.cv = selectedPointStyle.getRadius();
        this.kD = selectedPointStyle.getInnerRadius();
        this.kI = selectedPointStyle.getColor();
        this.kJ = selectedPointStyle.getInnerColor();
        this.kK = selectedPointStyle.getColorBelowBaseline();
        this.kL = selectedPointStyle.getInnerColorBelowBaseline();
        if (this.cO != FillStyle.NONE && x(this.fillColor)) {
            this.cH = areaLineColor;
            this.cK = areaLineColorBelowBaseline;
            this.ct = areaLineWidth;
        }
        if (this.cO != FillStyle.NONE && x(this.kN)) {
            this.cK = areaLineColorBelowBaseline;
        }
        this.ct = (float) (((double) this.ct) * d);
        this.cu = (float) (((double) this.cu) * d);
        this.kC = (float) (((double) this.kC) * d);
        this.cv = (float) (((double) this.cv) * d);
        this.kD = (float) (((double) this.kD) * d);
    }

    private boolean x(int i) {
        return i != 0;
    }

    private boolean ai() {
        return (((LineSeries) this.cZ).dM ? (LineSeriesStyle) ((LineSeries) this.cZ).ov : (LineSeriesStyle) ((LineSeries) this.cZ).ou).getPointStyle().arePointsShown() && this.cr > 0;
    }

    private boolean aj() {
        return (((LineSeries) this.cZ).dM ? (LineSeriesStyle) ((LineSeries) this.cZ).ov : (LineSeriesStyle) ((LineSeries) this.cZ).ou).getSelectedPointStyle().arePointsShown() && this.cq > 0;
    }
}
