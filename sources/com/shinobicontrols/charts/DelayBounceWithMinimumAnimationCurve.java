package com.shinobicontrols.charts;

public class DelayBounceWithMinimumAnimationCurve extends AnimationCurve {
    public float valueAtTime(float time) {
        if (time < 0.5f) {
            time = 0.0f;
        } else {
            time = (time - 0.5f) * 2.0f;
        }
        float b = b(time);
        if (b < 0.05f) {
            return 0.05f;
        }
        return b;
    }
}
