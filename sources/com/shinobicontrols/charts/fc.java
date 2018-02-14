package com.shinobicontrols.charts;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import com.shinobicontrols.charts.Legend.SymbolAlignment;
import com.shinobicontrols.charts.PieDonutSeries.RadialEffect;
import com.shinobicontrols.charts.SeriesStyle.FillStyle;
import com.shinobicontrols.charts.TickMark.Orientation;
import com.shinobicontrols.charts.Title.CentersOn;
import com.shinobicontrols.charts.Title.Position;

class fc {
    private static boolean bb;
    private static boolean bc;
    private static int bk;
    private static int bt;
    private static int bu;
    private static float ct;
    private static int qA;
    private static int qB;
    private static boolean qC;
    private static float[] qD;
    private static int qE;
    private static int qF;
    private static int qG;
    private static int qH;
    private static int qI;
    private static int qJ;
    private static int qK;
    private static int qL;
    private static int qM;
    private static int qN;
    private static int qO;
    private static int qP;
    private static int qQ;
    private static int qR;
    private static int qS;
    private static int qT;
    private static int qU;
    private static int qV;
    private static int qW;
    private static int qX;
    private static int qY;
    private static int qZ;
    private static int qx;
    private static int qy;
    private static float qz;
    private static float rA;
    private static float rB;
    private static int rC;
    private static int rD;
    private static int rE;
    private static int rF;
    private static int rG;
    private static int rH;
    private static float rI;
    private static float rJ;
    private static float rK;
    private static float rL;
    private static float rM;
    private static float rN;
    private static final Typeface rO = Typeface.create(null, 0);
    private static float rP;
    private static float rQ;
    private static int ra;
    private static int rb;
    private static int rc;
    private static int rd;
    private static int re;
    private static int rf;
    private static int rg;
    private static int rh;
    private static int ri;
    private static int rj;
    private static int rk;
    private static int rl;
    private static int rm;
    private static int rn;
    private static int ro;
    private static int rp;
    private static int rq;
    private static int rr;
    private static int rs;
    private static int rt;
    private static int ru;
    private static int rv;
    private static int rw;
    private static int rx;
    private static int ry = -16777216;
    private static float rz;

    static fb a(Context context, AttributeSet attributeSet) {
        a(context, attributeSet, 0);
        return a(eR());
    }

    static fb a(Context context, int i) {
        a(context, null, i);
        return a(eR());
    }

    static fb a(Context context, fb fbVar, int i) {
        a(context, null, i);
        return a(fbVar);
    }

    private static fb a(fb fbVar) {
        int i;
        int i2;
        ChartStyle eK = fbVar.eK();
        eK.F.c(Integer.valueOf(qH));
        eK.fm.c(Integer.valueOf(qH));
        eK.fn.c(Integer.valueOf(0));
        eK.fo.c(Float.valueOf(1.0f));
        eK.fp.c(Integer.valueOf(0));
        eK.fq.c(Integer.valueOf(qI));
        eK.fr.c(Integer.valueOf(qJ));
        eK.fs.c(Float.valueOf(1.6f));
        MainTitleStyle eL = fbVar.eL();
        eL.E.c(rO);
        eL.C.c(Integer.valueOf(qx));
        eL.F.c(Integer.valueOf(0));
        eL.D.c(Float.valueOf(16.0f));
        eL.sF.c(Float.valueOf(16.0f));
        eL.lf.c(Boolean.valueOf(false));
        eL.lg.c(CentersOn.PLOTTING_AREA);
        eL.jO.c(Float.valueOf(rP));
        eL.sH.c(Float.valueOf(rQ));
        LegendStyle eN = fbVar.eN();
        eN.F.c(Integer.valueOf(qM));
        eN.fn.c(Integer.valueOf(qL));
        eN.fo.c(Float.valueOf(1.0f));
        eN.jL.c(Float.valueOf(0.0f));
        eN.jM.c(rO);
        eN.C.c(Integer.valueOf(qK));
        eN.D.c(Float.valueOf(16.0f));
        eN.jO.c(Float.valueOf(20.0f));
        eN.jX.c(Float.valueOf(15.0f));
        eN.jQ.c(SymbolAlignment.LEFT);
        eN.jR.c(Float.valueOf(0.0f));
        eN.jN.c(Float.valueOf(15.0f));
        eN.jP.c(Boolean.valueOf(true));
        eN.jS.c(Float.valueOf(32.0f));
        eN.jT.c(Integer.valueOf(17));
        eN.jU.c(rO);
        eN.jV.c(Integer.valueOf(qK));
        eN.jW.c(Float.valueOf(16.0f));
        eN.jY.sH.c(Float.valueOf(rQ));
        eN.jY.jO.c(Float.valueOf(rP));
        for (i = 0; i < fbVar.qh.size(); i++) {
            LineSeriesStyle f = fbVar.f(i, false);
            a(f, a(f.getPointStyle(), G(i)), b(f.getSelectedPointStyle(), G(i)), G(i), H(i), I(i), J(i), ct);
        }
        for (i = 0; i < fbVar.qi.size(); i++) {
            f = fbVar.f(i, true);
            a(f, a(f.getPointStyle(), G(i)), f.getSelectedPointStyle(), qF, H(i), I(i), J(i), ct * 4.0f);
        }
        for (i2 = 0; i2 < fbVar.qj.size(); i2++) {
            a(fbVar.d(i2, false), G(i2), H(i2), J(i2));
        }
        for (i2 = 0; i2 < fbVar.qk.size(); i2++) {
            b(fbVar.d(i2, true), G(i2), H(i2), J(i2));
        }
        for (i2 = 0; i2 < fbVar.ql.size(); i2++) {
            a(fbVar.e(i2, false), G(i2), H(i2), J(i2));
        }
        for (i2 = 0; i2 < fbVar.qm.size(); i2++) {
            b(fbVar.e(i2, true), G(i2), H(i2), J(i2));
        }
        a(fbVar.g(0, false));
        b(fbVar.g(0, true));
        a(fbVar.h(0, false));
        b(fbVar.h(0, true));
        AxisStyle eO = fbVar.eO();
        AxisTitleStyle titleStyle = eO.getTitleStyle();
        b(titleStyle);
        TickStyle tickStyle = eO.getTickStyle();
        setTickStyle(tickStyle);
        setGridlineStyle(eO.getGridlineStyle());
        setGridStripeStyle(eO.getGridStripeStyle());
        a(eO, titleStyle, tickStyle);
        eO = fbVar.eP();
        titleStyle = eO.getTitleStyle();
        c(titleStyle);
        tickStyle = eO.getTickStyle();
        setTickStyle(tickStyle);
        setGridlineStyle(eO.getGridlineStyle());
        setGridStripeStyle(eO.getGridStripeStyle());
        b(eO, titleStyle, tickStyle);
        BandSeriesStyle a = fbVar.a(0, false);
        a(a, a.getPointStyle(), a.getSelectedPointStyle());
        a = fbVar.a(0, true);
        a(a, a.getPointStyle(), a.getSelectedPointStyle());
        a(fbVar.b(0, false));
        a(fbVar.b(0, true));
        a(fbVar.c(0, false));
        a(fbVar.c(0, true));
        b(fbVar.eM());
        b(fbVar.eQ());
        return fbVar;
    }

