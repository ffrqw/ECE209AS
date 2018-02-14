package com.shinobicontrols.charts;

public abstract class AnimationCurve {
    static final float d;
    static final float e;

    public abstract float valueAtTime(float f);

    static {
        float sqrt = (float) Math.sqrt(0.75d);
        d = sqrt;
        e = (float) Math.atan((double) (sqrt / 0.5f));
    }

    protected AnimationCurve() {
    }

    final float b(float f) {
        return 1.0f - (((1.0f / d) * ((float) Math.exp((double) (-4.8368f * f)))) * ((float) Math.sin((double) (((d * 9.6736f) * f) + e))));
    }
}
