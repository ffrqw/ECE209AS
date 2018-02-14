package com.shinobicontrols.charts;

class dp extends Animation {
    private AnimationCurve mN = new db();
    private AnimationCurve mO = new db();
    private AnimationCurve mP = new db();

    void b(AnimationCurve animationCurve) {
        if (animationCurve == null) {
            this.mN = new db();
        } else {
            this.mN = animationCurve;
        }
    }

    void c(AnimationCurve animationCurve) {
        if (animationCurve == null) {
            this.mO = new db();
        } else {
            this.mO = animationCurve;
        }
    }

    void d(AnimationCurve animationCurve) {
        if (animationCurve == null) {
            this.mP = new db();
        } else {
            this.mP = animationCurve;
        }
    }

    float dq() {
        if (a() < 0.0f) {
            return 0.0f;
        }
        if (a() > getDuration()) {
            return 1.0f;
        }
        return this.mN.valueAtTime(b());
    }

    float dr() {
        if (a() < 0.0f) {
            return 0.0f;
        }
        if (a() > getDuration()) {
            return 1.0f;
        }
        return 1.0f - this.mO.valueAtTime(1.0f - b());
    }

    double ds() {
        if (a() < 0.0f) {
            return 0.0d;
        }
        if (a() > getDuration()) {
            return 1.0d;
        }
        return (double) this.mP.valueAtTime(b());
    }
}
