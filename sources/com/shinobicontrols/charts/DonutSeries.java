package com.shinobicontrols.charts;

public class DonutSeries extends PieDonutSeries<DonutSeriesStyle> {
    /* synthetic */ SeriesStyle an() {
        return cl();
    }

    /* synthetic */ SeriesStyle b(fb fbVar, int i, boolean z) {
        return f(fbVar, i, z);
    }

    public DonutSeries() {
        this(new dk());
    }

    DonutSeries(ao dataLoadHelperFactory) {
        super(dataLoadHelperFactory);
        this.ot = new dm(this);
        setStyle(cl());
        setSelectedStyle(cl());
    }

    DonutSeriesStyle cl() {
        return new DonutSeriesStyle();
    }

    DonutSeriesStyle f(fb fbVar, int i, boolean z) {
        return fbVar.h(i, z);
    }

    public float getInnerRadius() {
        return super.getInnerRadius();
    }

    public void setInnerRadius(float innerRadius) {
        synchronized (ah.lock) {
            this.lZ.b(Float.valueOf(innerRadius));
            ac();
        }
    }

    float h(float f) {
        return f;
    }
}
