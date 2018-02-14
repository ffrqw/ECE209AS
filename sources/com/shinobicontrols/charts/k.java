package com.shinobicontrols.charts;

import com.shinobicontrols.charts.Series.Orientation;

class k extends em {
    k(BandSeries bandSeries) {
        super(false, bandSeries);
    }

    void ah() {
        l lVar = (l) this.pv.ot;
        lVar.cs = 0;
        lVar.cq = 0;
        lVar.cr = 0;
        lVar.h(this.pv.db.je.length);
        int i = 0;
        int i2 = 1;
        while (i < this.pv.db.je.length) {
            InternalDataPoint internalDataPoint = this.pv.db.je[i];
            a(i, internalDataPoint, lVar.bW, lVar.bZ, lVar);
            if (internalDataPoint.iU) {
                a(lVar.cq, internalDataPoint, lVar.bY, lVar.cb, lVar);
                lVar.cq++;
            } else {
                a(lVar.cr, internalDataPoint, lVar.bX, lVar.ca, lVar);
                lVar.cr++;
            }
            int i3 = i << 1;
            int g = g(i3);
            if (i3 > 0) {
                if (lVar.bW[g] >= lVar.bZ[g]) {
                    if (i2 == 0) {
                        lVar.cs++;
                        i3 = 1;
                    }
                } else if (i2 != 0) {
                    lVar.cs++;
                    i3 = 0;
                }
                i3 = i2;
            } else {
                i3 = lVar.bW[g] >= lVar.bZ[g] ? 1 : 0;
            }
            i++;
            i2 = i3;
        }
    }

    private int g(int i) {
        return this.pv.dR == Orientation.HORIZONTAL ? i + 1 : i;
    }

    private void a(int i, InternalDataPoint internalDataPoint, float[] fArr, float[] fArr2, l lVar) {
        int i2 = i << 1;
        if (this.pv.dR == Orientation.VERTICAL) {
            fArr[i2 + 1] = (float) internalDataPoint.x;
            fArr[i2] = ((Double) internalDataPoint.iW.get("High")).floatValue();
            fArr2[i2 + 1] = (float) internalDataPoint.x;
            fArr2[i2] = ((Double) internalDataPoint.iW.get("Low")).floatValue();
            internalDataPoint.iP = (((double) ((Double) internalDataPoint.iW.get("Low")).floatValue()) + ((Double) internalDataPoint.iW.get("High")).doubleValue()) / 2.0d;
            internalDataPoint.iQ = internalDataPoint.x;
            return;
        }
        fArr[i2] = (float) internalDataPoint.x;
        fArr[i2 + 1] = ((Double) internalDataPoint.iW.get("High")).floatValue();
        fArr2[i2] = (float) internalDataPoint.x;
        fArr2[i2 + 1] = ((Double) internalDataPoint.iW.get("Low")).floatValue();
        internalDataPoint.iP = internalDataPoint.x;
        internalDataPoint.iQ = (((double) ((Double) internalDataPoint.iW.get("Low")).floatValue()) + ((Double) internalDataPoint.iW.get("High")).doubleValue()) / 2.0d;
    }
}
