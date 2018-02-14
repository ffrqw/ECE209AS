package com.shinobicontrols.charts;

import com.shinobicontrols.charts.PieDonutSeries.DrawDirection;
import com.shinobicontrols.charts.PieDonutSeries.RadialEffect;

class dm extends s<PieDonutSeries<?>> {
    private float kC;
    private final a lO = new a();
    private final a lP = new a();
    private float lQ;
    private DrawDirection lR;
    private float lS;

    private static class a {
        boolean lT;
        boolean lU;
        Integer[] lV;
        Integer[] lW;
        RadialEffect lX;
        float lY;

        private a() {
        }

        int flavorColorAtIndex(int index) {
            return this.lV[index % this.lV.length].intValue();
        }

        int crustColorAtIndex(int index) {
            return this.lW[index % this.lW.length].intValue();
        }
    }

    dm(PieDonutSeries<?> pieDonutSeries) {
        super(pieDonutSeries);
    }

    public void a(bd bdVar, SChartGLDrawer sChartGLDrawer) {
        c(bdVar, sChartGLDrawer);
    }

    public void b(bd bdVar, SChartGLDrawer sChartGLDrawer) {
        c(bdVar, sChartGLDrawer);
    }

    private void c(bd bdVar, SChartGLDrawer sChartGLDrawer) {
        a((PieDonutSeries) this.cZ);
        float f = bdVar.cs().density;
        float max = 1.0f / Math.max(bdVar.cq(), bdVar.cr());
        int length = this.db.je.length;
        int i = 0;
        while (i < length) {
            double d;
            double d2;
            PieDonutSlice pieDonutSlice = (PieDonutSlice) this.db.je[i];
            a aVar = pieDonutSlice.iU ? this.lP : this.lO;
            if (this.lR == DrawDirection.ANTICLOCKWISE) {
                d = (double) (this.lS + pieDonutSlice.mF);
                d2 = (double) (this.lS + pieDonutSlice.mG);
            } else {
                d = (double) (this.lS - pieDonutSlice.mF);
                d2 = (double) (this.lS - pieDonutSlice.mG);
            }
            sChartGLDrawer.drawRadialSlice(((PieDonutSeries) this.cZ).ej(), i, this.cZ, (float) d, (float) d2, this.kC, this.lQ, pieDonutSlice.mI, aVar.lT ? aVar.flavorColorAtIndex(i) : 0, aVar.lU ? aVar.crustColorAtIndex(i) : 0, aVar.lY * f, (aVar.lT ? aVar.lX : RadialEffect.FLAT).getXmlValue(), max);
            i++;
        }
    }

    private void a(PieDonutSeries<?> pieDonutSeries) {
        PieDonutSeriesStyle pieDonutSeriesStyle = (PieDonutSeriesStyle) pieDonutSeries.ou;
        PieDonutSeriesStyle pieDonutSeriesStyle2 = (PieDonutSeriesStyle) pieDonutSeries.ov;
        this.lQ = pieDonutSeries.getOuterRadius();
        this.kC = pieDonutSeries.getInnerRadius();
        this.lR = pieDonutSeries.getDrawDirection();
        this.lS = pieDonutSeries.getRotation();
        this.db = pieDonutSeries.db;
        this.lO.lT = pieDonutSeriesStyle.isFlavorShown();
        this.lO.lU = pieDonutSeriesStyle.isCrustShown();
        this.lO.lV = pieDonutSeriesStyle.dp();
        this.lO.lW = pieDonutSeriesStyle.do();
        this.lO.lX = pieDonutSeriesStyle.getRadialEffect();
        this.lO.lY = pieDonutSeriesStyle.getCrustThickness();
        this.lP.lT = pieDonutSeriesStyle2.isFlavorShown();
        this.lP.lU = pieDonutSeriesStyle2.isCrustShown();
        this.lP.lV = pieDonutSeriesStyle2.dp();
        this.lP.lW = pieDonutSeriesStyle2.do();
        this.lP.lX = pieDonutSeriesStyle2.getRadialEffect();
        this.lP.lY = pieDonutSeriesStyle2.getCrustThickness();
    }
}
