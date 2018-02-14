package com.shinobicontrols.charts;

import com.shinobicontrols.charts.Title.CentersOn;

public final class MainTitleStyle extends TitleStyle {
    final fh<Boolean> lf = new fh(Boolean.valueOf(false));
    final fh<CentersOn> lg = new fh(CentersOn.PLOTTING_AREA);

    final void a(MainTitleStyle mainTitleStyle) {
        super.b(mainTitleStyle);
        this.lf.c(Boolean.valueOf(mainTitleStyle.getOverlapsChart()));
        this.lg.c(mainTitleStyle.getCentersOn());
    }

    public final CentersOn getCentersOn() {
        return (CentersOn) this.lg.sU;
    }

    public final void setCentersOn(CentersOn titleCentersOn) {
        this.lg.b(titleCentersOn);
    }

    public final boolean getOverlapsChart() {
        return ((Boolean) this.lf.sU).booleanValue();
    }

    public final void setOverlapsChart(boolean overlapChartTitle) {
        this.lf.b(Boolean.valueOf(overlapChartTitle));
    }
}
