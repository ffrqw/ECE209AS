package com.shinobicontrols.charts;

class cw {
    int lh;
    int li;

    cw() {
        reset();
    }

    void y(int i) {
        this.lh += i;
    }

    void z(int i) {
        this.li += i;
    }

    void reset() {
        this.li = 0;
        this.lh = 0;
    }
}
