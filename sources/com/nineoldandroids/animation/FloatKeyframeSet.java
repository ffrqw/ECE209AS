package com.nineoldandroids.animation;

import java.util.ArrayList;

final class FloatKeyframeSet extends KeyframeSet {
    private float deltaValue;
    private boolean firstTime = true;
    private float firstValue;
    private float lastValue;

    public FloatKeyframeSet(FloatKeyframe... keyframes) {
        super(keyframes);
    }

    public final Object getValue(float fraction) {
        return Float.valueOf(getFloatValue(fraction));
    }

    private FloatKeyframeSet clone() {
        ArrayList<Keyframe> keyframes = this.mKeyframes;
        int numKeyframes = this.mKeyframes.size();
        FloatKeyframe[] newKeyframes = new FloatKeyframe[numKeyframes];
        for (int i = 0; i < numKeyframes; i++) {
            newKeyframes[i] = (FloatKeyframe) ((Keyframe) keyframes.get(i)).clone();
        }
        return new FloatKeyframeSet(newKeyframes);
    }

    public final float getFloatValue(float fraction) {
        if (this.mNumKeyframes == 2) {
            if (this.firstTime) {
                this.firstTime = false;
                this.firstValue = ((FloatKeyframe) this.mKeyframes.get(0)).mValue;
                this.lastValue = ((FloatKeyframe) this.mKeyframes.get(1)).mValue;
                this.deltaValue = this.lastValue - this.firstValue;
            }
            if (this.mInterpolator != null) {
                fraction = this.mInterpolator.getInterpolation(fraction);
            }
            if (this.mEvaluator == null) {
                return this.firstValue + (this.deltaValue * fraction);
            }
            return ((Number) this.mEvaluator.evaluate(fraction, Float.valueOf(this.firstValue), Float.valueOf(this.lastValue))).floatValue();
        } else if (fraction <= 0.0f) {
            prevKeyframe = (FloatKeyframe) this.mKeyframes.get(0);
            nextKeyframe = (FloatKeyframe) this.mKeyframes.get(1);
            prevValue = prevKeyframe.mValue;
            nextValue = nextKeyframe.mValue;
            prevFraction = prevKeyframe.mFraction;
            nextFraction = nextKeyframe.mFraction;
            interpolator = nextKeyframe.getInterpolator();
            if (interpolator != null) {
                fraction = interpolator.getInterpolation(fraction);
            }
            intervalFraction = (fraction - prevFraction) / (nextFraction - prevFraction);
            return this.mEvaluator == null ? ((nextValue - prevValue) * intervalFraction) + prevValue : ((Number) this.mEvaluator.evaluate(intervalFraction, Float.valueOf(prevValue), Float.valueOf(nextValue))).floatValue();
        } else if (fraction >= 1.0f) {
            prevKeyframe = (FloatKeyframe) this.mKeyframes.get(this.mNumKeyframes - 2);
            nextKeyframe = (FloatKeyframe) this.mKeyframes.get(this.mNumKeyframes - 1);
            prevValue = prevKeyframe.mValue;
            nextValue = nextKeyframe.mValue;
            prevFraction = prevKeyframe.mFraction;
            nextFraction = nextKeyframe.mFraction;
            interpolator = nextKeyframe.getInterpolator();
            if (interpolator != null) {
                fraction = interpolator.getInterpolation(fraction);
            }
            intervalFraction = (fraction - prevFraction) / (nextFraction - prevFraction);
            return this.mEvaluator == null ? ((nextValue - prevValue) * intervalFraction) + prevValue : ((Number) this.mEvaluator.evaluate(intervalFraction, Float.valueOf(prevValue), Float.valueOf(nextValue))).floatValue();
        } else {
            prevKeyframe = (FloatKeyframe) this.mKeyframes.get(0);
            int i = 1;
            while (i < this.mNumKeyframes) {
                nextKeyframe = (FloatKeyframe) this.mKeyframes.get(i);
                if (fraction < nextKeyframe.mFraction) {
                    interpolator = nextKeyframe.getInterpolator();
                    if (interpolator != null) {
                        fraction = interpolator.getInterpolation(fraction);
                    }
                    intervalFraction = (fraction - prevKeyframe.mFraction) / (nextKeyframe.mFraction - prevKeyframe.mFraction);
                    prevValue = prevKeyframe.mValue;
                    nextValue = nextKeyframe.mValue;
                    return this.mEvaluator == null ? ((nextValue - prevValue) * intervalFraction) + prevValue : ((Number) this.mEvaluator.evaluate(intervalFraction, Float.valueOf(prevValue), Float.valueOf(nextValue))).floatValue();
                } else {
                    prevKeyframe = nextKeyframe;
                    i++;
                }
            }
            return ((Number) ((Keyframe) this.mKeyframes.get(this.mNumKeyframes - 1)).getValue()).floatValue();
        }
    }
}
