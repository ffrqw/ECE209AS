package com.rachio.iro.utils;

public class FuzzyEquality {
    public static boolean fuzzyEqual(double lhs, double rhs) {
        return Math.abs(lhs - rhs) < 0.05d;
    }
}