    private static fb eR() {
        fb fbVar = new fb();
        fbVar.c(new ChartStyle());
        fbVar.b(new MainTitleStyle());
        fbVar.f(new LegendStyle());
        for (int i = 0; i < 6; i++) {
            LineSeriesStyle lineSeriesStyle = new LineSeriesStyle();
            lineSeriesStyle.setPointStyle(new PointStyle());
            lineSeriesStyle.setSelectedPointStyle(new PointStyle());
            fbVar.a(lineSeriesStyle, false);
            lineSeriesStyle = new LineSeriesStyle();
            lineSeriesStyle.setPointStyle(new PointStyle());
            lineSeriesStyle.setSelectedPointStyle(new PointStyle());
            fbVar.a(lineSeriesStyle, true);
            fbVar.a(new ColumnSeriesStyle(), false);
            fbVar.a(new ColumnSeriesStyle(), true);
            fbVar.a(new BarSeriesStyle(), false);
            fbVar.a(new BarSeriesStyle(), true);
        }
        fbVar.a(new PieSeriesStyle(), false);
        fbVar.a(new PieSeriesStyle(), true);
        fbVar.a(new DonutSeriesStyle(), false);
        fbVar.a(new DonutSeriesStyle(), true);
        fbVar.b(new AnnotationStyle());
        AxisStyle axisStyle = new AxisStyle();
        axisStyle.setTitleStyle(new AxisTitleStyle());
        axisStyle.setTickStyle(new TickStyle());
        axisStyle.setGridlineStyle(new GridlineStyle());
        axisStyle.setGridStripeStyle(new GridStripeStyle());
        fbVar.b(axisStyle);
        axisStyle = new AxisStyle();
        axisStyle.setTitleStyle(new AxisTitleStyle());
        axisStyle.setTickStyle(new TickStyle());
        axisStyle.setGridlineStyle(new GridlineStyle());
        axisStyle.setGridStripeStyle(new GridStripeStyle());
        fbVar.c(axisStyle);
        BandSeriesStyle bandSeriesStyle = new BandSeriesStyle();
        bandSeriesStyle.setPointStyle(new PointStyle());
        bandSeriesStyle.setSelectedPointStyle(new PointStyle());
        fbVar.a(bandSeriesStyle, false);
        fbVar.a(new BandSeriesStyle(), true);
        fbVar.a(new CandlestickSeriesStyle(), false);
        fbVar.a(new CandlestickSeriesStyle(), true);
        fbVar.a(new OHLCSeriesStyle(), false);
        fbVar.a(new OHLCSeriesStyle(), true);
        fbVar.b(new CrosshairStyle());
        return fbVar;
    }

    private static void a(OHLCSeriesStyle oHLCSeriesStyle) {
        oHLCSeriesStyle.dt.c(Integer.valueOf(rC));
        oHLCSeriesStyle.dv.c(Integer.valueOf(rD));
        oHLCSeriesStyle.du.c(Integer.valueOf(rC));
        oHLCSeriesStyle.dw.c(Integer.valueOf(rD));
        oHLCSeriesStyle.lN.c(Float.valueOf(2.0f));
        oHLCSeriesStyle.lM.c(Float.valueOf(2.0f));
    }

    private static void a(CandlestickSeriesStyle candlestickSeriesStyle) {
        candlestickSeriesStyle.dt.c(Integer.valueOf(rC));
        candlestickSeriesStyle.dv.c(Integer.valueOf(rD));
        candlestickSeriesStyle.du.c(Integer.valueOf(rC));
        candlestickSeriesStyle.dw.c(Integer.valueOf(rD));
        candlestickSeriesStyle.dz.c(Float.valueOf(2.0f));
        candlestickSeriesStyle.dA.c(Float.valueOf(2.0f));
    }

