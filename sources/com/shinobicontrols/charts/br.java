package com.shinobicontrols.charts;

interface br {
    public static final br ja = new br() {
        public final InternalDataPoint[] e(CartesianSeries<?> cartesianSeries) {
            return cartesianSeries.db.je;
        }
    };
    public static final br jb = new br() {
        public final InternalDataPoint[] e(CartesianSeries<?> cartesianSeries) {
            return cartesianSeries.db.cS();
        }
    };

    InternalDataPoint[] e(CartesianSeries<?> cartesianSeries);
}
