package com.nineoldandroids.animation;

import android.view.animation.Interpolator;
import java.util.ArrayList;
import java.util.Arrays;

class KeyframeSet {
    TypeEvaluator mEvaluator;
    Keyframe mFirstKeyframe;
    Interpolator mInterpolator;
    ArrayList<Keyframe> mKeyframes = new ArrayList();
    Keyframe mLastKeyframe;
    int mNumKeyframes;

    public KeyframeSet(Keyframe... keyframes) {
        this.mNumKeyframes = keyframes.length;
        this.mKeyframes.addAll(Arrays.asList(keyframes));
        this.mFirstKeyframe = (Keyframe) this.mKeyframes.get(0);
        this.mLastKeyframe = (Keyframe) this.mKeyframes.get(this.mNumKeyframes - 1);
        this.mInterpolator = this.mLastKeyframe.getInterpolator();
    }

    public KeyframeSet clone() {
        ArrayList<Keyframe> keyframes = this.mKeyframes;
        int numKeyframes = this.mKeyframes.size();
        Keyframe[] newKeyframes = new Keyframe[numKeyframes];
        for (int i = 0; i < numKeyframes; i++) {
            newKeyframes[i] = ((Keyframe) keyframes.get(i)).clone();
        }
        return new KeyframeSet(newKeyframes);
    }

    public Object getValue(float fraction) {
        if (this.mNumKeyframes == 2) {
            if (this.mInterpolator != null) {
                fraction = this.mInterpolator.getInterpolation(fraction);
            }
            return this.mEvaluator.evaluate(fraction, this.mFirstKeyframe.getValue(), this.mLastKeyframe.getValue());
        } else if (fraction <= 0.0f) {
            nextKeyframe = (Keyframe) this.mKeyframes.get(1);
            interpolator = nextKeyframe.getInterpolator();
            if (interpolator != null) {
                fraction = interpolator.getInterpolation(fraction);
            }
            prevFraction = this.mFirstKeyframe.mFraction;
            return this.mEvaluator.evaluate((fraction - prevFraction) / (nextKeyframe.mFraction - prevFraction), this.mFirstKeyframe.getValue(), nextKeyframe.getValue());
        } else if (fraction >= 1.0f) {
            prevKeyframe = (Keyframe) this.mKeyframes.get(this.mNumKeyframes - 2);
            interpolator = this.mLastKeyframe.getInterpolator();
            if (interpolator != null) {
                fraction = interpolator.getInterpolation(fraction);
            }
            prevFraction = prevKeyframe.mFraction;
            return this.mEvaluator.evaluate((fraction - prevFraction) / (this.mLastKeyframe.mFraction - prevFraction), prevKeyframe.getValue(), this.mLastKeyframe.getValue());
        } else {
            prevKeyframe = this.mFirstKeyframe;
            for (int i = 1; i < this.mNumKeyframes; i++) {
                nextKeyframe = (Keyframe) this.mKeyframes.get(i);
                if (fraction < nextKeyframe.mFraction) {
                    interpolator = nextKeyframe.getInterpolator();
                    if (interpolator != null) {
                        fraction = interpolator.getInterpolation(fraction);
                    }
                    prevFraction = prevKeyframe.mFraction;
                    return this.mEvaluator.evaluate((fraction - prevFraction) / (nextKeyframe.mFraction - prevFraction), prevKeyframe.getValue(), nextKeyframe.getValue());
                }
                prevKeyframe = nextKeyframe;
            }
            return this.mLastKeyframe.getValue();
        }
    }

    public String toString() {
        String returnVal = " ";
        for (int i = 0; i < this.mNumKeyframes; i++) {
            returnVal = returnVal + ((Keyframe) this.mKeyframes.get(i)).getValue() + "  ";
        }
        return returnVal;
    }
}
