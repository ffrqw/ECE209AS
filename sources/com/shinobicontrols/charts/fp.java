package com.shinobicontrols.charts;

class fp implements ar {
    fp() {
    }

    public void l(Series<?> series) {
        if (series.oo != null && !series.oo.isEmpty() && !(series.oo.get(0).getY() instanceof Number)) {
            throw new IllegalStateException(series.J.getContext().getString(R.string.PieDonutSeriesInvalidData));
        }
    }
}
