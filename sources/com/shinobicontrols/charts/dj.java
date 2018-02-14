package com.shinobicontrols.charts;

class dj extends ac<OHLCSeries> {
    private float cP;
    private int[] cy = new int[2];
    private int[] dc;
    private int[] dd;
    private int di;
    private int dj;
    private int dk;
    private int dl;
    private int dm;
    private int dn;
    private int do;
    private int dp;
    private float lK;
    private float lL;
    private final int orientation;
    float[] points;

    public dj(OHLCSeries oHLCSeries) {
        super(oHLCSeries);
        this.orientation = oHLCSeries.dR.eo();
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
        if (((OHLCSeries) this.cZ).eh()) {
            a((OHLCSeries) this.cZ, (double) f);
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
            sChartGLDrawer.drawOHLCPoints(((OHLCSeries) this.cZ).ej(), this.points, this.cZ, this.cy, this.db.je.length, this.dc, this.dd, this.orientation, this.cP, this.lL, this.lK, fArr);
        }
    }

    private void a(OHLCSeries oHLCSeries, double d) {
        OHLCSeriesStyle oHLCSeriesStyle = oHLCSeries.dM ? (OHLCSeriesStyle) oHLCSeries.ov : (OHLCSeriesStyle) oHLCSeries.ou;
        this.cP = oHLCSeries.ao();
        this.db = oHLCSeries.db;
        this.lK = (float) (((double) oHLCSeriesStyle.getArmWidth()) * d);
        this.lL = (float) (((double) oHLCSeriesStyle.getTrunkWidth()) * d);
        this.di = ((OHLCSeriesStyle) oHLCSeries.ou).getFallingColor();
        this.dj = ((OHLCSeriesStyle) oHLCSeries.ou).getFallingColorGradient();
        this.dk = ((OHLCSeriesStyle) oHLCSeries.ou).getRisingColor();
        this.dl = ((OHLCSeriesStyle) oHLCSeries.ou).getRisingColorGradient();
        this.dm = ((OHLCSeriesStyle) oHLCSeries.ov).getFallingColor();
        this.dn = ((OHLCSeriesStyle) oHLCSeries.ov).getFallingColorGradient();
        this.do = ((OHLCSeriesStyle) oHLCSeries.ov).getRisingColor();
        this.dp = ((OHLCSeriesStyle) oHLCSeries.ov).getRisingColorGradient();
    }
}
