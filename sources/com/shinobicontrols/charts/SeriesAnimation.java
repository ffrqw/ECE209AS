package com.shinobicontrols.charts;

public class SeriesAnimation extends Animation {
    Float pa = Float.valueOf(0.5f);
    Float pb = Float.valueOf(0.5f);
    private AnimationCurve pc = new FlatAnimationCurve();
    private AnimationCurve pd = new FlatAnimationCurve();
    private AnimationCurve pe = new FlatAnimationCurve();
    private AnimationCurve pf = new FlatAnimationCurve();
    private AnimationCurve pg = new FlatAnimationCurve();

    public /* bridge */ /* synthetic */ float getDuration() {
        return super.getDuration();
    }

    public /* bridge */ /* synthetic */ void setDuration(float x0) {
        super.setDuration(x0);
    }

    public SeriesAnimation() {
        setDuration(2.4f);
    }

    public static SeriesAnimation createGrowAnimation() {
        SeriesAnimation seriesAnimation = new SeriesAnimation();
        seriesAnimation.pc = new BounceAnimationCurve();
        seriesAnimation.pd = new BounceAnimationCurve();
        return seriesAnimation;
    }

    public static SeriesAnimation createGrowHorizontalAnimation() {
        SeriesAnimation seriesAnimation = new SeriesAnimation();
        seriesAnimation.pc = new BounceAnimationCurve();
        seriesAnimation.pa = null;
        return seriesAnimation;
    }

    public static SeriesAnimation createGrowVerticalAnimation() {
        SeriesAnimation seriesAnimation = new SeriesAnimation();
        seriesAnimation.pd = new BounceAnimationCurve();
        seriesAnimation.pb = null;
        return seriesAnimation;
    }

    public static SeriesAnimation createFadeAnimation() {
        SeriesAnimation seriesAnimation = new SeriesAnimation();
        seriesAnimation.pg = new LinearAnimationCurve();
        return seriesAnimation;
    }

    public static SeriesAnimation createTelevisionAnimation() {
        SeriesAnimation seriesAnimation = new SeriesAnimation();
        seriesAnimation.pc = new BounceDelayAnimationCurve();
        seriesAnimation.pd = new DelayBounceAnimationCurve();
        return seriesAnimation;
    }

    public Float getXOrigin() {
        return this.pa;
    }

    public void setXOrigin(Float origin) {
        this.pa = origin;
    }

    public Float getYOrigin() {
        return this.pb;
    }

    public void setYOrigin(Float origin) {
        this.pb = origin;
    }

    public AnimationCurve getXScaleCurve() {
        return this.pc;
    }

    public void setXScaleCurve(AnimationCurve curve) {
        if (curve == null) {
            throw new IllegalArgumentException("Animation curves may not be null");
        }
        this.pc = curve;
    }

    public AnimationCurve getYScaleCurve() {
        return this.pd;
    }

    public void setYScaleCurve(AnimationCurve curve) {
        if (curve == null) {
            throw new IllegalArgumentException("Animation curves may not be null");
        }
        this.pd = curve;
    }

    public AnimationCurve getAlphaCurve() {
        return this.pg;
    }

    public void setAlphaCurve(AnimationCurve curve) {
        if (curve == null) {
            throw new IllegalArgumentException("Animation curves may not be null");
        }
        this.pg = curve;
    }

    float ep() {
        if (a() <= 0.0f) {
            return 0.0f;
        }
        if (a() >= getDuration()) {
            return 1.0f;
        }
        return this.pc.valueAtTime(b());
    }

    float eq() {
        if (a() <= 0.0f) {
            return 0.0f;
        }
        if (a() >= getDuration()) {
            return 1.0f;
        }
        return this.pd.valueAtTime(b());
    }

    float er() {
        if (a() <= 0.0f) {
            return 0.0f;
        }
        if (a() >= getDuration()) {
            return 1.0f;
        }
        return this.pg.valueAtTime(b());
    }
}
