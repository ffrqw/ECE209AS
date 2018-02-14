package com.shinobicontrols.charts;

public class BounceDelayAnimationCurve extends AnimationCurve {
    public float valueAtTime(float time) {
        if (time > 0.5f) {
            time = 1.0f;
        } else {
            time *= 2.0f;
        }
        return b(time);
    }
}
