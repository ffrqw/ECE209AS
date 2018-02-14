package com.shinobicontrols.charts;

class bo implements aq {
    bo() {
    }

    public void a(bt btVar, InternalDataPoint internalDataPoint) {
        btVar.jc.l(internalDataPoint.x);
        btVar.jd.l(((Double) internalDataPoint.iW.get("Low")).doubleValue());
        btVar.jd.l(((Double) internalDataPoint.iW.get("High")).doubleValue());
    }
}
