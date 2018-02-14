package com.shinobicontrols.charts;

class ad implements eq {
    ad() {
    }

    public void update(Series<?> series) {
        CartesianSeries cartesianSeries = (CartesianSeries) series;
        NumberRange aM = cartesianSeries.aM();
        aM.reset();
        aM.j(cartesianSeries.db.jc);
        aM = cartesianSeries.aN();
        aM.reset();
        aM.j(cartesianSeries.db.jd);
    }
}
