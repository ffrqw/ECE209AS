package com.shinobicontrols.charts;

class bk implements bm {
    private final bm iN;

    public bk() {
        this(bm.iO);
    }

    public bk(bm bmVar) {
        this.iN = bmVar;
    }

    public boolean n(Series<?> series) {
        return this.iN.n(series) && series.getDataAdapter() != null;
    }
}
