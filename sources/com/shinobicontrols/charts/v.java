package com.shinobicontrols.charts;

class v extends ac<CandlestickSeries> {
    private float cP;
    private int[] cy = new int[2];
    private int[] dc;
    private int[] dd;
    private float de;
    private float df;
    private int dg;
    private int dh;
    private int di;
    private int dj;
    private int dk;
    private int dl;
    private int dm;
    private int dn;
    private int do;
    private int dp;
    private boolean dq;
    private boolean dr;
    private boolean ds;
    private final int orientation;
    float[] points;

    public v(CandlestickSeries candlestickSeries) {
        super(candlestickSeries);
        this.orientation = candlestickSeries.dR.eo();
    }

    void k(int i) {
        if (this.points == null || this.points.length != i * 5) {
            this.points = new float[(i * 5)];
        }
        if (this.dc == null || this.dc.length != i) {
            this.dc = new int[i];
        }
        if (this.dd == null || this.dd.length != i) {
            this.dd = new int[i];
        }
    }

    void a(SChartGLDrawer sChartGLDrawer, float[] fArr, float f) {
        if (((CandlestickSeries) this.cZ).eh()) {
            a((CandlestickSeries) this.cZ, (double) f);
            for (int i = 0; i < this.db.je.length; i++) {
                InternalDataPoint internalDataPoint = this.db.je[i];
                if (internalDataPoint.cR()) {
                    if (internalDataPoint.iU) {
                        this.dc[i] = this.do;
                        this.dd[i] = this.dp;
                    } else {
                        this.dc[i] = this.dk;
                        this.dd[i] = this.dl;
                    }
                } else if (internalDataPoint.iU) {
                    this.dc[i] = this.dm;
                    this.dd[i] = this.dn;
                } else {
                    this.dc[i] = this.di;
                    this.dd[i] = this.dj;
                }
            }
            sChartGLDrawer.drawCandlesticks(((CandlestickSeries) this.cZ).ej(), this.points, this.cZ, this.cy, this.db.je.length, this.dc, this.dd, this.dg, this.dh, this.dq, this.dr, this.ds, this.cP, this.de, this.df, this.orientation, f, fArr);
        }
    }

    private void a(CandlestickSeries candlestickSeries, double d) {
        CandlestickSeriesStyle candlestickSeriesStyle;
        boolean z = true;
        if (candlestickSeries.dM) {
            candlestickSeriesStyle = (CandlestickSeriesStyle) candlestickSeries.ov;
        } else {
            candlestickSeriesStyle = (CandlestickSeriesStyle) candlestickSeries.ou;
        }
        this.cP = candlestickSeries.ao();
        this.db = candlestickSeries.db;
        this.de = (float) (((double) candlestickSeriesStyle.getOutlineWidth()) * d);
        this.df = (float) (((double) candlestickSeriesStyle.getStickWidth()) * d);
        this.dg = candlestickSeriesStyle.getOutlineColor();
        this.dh = candlestickSeriesStyle.getStickColor();
        this.di = ((CandlestickSeriesStyle) candlestickSeries.ou).getFallingColor();
        this.dj = ((CandlestickSeriesStyle) candlestickSeries.ou).getFallingColorGradient();
        this.dk = ((CandlestickSeriesStyle) candlestickSeries.ou).getRisingColor();
        this.dl = ((CandlestickSeriesStyle) candlestickSeries.ou).getRisingColorGradient();
        this.dm = ((CandlestickSeriesStyle) candlestickSeries.ov).getFallingColor();
        this.dn = ((CandlestickSeriesStyle) candlestickSeries.ov).getFallingColorGradient();
        this.do = ((CandlestickSeriesStyle) candlestickSeries.ov).getRisingColor();
        this.dp = ((CandlestickSeriesStyle) candlestickSeries.ov).getRisingColorGradient();
        boolean z2 = this.dg != 0 && this.de > 0.0f;
        this.dq = z2;
        if (this.dh == 0 || this.df <= 0.0f) {
            z = false;
        }
        this.dr = z;
        this.ds = this.dq;
    }
}
