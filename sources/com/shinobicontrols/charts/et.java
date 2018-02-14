package com.shinobicontrols.charts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

class et {
    private final b pF = new b();
    private final b pG = new b();
    private final a pH = new a();
    private final List<en> pI = new ArrayList();

    static class a extends HashMap<Axis<?, ?>, Set<CartesianSeries<?>>> {
        private static final long serialVersionUID = 1;

        a() {
        }
    }

    static class b extends LinkedHashMap<CartesianSeries<?>, Axis<?, ?>> {
        private static final long serialVersionUID = 1;

        b() {
        }
    }

    et() {
    }

    void u(Axis<?, ?> axis) {
        for (CartesianSeries cartesianSeries : this.pF.keySet()) {
            if (this.pF.get(cartesianSeries) == null) {
                a(cartesianSeries, (Axis) axis, (Axis) this.pG.get(cartesianSeries));
                cartesianSeries.aK();
            }
        }
    }

    void v(Axis<?, ?> axis) {
        for (CartesianSeries cartesianSeries : this.pG.keySet()) {
            if (this.pG.get(cartesianSeries) == null) {
                a(cartesianSeries, (Axis) this.pF.get(cartesianSeries), (Axis) axis);
                cartesianSeries.aK();
            }
        }
    }

    void a(CartesianSeries<?> cartesianSeries, Axis<?, ?> axis, Axis<?, ?> axis2) {
        if (cartesianSeries == null) {
            throw new IllegalStateException();
        } else if (axis == null || axis2 == null || axis != axis2) {
            a((CartesianSeries) cartesianSeries, (Axis) axis, this.pF);
            a((CartesianSeries) cartesianSeries, (Axis) axis2, this.pG);
            b(cartesianSeries, axis, axis2);
        } else {
            throw new IllegalStateException();
        }
    }

    private void b(CartesianSeries<?> cartesianSeries, Axis<?, ?> axis, Axis<?, ?> axis2) {
        if (axis != null && axis2 != null) {
            Object obj;
            for (en enVar : this.pI) {
                if (enVar.b(cartesianSeries, axis, axis2)) {
                    enVar.g(cartesianSeries);
                    obj = 1;
                    break;
                }
            }
            obj = null;
            if (obj == null) {
                this.pI.add(new en(axis, axis2, cartesianSeries));
            }
        }
    }

    private void a(CartesianSeries<?> cartesianSeries, Axis<?, ?> axis, b bVar) {
        bVar.put(cartesianSeries, axis);
        if (axis != null) {
            Set set = (Set) this.pH.get(axis);
            if (set == null) {
                set = new HashSet();
                this.pH.put(axis, set);
            }
            set.add(cartesianSeries);
            axis.b((Series) cartesianSeries);
            cartesianSeries.o(axis);
        }
    }

    void x(Series<?> series) {
        Axis xAxisForSeries = getXAxisForSeries(series);
        if (xAxisForSeries != null) {
            ((Set) this.pH.get(xAxisForSeries)).remove(series);
            xAxisForSeries.c((Series) series);
            series.p(xAxisForSeries);
        }
        xAxisForSeries = getYAxisForSeries(series);
        if (xAxisForSeries != null) {
            ((Set) this.pH.get(xAxisForSeries)).remove(series);
            xAxisForSeries.c((Series) series);
            series.p(xAxisForSeries);
        }
        this.pF.remove(series);
        this.pG.remove(series);
        D(series);
    }

    private void D(Series<?> series) {
        List<en> arrayList = new ArrayList();
        for (en enVar : this.pI) {
            if (enVar.w(series)) {
                enVar.x(series);
                if (enVar.ey() == 0) {
                    arrayList.add(enVar);
                }
            }
        }
        for (en enVar2 : arrayList) {
            this.pI.remove(enVar2);
        }
    }

    void removeXAxis(Axis<?, ?> axis) {
        a(axis, this.pF);
    }

    void removeYAxis(Axis<?, ?> axis) {
        a(axis, this.pG);
    }

    private void a(Axis<?, ?> axis, b bVar) {
        Set<Series> set = (Set) this.pH.get(axis);
        if (set != null) {
            for (Series series : set) {
                bVar.put(series, null);
                axis.c(series);
                series.p(axis);
            }
            this.pH.remove(axis);
        }
        this.pI.removeAll(w(axis));
    }

    private List<en> w(Axis<?, ?> axis) {
        List<en> arrayList = new ArrayList();
        for (en enVar : this.pI) {
            if (enVar.q(axis)) {
                arrayList.add(enVar);
            }
        }
        return arrayList;
    }

    Axis<?, ?> getXAxisForSeries(Series<?> series) {
        return (Axis) this.pF.get(series);
    }

    Axis<?, ?> getYAxisForSeries(Series<?> series) {
        return (Axis) this.pG.get(series);
    }

    Set<CartesianSeries<?>> x(Axis<?, ?> axis) {
        return (Set) this.pH.get(axis);
    }

    List<en> aR() {
        return this.pI;
    }
}
