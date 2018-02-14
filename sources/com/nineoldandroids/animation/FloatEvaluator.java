package com.nineoldandroids.animation;

public final class FloatEvaluator implements TypeEvaluator<Number> {
    public final /* bridge */ /* synthetic */ Object evaluate(float x0, Object x1, Object x2) {
        Number number = (Number) x2;
        float floatValue = ((Number) x1).floatValue();
        return Float.valueOf(floatValue + ((number.floatValue() - floatValue) * x0));
    }
}
