package com.shinobicontrols.charts;

class fh<T> {
    T sU;
    boolean sV;

    fh(T t) {
        this.sU = t;
    }

    void b(T t) {
        this.sU = t;
        this.sV = true;
    }

    void c(T t) {
        if (!this.sV) {
            this.sU = t;
        }
    }
}
