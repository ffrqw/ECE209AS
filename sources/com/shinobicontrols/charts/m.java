package com.shinobicontrols.charts;

import com.shinobicontrols.charts.Series.Orientation;
import java.util.List;

class m extends em {
    m(BarColumnSeries<?> barColumnSeries) {
        super(true, barColumnSeries);
    }

    void e(List<CartesianSeries<?>> list) {
        f(list);
        g(list);
    }

    void ah() {
        int i = 0;
        n nVar = (n) this.pv.ot;
        nVar.dJ = (float) this.pv.oy.h(this.pv);
        nVar.cG = 0;
        nVar.i(this.pv.db.je.length * 2);
        nVar.bZ = null;
        while (i < this.pv.db.je.length) {
            InternalDataPoint internalDataPoint = this.pv.db.je[i];
            float[] fArr = nVar.points;
            int i2 = nVar.cG;
            nVar.cG = i2 + 1;
            fArr[i2] = (float) internalDataPoint.iP;
            fArr = nVar.points;
            i2 = nVar.cG;
            nVar.cG = i2 + 1;
            fArr[i2] = (float) internalDataPoint.iQ;
            i++;
        }
    }

    private static void f(List<CartesianSeries<?>> list) {
        for (CartesianSeries cartesianSeries : list) {
            n nVar = (n) cartesianSeries.ot;
            nVar.dJ = (float) cartesianSeries.oy.h(cartesianSeries);
            nVar.i(cartesianSeries.db.je.length * 2);
            nVar.j(cartesianSeries.db.je.length);
            nVar.cG = 0;
        }
    }

    private static void g(List<CartesianSeries<?>> list) {
        fa faVar = new fa(list, false, br.ja);
        while (faVar.hasNext()) {
            a eG = faVar.eG();
            CartesianSeries cartesianSeries = (CartesianSeries) list.get(list.size() - 1);
            while (true) {
                b k = eG.k(cartesianSeries);
                if (k.eI()) {
                    n nVar = (n) cartesianSeries.ot;
                    InternalDataPoint internalDataPoint = cartesianSeries.db.je[k.index];
                    if ((cartesianSeries.dR == Orientation.HORIZONTAL ? internalDataPoint.x : internalDataPoint.y) == eG.pY) {
                        double a = a(cartesianSeries, eG);
                        float[] fArr;
                        int i;
                        if (cartesianSeries.dR == Orientation.HORIZONTAL) {
                            fArr = nVar.points;
                            i = nVar.cG;
                            nVar.cG = i + 1;
                            fArr[i] = (float) internalDataPoint.iP;
                            fArr = nVar.points;
                            i = nVar.cG;
                            nVar.cG = i + 1;
                            fArr[i] = (float) a;
                            nVar.bZ[k.index] = (float) (a - internalDataPoint.y);
                            internalDataPoint.iQ = a;
                        } else {
                            fArr = nVar.points;
                            i = nVar.cG;
                            nVar.cG = i + 1;
                            fArr[i] = (float) a;
                            fArr = nVar.points;
                            i = nVar.cG;
                            nVar.cG = i + 1;
                            fArr[i] = (float) internalDataPoint.iQ;
                            nVar.bZ[k.index] = (float) (a - internalDataPoint.x);
                            internalDataPoint.iP = a;
                        }
                    }
                }
                CartesianSeries aJ = cartesianSeries.aJ();
                if (aJ != null) {
                    cartesianSeries = aJ;
                }
            }
        }
    }

    void a(List<CartesianSeries<?>> list, a aVar, NumberRange numberRange, br brVar) {
        CartesianSeries cartesianSeries = (CartesianSeries) list.get(list.size() - 1);
        do {
            if (!cartesianSeries.oC && aVar.k(cartesianSeries).eI()) {
                numberRange.l(a(cartesianSeries, aVar) * 1.01d);
            }
            cartesianSeries = cartesianSeries.aJ();
        } while (cartesianSeries != null);
        cartesianSeries = (CartesianSeries) list.get(0);
        numberRange.l(cartesianSeries.oy.h(cartesianSeries));
    }

    void b(List<CartesianSeries<?>> list, a aVar, NumberRange numberRange, br brVar) {
        CartesianSeries cartesianSeries = (CartesianSeries) list.get(list.size() - 1);
        do {
            if (aVar.k(cartesianSeries).eI()) {
                numberRange.l(a(cartesianSeries, aVar));
            }
            cartesianSeries = cartesianSeries.aJ();
        } while (cartesianSeries != null);
        numberRange.l(((CartesianSeries) list.get(0)).oy.h((CartesianSeries) list.get(0)));
    }

    private static double a(CartesianSeries<?> cartesianSeries, a aVar) {
        CartesianSeries aJ = cartesianSeries.aJ();
        double a = aJ == null ? (double) ((n) cartesianSeries.ot).dJ : a(aJ, aVar);
        b k = aVar.k(cartesianSeries);
        if (!k.eI()) {
            return a;
        }
        InternalDataPoint internalDataPoint = cartesianSeries.db.je[k.index];
        double d = cartesianSeries.dR == Orientation.HORIZONTAL ? internalDataPoint.x : internalDataPoint.y;
        double d2 = cartesianSeries.dR == Orientation.HORIZONTAL ? internalDataPoint.y : internalDataPoint.x;
        if (d == aVar.pY) {
            return a + d2;
        }
        return a;
    }
}
