package com.shinobicontrols.charts;

import android.graphics.PointF;
import com.shinobicontrols.charts.ShinobiChart.OnGestureListener;

class eu implements OnGestureListener {
    private final af J;
    private final ej om;

    eu(af afVar) {
        this.J = afVar;
        this.om = new ej(afVar);
    }

    public void onDoubleTapDown(ShinobiChart sender, PointF position) {
    }

    public void onDoubleTapUp(ShinobiChart sender, PointF position) {
    }

    public void onLongTouchDown(ShinobiChart sender, PointF position) {
        n(position);
    }

    public void onLongTouchUp(ShinobiChart sender, PointF position) {
    }

    public void onSingleTouchDown(ShinobiChart sender, PointF position) {
    }

    public void onSingleTouchUp(ShinobiChart sender, PointF position) {
        p(position);
    }

    public void onSwipe(ShinobiChart sender, PointF startPosition, PointF endPosition) {
        o(endPosition);
    }

    public void onSwipeEnd(ShinobiChart sender, PointF position, boolean flinging, PointF velocity) {
    }

    public void onSecondTouchDown(ShinobiChart sender, PointF position, PointF position2) {
    }

    public void onSecondTouchUp(ShinobiChart sender, PointF position, PointF position2) {
    }

    public void onPinch(ShinobiChart sender, PointF startFocus, PointF endFocus, PointF scaleFactor) {
    }

    public void onPinchEnd(ShinobiChart sender, PointF focus, boolean flinging, PointF scaleFactor) {
    }

    private void n(PointF pointF) {
        Crosshair crosshair = this.J.ev;
        if (crosshair != null && this.J.bu()) {
            if (this.J.eO.fA >= 60 || !crosshair.isShown()) {
                a a = this.om.a(pointF, b.CROSSHAIR_ENABLED);
                if (a.b(a)) {
                    c(a);
                }
            } else if (crosshair.isShown()) {
                crosshair.bO();
            }
        }
    }

    private void o(PointF pointF) {
        Crosshair crosshair = this.J.ev;
        if (crosshair != null) {
            a a;
            switch (crosshair.fJ) {
                case SINGLE_SERIES:
                    a = this.om.a(crosshair.getTrackedSeries(), pointF, true, b.CROSSHAIR_ENABLED);
                    break;
                case FLOATING:
                    a = this.om.a(pointF, b.CROSSHAIR_ENABLED);
                    break;
                default:
                    a = null;
                    break;
            }
            if (a.b(a)) {
                c(a);
            }
        }
    }

    private void c(a aVar) {
        CartesianSeries cartesianSeries = (CartesianSeries) aVar.em();
        InternalDataPoint el = aVar.el();
        DataPoint a = ap.a(el, cartesianSeries);
        DataPoint b = ap.b(el, cartesianSeries);
        du en = aVar.en();
        this.J.ev.c(cartesianSeries, a, b, en != null ? ap.a(en, el, cartesianSeries) : null);
    }

    private void p(PointF pointF) {
        if (this.J.eO.fA >= 60) {
            Crosshair crosshair = this.J.ev;
            if (crosshair != null) {
                crosshair.bO();
            }
        }
    }
}
