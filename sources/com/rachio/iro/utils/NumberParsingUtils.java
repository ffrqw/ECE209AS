package com.rachio.iro.utils;

import java.text.NumberFormat;
import java.text.ParseException;

public class NumberParsingUtils {
    private static NumberFormat numberFormat = NumberFormat.getNumberInstance();

    public static final double parseDouble(String asString, double defaultTo) {
        try {
            return numberFormat.parse(asString).doubleValue();
        } catch (ParseException pe) {
            CrashReporterUtils.silentExceptionThatCrashesDebugBuilds(pe);
            return 0.0d;
        }
    }
}
