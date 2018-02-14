package com.shinobicontrols.charts;

import android.util.DisplayMetrics;

class bd extends be<bl> implements bl {
    private float hm = 1.0f;
    private float hn = 1.0f;
    private DisplayMetrics ho;

    bd() {
    }

    float cq() {
        return this.hm;
    }

    float cr() {
        return this.hn;
    }

    DisplayMetrics cs() {
        return this.ho;
    }

    public void a(float f, float f2, DisplayMetrics displayMetrics) {
        this.hn = f;
        this.hm = f2;
        this.ho = displayMetrics;
        update();
    }

    void a(bl blVar) {
        blVar.a(this.hn, this.hm, this.ho);
    }
}
