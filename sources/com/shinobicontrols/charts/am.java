package com.shinobicontrols.charts;

interface am {
    public static final am gp = new am() {
        public final boolean a(Series<?> series) {
            return false;
        }

        public final boolean a(Data<?, ?> data, Series<?> series) {
            return false;
        }
    };

    boolean a(Data<?, ?> data, Series<?> series);

    boolean a(Series<?> series);
}