    private static void a(BandSeriesStyle bandSeriesStyle, PointStyle pointStyle, PointStyle pointStyle2) {
        bandSeriesStyle.cz.c(Boolean.valueOf(true));
        bandSeriesStyle.cA.c(Integer.valueOf(rE));
        bandSeriesStyle.cB.c(Integer.valueOf(rF));
        bandSeriesStyle.bQ.c(Float.valueOf(2.0f));
        bandSeriesStyle.cC.c(Integer.valueOf(rG));
        bandSeriesStyle.cD.c(Integer.valueOf(rH));
        a(pointStyle, qV);
        bandSeriesStyle.setPointStyle(pointStyle);
        b(pointStyle2, qV);
        bandSeriesStyle.setSelectedPointStyle(pointStyle2);
    }

    private static void a(LineSeriesStyle lineSeriesStyle, PointStyle pointStyle, PointStyle pointStyle2, int i, int i2, int i3, int i4, float f) {
        lineSeriesStyle.cV.c(Boolean.valueOf(true));
        lineSeriesStyle.bQ.c(Float.valueOf(f));
        lineSeriesStyle.bP.c(Integer.valueOf(i));
        a(pointStyle, i2);
        lineSeriesStyle.setPointStyle(pointStyle);
        b(pointStyle2, i2);
        lineSeriesStyle.setSelectedPointStyle(pointStyle2);
        lineSeriesStyle.cQ.c(Integer.valueOf(i2));
        lineSeriesStyle.cR.c(Integer.valueOf(i2));
        lineSeriesStyle.cT.c(Integer.valueOf(i3));
        lineSeriesStyle.cS.c(Integer.valueOf(i3));
        lineSeriesStyle.kW.c(Integer.valueOf(i4));
        lineSeriesStyle.kX.c(Integer.valueOf(i4));
        lineSeriesStyle.kY.c(Float.valueOf(f));
        lineSeriesStyle.cW.c(FillStyle.NONE);
        lineSeriesStyle.cU.c(Integer.valueOf(i));
        lineSeriesStyle.pD.c(Boolean.valueOf(false));
    }

    private static PointStyle a(PointStyle pointStyle, int i) {
        pointStyle.nd.c(Integer.valueOf(i));
        pointStyle.ne.c(Integer.valueOf(i));
        pointStyle.nj.c(Boolean.valueOf(false));
        if (!pointStyle.nf.sV) {
            pointStyle.m(0.0f);
            pointStyle.nf.sV = false;
        }
        pointStyle.ng.c(Integer.valueOf(qI));
        pointStyle.nh.c(Integer.valueOf(qI));
        pointStyle.lZ.c(Float.valueOf(3.0f));
        pointStyle.ni.c(Float.valueOf(5.0f));
        pointStyle.nk.c(null);
        return pointStyle;
    }

    private static PointStyle b(PointStyle pointStyle, int i) {
        pointStyle.nd.c(Integer.valueOf(qF));
        pointStyle.ne.c(Integer.valueOf(qF));
        pointStyle.nj.c(Boolean.valueOf(false));
        if (!pointStyle.nf.sV) {
            pointStyle.m(0.0f);
            pointStyle.nf.sV = false;
        }
        pointStyle.ng.c(Integer.valueOf(qI));
        pointStyle.nh.c(Integer.valueOf(qI));
        pointStyle.lZ.c(Float.valueOf(3.0f));
        pointStyle.ni.c(Float.valueOf(7.5f));
        pointStyle.nk.c(null);
        return pointStyle;
    }

    private static ColumnSeriesStyle a(ColumnSeriesStyle columnSeriesStyle, int i, int i2, int i3) {
        columnSeriesStyle.cV.c(Boolean.valueOf(true));
        columnSeriesStyle.bQ.c(Float.valueOf(rz));
        columnSeriesStyle.bP.c(Integer.valueOf(i));
        columnSeriesStyle.cU.c(Integer.valueOf(i));
        columnSeriesStyle.cW.c(FillStyle.GRADIENT);
        columnSeriesStyle.cQ.c(Integer.valueOf(i2));
        columnSeriesStyle.cT.c(Integer.valueOf(i3));
        columnSeriesStyle.cR.c(Integer.valueOf(i2));
        columnSeriesStyle.cS.c(Integer.valueOf(i3));
        if (!columnSeriesStyle.pD.sV) {
            columnSeriesStyle.k(false);
            columnSeriesStyle.pD.sV = false;
        }
        return columnSeriesStyle;
    }

    private static ColumnSeriesStyle b(ColumnSeriesStyle columnSeriesStyle, int i, int i2, int i3) {
        columnSeriesStyle.cV.c(Boolean.valueOf(true));
        columnSeriesStyle.cW.c(FillStyle.GRADIENT);
        columnSeriesStyle.cQ.c(Integer.valueOf(i2));
        columnSeriesStyle.cT.c(Integer.valueOf(i3));
        columnSeriesStyle.cR.c(Integer.valueOf(i2));
        columnSeriesStyle.cS.c(Integer.valueOf(i3));
        if (!columnSeriesStyle.pD.sV) {
            columnSeriesStyle.k(false);
            columnSeriesStyle.pD.sV = false;
        }
        columnSeriesStyle.bP.c(Integer.valueOf(qF));
        columnSeriesStyle.bQ.c(Float.valueOf(columnSeriesStyle.getLineWidth() * 4.0f));
        columnSeriesStyle.cU.c(Integer.valueOf(qF));
        return columnSeriesStyle;
    }

