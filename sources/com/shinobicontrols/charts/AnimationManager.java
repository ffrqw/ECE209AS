package com.shinobicontrols.charts;

class AnimationManager {
    private final long nativeHandle = 0;

    private native void setCurrentState(float f, float f2, float f3, boolean z, boolean z2, boolean z3, double d, double d2, double d3);

    static {
        System.loadLibrary("shinobicharts-android");
    }

    AnimationManager() {
    }

    void update(Series<?> series) {
        if (series.oz != null) {
            SeriesAnimation seriesAnimation = series.oz;
            setCurrentState(seriesAnimation.ep(), seriesAnimation.eq(), seriesAnimation.er(), true, true, true, series.as(), series.al(), (double) seriesAnimation.getDuration());
            return;
        }
        setCurrentState(1.0f, 1.0f, 1.0f, false, true, false, 0.0d, 0.0d, 0.0d);
    }
}
