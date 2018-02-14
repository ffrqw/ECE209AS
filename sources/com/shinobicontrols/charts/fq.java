package com.shinobicontrols.charts;

class fq implements ar {
    private final ar jk;

    public fq() {
        this(ar.gs);
    }

    public fq(ar arVar) {
        this.jk = arVar;
    }

    public void l(Series<?> series) {
        this.jk.l(series);
        if (series.oo != null && !series.oo.isEmpty()) {
            af afVar = series.J;
            if (!afVar.getYAxisForSeries(series).isDataValid(series.oo.get(0).getY())) {
                throw new IllegalStateException(afVar.getContext().getString(R.string.CartesianInvalidDataY));
            }
        }
    }
}
