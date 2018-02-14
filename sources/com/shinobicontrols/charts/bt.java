package com.shinobicontrols.charts;

import java.util.List;

class bt {
    NumberRange jc = new NumberRange();
    NumberRange jd = new NumberRange();
    InternalDataPoint[] je = new InternalDataPoint[0];
    private InternalDataPoint[] jf = new InternalDataPoint[0];
    private InternalDataPoint[] jg = new InternalDataPoint[0];

    bt() {
    }

    InternalDataPoint[] cS() {
        return this.jf;
    }

    void n(int i) {
        if (this.je == null || this.je.length != i) {
            this.je = new InternalDataPoint[i];
        }
    }

    private void o(int i) {
        if (this.jf == null || this.jf.length != i) {
            this.jf = new InternalDataPoint[i];
        }
    }

    private void p(int i) {
        if (this.jg == null || this.jg.length != i) {
            this.jg = new InternalDataPoint[i];
        }
    }

    private void k(List<InternalDataPoint> list) {
        o(list.size());
        this.jf = (InternalDataPoint[]) list.toArray(this.jf);
    }

    private void cT() {
        p(this.jf.length);
        System.arraycopy(this.jf, 0, this.jg, 0, this.jf.length);
    }

    void l(List<InternalDataPoint> list) {
        k(list);
    }

    void m(List<InternalDataPoint> list) {
        k(list);
        cT();
    }

    void cU() {
        if (this.jg != null) {
            o(this.jg.length);
            System.arraycopy(this.jg, 0, this.jf, 0, this.jg.length);
        }
    }

    void cV() {
        this.jc.reset();
        this.jd.reset();
    }
}
