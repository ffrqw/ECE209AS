package com.shinobicontrols.charts;

import com.shinobicontrols.charts.Series.Orientation;
import java.util.List;

class cq extends em {
    private final a ko;
    private final a kp;

    static class a {
        int cq;
        int cr;
        float dJ;
        int kq = 0;
        boolean kr = false;
        float[] ks;
        int[] kt;
        int ku;
        float kv;
        float kw;
        int kx;
        boolean ky;
        float[] points;

        a() {
        }

        void i(int i) {
            if (this.points == null || this.points.length != i) {
                this.points = new float[i];
            }
        }

        void v(int i) {
            if (this.ks == null || this.ks.length != i) {
                this.ks = new float[i];
            }
        }

        void w(int i) {
            if (this.kt == null || this.kt.length != i) {
                this.kt = new int[i];
            }
        }
    }

    cq(LineSeries lineSeries, a aVar, a aVar2) {
        super(true, lineSeries);
        this.ko = aVar;
        this.kp = aVar2;
    }

    public void e(List<CartesianSeries<?>> list) {
        CartesianSeries cartesianSeries = (LineSeries) list.get(0);
        cq cqVar = (cq) cartesianSeries.dU;
        cqVar.a(cartesianSeries, cartesianSeries.ko);
        a((List) list, br.ja, cr.kz);
        b(list, br.ja, cr.kz);
        cqVar.a(true, cartesianSeries.ko.dJ, br.ja);
        cqVar.b(cartesianSeries.ko, br.ja);
        cqVar.c(cartesianSeries.ko, br.ja);
        cqVar.a(cartesianSeries, cartesianSeries.kp);
        a((List) list, br.jb, cr.kA);
        b(list, br.jb, cr.kA);
        cqVar.a(true, cartesianSeries.kp.dJ, br.jb);
        cqVar.b(cartesianSeries.kp, br.jb);
        cqVar.c(cartesianSeries.kp, br.jb);
    }

    public void ah() {
        a(this.ko, br.ja);
        a(this.kp, br.jb);
    }

    private void a(a aVar, br brVar) {
        a(this.pv, aVar);
        a(false, aVar.dJ, brVar);
        b(aVar, brVar);
        c(aVar, brVar);
    }

    private void a(CartesianSeries<?> cartesianSeries, a aVar) {
        aVar.dJ = (float) cartesianSeries.oy.h(cartesianSeries);
    }

    private void a(boolean z, float f, br brVar) {
        if (!z) {
            f = 0.0f;
        }
        for (InternalDataPoint internalDataPoint : brVar.e(this.pv)) {
            if (this.pv.dR == Orientation.VERTICAL) {
                internalDataPoint.iP = internalDataPoint.x + ((double) f);
            } else {
                internalDataPoint.iQ = internalDataPoint.y + ((double) f);
            }
        }
    }

    private void b(a aVar, br brVar) {
        d(aVar, brVar);
        aVar.cq = 0;
        aVar.cr = 0;
        this.pv.db.cU();
        InternalDataPoint[] e = brVar.e(this.pv);
        aVar.i((e.length + (aVar.ku << 1)) * 2);
        aVar.w(e.length);
    }

    private void c(a aVar, br brVar) {
        Object obj = 1;
        int i = 0;
        InternalDataPoint[] e = brVar.e(this.pv);
        int length = e.length;
        int i2 = 0;
        while (i2 < length) {
            Object obj2;
            Object obj3;
            int i3;
            InternalDataPoint internalDataPoint = e[i2];
            float f = (float) (this.pv.dR == Orientation.VERTICAL ? internalDataPoint.iP : internalDataPoint.iQ);
            float f2 = (float) (this.pv.dR == Orientation.VERTICAL ? internalDataPoint.iQ : internalDataPoint.iP);
            if (i > 0) {
                if (f >= aVar.dJ) {
                    if (obj == null) {
                        obj2 = 1;
                        obj3 = 1;
                    }
                } else if (obj != null) {
                    obj2 = null;
                    obj3 = 1;
                }
                obj3 = null;
                obj2 = obj;
            } else {
                obj2 = f >= aVar.dJ ? 1 : null;
                obj3 = null;
            }
            if (obj3 != null) {
                float abs = Math.abs(aVar.points[g(i - 2)] - aVar.dJ);
                float abs2 = Math.abs(f - aVar.dJ);
                if (abs == 0.0f && abs2 == 0.0f) {
                    abs = 1.0f;
                } else {
                    abs /= abs2 + abs;
                }
                abs2 = aVar.dJ;
                f = aVar.points[u(i - 2)];
                abs = (abs * (f2 - f)) + f;
                aVar.points[g(i)] = abs2;
                aVar.points[u(i)] = abs;
                i += 2;
                aVar.points[g(i)] = abs2;
                aVar.points[u(i)] = abs;
                i3 = i + 2;
            } else {
                i3 = i;
            }
            aVar.points[i3] = (float) internalDataPoint.iP;
            aVar.points[i3 + 1] = (float) internalDataPoint.iQ;
            a(internalDataPoint, aVar);
            i = i3 + 2;
            i2++;
            obj = obj2;
        }
        aVar.kq = i;
    }

