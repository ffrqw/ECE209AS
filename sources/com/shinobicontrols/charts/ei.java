package com.shinobicontrols.charts;

import android.graphics.PointF;
import com.shinobicontrols.charts.Series.SelectionMode;
import com.shinobicontrols.charts.ShinobiChart.OnGestureListener;

class ei implements OnGestureListener {
    private final af J;
    boolean ol;
    private final ej om;

    ei(af afVar) {
        this.J = afVar;
        this.om = new ej(afVar);
    }

    public void onDoubleTapDown(ShinobiChart sender, PointF position) {
    }

    public void onDoubleTapUp(ShinobiChart sender, PointF position) {
    }

    public void onLongTouchDown(ShinobiChart sender, PointF position) {
    }

    public void onLongTouchUp(ShinobiChart sender, PointF position) {
    }

    public void onSingleTouchDown(ShinobiChart sender, PointF position) {
    }

    public void onSingleTouchUp(ShinobiChart sender, PointF position) {
        j(position);
    }

    public void onSwipe(ShinobiChart sender, PointF startPosition, PointF endPosition) {
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

    private void j(PointF pointF) {
        if (this.J.bn()) {
            l(pointF);
        } else {
            k(pointF);
        }
    }

    private void k(PointF pointF) {
        boolean z = true;
        int i = 0;
        a a = this.om.a(pointF, b.SELECTION_MODE_NOT_NONE);
        if (a.b(a)) {
            CartesianSeries cartesianSeries = (CartesianSeries) a.em();
            InternalDataPoint el = a.el();
            switch (cartesianSeries.ox) {
                case POINT_SINGLE:
                case POINT_MULTIPLE:
                    synchronized (ah.lock) {
                        if (cartesianSeries.ox == SelectionMode.POINT_MULTIPLE || el.iU) {
                            if (el.iU) {
                                z = false;
                            }
                            cartesianSeries.a(el, z);
                        } else {
                            int length = cartesianSeries.db.je.length;
                            while (i < length) {
                                InternalDataPoint internalDataPoint = cartesianSeries.db.je[i];
                                if (internalDataPoint != el) {
                                    cartesianSeries.a(internalDataPoint, false);
                                }
                                i++;
                            }
                            cartesianSeries.a(el, true);
                        }
                    }
                    break;
                case SERIES:
                    synchronized (ah.lock) {
                        if (!this.ol) {
                            if (cartesianSeries.dM) {
                                z = false;
                            }
                            cartesianSeries.setSelected(z);
                        } else if (cartesianSeries.dM) {
                            cartesianSeries.setSelected(false);
                        } else {
                            for (CartesianSeries cartesianSeries2 : this.J.bj()) {
                                if (cartesianSeries2 != cartesianSeries) {
                                    cartesianSeries2.setSelected(false);
                                }
                            }
                            cartesianSeries.setSelected(true);
                        }
                    }
                    break;
            }
            if (cartesianSeries.ox != SelectionMode.NONE) {
                this.J.em.av();
                this.J.em.invalidate();
                this.J.redrawChart();
            }
        }
    }

    private void l(PointF pointF) {
        a m = this.om.m(pointF);
        if (a.b(m)) {
            PieDonutSeries pieDonutSeries = (PieDonutSeries) m.em();
            PieDonutSlice pieDonutSlice = (PieDonutSlice) m.el();
            switch (pieDonutSeries.ox) {
                case POINT_SINGLE:
                case POINT_MULTIPLE:
                    a(!pieDonutSlice.iU, pieDonutSeries, pieDonutSlice, pieDonutSeries.ox, true);
                    return;
                default:
                    return;
            }
        }
    }

    void a(boolean z, PieDonutSeries<?> pieDonutSeries, PieDonutSlice pieDonutSlice, SelectionMode selectionMode, boolean z2) {
        int i = 0;
        synchronized (ah.lock) {
            int a;
            if (selectionMode == SelectionMode.POINT_SINGLE && !pieDonutSlice.iU) {
                int length = pieDonutSeries.db.je.length;
                int i2 = 0;
                while (i2 < length) {
                    PieDonutSlice pieDonutSlice2 = (PieDonutSlice) pieDonutSeries.db.je[i2];
                    if (pieDonutSlice2 != pieDonutSlice) {
                        a = pieDonutSeries.a(pieDonutSlice2, false) | i;
                    } else {
                        a = i;
                    }
                    i2++;
                    i = a;
                }
            }
            a = pieDonutSeries.a(pieDonutSlice, z) | i;
        }
        if (a != 0) {
            pieDonutSeries.a(pieDonutSlice, z2);
        }
    }
}
