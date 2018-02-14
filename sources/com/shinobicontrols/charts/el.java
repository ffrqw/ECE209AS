package com.shinobicontrols.charts;

import java.util.ArrayList;
import java.util.List;

class el {
    private final af J;

    enum a {
        SHOW,
        HIDE,
        REMOVE,
        ADD,
        STACK_ID
    }

    el(af afVar) {
        this.J = afVar;
    }

    void u(Series<?> series) {
        List arrayList = new ArrayList();
        arrayList.add(series);
        o(arrayList);
    }

    void o(List<Series<?>> list) {
        a((List) list, a.HIDE);
    }

    void v(Series<?> series) {
        List arrayList = new ArrayList();
        arrayList.add(series);
        p(arrayList);
    }

    void p(List<Series<?>> list) {
        a((List) list, a.SHOW);
    }

    void a(Series<?> series, af afVar) {
        List arrayList = new ArrayList();
        arrayList.add(series);
        a(arrayList, a.ADD);
    }

    void b(Series<?> series, af afVar) {
        List arrayList = new ArrayList();
        arrayList.add(series);
        a(arrayList, a.REMOVE);
    }

    private void a(List<Series<?>> list, a aVar) {
        List arrayList = new ArrayList();
        List<ek> arrayList2 = new ArrayList();
        for (Series series : list) {
            ek a;
            if (!(series.isAnimating() || arrayList.contains(series))) {
                a = ek.a(series, list, this.J, aVar);
                if (!a.eu()) {
                    a.es();
                    arrayList2.add(a);
                }
                arrayList.addAll(a.et());
            }
        }
        for (ek a2 : arrayList2) {
            a2.start();
        }
    }
}
