package com.shinobicontrols.charts;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

class InternalDataPoint {
    static a iY = new a();
    static b iZ = new b();
    double iP;
    double iQ;
    int iR;
    double iS;
    double iT;
    boolean iU;
    int iV;
    Map<String, Double> iW;
    final boolean iX;
    double x;
    double y;

    static class a implements Comparator<InternalDataPoint> {
        a() {
        }

        public /* synthetic */ int compare(Object x0, Object x1) {
            return a((InternalDataPoint) x0, (InternalDataPoint) x1);
        }

        public int a(InternalDataPoint internalDataPoint, InternalDataPoint internalDataPoint2) {
            return Double.compare(internalDataPoint.x, internalDataPoint2.x);
        }
    }

    static class b implements Comparator<InternalDataPoint> {
        b() {
        }

        public /* synthetic */ int compare(Object x0, Object x1) {
            return a((InternalDataPoint) x0, (InternalDataPoint) x1);
        }

        public int a(InternalDataPoint internalDataPoint, InternalDataPoint internalDataPoint2) {
            return Double.compare(internalDataPoint.y, internalDataPoint2.y);
        }
    }

    InternalDataPoint() {
        this.iR = 0;
        this.iS = 0.0d;
        this.iT = 0.0d;
        this.iU = false;
        this.iX = true;
    }

    InternalDataPoint(double x, double y) {
        this.iR = 0;
        this.iS = 0.0d;
        this.iT = 0.0d;
        this.iU = false;
        this.x = x;
        this.y = y;
        this.iP = x;
        this.iQ = y;
        this.iX = false;
    }

    InternalDataPoint(InternalDataPoint internalDataPoint) {
        this.iR = 0;
        this.iS = 0.0d;
        this.iT = 0.0d;
        this.iU = false;
        this.x = internalDataPoint.x;
        this.y = internalDataPoint.y;
        this.iP = internalDataPoint.iP;
        this.iQ = internalDataPoint.iQ;
        this.iR = internalDataPoint.iR;
        this.iS = internalDataPoint.iS;
        this.iT = internalDataPoint.iT;
        this.iU = internalDataPoint.iU;
        this.iV = internalDataPoint.iV;
        if (internalDataPoint.iW != null) {
            cQ();
            for (Entry entry : internalDataPoint.iW.entrySet()) {
                this.iW.put(entry.getKey(), entry.getValue());
            }
        }
        this.iX = internalDataPoint.iX;
    }

    void a(double d, double d2, double d3, double d4) {
        b(d3, d2);
        this.iW.put("Open", Double.valueOf(d));
        this.iW.put("Close", Double.valueOf(d4));
    }

    void b(double d, double d2) {
        if (this.iX) {
            cQ();
            this.iW.put("Low", Double.valueOf(d));
            this.iW.put("High", Double.valueOf(d2));
            return;
        }
        throw new IllegalStateException("Cannot add additional values to a non-multi value InternalDataPoint.");
    }

    private void cQ() {
        if (this.iW == null) {
            this.iW = new HashMap();
        }
    }

    boolean cR() {
        return ((Double) this.iW.get("Close")).doubleValue() > ((Double) this.iW.get("Open")).doubleValue();
    }

    public String toString() {
        if (this.iX) {
            return String.format("x: %f, open: %f, high: %f, low: %f, close: %f", new Object[]{Double.valueOf(this.x), this.iW.get("Open"), this.iW.get("High"), this.iW.get("Low"), this.iW.get("Close")});
        }
        return String.format("x: %f, y: %f, xCoord: %f, yCoord: %f", new Object[]{Double.valueOf(this.x), Double.valueOf(this.y), Double.valueOf(this.iP), Double.valueOf(this.iQ)});
    }
}
