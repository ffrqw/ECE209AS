package com.shinobicontrols.charts;

class l extends ac<BandSeries> {
    float[] bW;
    float[] bX;
    float[] bY;
    float[] bZ;
    float[] ca;
    float[] cb;
    private float[] cc;
    private float[] cd;
    private float[] ce;
    private int[] cf;
    private int[] cg;
    private int[] ch;
    private int ci;
    private int cj;
    private int ck;
    private int cl;
    private int cm;
    private int cn;
    private int co;
    private int cp;
    int cq;
    int cr;
    int cs;
    private float ct;
    private float cu;
    private float cv;
    private boolean cw;
    private boolean cx;
    private int[] cy = new int[4];

    public l(BandSeries bandSeries) {
        super(bandSeries);
    }

    void h(int i) {
        if (this.bW == null || this.bW.length != (i << 1)) {
            this.bW = new float[(i << 1)];
        }
        if (this.bZ == null || this.bZ.length != (i << 1)) {
            this.bZ = new float[(i << 1)];
        }
        if (this.cc == null || this.cc.length != i) {
            this.cc = new float[i];
        }
        if (this.cf == null || this.cf.length != i) {
            this.cf = new int[i];
        }
        if (this.bX == null || this.bX.length != (i << 1)) {
            this.bX = new float[(i << 1)];
        }
        if (this.ca == null || this.ca.length != (i << 1)) {
            this.ca = new float[(i << 1)];
        }
        if (this.cd == null || this.cd.length != i) {
            this.cd = new float[i];
        }
        if (this.cg == null || this.cg.length != i) {
            this.cg = new int[i];
        }
        if (this.bY == null || this.bY.length != (i << 1)) {
            this.bY = new float[(i << 1)];
        }
        if (this.cb == null || this.cb.length != (i << 1)) {
            this.cb = new float[(i << 1)];
        }
        if (this.ce == null || this.ce.length != i) {
            this.ce = new float[i];
        }
        if (this.ch == null || this.ch.length != i) {
            this.ch = new int[i];
        }
    }

    void a(SChartGLDrawer sChartGLDrawer, float[] fArr, float f) {
        if (((BandSeries) this.cZ).eh()) {
            a((BandSeries) this.cZ, (double) f);
            sChartGLDrawer.drawBandSeriesFill(((BandSeries) this.cZ).ej(), this.bW, this.cZ, this.bZ, this.cy, this.db.je.length << 1, this.co, this.cp, ((BandSeries) this.cZ).dR.eo(), this.cs, fArr);
            sChartGLDrawer.drawLineStrip(((BandSeries) this.cZ).ej(), this.bZ, this.cZ, this.cy, this.db.je.length << 1, this.cj, this.cj, this.ct, this.dJ, ((BandSeries) this.cZ).dR.eo(), fArr);
            sChartGLDrawer.drawLineStrip(((BandSeries) this.cZ).ej(), this.bW, this.cZ, this.cy, this.db.je.length << 1, this.ci, this.ci, this.ct, this.dJ, ((BandSeries) this.cZ).dR.eo(), fArr);
            if (this.cq > 0) {
                if (ai()) {
                    sChartGLDrawer.drawDataPoints(((BandSeries) this.cZ).ej(), this.ca, this.cZ, this.cy, this.cg, this.cr << 1, this.cl, this.cl, this.cu, this.dJ, ((BandSeries) this.cZ).dR.eo(), fArr);
                    sChartGLDrawer.drawDataPoints(((BandSeries) this.cZ).ej(), this.bX, this.cZ, this.cy, this.cg, this.cr << 1, this.ck, this.ck, this.cu, this.dJ, ((BandSeries) this.cZ).dR.eo(), fArr);
                }
                if (aj()) {
                    sChartGLDrawer.drawDataPoints(((BandSeries) this.cZ).ej(), this.cb, this.cZ, this.cy, this.ch, this.cq << 1, this.cn, this.cn, this.cv, this.dJ, ((BandSeries) this.cZ).dR.eo(), fArr);
                    sChartGLDrawer.drawDataPoints(((BandSeries) this.cZ).ej(), this.bY, this.cZ, this.cy, this.ch, this.cq << 1, this.cm, this.cm, this.cv, this.dJ, ((BandSeries) this.cZ).dR.eo(), fArr);
                }
            } else if (ai()) {
                sChartGLDrawer.drawDataPoints(((BandSeries) this.cZ).ej(), this.bZ, this.cZ, this.cy, this.cf, this.db.je.length << 1, this.cl, this.cl, this.cu, this.dJ, ((BandSeries) this.cZ).dR.eo(), fArr);
                sChartGLDrawer.drawDataPoints(((BandSeries) this.cZ).ej(), this.bW, this.cZ, this.cy, this.cf, this.db.je.length << 1, this.ck, this.ck, this.cu, this.dJ, ((BandSeries) this.cZ).dR.eo(), fArr);
            }
        }
    }

    private boolean ai() {
        return this.cw && this.cr > 0;
    }

    private boolean aj() {
        return this.cx && this.cq > 0;
    }

    private void a(BandSeries bandSeries, double d) {
        int areaColorNormal;
        int i = 0;
        BandSeriesStyle bandSeriesStyle = bandSeries.dM ? (BandSeriesStyle) bandSeries.ov : (BandSeriesStyle) bandSeries.ou;
        this.db = bandSeries.db;
        this.cw = bandSeriesStyle.getPointStyle().arePointsShown();
        this.cx = bandSeriesStyle.getSelectedPointStyle().arePointsShown();
        this.ci = bandSeriesStyle.getLineColorHigh();
        this.cj = bandSeriesStyle.getLineColorLow();
        if (bandSeriesStyle.isFillShown()) {
            areaColorNormal = bandSeriesStyle.getAreaColorNormal();
        } else {
            areaColorNormal = 0;
        }
        this.co = areaColorNormal;
        if (bandSeriesStyle.isFillShown()) {
            areaColorNormal = bandSeriesStyle.getAreaColorInverted();
        } else {
            areaColorNormal = 0;
        }
        this.cp = areaColorNormal;
        this.ct = bandSeriesStyle.getLineWidth() * ((float) d);
        if (bandSeriesStyle.getPointStyle().arePointsShown()) {
            areaColorNormal = bandSeriesStyle.getPointStyle().getColor();
        } else {
            areaColorNormal = 0;
        }
        this.ck = areaColorNormal;
        if (bandSeriesStyle.getPointStyle().arePointsShown()) {
            areaColorNormal = bandSeriesStyle.getPointStyle().getColorBelowBaseline();
        } else {
            areaColorNormal = 0;
        }
        this.cl = areaColorNormal;
        this.cu = bandSeriesStyle.getPointStyle().getRadius() * ((float) d);
        if (bandSeriesStyle.getSelectedPointStyle().arePointsShown()) {
            areaColorNormal = bandSeriesStyle.getSelectedPointStyle().getColor();
        } else {
            areaColorNormal = 0;
        }
        this.cm = areaColorNormal;
        if (bandSeriesStyle.getSelectedPointStyle().arePointsShown()) {
            i = bandSeriesStyle.getSelectedPointStyle().getColorBelowBaseline();
        }
        this.cn = i;
        this.cv = bandSeriesStyle.getSelectedPointStyle().getRadius() * ((float) d);
    }
}