    private static void a(BarSeriesStyle barSeriesStyle, int i, int i2, int i3) {
        barSeriesStyle.cV.c(Boolean.valueOf(true));
        barSeriesStyle.bQ.c(Float.valueOf(rA));
        barSeriesStyle.bP.c(Integer.valueOf(i));
        barSeriesStyle.cU.c(Integer.valueOf(i));
        barSeriesStyle.cW.c(FillStyle.GRADIENT);
        barSeriesStyle.cQ.c(Integer.valueOf(i2));
        barSeriesStyle.cT.c(Integer.valueOf(i3));
        barSeriesStyle.cR.c(Integer.valueOf(i2));
        barSeriesStyle.cS.c(Integer.valueOf(i3));
        if (!barSeriesStyle.pD.sV) {
            barSeriesStyle.k(false);
            barSeriesStyle.pD.sV = false;
        }
    }

    private static void b(BarSeriesStyle barSeriesStyle, int i, int i2, int i3) {
        barSeriesStyle.bP.c(Integer.valueOf(qF));
        barSeriesStyle.bQ.c(Float.valueOf(barSeriesStyle.getLineWidth() * 4.0f));
        barSeriesStyle.cU.c(Integer.valueOf(qF));
        barSeriesStyle.cV.c(Boolean.valueOf(true));
        barSeriesStyle.cW.c(FillStyle.GRADIENT);
        barSeriesStyle.cQ.c(Integer.valueOf(i2));
        barSeriesStyle.cT.c(Integer.valueOf(i3));
        barSeriesStyle.cR.c(Integer.valueOf(i2));
        barSeriesStyle.cS.c(Integer.valueOf(i3));
        if (!barSeriesStyle.pD.sV) {
            barSeriesStyle.k(false);
            barSeriesStyle.pD.sV = false;
        }
    }

    private static void a(PieSeriesStyle pieSeriesStyle) {
        if (!pieSeriesStyle.my.sV) {
            pieSeriesStyle.setFlavorColors(new int[]{rs, rt, ru, rv, rw, rx});
            pieSeriesStyle.my.sV = false;
        }
        if (!pieSeriesStyle.mw.sV) {
            pieSeriesStyle.setCrustColors(new int[]{ry});
            pieSeriesStyle.mw.sV = false;
        }
        pieSeriesStyle.mr.c(Boolean.valueOf(true));
        pieSeriesStyle.ms.c(Boolean.valueOf(true));
        pieSeriesStyle.mt.c(Boolean.valueOf(true));
        pieSeriesStyle.mu.c(RadialEffect.DEFAULT);
        pieSeriesStyle.mv.c(Float.valueOf(0.0f));
        pieSeriesStyle.mx.c(Float.valueOf(rB));
        pieSeriesStyle.mz.c(Float.valueOf(0.0f));
        pieSeriesStyle.mA.c(rO);
        pieSeriesStyle.mB.c(Float.valueOf(16.0f));
        pieSeriesStyle.mC.c(Integer.valueOf(qG));
        pieSeriesStyle.mD.c(Integer.valueOf(0));
    }

    private static void b(PieSeriesStyle pieSeriesStyle) {
        if (!pieSeriesStyle.my.sV) {
            pieSeriesStyle.setFlavorColors(new int[]{rs, rt, ru, rv, rw, rx});
            pieSeriesStyle.my.sV = false;
        }
        if (!pieSeriesStyle.mw.sV) {
            pieSeriesStyle.setCrustColors(new int[]{ry});
            pieSeriesStyle.mw.sV = false;
        }
        pieSeriesStyle.mr.c(Boolean.valueOf(true));
        pieSeriesStyle.ms.c(Boolean.valueOf(true));
        pieSeriesStyle.mt.c(Boolean.valueOf(true));
        pieSeriesStyle.mu.c(RadialEffect.DEFAULT);
        pieSeriesStyle.mv.c(Float.valueOf(0.0f));
        pieSeriesStyle.mx.c(Float.valueOf(rB));
        pieSeriesStyle.mz.c(Float.valueOf(0.1f));
        pieSeriesStyle.mA.c(rO);
        pieSeriesStyle.mB.c(Float.valueOf(16.0f));
        pieSeriesStyle.mC.c(Integer.valueOf(qG));
        pieSeriesStyle.mD.c(Integer.valueOf(0));
    }

    private static void a(DonutSeriesStyle donutSeriesStyle) {
        if (!donutSeriesStyle.my.sV) {
            donutSeriesStyle.setFlavorColors(new int[]{rs, rt, ru, rv, rw, rx});
            donutSeriesStyle.my.sV = false;
        }
        if (!donutSeriesStyle.mw.sV) {
            donutSeriesStyle.setCrustColors(new int[]{ry});
            donutSeriesStyle.mw.sV = false;
        }
        donutSeriesStyle.mr.c(Boolean.valueOf(true));
        donutSeriesStyle.ms.c(Boolean.valueOf(true));
        donutSeriesStyle.mt.c(Boolean.valueOf(true));
        donutSeriesStyle.mu.c(RadialEffect.DEFAULT);
        donutSeriesStyle.mv.c(Float.valueOf(0.0f));
        donutSeriesStyle.mx.c(Float.valueOf(rB));
        donutSeriesStyle.mz.c(Float.valueOf(0.0f));
        donutSeriesStyle.mA.c(rO);
        donutSeriesStyle.mB.c(Float.valueOf(16.0f));
        donutSeriesStyle.mC.c(Integer.valueOf(qG));
        donutSeriesStyle.mD.c(Integer.valueOf(0));
    }

