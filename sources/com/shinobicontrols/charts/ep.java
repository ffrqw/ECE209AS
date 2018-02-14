package com.shinobicontrols.charts;

import java.util.List;

abstract class ep {
    List<Series<?>> pk;
    List<Series<?>> pl;

    private static class a extends ep {
        private final af J;

        public a(List<Series<?>> list, List<Series<?>> list2, af afVar) {
            super(list, list2);
            this.J = afVar;
        }

        void eA() {
            synchronized (ah.lock) {
                for (Series series : this.pk) {
                    if (!series.oC) {
                        this.pl.add(series);
                    }
                    this.J.f(series);
                }
            }
        }
    }

    private static class b extends ep {
        public b(List<Series<?>> list, List<Series<?>> list2) {
            super(list, list2);
        }

        void eA() {
            this.pl.removeAll(this.pk);
        }
    }

    private static class c extends ep {
        private final af J;

        public c(List<Series<?>> list, List<Series<?>> list2, af afVar) {
            super(list, list2);
            this.J = afVar;
        }

        void eA() {
            this.pl.removeAll(this.pk);
            synchronized (ah.lock) {
                for (Series g : this.pk) {
                    this.J.g(g);
                }
            }
        }
    }

    private static class d extends ep {
        public d(List<Series<?>> list, List<Series<?>> list2) {
            super(list, list2);
        }

        void eA() {
            this.pl.addAll(this.pk);
        }
    }

    abstract void eA();

    static ep a(List<Series<?>> list, List<Series<?>> list2, af afVar, a aVar) {
        switch (aVar) {
            case HIDE:
                return new b(list, list2);
            case SHOW:
                return new d(list, list2);
            case ADD:
                return new a(list, list2, afVar);
            case REMOVE:
                return new c(list, list2, afVar);
            default:
                return null;
        }
    }

    public ep(List<Series<?>> list, List<Series<?>> list2) {
        this.pk = list;
        this.pl = list2;
    }

    List<Series<?>> eB() {
        return this.pl;
    }
}
