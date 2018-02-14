package com.shinobicontrols.charts;

import java.util.ArrayList;
import java.util.List;

class dv {
    private final du mV = new du();
    private final du mW = new du();
    private final List<a> mX = new ArrayList();
    private final du mY = new du();

    private static class a {
        double mZ;
        double na;
        double nb;
        double nc;

        private a() {
        }

        static a a(du duVar, du duVar2) {
            a aVar = new a();
            aVar.mZ = duVar.x;
            aVar.na = duVar.y;
            aVar.nb = duVar2.x;
            aVar.nc = duVar2.y;
            return aVar;
        }
    }

    dv() {
    }

    du a(InternalDataPoint internalDataPoint, du duVar, InternalDataPoint[] internalDataPointArr, boolean z) {
        double d;
        double d2;
        reset();
        if (z) {
            d = duVar.x;
            d2 = duVar.y;
        } else {
            d = duVar.y;
            d2 = duVar.x;
        }
        a(internalDataPointArr, d, z);
        if (!a(z, d, d2)) {
            b(internalDataPoint);
        }
        return this.mY;
    }

    private void reset() {
        this.mV.x = 0.0d;
        this.mV.y = 0.0d;
        this.mW.x = 0.0d;
        this.mW.y = 0.0d;
        this.mX.clear();
        this.mY.x = 0.0d;
        this.mY.y = 0.0d;
    }

    private void a(InternalDataPoint[] internalDataPointArr, double d, boolean z) {
        int length = internalDataPointArr.length;
        for (int i = 0; i < length - 1; i++) {
            this.mV.x = internalDataPointArr[i].iP;
            this.mV.y = internalDataPointArr[i].iQ;
            this.mW.x = internalDataPointArr[i + 1].iP;
            this.mW.y = internalDataPointArr[i + 1].iQ;
            f(z);
            double d2 = z ? this.mV.x : this.mV.y;
            double d3 = z ? this.mW.x : this.mW.y;
            if (d >= d2 && d <= d3) {
                this.mX.add(a.a(this.mV, this.mW));
            }
        }
    }

    private void f(boolean z) {
        Object obj = 1;
        if (z) {
            if (this.mV.x <= this.mW.x) {
                obj = null;
            }
        } else if (this.mV.y <= this.mW.y) {
            obj = null;
        }
        if (obj != null) {
            double d = this.mV.x;
            double d2 = this.mV.y;
            this.mV.x = this.mW.x;
            this.mV.y = this.mW.y;
            this.mW.x = d;
            this.mW.y = d2;
        }
    }

    private boolean a(boolean z, double d, double d2) {
        double d3 = Double.MAX_VALUE;
        boolean z2 = false;
        Object[] toArray = this.mX.toArray();
        int length = toArray.length;
        int i = 0;
        while (i < length) {
            boolean z3;
            a aVar = (a) toArray[i];
            double a = a(aVar, z, d);
            double d4 = aVar.mZ + ((aVar.nb - aVar.mZ) * a);
            double d5 = aVar.na + (a * (aVar.nc - aVar.na));
            if (z) {
                a = Math.abs(d2 - d5);
            } else {
                a = Math.abs(d2 - d4);
            }
            if (a < d3) {
                this.mY.x = d4;
                this.mY.y = d5;
                z3 = true;
            } else {
                z3 = z2;
                a = d3;
            }
            i++;
            d3 = a;
            z2 = z3;
        }
        return z2;
    }

    private double a(a aVar, boolean z, double d) {
        double d2 = z ? aVar.mZ : aVar.na;
        double d3 = z ? aVar.nb : aVar.nc;
        return d3 != d2 ? Math.abs((d - d2) / (d3 - d2)) : 1.0d;
    }

    private void b(InternalDataPoint internalDataPoint) {
        this.mY.x = internalDataPoint.iP;
        this.mY.y = internalDataPoint.iQ;
    }
}