    private static void b(DonutSeriesStyle donutSeriesStyle) {
        if (!donutSeriesStyle.my.sV) {
            donutSeriesStyle.setFlavorColors(new int[]{rs, rt, ru, rv, rw, rx});
            donutSeriesStyle.my.sV = false;
        }
        if (!donutSeriesStyle.mw.sV) {
            donutSeriesStyle.setCrustColors(new int[]{ry});
            donutSeriesStyle.mw.sV = false;
        }
        donutSeriesStyle.mr.c(Boolean.valueOf(true));
        donutSeriesStyle.ms.c(Boolean.valueOf(true));
        donutSeriesStyle.mt.c(Boolean.valueOf(true));
        donutSeriesStyle.mu.c(RadialEffect.DEFAULT);
        donutSeriesStyle.mv.c(Float.valueOf(0.0f));
        donutSeriesStyle.mx.c(Float.valueOf(rB));
        donutSeriesStyle.mz.c(Float.valueOf(0.1f));
        donutSeriesStyle.mA.c(rO);
        donutSeriesStyle.mB.c(Float.valueOf(16.0f));
        donutSeriesStyle.mC.c(Integer.valueOf(qG));
        donutSeriesStyle.mD.c(Integer.valueOf(0));
    }

    private static void a(AxisStyle axisStyle, AxisTitleStyle axisTitleStyle, TickStyle tickStyle) {
        b(axisTitleStyle);
        axisStyle.setTitleStyle(axisTitleStyle);
        setTickStyle(tickStyle);
        axisStyle.setTickStyle(tickStyle);
        axisStyle.bQ.c(Float.valueOf(1.6f));
        axisStyle.bP.c(Integer.valueOf(qE));
        axisStyle.setInterSeriesPadding(0.2f);
        axisStyle.bN.sV = false;
        axisStyle.setInterSeriesSetPadding(0.2f);
        axisStyle.bO.sV = false;
    }

    private static void b(AxisStyle axisStyle, AxisTitleStyle axisTitleStyle, TickStyle tickStyle) {
        c(axisTitleStyle);
        axisStyle.setTitleStyle(axisTitleStyle);
        setTickStyle(tickStyle);
        axisStyle.setTickStyle(tickStyle);
        axisStyle.bQ.c(Float.valueOf(1.6f));
        axisStyle.bP.c(Integer.valueOf(qE));
        axisStyle.bN.c(Float.valueOf(0.0f));
        axisStyle.setInterSeriesSetPadding(0.2f);
        axisStyle.bO.sV = false;
    }

    private static void setTickStyle(TickStyle majorTickStyle) {
        majorTickStyle.st.c(Boolean.valueOf(true));
        majorTickStyle.su.c(Boolean.valueOf(true));
        majorTickStyle.sv.c(Boolean.valueOf(false));
        majorTickStyle.sx.c(Orientation.HORIZONTAL);
        majorTickStyle.sw.c(Float.valueOf(5.0f));
        majorTickStyle.ss.c(Float.valueOf(10.0f));
        majorTickStyle.mA.c(rO);
        majorTickStyle.mB.c(Float.valueOf(16.0f));
        majorTickStyle.sq.c(Integer.valueOf(qA));
        majorTickStyle.sr.c(Integer.valueOf(0));
        majorTickStyle.bP.c(Integer.valueOf(bk));
        majorTickStyle.bQ.c(Float.valueOf(1.0f));
    }

    private static void b(AxisTitleStyle axisTitleStyle) {
        d(axisTitleStyle);
        axisTitleStyle.bV.c(Title.Orientation.HORIZONTAL);
    }

    private static void c(AxisTitleStyle axisTitleStyle) {
        d(axisTitleStyle);
        axisTitleStyle.bV.c(Title.Orientation.VERTICAL);
    }

    private static void d(AxisTitleStyle axisTitleStyle) {
        axisTitleStyle.sG.c(Position.CENTER);
        axisTitleStyle.E.c(rO);
        axisTitleStyle.D.c(Float.valueOf(qz));
        axisTitleStyle.F.c(Integer.valueOf(0));
        axisTitleStyle.sF.c(Float.valueOf(16.0f));
        axisTitleStyle.C.c(Integer.valueOf(qy));
        axisTitleStyle.jO.c(Float.valueOf(rP));
        axisTitleStyle.sH.c(Float.valueOf(rQ));
    }

    private static void setGridlineStyle(GridlineStyle gridlineStyle) {
        gridlineStyle.iH.c(Boolean.valueOf(qC));
        gridlineStyle.iI.c(qD);
        gridlineStyle.bP.c(Integer.valueOf(qB));
        gridlineStyle.iG.c(Boolean.valueOf(bb));
    }

    private static void setGridStripeStyle(GridStripeStyle gridStripeStyle) {
        gridStripeStyle.iD.c(Integer.valueOf(bt));
        gridStripeStyle.iE.c(Integer.valueOf(bu));
        gridStripeStyle.iF.c(Boolean.valueOf(bc));
    }

