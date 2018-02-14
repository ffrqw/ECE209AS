package com.shinobicontrols.charts;

class bj implements bm {
    private final bm iN;

    public bj() {
        this(bm.iO);
    }

    public bj(bm bmVar) {
        this.iN = bmVar;
    }

    public boolean n(Series<?> series) {
        return this.iN.n(series) && series.J != null;
    }
}
