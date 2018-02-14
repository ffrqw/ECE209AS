package com.shinobicontrols.charts;

import com.shinobicontrols.charts.Title.Orientation;

public final class AxisTitleStyle extends TitleStyle {
    final fh<Orientation> bV = new fh(Orientation.HORIZONTAL);

    public final Orientation getOrientation() {
        return (Orientation) this.bV.sU;
    }

    public final void setOrientation(Orientation orientation) {
        this.bV.b(orientation);
    }

    final void a(AxisTitleStyle axisTitleStyle) {
        super.b(axisTitleStyle);
        this.bV.c(axisTitleStyle.getOrientation());
    }
}
