package com.shinobicontrols.charts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class eo {
    Map<Class<? extends Series>, a> pA = new HashMap();

    static class a extends ArrayList<Series<?>> {
        private static final long serialVersionUID = -6971678076805971571L;

        a() {
        }

        void a(Series<?> series, Series<?> series2) {
            int indexOf = indexOf(series);
            if (indexOf != -1) {
                remove(series);
                add(indexOf, series2);
            }
        }

        void C(Series<?> series) {
            a(series, null);
        }

        boolean ez() {
            return indexOf(null) != -1;
        }
    }

    eo() {
    }

    int z(Series<?> series) {
        a aVar;
        Class cls = series.getClass();
        if (this.pA.containsKey(cls)) {
            aVar = (a) this.pA.get(cls);
            if (aVar.ez()) {
                b(aVar, series);
            } else {
                a(aVar, series);
            }
        } else {
            aVar = new a();
            this.pA.put(cls, aVar);
            a(aVar, series);
        }
        return c(aVar, series);
    }

    private void a(a aVar, Series<?> series) {
        aVar.add(series);
    }

    private void b(a aVar, Series<?> series) {
        aVar.a(null, series);
    }

    void A(Series<?> series) {
        Class cls = series.getClass();
        if (this.pA.containsKey(cls)) {
            a aVar = (a) this.pA.get(cls);
            if (aVar.contains(series)) {
                aVar.C(series);
            }
        }
    }

    int B(Series<?> series) {
        Class cls = series.getClass();
        if (this.pA.containsKey(cls)) {
            return c((a) this.pA.get(cls), series);
        }
        return -1;
    }

    private int c(a aVar, Series<?> series) {
        return aVar.indexOf(series);
    }
}
