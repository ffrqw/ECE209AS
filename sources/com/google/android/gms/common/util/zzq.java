package com.google.android.gms.common.util;

import android.os.Build.VERSION;

public final class zzq {
    public static boolean isAtLeastO() {
        return VERSION.SDK_INT > 25 || "O".equals(VERSION.CODENAME) || VERSION.CODENAME.startsWith("OMR");
    }

    public static boolean zzsb() {
        return VERSION.SDK_INT >= 18;
    }

    public static boolean zzsd() {
        return VERSION.SDK_INT >= 20;
    }

    public static boolean zzse() {
        return VERSION.SDK_INT >= 21;
    }
}