    private static void a(InternalDataPoint internalDataPoint, a aVar) {
        if (internalDataPoint.iU) {
            aVar.kr = true;
            aVar.cq++;
            return;
        }
        aVar.cr++;
    }

    private static void a(List<CartesianSeries<?>> list, br brVar, cr crVar) {
        int a = a((List) list, false, brVar);
        for (LineSeries lineSeries = (LineSeries) list.get(list.size() - 1); lineSeries.aL(); lineSeries = (LineSeries) lineSeries.aJ()) {
            lineSeries.db.cU();
            ((cq) lineSeries.dU).a(lineSeries, a, crVar);
        }
    }

    private static int a(List<CartesianSeries<?>> list, boolean z, br brVar) {
        fa faVar = new fa(list, z, brVar);
        int i = 0;
        while (faVar.hasNext()) {
            faVar.eG();
            i++;
        }
        return i;
    }

    private void a(LineSeries lineSeries, int i, cr crVar) {
        a a = crVar.a(lineSeries);
        a.i(i * 2);
        a.v(i * 2);
        a.w(i);
        a.ku = 0;
        a.kq = 0;
        a.cq = 0;
        a.cr = 0;
        a.kx = 0;
        a.ky = false;
        lineSeries.kU.clear();
    }

    private static void b(List<CartesianSeries<?>> list, br brVar, cr crVar) {
        fa faVar = new fa(list, false, brVar);
        while (faVar.hasNext()) {
            a eG = faVar.eG();
            CartesianSeries cartesianSeries = (LineSeries) list.get(list.size() - 1);
            while (cartesianSeries.aL()) {
                CartesianSeries cartesianSeries2 = (LineSeries) cartesianSeries.aJ();
                b k = eG.k(cartesianSeries);
                if (a(k, eG.pY, brVar)) {
                    InternalDataPoint internalDataPoint = brVar.e(cartesianSeries)[k.index];
                    a a = crVar.a(cartesianSeries);
                    ct ctVar = (ct) cartesianSeries.ot;
                    ctVar.dJ = (float) cartesianSeries.oy.h(cartesianSeries);
                    ((cq) cartesianSeries.dU).a(ctVar, internalDataPoint, eG, cartesianSeries2, a, brVar);
                    a(internalDataPoint, a);
                }
                cartesianSeries = cartesianSeries2;
            }
        }
    }

    private void a(ct ctVar, InternalDataPoint internalDataPoint, a aVar, LineSeries lineSeries, a aVar2, br brVar) {
        float f;
        float f2;
        float f3 = (float) aVar.pY;
        if (internalDataPoint.x == aVar.pY) {
            internalDataPoint.iQ = a(this.pv, aVar, brVar);
            f = (float) internalDataPoint.iQ;
            f2 = (float) (internalDataPoint.iQ - internalDataPoint.y);
        } else {
            f = (float) a(this.pv, aVar, brVar);
            f2 = lineSeries != null ? (float) a(this.pv.aJ(), aVar, brVar) : ctVar.dJ;
        }
        aVar2.points[aVar2.kx] = f3;
        aVar2.points[aVar2.kx + 1] = f;
        if (this.pv.dR == Orientation.VERTICAL) {
            aVar2.ks[aVar2.kx] = f3;
            aVar2.ks[aVar2.kx + 1] = f;
        } else {
            aVar2.ks[aVar2.kx] = f3;
            aVar2.ks[aVar2.kx + 1] = f2;
        }
        if (aVar2.kx <= 0) {
            aVar2.ky = aVar2.points[g(aVar2.kx)] >= aVar2.ks[g(aVar2.kx)];
        } else if (aVar2.points[g(aVar2.kx)] >= aVar2.ks[g(aVar2.kx)]) {
            if (!aVar2.ky) {
                aVar2.ky = true;
                aVar2.ku++;
            }
        } else if (aVar2.ky) {
            aVar2.ky = false;
            aVar2.ku++;
        }
        aVar2.kx += 2;
        aVar2.kq = aVar2.kx;
        LineSeries lineSeries2 = (LineSeries) this.pv;
        if (lineSeries2.di()) {
            Object internalDataPoint2;
            if (internalDataPoint.x == aVar.pY) {
                internalDataPoint2 = new InternalDataPoint(internalDataPoint);
            } else {
                InternalDataPoint internalDataPoint3 = new InternalDataPoint((double) f3, (double) f);
            }
            lineSeries2.kU.add(internalDataPoint2);
        }
    }

