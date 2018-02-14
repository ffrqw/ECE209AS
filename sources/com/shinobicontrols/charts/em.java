package com.shinobicontrols.charts;

import java.util.List;

abstract class em {
    private final boolean pu;
    protected final CartesianSeries<?> pv;

    abstract void ah();

    em(boolean z, CartesianSeries<?> cartesianSeries) {
        this.pu = z;
        this.pv = cartesianSeries;
    }

    void e(List<CartesianSeries<?>> list) {
        for (CartesianSeries cartesianSeries : list) {
            if (cartesianSeries.eh()) {
                cartesianSeries.dU.ah();
            }
        }
    }

    void a(List<CartesianSeries<?>> list, a aVar, NumberRange numberRange, br brVar) {
    }

    void b(List<CartesianSeries<?>> list, a aVar, NumberRange numberRange, br brVar) {
    }

    NumberRange a(Axis<?, ?> axis, List<CartesianSeries<?>> list) {
        NumberRange numberRange = new NumberRange();
        if (!this.pu || axis.a(this.pv.dR) || list.size() == 1) {
            for (CartesianSeries cartesianSeries : list) {
                if (!cartesianSeries.oC) {
                    numberRange.j(cartesianSeries.g(axis));
                }
            }
        } else {
            a(list, br.ja, numberRange);
            if (((CartesianSeries) list.get(0)).aO()) {
                a(list, br.jb, numberRange);
            }
        }
        return numberRange;
    }

    private void a(List<CartesianSeries<?>> list, br brVar, NumberRange numberRange) {
        fa faVar = new fa(list, false, brVar);
        while (faVar.hasNext()) {
            a(list, faVar.eG(), numberRange, brVar);
        }
    }

    NumberRange b(Axis<?, ?> axis, List<CartesianSeries<?>> list) {
        NumberRange numberRange = new NumberRange();
        if (!this.pu || axis.a(this.pv.dR) || list.size() == 1) {
            for (CartesianSeries h : list) {
                numberRange.j(h.h(axis));
            }
        } else {
            b(list, br.ja, numberRange);
            if (((CartesianSeries) list.get(0)).aO()) {
                b(list, br.jb, numberRange);
            }
        }
        return numberRange;
    }

    private void b(List<CartesianSeries<?>> list, br brVar, NumberRange numberRange) {
        fa faVar = new fa(list, true, brVar);
        while (faVar.hasNext()) {
            b(list, faVar.eG(), numberRange, brVar);
        }
    }
}
