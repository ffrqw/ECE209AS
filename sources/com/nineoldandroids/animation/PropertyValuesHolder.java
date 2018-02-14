package com.nineoldandroids.animation;

import com.nineoldandroids.util.Property;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class PropertyValuesHolder implements Cloneable {
    private static Class[] DOUBLE_VARIANTS = new Class[]{Double.TYPE, Double.class, Float.TYPE, Integer.TYPE, Float.class, Integer.class};
    private static Class[] FLOAT_VARIANTS = new Class[]{Float.TYPE, Float.class, Double.TYPE, Integer.TYPE, Double.class, Integer.class};
    private static Class[] INTEGER_VARIANTS = new Class[]{Integer.TYPE, Integer.class, Float.TYPE, Double.TYPE, Float.class, Double.class};
    private static final TypeEvaluator sFloatEvaluator = new FloatEvaluator();
    private static final HashMap<Class, HashMap<String, Method>> sGetterPropertyMap = new HashMap();
    private static final TypeEvaluator sIntEvaluator = new IntEvaluator();
    private static final HashMap<Class, HashMap<String, Method>> sSetterPropertyMap = new HashMap();
    private Object mAnimatedValue;
    private TypeEvaluator mEvaluator;
    private Method mGetter;
    KeyframeSet mKeyframeSet;
    protected Property mProperty;
    final ReentrantReadWriteLock mPropertyMapLock;
    String mPropertyName;
    Method mSetter;
    final Object[] mTmpValueArray;
    Class mValueType;

    static class FloatPropertyValuesHolder extends PropertyValuesHolder {
        float mFloatAnimatedValue;
        FloatKeyframeSet mFloatKeyframeSet;

        public FloatPropertyValuesHolder(String propertyName, float... values) {
            super(propertyName);
            setFloatValues(values);
        }

        public final void setFloatValues(float... values) {
            super.setFloatValues(values);
            this.mFloatKeyframeSet = (FloatKeyframeSet) this.mKeyframeSet;
        }

        final void calculateValue(float fraction) {
            this.mFloatAnimatedValue = this.mFloatKeyframeSet.getFloatValue(fraction);
        }

        final Object getAnimatedValue() {
            return Float.valueOf(this.mFloatAnimatedValue);
        }

        private FloatPropertyValuesHolder clone() {
            FloatPropertyValuesHolder newPVH = (FloatPropertyValuesHolder) super.clone();
            newPVH.mFloatKeyframeSet = (FloatKeyframeSet) newPVH.mKeyframeSet;
            return newPVH;
        }
    }

    private PropertyValuesHolder(String propertyName) {
        this.mSetter = null;
        this.mGetter = null;
        this.mKeyframeSet = null;
        this.mPropertyMapLock = new ReentrantReadWriteLock();
        this.mTmpValueArray = new Object[1];
        this.mPropertyName = propertyName;
    }

    public static PropertyValuesHolder ofFloat(String propertyName, float... values) {
        return new FloatPropertyValuesHolder(propertyName, values);
    }

    public void setFloatValues(float... values) {
        int i = 1;
        this.mValueType = Float.TYPE;
        int length = values.length;
        FloatKeyframe[] floatKeyframeArr = new FloatKeyframe[Math.max(length, 2)];
        if (length == 1) {
            floatKeyframeArr[0] = new FloatKeyframe(0.0f);
            floatKeyframeArr[1] = (FloatKeyframe) Keyframe.ofFloat(1.0f, values[0]);
        } else {
            floatKeyframeArr[0] = (FloatKeyframe) Keyframe.ofFloat(0.0f, values[0]);
            while (i < length) {
                floatKeyframeArr[i] = (FloatKeyframe) Keyframe.ofFloat(((float) i) / ((float) (length - 1)), values[i]);
                i++;
            }
        }
        this.mKeyframeSet = new FloatKeyframeSet(floatKeyframeArr);
    }

    public PropertyValuesHolder clone() {
        try {
            PropertyValuesHolder newPVH = (PropertyValuesHolder) super.clone();
            newPVH.mPropertyName = this.mPropertyName;
            newPVH.mProperty = this.mProperty;
            newPVH.mKeyframeSet = this.mKeyframeSet.clone();
            newPVH.mEvaluator = this.mEvaluator;
            return newPVH;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    final void init() {
        if (this.mEvaluator == null) {
            TypeEvaluator typeEvaluator = this.mValueType == Integer.class ? sIntEvaluator : this.mValueType == Float.class ? sFloatEvaluator : null;
            this.mEvaluator = typeEvaluator;
        }
        if (this.mEvaluator != null) {
            this.mKeyframeSet.mEvaluator = this.mEvaluator;
        }
    }

    void calculateValue(float fraction) {
        this.mAnimatedValue = this.mKeyframeSet.getValue(fraction);
    }

    Object getAnimatedValue() {
        return this.mAnimatedValue;
    }

    public String toString() {
        return this.mPropertyName + ": " + this.mKeyframeSet.toString();
    }
}
