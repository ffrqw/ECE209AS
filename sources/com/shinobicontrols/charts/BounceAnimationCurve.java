package com.shinobicontrols.charts;

public class BounceAnimationCurve extends AnimationCurve {
    public float valueAtTime(float time) {
        return b(time);
    }
}
