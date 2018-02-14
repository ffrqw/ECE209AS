package com.shinobicontrols.charts;

public final class GridStripeStyle {
    final fh<Integer> iD = new fh(Integer.valueOf(-3355444));
    final fh<Integer> iE = new fh(Integer.valueOf(-7829368));
    final fh<Boolean> iF = new fh(Boolean.valueOf(false));

    final void a(GridStripeStyle gridStripeStyle) {
        if (gridStripeStyle != null) {
            this.iD.c(Integer.valueOf(gridStripeStyle.getStripeColor()));
            this.iE.c(Integer.valueOf(gridStripeStyle.getAlternateStripeColor()));
            this.iF.c(Boolean.valueOf(gridStripeStyle.areGridStripesShown()));
        }
    }

    public final int getStripeColor() {
        return ((Integer) this.iD.sU).intValue();
    }

    public final void setStripeColor(int stripeColor) {
        this.iD.b(Integer.valueOf(stripeColor));
    }

    public final int getAlternateStripeColor() {
        return ((Integer) this.iE.sU).intValue();
    }

    public final void setAlternateStripeColor(int alternateStripeColor) {
        this.iE.b(Integer.valueOf(alternateStripeColor));
    }

    public final boolean areGridStripesShown() {
        return ((Boolean) this.iF.sU).booleanValue();
    }

    public final void setGridStripesShown(boolean showGridStripes) {
        this.iF.b(Boolean.valueOf(showGridStripes));
    }
}
