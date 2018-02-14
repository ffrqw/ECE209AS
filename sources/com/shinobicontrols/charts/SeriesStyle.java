package com.shinobicontrols.charts;

public abstract class SeriesStyle {
    final fh<Boolean> pD = new fh(Boolean.valueOf(false));
    private final bg u = new bg();

    public enum FillStyle {
        NONE,
        FLAT,
        GRADIENT
    }

    SeriesStyle() {
    }

    void a(SeriesStyle seriesStyle) {
        if (seriesStyle != null) {
            this.pD.c(Boolean.valueOf(seriesStyle.eC()));
        }
    }

    boolean eC() {
        return ((Boolean) this.pD.sU).booleanValue();
    }

    void k(boolean z) {
        synchronized (ah.lock) {
            this.pD.b(Boolean.valueOf(z));
            ag();
        }
    }

    final void ag() {
        this.u.a(new ea());
    }

    bh a(a aVar) {
        return this.u.a(ea.A, (a) aVar);
    }
}
