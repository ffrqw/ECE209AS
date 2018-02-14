package com.rachio.iro.utils;

import com.crashlytics.android.Crashlytics;

public class CrashReporterUtils {
    public static void logDebug(String tag, String msg) {
        Crashlytics.log(3, tag, msg);
    }

    public static void silentException(Exception ex) {
        ex.printStackTrace();
        Crashlytics.logException(ex);
    }

    public static void silentExceptionThatCrashesDebugBuilds(Exception ex) {
        silentException(ex);
    }
}
