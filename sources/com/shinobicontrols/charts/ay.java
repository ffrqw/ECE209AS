package com.shinobicontrols.charts;

abstract class ay {
    final Series<?> cZ;

    private static class a extends ay {
        public void cm() {
        }

        public a(Series<?> series) {
            super(series);
        }
    }

    private static class b extends ay {
        public void cm() {
            this.cZ.J.em.invalidate();
        }

        public b(Series<?> series) {
            super(series);
        }
    }

    public abstract void cm();

    public static ay m(Series<?> series) {
        return series instanceof PieDonutSeries ? new b(series) : new a(series);
    }

    public ay(Series<?> series) {
        this.cZ = series;
    }
}
