package com.shinobicontrols.charts;

public abstract class Range<T extends Comparable<T>> {
    double nv;
    double nw;

    abstract Range<T> bZ();

    abstract T getMaximum();

    abstract T getMinimum();

    static boolean h(Range<?> range) {
        return range == null || range.dG();
    }

    static boolean i(Range<?> range) {
        return !h(range);
    }

    Range(double min, double max) {
        this.nv = min;
        this.nw = max;
    }

    public double getSpan() {
        return dF();
    }

    double dF() {
        return this.nw - this.nv;
    }

    void l(double d) {
        if (d < this.nv) {
            this.nv = d;
        }
        if (d > this.nw) {
            this.nw = d;
        }
    }

    void j(Range<T> range) {
        if (!h(range)) {
            l(range.nv);
            l(range.nw);
        }
    }

    public boolean equals(Object other) {
        Range range = (Range) other;
        return (other == null || range == null || !k(range)) ? false : true;
    }

    boolean k(Range<T> range) {
        return range != null && this.nv == range.nv && this.nw == range.nw;
    }

    private boolean dG() {
        return Double.isInfinite(this.nv) || Double.isInfinite(this.nw) || this.nv > this.nw;
    }

    boolean isEmpty() {
        return this.nv == this.nw;
    }

    boolean f(double d, double d2) {
        boolean z = false;
        double dF = dF();
        if (dF > d2 - d) {
            this.nv = d;
            this.nw = d2;
            dF = this.nw - this.nv;
            z = true;
        }
        if (this.nv < d) {
            this.nv = d;
            this.nw = this.nv + dF;
            z = true;
        }
        if (this.nw <= d2) {
            return z;
        }
        this.nw = d2;
        this.nv = this.nw - dF;
        return true;
    }

    public int hashCode() {
        long doubleToLongBits = Double.doubleToLongBits(this.nv);
        long doubleToLongBits2 = Double.doubleToLongBits(this.nw);
        return ((((int) (doubleToLongBits ^ (doubleToLongBits >>> 32))) + 527) * 31) + ((int) (doubleToLongBits2 ^ (doubleToLongBits2 >>> 32)));
    }

    boolean a(Range<T> range, boolean z) {
        if (range == null) {
            return false;
        }
        if (this.nv > range.nv) {
            Range<T> range2 = range;
            range = this;
        }
        if (z) {
            if (this.nv + dF() >= range.nv) {
                return true;
            }
            return false;
        } else if (this.nv + dF() > range.nv) {
            return true;
        } else {
            return false;
        }
    }

    public String toString() {
        return String.format("[%s - %s]", new Object[]{getMinimum().toString(), getMaximum().toString()});
    }
}
