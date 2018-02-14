package com.shinobicontrols.charts;

import java.util.ArrayList;
import java.util.List;

class c extends Animation {
    private final List<Animation> k = new ArrayList();
    private Animation[] l;

    c() {
    }

    void c(Animation animation) {
        this.k.add(animation);
    }

    public float getDuration() {
        float f = 0.0f;
        int size = this.k.size();
        a(size);
        Animation[] animationArr = (Animation[]) this.k.toArray(this.l);
        int i = 0;
        while (i < size) {
            float duration = animationArr[i].getDuration();
            if (duration <= f) {
                duration = f;
            }
            i++;
            f = duration;
        }
        return f;
    }

    void a(float f) {
        int size = this.k.size();
        a(size);
        Animation[] animationArr = (Animation[]) this.k.toArray(this.l);
        for (int i = 0; i < size; i++) {
            animationArr[i].a(f);
        }
    }

    boolean isFinished() {
        int size = this.k.size();
        a(size);
        Animation[] animationArr = (Animation[]) this.k.toArray(this.l);
        int i = 0;
        boolean z = true;
        while (i < size) {
            if (z && animationArr[i].isFinished()) {
                z = true;
            } else {
                z = false;
            }
            i++;
        }
        return z;
    }

    void a(int i) {
        if (this.l == null || this.l.length != i) {
            this.l = new Animation[i];
        }
    }
}
