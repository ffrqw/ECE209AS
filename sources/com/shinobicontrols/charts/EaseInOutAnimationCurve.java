package com.shinobicontrols.charts;

public class EaseInOutAnimationCurve extends AnimationCurve {
    public float valueAtTime(float time) {
        if (time < 0.5f) {
            time *= 2.0f;
            return ((time * time) * time) * 0.5f;
        }
        time = 1.0f - ((time - 0.5f) * 2.0f);
        return ((1.0f - ((time * time) * time)) * 0.5f) + 0.5f;
    }
}
