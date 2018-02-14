package com.shinobicontrols.charts;

class cx extends AnimationCurve {
    private static final float lj = (-((float) Math.log(0.012000000104308128d)));
    private final float lk = h(1.0d);

    cx() {
    }

    private float h(double d) {
        return (1.0f - ((float) Math.pow(2.718281828459045d, (-d) * ((double) lj)))) / lj;
    }

    public float valueAtTime(float time) {
        return h((double) time) / this.lk;
    }
}
