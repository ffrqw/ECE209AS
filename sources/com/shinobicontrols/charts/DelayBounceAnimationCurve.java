package com.shinobicontrols.charts;

public class DelayBounceAnimationCurve extends AnimationCurve {
    public float valueAtTime(float time) {
        if (time < 0.5f) {
            time = 0.0f;
        } else {
            time = (time - 0.5f) * 2.0f;
        }
        return b(time);
    }
}
