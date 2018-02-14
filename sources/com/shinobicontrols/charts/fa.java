package com.shinobicontrols.charts;

import com.shinobicontrols.charts.Series.Orientation;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

class fa implements Iterator<a> {
    private final Orientation dR;
    private final List<CartesianSeries<?>> pU;
    private final a pV;
    private final boolean pW;
    private final br pX;

    static class a {
        double pY;
        private final Map<CartesianSeries<?>, b> pZ;

        private a(List<CartesianSeries<?>> list, br brVar) {
            this.pY = Double.NEGATIVE_INFINITY;
            this.pZ = new HashMap();
            for (CartesianSeries cartesianSeries : list) {
                this.pZ.put(cartesianSeries, new b(cartesianSeries, brVar));
            }
        }

        public b k(CartesianSeries<?> cartesianSeries) {
            return (b) this.pZ.get(cartesianSeries);
        }
    }

    static class b {
        int index;
        private final br pX;
        final CartesianSeries<?> pv;

        private b(CartesianSeries<?> cartesianSeries, br brVar) {
            this.index = -1;
            this.pv = cartesianSeries;
            this.pX = brVar;
        }

        private boolean eH() {
            return this.index == -1;
        }

        private boolean hasNext() {
            return this.index != -2 && this.index + 1 < this.pX.e(this.pv).length;
        }

        boolean eI() {
            return (this.index == -1 || this.index == -2) ? false : true;
        }

        private void eJ() {
            this.index++;
            if (this.index >= this.pX.e(this.pv).length) {
                this.index = -2;
            }
        }
    }

    public /* synthetic */ Object next() {
        return eG();
    }

    public fa(List<CartesianSeries<?>> list, boolean z, br brVar) {
        if (list.size() <= 0) {
            throw new IllegalStateException("There must be at least one series in a stacking group");
        }
        this.pU = list;
        this.dR = ((CartesianSeries) list.get(0)).dR;
        this.pV = new a(list, brVar);
        this.pW = z;
        this.pX = brVar;
    }

    public boolean hasNext() {
        for (CartesianSeries cartesianSeries : this.pU) {
            if (E(cartesianSeries)) {
                b k = this.pV.k(cartesianSeries);
                if (k.hasNext()) {
                    return true;
                }
                if (k.eI()) {
                    InternalDataPoint internalDataPoint = this.pX.e(cartesianSeries)[k.index];
                    if ((this.dR == Orientation.HORIZONTAL ? internalDataPoint.x : internalDataPoint.y) > this.pV.pY) {
                        return true;
                    }
                } else {
                    continue;
                }
            }
        }
        return false;
    }

    public a eG() {
        for (CartesianSeries cartesianSeries : this.pU) {
            if (E(cartesianSeries)) {
                b k = this.pV.k(cartesianSeries);
                if (k.eH()) {
                    k.eJ();
                } else if (k.eI()) {
                    InternalDataPoint internalDataPoint = this.pX.e(cartesianSeries)[k.index];
                    if ((this.dR == Orientation.HORIZONTAL ? internalDataPoint.x : internalDataPoint.y) == this.pV.pY) {
                        k.eJ();
                    }
                }
            }
        }
        double d = Double.POSITIVE_INFINITY;
        for (CartesianSeries cartesianSeries2 : this.pU) {
            double d2;
            if (E(cartesianSeries2)) {
                b k2 = this.pV.k(cartesianSeries2);
                if (k2.eI()) {
                    InternalDataPoint internalDataPoint2 = this.pX.e(cartesianSeries2)[k2.index];
                    double d3 = this.dR == Orientation.HORIZONTAL ? internalDataPoint2.x : internalDataPoint2.y;
                    if (d3 <= this.pV.pY) {
                        throw new IllegalStateException(cartesianSeries2.J.getContext().getString(R.string.StackSeriesIteratorOrdinatesOutofOrder));
                    } else if (d3 < d) {
                        d2 = d3;
                        d = d2;
                    }
                }
            }
            d2 = d;
            d = d2;
        }
        this.pV.pY = d;
        return this.pV;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    private boolean E(Series<?> series) {
        if (!this.pW && series.oC) {
            return false;
        }
        return true;
    }
}
