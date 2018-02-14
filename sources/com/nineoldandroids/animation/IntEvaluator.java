package com.nineoldandroids.animation;

public final class IntEvaluator implements TypeEvaluator<Integer> {
    public final /* bridge */ /* synthetic */ Object evaluate(float x0, Object x1, Object x2) {
        Integer num = (Integer) x2;
        int intValue = ((Integer) x1).intValue();
        return Integer.valueOf((int) ((((float) (num.intValue() - intValue)) * x0) + ((float) intValue)));
    }
}
