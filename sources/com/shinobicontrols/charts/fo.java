package com.shinobicontrols.charts;

class fo implements ar {
    private final ar jk;

    public fo() {
        this(ar.gs);
    }

    public fo(ar arVar) {
        this.jk = arVar;
    }

    public void l(Series<?> series) {
        this.jk.l(series);
        if (series.oo != null && !series.oo.isEmpty()) {
            af afVar = series.J;
            if (!afVar.getXAxisForSeries(series).isDataValid(series.oo.get(0).getX())) {
                throw new IllegalStateException(afVar.getContext().getString(R.string.CartesianInvalidDataX));
            }
        }
    }
}
