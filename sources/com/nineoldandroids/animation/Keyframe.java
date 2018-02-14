package com.nineoldandroids.animation;

import android.view.animation.Interpolator;

public abstract class Keyframe implements Cloneable {
    float mFraction;
    boolean mHasValue = false;
    private Interpolator mInterpolator = null;
    Class mValueType;

    static class FloatKeyframe extends Keyframe {
        float mValue;

        FloatKeyframe(float fraction, float value) {
            this.mFraction = fraction;
            this.mValue = value;
            this.mValueType = Float.TYPE;
            this.mHasValue = true;
        }

        FloatKeyframe(float fraction) {
            this.mFraction = fraction;
            this.mValueType = Float.TYPE;
        }

        public final Object getValue() {
            return Float.valueOf(this.mValue);
        }

        private FloatKeyframe clone() {
            FloatKeyframe kfClone = new FloatKeyframe(this.mFraction, this.mValue);
            kfClone.setInterpolator(getInterpolator());
            return kfClone;
        }
    }

    public abstract Keyframe clone();

    public abstract Object getValue();

    public static Keyframe ofFloat(float fraction, float value) {
        return new FloatKeyframe(fraction, value);
    }

    public final Interpolator getInterpolator() {
        return this.mInterpolator;
    }

    public final void setInterpolator(Interpolator interpolator) {
        this.mInterpolator = interpolator;
    }
}
