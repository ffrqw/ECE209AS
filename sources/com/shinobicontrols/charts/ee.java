package com.shinobicontrols.charts;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

abstract class ee {
    private static ee og = new ee() {
        final List<Series<?>> s(Series<?> series) {
            List<Series<?>> arrayList = new ArrayList();
            arrayList.add(series);
            return arrayList;
        }
    };
    private static ee oh = new ee() {
        final List<Series<?>> s(Series<?> series) {
            List<Series<?>> arrayList = new ArrayList();
            for (Series add : series.oy.y(series)) {
                arrayList.add(add);
            }
            return arrayList;
        }
    };

    abstract List<Series<?>> s(Series<?> series);

    static List<Series<?>> o(Series<?> series) {
        if (series.oy == null || !q(series)) {
            return og.s(series);
        }
        return oh.s(series);
    }

    static List<Series<?>> p(Series<?> series) {
        return n(o(series));
    }

    private ee() {
    }

    private static boolean q(Series<?> series) {
        if (!(series instanceof CartesianSeries) || ((CartesianSeries) series).dL == null) {
            return false;
        }
        return true;
    }

    private static List<Series<?>> n(List<Series<?>> list) {
        Iterator it = list.iterator();
        while (it.hasNext()) {
            if (r((Series) it.next())) {
                it.remove();
            }
        }
        return list;
    }

    private static boolean r(Series<?> series) {
        return series.J == null || series.oC || series.isAnimating();
    }
}
