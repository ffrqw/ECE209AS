package com.shinobicontrols.charts;

class bx implements ar {
    private final ar jk;

    public bx() {
        this(ar.gs);
    }

    public bx(ar arVar) {
        this.jk = arVar;
    }

    public void l(Series<?> series) {
        this.jk.l(series);
        if (series.oo != null && !series.oo.isEmpty() && !(series.oo.get(0) instanceof MultiValueData)) {
            throw new IllegalStateException(series.J.getContext().getString(R.string.CartesianNeedMultiValueData));
        }
    }
}