    private static void b(CrosshairStyle crosshairStyle) {
        crosshairStyle.bP.c(Integer.valueOf(qN));
        crosshairStyle.bQ.c(Float.valueOf(rI));
        crosshairStyle.ge.c(Float.valueOf(rJ));
        crosshairStyle.gf.c(rO);
        crosshairStyle.gg.c(Float.valueOf(rK));
        crosshairStyle.gh.c(Integer.valueOf(qO));
        crosshairStyle.gi.c(Integer.valueOf(qP));
        crosshairStyle.gj.c(Integer.valueOf(qQ));
        crosshairStyle.gk.c(Float.valueOf(rL));
        crosshairStyle.gl.c(Float.valueOf(rM));
        crosshairStyle.gm.c(Integer.valueOf(qR));
    }

    private static void b(AnnotationStyle annotationStyle) {
        annotationStyle.F.c(Integer.valueOf(qS));
        annotationStyle.C.c(Integer.valueOf(qT));
        annotationStyle.D.c(Float.valueOf(rN));
        annotationStyle.E.c(rO);
    }

    private static int G(int i) {
        switch (i % 6) {
            case 0:
                return qU;
            case 1:
                return qY;
            case 2:
                return rc;
            case 3:
                return rg;
            case 4:
                return rk;
            default:
                return ro;
        }
    }

    private static int H(int i) {
        switch (i % 6) {
            case 0:
                return qV;
            case 1:
                return qZ;
            case 2:
                return rd;
            case 3:
                return rh;
            case 4:
                return rl;
            default:
                return rp;
        }
    }

    private static int I(int i) {
        switch (i % 6) {
            case 0:
                return qX;
            case 1:
                return rb;
            case 2:
                return rf;
            case 3:
                return rj;
            case 4:
                return rn;
            default:
                return rr;
        }
    }

    private static int J(int i) {
        switch (i % 6) {
            case 0:
                return qW;
            case 1:
                return ra;
            case 2:
                return re;
            case 3:
                return ri;
            case 4:
                return rm;
            default:
                return rq;
        }
    }

