package com.shinobicontrols.charts;

public class EaseInAnimationCurve extends AnimationCurve {
    public float valueAtTime(float time) {
        return (float) Math.pow((double) time, 4.0d);
    }
}
