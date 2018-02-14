package com.shinobicontrols.charts;

import android.view.ViewConfiguration;

class ai {
    private final af J;
    int fA;
    final int fB;
    private final ViewConfiguration fv;
    final int fw = this.fv.getScaledTouchSlop();
    final int fx;
    final int fy;
    final int fz;

    ai(af afVar) {
        this.J = afVar;
        this.fv = ViewConfiguration.get(afVar.getContext());
        float f = afVar.getResources().getDisplayMetrics().density;
        this.fx = ca.c(f, 5.0f);
        this.fy = this.fv.getScaledMinimumFlingVelocity();
        this.fz = ViewConfiguration.getDoubleTapTimeout();
        this.fA = ViewConfiguration.getLongPressTimeout();
        this.fB = ca.c(f, 20.0f);
    }

    int bL() {
        return this.J.bu() ? this.fw : this.fx;
    }
}
