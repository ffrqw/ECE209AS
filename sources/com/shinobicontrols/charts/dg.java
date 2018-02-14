package com.shinobicontrols.charts;

class dg extends em {
    dg(OHLCSeries oHLCSeries) {
        super(false, oHLCSeries);
    }

    void ah() {
        int i = 0;
        dj djVar = (dj) this.pv.ot;
        djVar.k(this.pv.db.je.length);
        int i2 = 0;
        while (i2 < this.pv.db.je.length) {
            InternalDataPoint internalDataPoint = this.pv.db.je[i2];
            int i3 = i + 1;
            djVar.points[i] = (float) internalDataPoint.iP;
            int i4 = i3 + 1;
            djVar.points[i3] = ((Double) internalDataPoint.iW.get("High")).floatValue();
            i3 = i4 + 1;
            djVar.points[i4] = ((Double) internalDataPoint.iW.get("Open")).floatValue();
            i4 = i3 + 1;
            djVar.points[i3] = ((Double) internalDataPoint.iW.get("Close")).floatValue();
            int i5 = i4 + 1;
            djVar.points[i4] = ((Double) internalDataPoint.iW.get("Low")).floatValue();
            internalDataPoint.iQ = (((Double) internalDataPoint.iW.get("High")).doubleValue() + ((double) ((Double) internalDataPoint.iW.get("Low")).floatValue())) / 2.0d;
            i2++;
            i = i5;
        }
    }
}
