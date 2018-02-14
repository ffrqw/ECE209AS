package com.shinobicontrols.charts;

abstract class ak {
    boolean gc;
    final boolean gd;

    static class a extends ak {
        a() {
            super(false);
        }

        DataPoint<?, ?> a(DataPoint<?, ?> dataPoint, DataPoint<?, ?> dataPoint2, DataPoint<?, ?> dataPoint3, boolean z) {
            return dataPoint2;
        }

        DataPoint<?, ?> b(DataPoint<?, ?> dataPoint, DataPoint<?, ?> dataPoint2, DataPoint<?, ?> dataPoint3, boolean z) {
            return new DataPoint(dataPoint2.getX(), dataPoint.getY());
        }
    }

    static class b extends ak {
        b() {
            super(false);
        }

        DataPoint<?, ?> a(DataPoint<?, ?> dataPoint, DataPoint<?, ?> dataPoint2, DataPoint<?, ?> dataPoint3, boolean z) {
            return dataPoint2;
        }

        DataPoint<?, ?> b(DataPoint<?, ?> dataPoint, DataPoint<?, ?> dataPoint2, DataPoint<?, ?> dataPoint3, boolean z) {
            return new DataPoint(dataPoint.getX(), dataPoint2.getY());
        }
    }

    static class c extends ak {
        c() {
            super(true);
        }

        DataPoint<?, ?> a(DataPoint<?, ?> dataPoint, DataPoint<?, ?> dataPoint2, DataPoint<?, ?> dataPoint3, boolean z) {
            return z ? dataPoint3 : dataPoint2;
        }

        DataPoint<?, ?> b(DataPoint<?, ?> dataPoint, DataPoint<?, ?> dataPoint2, DataPoint<?, ?> dataPoint3, boolean z) {
            return z ? dataPoint3 : dataPoint2;
        }
    }

    static class d extends ak {
        d() {
            super(false);
        }

        DataPoint<?, ?> a(DataPoint<?, ?> dataPoint, DataPoint<?, ?> dataPoint2, DataPoint<?, ?> dataPoint3, boolean z) {
            return dataPoint2;
        }

        DataPoint<?, ?> b(DataPoint<?, ?> dataPoint, DataPoint<?, ?> dataPoint2, DataPoint<?, ?> dataPoint3, boolean z) {
            return dataPoint;
        }
    }

    abstract DataPoint<?, ?> a(DataPoint<?, ?> dataPoint, DataPoint<?, ?> dataPoint2, DataPoint<?, ?> dataPoint3, boolean z);

    abstract DataPoint<?, ?> b(DataPoint<?, ?> dataPoint, DataPoint<?, ?> dataPoint2, DataPoint<?, ?> dataPoint3, boolean z);

    private ak(boolean z) {
        this.gc = false;
        this.gd = z;
    }
}