    private static void a(Context context, AttributeSet attributeSet, int i) {
        TypedArray obtainStyledAttributes;
        if (i != 0) {
            obtainStyledAttributes = context.getTheme().obtainStyledAttributes(i, R.styleable.ChartTheme);
        } else {
            obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.ChartTheme);
        }
        Resources resources = context.getResources();
        TypedValue typedValue = new TypedValue();
        qx = obtainStyledAttributes.getColor(R.styleable.ChartTheme_sc_chartTitleColor, resources.getColor(R.color.sc_chartDefaultTitleColor));
        qy = obtainStyledAttributes.getColor(R.styleable.ChartTheme_sc_axisTitleColor, resources.getColor(R.color.sc_axisDefaultTitleColor));
        resources.getValue(R.dimen.sc_axisTitleTextSize, typedValue, true);
        qz = obtainStyledAttributes.getFloat(R.styleable.ChartTheme_sc_axisTitleTextSize, typedValue.getFloat());
        bk = obtainStyledAttributes.getColor(R.styleable.ChartTheme_sc_tickLineColor, resources.getColor(R.color.sc_tickDefaultLineColor));
        qA = obtainStyledAttributes.getColor(R.styleable.ChartTheme_sc_tickLabelColor, resources.getColor(R.color.sc_tickDefaultLabelColor));
        qB = obtainStyledAttributes.getColor(R.styleable.ChartTheme_sc_gridLineColor, resources.getColor(R.color.sc_gridDefaultLineColor));
        bb = obtainStyledAttributes.getBoolean(R.styleable.ChartTheme_sc_showGridLines, resources.getBoolean(R.bool.sc_defaultShowGridLines));
        qC = obtainStyledAttributes.getBoolean(R.styleable.ChartTheme_sc_dashedGridLines, resources.getBoolean(R.bool.sc_dashedGridLines));
        bc = obtainStyledAttributes.getBoolean(R.styleable.ChartTheme_sc_showGridStripes, resources.getBoolean(R.bool.sc_showGridStripes));
        bt = obtainStyledAttributes.getColor(R.styleable.ChartTheme_sc_gridStripeColor, resources.getColor(R.color.sc_defaultGridStripeColor));
        qD = new float[]{10.0f, 10.0f};
        bu = obtainStyledAttributes.getColor(R.styleable.ChartTheme_sc_alternateGridStripeColor, resources.getColor(R.color.sc_defaultAlternateGridStripeColor));
        qE = obtainStyledAttributes.getColor(R.styleable.ChartTheme_sc_axisLineColor, resources.getColor(R.color.sc_axisDefaultLineColor));
        qF = obtainStyledAttributes.getColor(R.styleable.ChartTheme_sc_defaultSelectedSeriesColor, resources.getColor(R.color.sc_defaultSelectedSeriesColor));
        qH = obtainStyledAttributes.getColor(R.styleable.ChartTheme_sc_chartBackgroundColor, resources.getColor(R.color.sc_chartDefaultBackgroundColor));
        qI = obtainStyledAttributes.getColor(R.styleable.ChartTheme_sc_plotAreaColor, resources.getColor(R.color.sc_plotDefaultAreaColor));
        qG = obtainStyledAttributes.getColor(R.styleable.ChartTheme_sc_pieDonutLabelColor, resources.getColor(R.color.sc_pieDonutLabelDefaultColor));
        qJ = resources.getColor(R.color.sc_plotDefaultAreaBorderColor);
        qK = obtainStyledAttributes.getColor(R.styleable.ChartTheme_sc_legendTextColor, resources.getColor(R.color.sc_legendDefaultTextColor));
        qL = obtainStyledAttributes.getColor(R.styleable.ChartTheme_sc_legendBorderColor, resources.getColor(R.color.sc_legendDefaultBorderColor));
        qM = obtainStyledAttributes.getColor(R.styleable.ChartTheme_sc_legendBackgroundColor, resources.getColor(R.color.sc_legendDefaultBackgroundColor));
        qN = obtainStyledAttributes.getColor(R.styleable.ChartTheme_sc_crosshairLineColor, resources.getColor(R.color.sc_crosshairDefaultLineColor));
        qO = obtainStyledAttributes.getColor(R.styleable.ChartTheme_sc_crosshairTooltipTextColor, resources.getColor(R.color.sc_crosshairTooltipDefaultTextColor));
        qP = obtainStyledAttributes.getColor(R.styleable.ChartTheme_sc_crosshairTooltipLabelBackgroundColor, resources.getColor(R.color.sc_crosshairTooltipDefaultLabelBackgroundColor));
        qQ = obtainStyledAttributes.getColor(R.styleable.ChartTheme_sc_crosshairTooltipBackgroundColor, resources.getColor(R.color.sc_crosshairTooltipDefaultBackgroundColor));
        qR = obtainStyledAttributes.getColor(R.styleable.ChartTheme_sc_crosshairTooltipBorderColor, resources.getColor(R.color.sc_crosshairTooltipDefaultBorderColor));
        resources.getValue(R.dimen.sc_defaultLineWidth, typedValue, true);
        ct = obtainStyledAttributes.getFloat(R.styleable.ChartTheme_sc_lineWidth, typedValue.getFloat());
        resources.getValue(R.dimen.sc_defaultColumnLineWidth, typedValue, true);
        rz = obtainStyledAttributes.getFloat(R.styleable.ChartTheme_sc_columnLineWidth, typedValue.getFloat());
        resources.getValue(R.dimen.sc_defaultBarLineWidth, typedValue, true);
        rA = obtainStyledAttributes.getFloat(R.styleable.ChartTheme_sc_barLineWidth, typedValue.getFloat());
        resources.getValue(R.dimen.sc_defaultDonutCrustThickness, typedValue, true);
        rB = obtainStyledAttributes.getFloat(R.styleable.ChartTheme_sc_donutCrustThickness, typedValue.getFloat());
        resources.getValue(R.dimen.sc_defaultCrosshairLineWidth, typedValue, true);
        rI = obtainStyledAttributes.getFloat(R.styleable.ChartTheme_sc_crosshairLineWidth, typedValue.getFloat());
        resources.getValue(R.dimen.sc_defaultCrosshairPadding, typedValue, true);
        rJ = obtainStyledAttributes.getFloat(R.styleable.ChartTheme_sc_crosshairPadding, typedValue.getFloat());
        resources.getValue(R.dimen.sc_defaultCrosshairTooltipTextSize, typedValue, true);
        rK = obtainStyledAttributes.getFloat(R.styleable.ChartTheme_sc_crosshairTooltipTextSize, typedValue.getFloat());
        resources.getValue(R.dimen.sc_defaultCrosshairTooltipCornerRadius, typedValue, true);
        rL = obtainStyledAttributes.getFloat(R.styleable.ChartTheme_sc_crosshairTooltipCornerRadius, typedValue.getFloat());
        resources.getValue(R.dimen.sc_defaultCrosshairTooltipBorderWidth, typedValue, true);
        rM = obtainStyledAttributes.getFloat(R.styleable.ChartTheme_sc_crosshairTooltipBorderWidth, typedValue.getFloat());
        qU = obtainStyledAttributes.getColor(R.styleable.ChartTheme_sc_seriesLineColor1, resources.getColor(R.color.sc_purpleDark));
        qV = obtainStyledAttributes.getColor(R.styleable.ChartTheme_sc_seriesAreaColor1, resources.getColor(R.color.sc_purpleLightFill));
        qW = obtainStyledAttributes.getColor(R.styleable.ChartTheme_sc_seriesAreaGradientColor1, resources.getColor(R.color.sc_purpleDarkFill));
        qX = obtainStyledAttributes.getColor(R.styleable.ChartTheme_sc_seriesAreaGradientTransparentColor1, resources.getColor(R.color.sc_purpleDarkFillAlpha));
        qY = obtainStyledAttributes.getColor(R.styleable.ChartTheme_sc_seriesLineColor2, resources.getColor(R.color.sc_blueDark));
        qZ = obtainStyledAttributes.getColor(R.styleable.ChartTheme_sc_seriesAreaColor2, resources.getColor(R.color.sc_blueLightFill));
        ra = obtainStyledAttributes.getColor(R.styleable.ChartTheme_sc_seriesAreaGradientColor2, resources.getColor(R.color.sc_blueDarkFill));
        rb = obtainStyledAttributes.getColor(R.styleable.ChartTheme_sc_seriesAreaGradientTransparentColor2, resources.getColor(R.color.sc_blueDarkFillAlpha));
        rc = obtainStyledAttributes.getColor(R.styleable.ChartTheme_sc_seriesLineColor3, resources.getColor(R.color.sc_orangeDark));
        rd = obtainStyledAttributes.getColor(R.styleable.ChartTheme_sc_seriesAreaColor3, resources.getColor(R.color.sc_orangeLightFill));
        re = obtainStyledAttributes.getColor(R.styleable.ChartTheme_sc_seriesAreaGradientColor3, resources.getColor(R.color.sc_orangeDarkFill));
        rf = obtainStyledAttributes.getColor(R.styleable.ChartTheme_sc_seriesAreaGradientTransparentColor3, resources.getColor(R.color.sc_orangeDarkFillAlpha));
        rg = obtainStyledAttributes.getColor(R.styleable.ChartTheme_sc_seriesLineColor4, resources.getColor(R.color.sc_greenDark));
        rh = obtainStyledAttributes.getColor(R.styleable.ChartTheme_sc_seriesAreaColor4, resources.getColor(R.color.sc_greenLightFill));
        ri = obtainStyledAttributes.getColor(R.styleable.ChartTheme_sc_seriesAreaGradientColor4, resources.getColor(R.color.sc_greenDarkFill));
        rj = obtainStyledAttributes.getColor(R.styleable.ChartTheme_sc_seriesAreaGradientTransparentColor4, resources.getColor(R.color.sc_greenDarkFillAlpha));
        rk = obtainStyledAttributes.getColor(R.styleable.ChartTheme_sc_seriesLineColor5, resources.getColor(R.color.sc_yellowDark));
        rl = obtainStyledAttributes.getColor(R.styleable.ChartTheme_sc_seriesAreaColor5, resources.getColor(R.color.sc_yellowLightFill));
        rm = obtainStyledAttributes.getColor(R.styleable.ChartTheme_sc_seriesAreaGradientColor5, resources.getColor(R.color.sc_yellowDarkFill));
        rn = obtainStyledAttributes.getColor(R.styleable.ChartTheme_sc_seriesAreaGradientTransparentColor5, resources.getColor(R.color.sc_yellowDarkFillAlpha));
        ro = obtainStyledAttributes.getColor(R.styleable.ChartTheme_sc_seriesLineColor6, resources.getColor(R.color.sc_pinkDark));
        rp = obtainStyledAttributes.getColor(R.styleable.ChartTheme_sc_seriesAreaColor6, resources.getColor(R.color.sc_pinkLightFill));
        rq = obtainStyledAttributes.getColor(R.styleable.ChartTheme_sc_seriesAreaGradientColor6, resources.getColor(R.color.sc_pinkDarkFill));
        rr = obtainStyledAttributes.getColor(R.styleable.ChartTheme_sc_seriesAreaGradientTransparentColor6, resources.getColor(R.color.sc_pinkDarkFillAlpha));
        rs = obtainStyledAttributes.getColor(R.styleable.ChartTheme_sc_pieDonutFlavorColor1, resources.getColor(R.color.sc_radialPurple));
        rt = obtainStyledAttributes.getColor(R.styleable.ChartTheme_sc_pieDonutFlavorColor2, resources.getColor(R.color.sc_radialBlue));
        ru = obtainStyledAttributes.getColor(R.styleable.ChartTheme_sc_pieDonutFlavorColor3, resources.getColor(R.color.sc_radialOrange));
        rv = obtainStyledAttributes.getColor(R.styleable.ChartTheme_sc_pieDonutFlavorColor4, resources.getColor(R.color.sc_radialGreen));
        rw = obtainStyledAttributes.getColor(R.styleable.ChartTheme_sc_pieDonutFlavorColor5, resources.getColor(R.color.sc_radialYellow));
        rx = obtainStyledAttributes.getColor(R.styleable.ChartTheme_sc_pieDonutFlavorColor6, resources.getColor(R.color.sc_radialPink));
        ry = obtainStyledAttributes.getColor(R.styleable.ChartTheme_sc_defaultCrustColor, resources.getColor(R.color.sc_crustColor));
        rC = obtainStyledAttributes.getColor(R.styleable.ChartTheme_sc_financialRisingColor, resources.getColor(R.color.sc_financialGreen));
        rD = obtainStyledAttributes.getColor(R.styleable.ChartTheme_sc_financialFallingColor, resources.getColor(R.color.sc_financialRed));
        rE = obtainStyledAttributes.getColor(R.styleable.ChartTheme_sc_bandSeriesHighColor, resources.getColor(R.color.sc_financialBlue));
        rF = obtainStyledAttributes.getColor(R.styleable.ChartTheme_sc_bandSeriesLowColor, resources.getColor(R.color.sc_financialBlue));
        rG = obtainStyledAttributes.getColor(R.styleable.ChartTheme_sc_bandSeriesAreaColor, resources.getColor(R.color.sc_financialBlueAlpha));
        rH = obtainStyledAttributes.getColor(R.styleable.ChartTheme_sc_bandSeriesAreaInvertedColor, resources.getColor(R.color.sc_financialBlueAlpha));
        resources.getValue(R.dimen.sc_defaultTitlePadding, typedValue, true);
        rP = obtainStyledAttributes.getFloat(R.styleable.ChartTheme_sc_titlePadding, typedValue.getFloat());
        resources.getValue(R.dimen.sc_defaultTitleMargin, typedValue, true);
        rQ = obtainStyledAttributes.getFloat(R.styleable.ChartTheme_sc_titleMargin, typedValue.getFloat());
        qS = obtainStyledAttributes.getColor(R.styleable.ChartTheme_sc_annotationBackgroundColor, resources.getColor(R.color.sc_annotationDefaultBackgroundColor));
        qT = obtainStyledAttributes.getColor(R.styleable.ChartTheme_sc_annotationTextColor, resources.getColor(R.color.sc_annotationDefaultTextColor));
        resources.getValue(R.dimen.sc_defaultAnnotationTextSize, typedValue, true);
        rN = obtainStyledAttributes.getFloat(R.styleable.ChartTheme_sc_annotationTextSize, typedValue.getFloat());
        obtainStyledAttributes.recycle();
    }
}
