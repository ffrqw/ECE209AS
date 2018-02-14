package com.shinobicontrols.charts;

class df implements aq {
    df() {
    }

    public void a(bt btVar, InternalDataPoint internalDataPoint) {
        btVar.jc.l(internalDataPoint.x);
        btVar.jd.l(((Double) internalDataPoint.iW.get("Low")).doubleValue());
        btVar.jd.l(((Double) internalDataPoint.iW.get("High")).doubleValue());
        btVar.jd.l(((Double) internalDataPoint.iW.get("Open")).doubleValue());
        btVar.jd.l(((Double) internalDataPoint.iW.get("Close")).doubleValue());
    }
}
