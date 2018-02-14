package com.shinobicontrols.charts;

class ed {
    double oc = 0.0d;
    double od = 0.0d;
    double oe = 0.0d;
    double of = 0.0d;

    ed() {
    }

    double ed() {
        return this.oe - this.oc;
    }

    double ee() {
        return this.of - this.od;
    }

    void b(double d, double d2, double d3, double d4) {
        this.oc = d;
        this.od = d2;
        this.oe = d3;
        this.of = d4;
    }

    void q(double d, double d2) {
        this.oc += d;
        this.od += d2;
        this.oe += d;
        this.of += d2;
    }

    void r(double d, double d2) {
        this.oe += d - this.oc;
        this.of += d2 - this.od;
        this.oc = d;
        this.od = d2;
    }
}
