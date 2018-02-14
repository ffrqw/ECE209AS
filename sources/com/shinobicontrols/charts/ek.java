package com.shinobicontrols.charts;

import java.util.ArrayList;
import java.util.List;

class ek {
    private final af J;
    private final b ph = new b();
    private final c pi = new c();
    private final List<Series<?>> pj;
    private final List<Series<?>> pk;
    private final List<Series<?>> pl;
    private final ep pm;

    private static abstract class b implements a {
        final af J;

        public b(af afVar) {
            this.J = afVar;
        }

        public void onAnimationStart() {
        }

        public void b(Animation animation) {
            synchronized (ah.lock) {
                this.J.em.bC();
            }
        }

        public void onAnimationEnd() {
        }

        public void c() {
        }
    }

    private static class a extends b {
        private final List<Series<?>> pk;
        private final ep pm;

        public a(af afVar, List<Series<?>> list, ep epVar) {
            super(afVar);
            this.pk = list;
            this.pm = epVar;
        }

        public void onAnimationStart() {
            for (Series series : this.pm.eB()) {
                series.oE.cm();
            }
        }

        public void onAnimationEnd() {
            synchronized (ah.lock) {
                for (Series series : this.pm.eB()) {
                    series.oz = null;
                    series.j(true);
                }
            }
            this.pm.eA();
            ev();
        }

        private void ev() {
            b bVar = new b();
            Animation cVar = new c();
            List<Series> eB = this.pm.eB();
            for (Series series : eB) {
                series.oA.a(false);
                series.oz = series.oA;
                synchronized (ah.lock) {
                    series.j(false);
                }
                cVar.c(series.oz);
            }
            bVar.a(cVar);
            bVar.a(new c(this.J, this.pk, eB));
            bVar.start();
        }
    }

    private static class c extends b {
        private final List<Series<?>> pk;
        private final List<Series<?>> pn;

        public c(af afVar, List<Series<?>> list, List<Series<?>> list2) {
            super(afVar);
            this.pk = list;
            this.pn = list2;
        }

        public void onAnimationEnd() {
            synchronized (ah.lock) {
                for (Series series : this.pn) {
                    series.oz = null;
                    series.ot.av();
                    series.oE.cm();
                }
            }
            for (Series series2 : this.pk) {
                this.J.onSeriesAnimationFinished(series2);
            }
        }
    }

    static ek a(Series<?> series, List<Series<?>> list, af afVar, a aVar) {
        List o = ee.o(series);
        List a = a(o, list);
        List p = ee.p(series);
        return new ek(o, a, p, ep.a(a, p, afVar, aVar), afVar);
    }

    private static List<Series<?>> a(List<Series<?>> list, List<Series<?>> list2) {
        List<Series<?>> arrayList = new ArrayList(list2);
        arrayList.retainAll(list);
        return arrayList;
    }

    private ek(List<Series<?>> list, List<Series<?>> list2, List<Series<?>> list3, ep epVar, af afVar) {
        this.pj = list;
        this.pk = list2;
        this.pl = list3;
        this.pm = epVar;
        this.J = afVar;
    }

    void es() {
        for (Series series : this.pl) {
            series.oB.a(true);
            series.oz = series.oB;
            series.ot.av();
            this.pi.c(series.oz);
        }
        this.ph.a(this.pi);
        this.ph.a(new a(this.J, this.pk, this.pm));
    }

    void start() {
        this.ph.start();
    }

    List<Series<?>> et() {
        return this.pk;
    }

    boolean eu() {
        for (Series isAnimating : this.pj) {
            if (isAnimating.isAnimating()) {
                return true;
            }
        }
        return false;
    }
}
