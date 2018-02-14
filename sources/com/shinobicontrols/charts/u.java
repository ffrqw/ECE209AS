package com.shinobicontrols.charts;

class u extends em {
    u(CandlestickSeries candlestickSeries) {
        super(false, candlestickSeries);
    }

    void ah() {
        int i = 0;
        v vVar = (v) this.pv.ot;
        vVar.k(this.pv.db.je.length);
        int i2 = 0;
        while (i2 < this.pv.db.je.length) {
            InternalDataPoint internalDataPoint = this.pv.db.je[i2];
            int i3 = i + 1;
            vVar.points[i] = (float) internalDataPoint.iP;
            int i4 = i3 + 1;
            vVar.points[i3] = ((Double) internalDataPoint.iW.get("High")).floatValue();
            i3 = i4 + 1;
            vVar.points[i4] = ((Double) internalDataPoint.iW.get("Open")).floatValue();
            i4 = i3 + 1;
            vVar.points[i3] = ((Double) internalDataPoint.iW.get("Close")).floatValue();
            int i5 = i4 + 1;
            vVar.points[i4] = ((Double) internalDataPoint.iW.get("Low")).floatValue();
            internalDataPoint.iQ = (((Double) internalDataPoint.iW.get("High")).doubleValue() + ((double) ((Double) internalDataPoint.iW.get("Low")).floatValue())) / 2.0d;
            i2++;
            i = i5;
        }
    }
}
