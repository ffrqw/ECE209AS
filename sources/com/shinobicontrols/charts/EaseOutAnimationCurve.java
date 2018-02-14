package com.shinobicontrols.charts;

public class EaseOutAnimationCurve extends AnimationCurve {
    public float valueAtTime(float time) {
        return 1.0f - ((float) Math.pow((double) (1.0f - time), 5.0d));
    }
}
