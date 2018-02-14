package com.shinobicontrols.charts;

import android.annotation.SuppressLint;
import com.shinobicontrols.charts.Series.Orientation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressLint({"UseSparseArrays"})
class en {
    private final Axis<?, ?> o;
    private final Axis<?, ?> p;
    private final List<CartesianSeries<?>> pw = new ArrayList();
    private final List<CartesianSeries<?>> px = new ArrayList();
    private final Map<Integer, List<CartesianSeries<?>>> py = new HashMap();
    private final Class pz;

    en(Axis<?, ?> axis, Axis<?, ?> axis2, CartesianSeries<?> cartesianSeries) {
        if (cartesianSeries == null) {
            throw new NullPointerException();
        }
        this.o = axis;
        this.p = axis2;
        g(cartesianSeries);
        this.pz = cartesianSeries.getClass();
    }

    boolean q(Axis<?, ?> axis) {
        return axis == this.o || axis == this.p;
    }

    void ah() {
        for (CartesianSeries cartesianSeries : this.px) {
            cartesianSeries.dU.ah();
        }
        for (List list : this.py.values()) {
            ((CartesianSeries) list.get(0)).dU.e(list);
        }
    }

    NumberRange r(Axis axis) {
        NumberRange numberRange = new NumberRange();
        for (CartesianSeries cartesianSeries : this.px) {
            if (!cartesianSeries.oC) {
                numberRange.j(cartesianSeries.g(axis));
            }
        }
        for (List list : this.py.values()) {
            numberRange.j(((CartesianSeries) list.get(0)).dU.a(axis, list));
        }
        return numberRange;
    }

    NumberRange s(Axis axis) {
        NumberRange numberRange = new NumberRange();
        for (CartesianSeries h : this.px) {
            numberRange.j(h.h(axis));
        }
        for (List list : this.py.values()) {
            numberRange.j(((CartesianSeries) list.get(0)).dU.b(axis, list));
        }
        return numberRange;
    }

    void g(CartesianSeries<?> cartesianSeries) {
        if (!w(cartesianSeries)) {
            this.pw.add(cartesianSeries);
        }
        cartesianSeries.a(this);
        ew();
    }

    void ew() {
        this.px.clear();
        this.py.clear();
        int i = 0;
        for (CartesianSeries cartesianSeries : this.pw) {
            if (cartesianSeries.dL == null) {
                cartesianSeries.a(i, 0, null);
                this.px.add(cartesianSeries);
                i++;
            } else {
                List list;
                int i2;
                if (this.py.containsKey(cartesianSeries.dL)) {
                    List list2 = (List) this.py.get(cartesianSeries.dL);
                    cartesianSeries.a(((CartesianSeries) list2.get(0)).aI(), this.py.size(), (CartesianSeries) list2.get(list2.size() - 1));
                    list = list2;
                    i2 = i;
                } else {
                    cartesianSeries.a(i, 0, null);
                    ArrayList arrayList = new ArrayList();
                    this.py.put(cartesianSeries.dL, arrayList);
                    Object obj = arrayList;
                    i2 = i + 1;
                }
                list.add(cartesianSeries);
                i = i2;
            }
        }
    }

    int ex() {
        return this.px.size() + this.py.size();
    }

    void t(Axis<?, ?> axis) {
        for (Series f : this.pw) {
            f.f(axis);
        }
    }

    int ey() {
        return this.pw.size();
    }

    boolean w(Series<?> series) {
        return this.pw.contains(series);
    }

    void x(Series<?> series) {
        if (w(series)) {
            this.pw.remove(series);
            series.a(null);
            ew();
        }
    }

    boolean b(Series<?> series, Axis<?, ?> axis, Axis<?, ?> axis2) {
        return this.o == axis && this.p == axis2 && series.getClass().equals(this.pz);
    }

    double h(CartesianSeries<?> cartesianSeries) {
        if (cartesianSeries.aL()) {
            return h(cartesianSeries.aJ());
        }
        Axis i;
        if (cartesianSeries.dK != null) {
            i = i(cartesianSeries);
            if (i.isDataValid(cartesianSeries.dK)) {
                return i.translatePoint(cartesianSeries.dK);
            }
            throw new IllegalStateException(cartesianSeries.J != null ? cartesianSeries.J.getContext().getString(R.string.CartesianBaselineWrongType) : "Current baseline for series is invalid for the assigned x/y axes.");
        } else if ((cartesianSeries instanceof BarColumnSeries) || cartesianSeries.dL != null) {
            i = i(cartesianSeries);
            return i.translatePoint(i.getDefaultBaseline());
        } else {
            i = i(cartesianSeries);
            return i.al != null ? i.al.nv : i.n();
        }
    }

    private Axis<?, ?> i(CartesianSeries<?> cartesianSeries) {
        return cartesianSeries.dR == Orientation.HORIZONTAL ? this.p : this.o;
    }

    List<? extends Series<?>> y(Series<?> series) {
        for (List<? extends Series<?>> list : this.py.values()) {
            if (list.contains(series)) {
                return list;
            }
        }
        return Collections.emptyList();
    }

    boolean j(CartesianSeries<?> cartesianSeries) {
        List y = y(cartesianSeries);
        if (y.isEmpty() || y.get(0) != cartesianSeries) {
            return false;
        }
        return true;
    }
}
