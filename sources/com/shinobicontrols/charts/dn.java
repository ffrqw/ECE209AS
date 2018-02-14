package com.shinobicontrols.charts;

class dn implements eq {
    dn() {
    }

    public void update(Series<?> series) {
        PieDonutSeries pieDonutSeries = (PieDonutSeries) series;
        NumberRange aN = pieDonutSeries.aN();
        aN.reset();
        aN.j(pieDonutSeries.db.jd);
    }
}
