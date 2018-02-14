package com.shinobicontrols.charts;

class bi implements bm {
    private final bm iN;

    public bi() {
        this(bm.iO);
    }

    public bi(bm bmVar) {
        this.iN = bmVar;
    }

    public boolean n(Series<?> series) {
        return (!this.iN.n(series) || series.getXAxis() == null || series.getYAxis() == null) ? false : true;
    }
}
