package com.shinobicontrols.charts;

import android.graphics.PointF;
import com.shinobicontrols.charts.Series.SelectionMode;

class ej {
    private final af J;

    private static class a {
        double oW;
        double w;
        double x;
        double y;

        private a() {
        }
    }

    enum b {
        CROSSHAIR_ENABLED,
        SELECTION_MODE_NOT_NONE
    }

    ej(af afVar) {
        this.J = afVar;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    com.shinobicontrols.charts.Series.a a(android.graphics.PointF r6, com.shinobicontrols.charts.ej.b r7) {
        /*
        r5 = this;
        r1 = 0;
        r0 = r5.J;
        r0 = r0.bj();
        r2 = r0.iterator();
    L_0x000b:
        r0 = r2.hasNext();
        if (r0 == 0) goto L_0x0041;
    L_0x0011:
        r0 = r2.next();
        r0 = (com.shinobicontrols.charts.CartesianSeries) r0;
        r3 = r0.oC;
        if (r3 != 0) goto L_0x000b;
    L_0x001b:
        r3 = com.shinobicontrols.charts.ej.AnonymousClass1.oU;
        r4 = r7.ordinal();
        r3 = r3[r4];
        switch(r3) {
            case 1: goto L_0x0033;
            case 2: goto L_0x003a;
            default: goto L_0x0026;
        };
    L_0x0026:
        r3 = 0;
        r0 = r5.a(r0, r6, r3, r7);
        r3 = r0.a(r1);
        if (r3 == 0) goto L_0x0042;
    L_0x0031:
        r1 = r0;
        goto L_0x000b;
    L_0x0033:
        r3 = r0.dX;
        r3 = r3.gc;
        if (r3 != 0) goto L_0x0026;
    L_0x0039:
        goto L_0x000b;
    L_0x003a:
        r3 = r0.ox;
        r4 = com.shinobicontrols.charts.Series.SelectionMode.NONE;
        if (r3 == r4) goto L_0x000b;
    L_0x0040:
        goto L_0x0026;
    L_0x0041:
        return r1;
    L_0x0042:
        r0 = r1;
        goto L_0x0031;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.shinobicontrols.charts.ej.a(android.graphics.PointF, com.shinobicontrols.charts.ej$b):com.shinobicontrols.charts.Series$a");
    }

    a m(PointF pointF) {
        double d = (double) (-(pointF.x - ((float) this.J.em.aX.centerX())));
        double centerY = (double) (pointF.y - ((float) this.J.em.aX.centerY()));
        float sqrt = (float) (Math.sqrt((centerY * centerY) + (d * d)) / (((double) Math.min(this.J.em.aX.width(), this.J.em.aX.height())) / 2.0d));
        for (Series series : this.J.en) {
            if (!(series.getSelectionMode() == SelectionMode.NONE || series.getSelectionMode() == SelectionMode.SERIES || !series.eh())) {
                PieDonutSeries pieDonutSeries = (PieDonutSeries) series;
                if (pieDonutSeries.getInnerRadius() < sqrt) {
                    if (sqrt <= Math.max(((PieDonutSeriesStyle) pieDonutSeries.ov).getProtrusion(), ((PieDonutSeriesStyle) pieDonutSeries.ou).getProtrusion()) + pieDonutSeries.getOuterRadius()) {
                        a aVar = new a(pieDonutSeries);
                        int length = series.db.je.length;
                        if (length == 1) {
                            aVar.c(series.db.je[0]);
                            return aVar;
                        }
                        eb dn = pieDonutSeries.dn();
                        float rotation = pieDonutSeries.getRotation();
                        float d2 = dn.d(d, centerY);
                        int i = 0;
                        while (i < length) {
                            PieDonutSlice pieDonutSlice = (PieDonutSlice) series.db.je[i];
                            float d3 = dn.d(rotation, pieDonutSlice.mF);
                            float e = dn.e(rotation, pieDonutSlice.mG);
                            if ((e <= d2 || d2 <= d3) && (d3 <= e || (d2 <= d3 && e <= d2))) {
                                i++;
                            } else {
                                aVar.c(pieDonutSlice);
                                return aVar;
                            }
                        }
                        return aVar;
                    }
                } else {
                    continue;
                }
            }
        }
        return null;
    }

    a a(CartesianSeries<?> cartesianSeries, PointF pointF, boolean z, b bVar) {
        a aVar = new a(cartesianSeries);
        cartesianSeries.a(aVar, new du(cartesianSeries.getXAxis().e((double) pointF.x), cartesianSeries.getYAxis().e((double) pointF.y)), z, bVar);
        return aVar;
    }

    static double a(du duVar, du duVar2, du duVar3, a aVar) {
        double d = (duVar.x - duVar2.x) / duVar3.x;
        double d2 = (duVar.y - duVar2.y) / duVar3.y;
        switch (aVar) {
            case CROW_FLIES:
                return Math.sqrt((d * d) + (d2 * d2));
            case HORIZONTAL:
                return Math.abs(d);
            case VERTICAL:
                return Math.abs(d2);
            default:
                throw new IllegalStateException(String.format("Bad distance mode %d", new Object[]{aVar}));
        }
    }

    static du f(CartesianSeries<?> cartesianSeries) {
        return new du(cartesianSeries.getXAxis().ai.getSpan(), (((double) cartesianSeries.J.em.aX.width()) / ((double) cartesianSeries.J.em.aX.height())) * cartesianSeries.getYAxis().ai.getSpan());
    }

    private static void a(CartesianSeries<?> cartesianSeries, a aVar, du duVar, boolean z, InternalDataPoint[] internalDataPointArr) {
        du f = f(cartesianSeries);
        double d = Double.MAX_VALUE;
        du duVar2 = new du();
        int length = internalDataPointArr.length;
        int i = 0;
        while (i < length) {
            InternalDataPoint internalDataPoint = internalDataPointArr[i];
            duVar2.x = internalDataPoint.iP;
            duVar2.y = internalDataPoint.iQ;
            double a = a(duVar, duVar2, f, z ? cartesianSeries.ar() : cartesianSeries.aq());
            if (a < d) {
                aVar.t(a);
                aVar.c(internalDataPoint);
            } else {
                a = d;
            }
            i++;
            d = a;
        }
    }

    static void b(CartesianSeries<?> cartesianSeries, a aVar, du duVar, boolean z, InternalDataPoint[] internalDataPointArr) {
        a((CartesianSeries) cartesianSeries, aVar, duVar, z, internalDataPointArr);
        if (a.b(aVar)) {
            cartesianSeries.a(aVar, duVar);
        }
    }

    static void a(BarColumnSeries<?> barColumnSeries, a aVar, du duVar, boolean z, InternalDataPoint[] internalDataPointArr) {
        Object obj = 1;
        b((CartesianSeries) barColumnSeries, aVar, duVar, z, internalDataPointArr);
        if (a.b(aVar)) {
            InternalDataPoint el = aVar.el();
            double d = (double) barColumnSeries.cP;
            a aVar2 = new a();
            aVar2.y = el.iQ - (d / 2.0d);
            aVar2.oW = d;
            d = barColumnSeries.oy.h(barColumnSeries);
            double d2 = el.x;
            if (barColumnSeries.dL != null) {
                if (d2 >= 0.0d) {
                    obj = null;
                }
                if (obj != null) {
                    aVar2.x = el.iP;
                    aVar2.w = d - el.x;
                } else {
                    aVar2.x = el.iP - el.x;
                    aVar2.w = d2;
                }
            } else {
                if (d2 >= d) {
                    obj = null;
                }
                if (obj != null) {
                    aVar2.x = d2;
                    aVar2.w = d - d2;
                } else {
                    aVar2.x = d;
                    aVar2.w = d2 - d;
                }
            }
            a(aVar, duVar, aVar2, z);
        }
    }

    static void b(BarColumnSeries<?> barColumnSeries, a aVar, du duVar, boolean z, InternalDataPoint[] internalDataPointArr) {
        Object obj = 1;
        b((CartesianSeries) barColumnSeries, aVar, duVar, z, internalDataPointArr);
        if (a.b(aVar)) {
            InternalDataPoint el = aVar.el();
            double d = (double) barColumnSeries.cP;
            a aVar2 = new a();
            aVar2.x = el.iP - (d / 2.0d);
            aVar2.w = d;
            d = barColumnSeries.oy.h(barColumnSeries);
            double d2 = el.y;
            if (barColumnSeries.dL != null) {
                if (d2 >= 0.0d) {
                    obj = null;
                }
                if (obj != null) {
                    aVar2.y = el.iQ;
                    aVar2.oW = d - el.y;
                } else {
                    aVar2.y = el.iQ - el.y;
                    aVar2.oW = d2;
                }
            } else {
                if (d2 >= d) {
                    obj = null;
                }
                if (obj != null) {
                    aVar2.y = d2;
                    aVar2.oW = d - d2;
                } else {
                    aVar2.y = d;
                    aVar2.oW = d2 - d;
                }
            }
            a(aVar, duVar, aVar2, z);
        }
    }

    private static void a(a aVar, du duVar, a aVar2, boolean z) {
        if (a(duVar, aVar2)) {
            aVar.t(0.0d);
            return;
        }
        aVar.t(Double.MAX_VALUE);
        if (!z) {
            aVar.invalidate();
        }
    }

    private static boolean a(du duVar, a aVar) {
        a(aVar);
        return aVar.x <= duVar.x && duVar.x <= aVar.x + aVar.w && aVar.y <= duVar.y && duVar.y <= aVar.y + aVar.oW;
    }

    private static void a(a aVar) {
        if (aVar.w < 0.0d) {
            aVar.x += aVar.w;
            aVar.w = Math.abs(aVar.w);
        }
        if (aVar.oW < 0.0d) {
            aVar.y += aVar.oW;
            aVar.oW = Math.abs(aVar.oW);
        }
    }
}