    private void d(a aVar, br brVar) {
        float f = -3.4028235E38f;
        float f2 = Float.MAX_VALUE;
        aVar.ku = 0;
        InternalDataPoint[] e = brVar.e(this.pv);
        int length = e.length;
        int i = 1;
        for (int i2 = 0; i2 < length; i2++) {
            InternalDataPoint internalDataPoint = e[i2];
            float f3 = (float) (this.pv.dR == Orientation.VERTICAL ? internalDataPoint.iP : internalDataPoint.iQ);
            if (f3 > f) {
                f = f3;
            }
            if (f3 < f2) {
                f2 = f3;
            }
            if (i2 <= 0) {
                i = f3 >= aVar.dJ ? 1 : 0;
            } else if (f3 >= aVar.dJ) {
                if (i == 0) {
                    aVar.ku++;
                    i = 1;
                }
            } else if (i != 0) {
                aVar.ku++;
                i = 0;
            }
        }
        aVar.kv = Math.abs(f - aVar.dJ);
        if (aVar.kv == 0.0f) {
            aVar.kv = 0.01f;
        }
        aVar.kw = Math.abs(aVar.dJ - f2);
        if (aVar.kw == 0.0f) {
            aVar.kw = 0.01f;
        }
    }

    private static double a(InternalDataPoint internalDataPoint, InternalDataPoint internalDataPoint2, double d, Orientation orientation) {
        if (orientation == Orientation.HORIZONTAL) {
            return internalDataPoint.y + (((internalDataPoint2.y - internalDataPoint.y) * (d - internalDataPoint.x)) / (internalDataPoint2.x - internalDataPoint.x));
        }
        return internalDataPoint.x + (((internalDataPoint2.x - internalDataPoint.x) * (d - internalDataPoint.y)) / (internalDataPoint2.y - internalDataPoint.y));
    }

    private static double a(CartesianSeries<?> cartesianSeries, a aVar, br brVar) {
        CartesianSeries aJ = cartesianSeries.aJ();
        double h = aJ == null ? cartesianSeries.oy.h(cartesianSeries) : a(aJ, aVar, brVar);
        b k = aVar.k(cartesianSeries);
        if (!a(k, aVar.pY, brVar)) {
            return h;
        }
        InternalDataPoint[] e = brVar.e(cartesianSeries);
        InternalDataPoint internalDataPoint = e[k.index];
        double d = cartesianSeries.dR == Orientation.HORIZONTAL ? internalDataPoint.x : internalDataPoint.y;
        double d2 = cartesianSeries.dR == Orientation.HORIZONTAL ? internalDataPoint.y : internalDataPoint.x;
        if (d == aVar.pY) {
            return h + d2;
        }
        return h + a(e[k.index - 1], e[k.index], aVar.pY, cartesianSeries.dR);
    }

    void a(List<CartesianSeries<?>> list, a aVar, NumberRange numberRange, br brVar) {
        CartesianSeries cartesianSeries = (CartesianSeries) list.get(list.size() - 1);
        do {
            if (!cartesianSeries.oC && a(aVar.k(cartesianSeries), aVar.pY, brVar)) {
                numberRange.l(a(cartesianSeries, aVar, brVar));
            }
            cartesianSeries = cartesianSeries.aJ();
        } while (cartesianSeries != null);
    }

    void b(List<CartesianSeries<?>> list, a aVar, NumberRange numberRange, br brVar) {
        CartesianSeries cartesianSeries = (CartesianSeries) list.get(list.size() - 1);
        do {
            if (a(aVar.k(cartesianSeries), aVar.pY, brVar)) {
                numberRange.l(a(cartesianSeries, aVar, brVar));
            }
            cartesianSeries = cartesianSeries.aJ();
        } while (cartesianSeries != null);
    }

    private int g(int i) {
        return this.pv.dR == Orientation.HORIZONTAL ? i + 1 : i;
    }

    private int u(int i) {
        return this.pv.dR == Orientation.HORIZONTAL ? i : i + 1;
    }

    private static boolean a(b bVar, double d, br brVar) {
        if (!bVar.eI()) {
            return false;
        }
        InternalDataPoint[] e = brVar.e(bVar.pv);
        if (e[0].x > d || e[e.length - 1].x < d) {
            return false;
        }
        return true;
    }
}
