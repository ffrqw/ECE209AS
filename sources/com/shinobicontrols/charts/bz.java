package com.shinobicontrols.charts;

import android.graphics.Rect;

class bz {
    final Rect jq = new Rect();
    final Rect jr = new Rect();
    private final Rect js = new Rect();

    bz() {
    }

    void cW() {
        this.jq.set(this.js);
    }

    Rect cX() {
        this.jr.setEmpty();
        return this.jr;
    }

    void cY() {
        this.jq.set(this.jr);
    }

    void b(int i, int i2, int i3, int i4) {
        this.js.set(i, i2, i3, i4);
    }

    void q(int i) {
        Rect rect = this.js;
        rect.top += i;
    }
}
