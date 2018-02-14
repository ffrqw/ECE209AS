package com.shinobicontrols.charts;

class h extends Animation {
    private AnimationCurve bJ = new db();

    void a(AnimationCurve animationCurve) {
        if (animationCurve == null) {
            this.bJ = new db();
        } else {
            this.bJ = animationCurve;
        }
    }

    float ad() {
        if (a() < 0.0f) {
            return 0.0f;
        }
        if (a() > getDuration()) {
            return 1.0f;
        }
        return this.bJ.valueAtTime(b());
    }
}
