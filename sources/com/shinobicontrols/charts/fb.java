package com.shinobicontrols.charts;

import java.util.ArrayList;
import java.util.List;

class fb {
    private ChartStyle qa;
    private MainTitleStyle qb;
    private CrosshairStyle qc;
    private LegendStyle qd;
    private AxisStyle qe;
    private AxisStyle qf;
    private AnnotationStyle qg;
    final List<LineSeriesStyle> qh = new ArrayList();
    final List<LineSeriesStyle> qi = new ArrayList();
    final List<ColumnSeriesStyle> qj = new ArrayList();
    final List<ColumnSeriesStyle> qk = new ArrayList();
    final List<BarSeriesStyle> ql = new ArrayList();
    final List<BarSeriesStyle> qm = new ArrayList();
    final List<CandlestickSeriesStyle> qn = new ArrayList();
    final List<CandlestickSeriesStyle> qo = new ArrayList();
    final List<OHLCSeriesStyle> qp = new ArrayList();
    final List<OHLCSeriesStyle> qq = new ArrayList();
    final List<BandSeriesStyle> qr = new ArrayList();
    final List<BandSeriesStyle> qs = new ArrayList();
    final List<PieSeriesStyle> qt = new ArrayList();
    final List<PieSeriesStyle> qu = new ArrayList();
    final List<DonutSeriesStyle> qv = new ArrayList();
    final List<DonutSeriesStyle> qw = new ArrayList();

    fb() {
    }

    public void a(ColumnSeriesStyle columnSeriesStyle, boolean z) {
        if (z) {
            this.qk.add(columnSeriesStyle);
        } else {
            this.qj.add(columnSeriesStyle);
        }
    }

    public void a(BarSeriesStyle barSeriesStyle, boolean z) {
        if (z) {
            this.qm.add(barSeriesStyle);
        } else {
            this.ql.add(barSeriesStyle);
        }
    }

    void a(LineSeriesStyle lineSeriesStyle, boolean z) {
        if (z) {
            this.qi.add(lineSeriesStyle);
        } else {
            this.qh.add(lineSeriesStyle);
        }
    }

    void a(PieSeriesStyle pieSeriesStyle, boolean z) {
        if (z) {
            this.qu.add(pieSeriesStyle);
        } else {
            this.qt.add(pieSeriesStyle);
        }
    }

    void a(DonutSeriesStyle donutSeriesStyle, boolean z) {
        if (z) {
            this.qw.add(donutSeriesStyle);
        } else {
            this.qv.add(donutSeriesStyle);
        }
    }

    void a(BandSeriesStyle bandSeriesStyle, boolean z) {
        if (z) {
            this.qs.add(bandSeriesStyle);
        } else {
            this.qr.add(bandSeriesStyle);
        }
    }

    void a(CandlestickSeriesStyle candlestickSeriesStyle, boolean z) {
        if (z) {
            this.qo.add(candlestickSeriesStyle);
        } else {
            this.qn.add(candlestickSeriesStyle);
        }
    }

    void a(OHLCSeriesStyle oHLCSeriesStyle, boolean z) {
        if (z) {
            this.qq.add(oHLCSeriesStyle);
        } else {
            this.qp.add(oHLCSeriesStyle);
        }
    }

    BandSeriesStyle a(int i, boolean z) {
        return (BandSeriesStyle) b(z ? this.qs : this.qr, i);
    }

    CandlestickSeriesStyle b(int i, boolean z) {
        return (CandlestickSeriesStyle) b(z ? this.qo : this.qn, i);
    }

    OHLCSeriesStyle c(int i, boolean z) {
        return (OHLCSeriesStyle) b(z ? this.qq : this.qp, i);
    }

    public ColumnSeriesStyle d(int i, boolean z) {
        return (ColumnSeriesStyle) b(z ? this.qk : this.qj, i);
    }

    public BarSeriesStyle e(int i, boolean z) {
        return (BarSeriesStyle) b(z ? this.qm : this.ql, i);
    }

    LineSeriesStyle f(int i, boolean z) {
        return (LineSeriesStyle) b(z ? this.qi : this.qh, i);
    }

    private <T extends SeriesStyle> T b(List<T> list, int i) {
        return (SeriesStyle) list.get(i % list.size());
    }

    PieSeriesStyle g(int i, boolean z) {
        return (PieSeriesStyle) b(z ? this.qu : this.qt, i);
    }

    DonutSeriesStyle h(int i, boolean z) {
        return (DonutSeriesStyle) b(z ? this.qw : this.qv, i);
    }

    public ChartStyle eK() {
        return this.qa;
    }

    public void c(ChartStyle chartStyle) {
        this.qa = chartStyle;
    }

    public MainTitleStyle eL() {
        return this.qb;
    }

    public void b(MainTitleStyle mainTitleStyle) {
        this.qb = mainTitleStyle;
    }

    public CrosshairStyle eM() {
        return this.qc;
    }

    public void b(CrosshairStyle crosshairStyle) {
        this.qc = crosshairStyle;
    }

    public LegendStyle eN() {
        return this.qd;
    }

    public void f(LegendStyle legendStyle) {
        this.qd = legendStyle;
    }

    public AxisStyle eO() {
        return this.qe;
    }

    public void b(AxisStyle axisStyle) {
        this.qe = axisStyle;
    }

    public AxisStyle eP() {
        return this.qf;
    }

    public void c(AxisStyle axisStyle) {
        this.qf = axisStyle;
    }

    public AnnotationStyle eQ() {
        return this.qg;
    }

    public void b(AnnotationStyle annotationStyle) {
        this.qg = annotationStyle;
    }
}
