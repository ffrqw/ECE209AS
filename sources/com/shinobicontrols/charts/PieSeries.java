package com.shinobicontrols.charts;

public class PieSeries extends PieDonutSeries<PieSeriesStyle> {
    /* synthetic */ SeriesStyle an() {
        return dt();
    }

    /* synthetic */ SeriesStyle b(fb fbVar, int i, boolean z) {
        return i(fbVar, i, z);
    }

    public PieSeries() {
        this(new dk());
    }

    PieSeries(ao dataLoadHelperFactory) {
        super(dataLoadHelperFactory);
        this.ot = new dm(this);
        setStyle(dt());
        setSelectedStyle(dt());
    }

    PieSeriesStyle dt() {
        return new PieSeriesStyle();
    }

    PieSeriesStyle i(fb fbVar, int i, boolean z) {
        return fbVar.g(i, z);
    }

    float h(float f) {
        return 0.0f;
    }
}
