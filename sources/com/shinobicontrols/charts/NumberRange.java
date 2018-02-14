package com.shinobicontrols.charts;

public class NumberRange extends Range<Double> {
    NumberRange() {
        super(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY);
    }

    public NumberRange(Double min, Double max) {
        super(min.doubleValue(), max.doubleValue());
    }

    void c(double d, double d2) {
        this.nv = d;
        this.nw = d2;
    }

    public Double getMinimum() {
        return Double.valueOf(this.nv);
    }

    public Double getMaximum() {
        return Double.valueOf(this.nw);
    }

    Range<Double> bZ() {
        return new NumberRange(Double.valueOf(this.nv), Double.valueOf(this.nw));
    }

    void reset() {
        this.nv = Double.POSITIVE_INFINITY;
        this.nw = Double.NEGATIVE_INFINITY;
    }
}
