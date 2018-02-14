package com.google.android.gms.analytics;

import com.google.android.gms.internal.zzaob;

public final class zzf {
    public static String zzD(int i) {
        return zzc("cd", i);
    }

    public static String zzF(int i) {
        return zzc("cm", i);
    }

    public static String zzG(int i) {
        return zzc("&pr", i);
    }

    public static String zzH(int i) {
        return zzc("pr", i);
    }

    public static String zzI(int i) {
        return zzc("&promo", i);
    }

    public static String zzJ(int i) {
        return zzc("promo", i);
    }

    public static String zzK(int i) {
        return zzc("pi", i);
    }

    public static String zzL(int i) {
        return zzc("&il", i);
    }

    public static String zzM(int i) {
        return zzc("il", i);
    }

    private static String zzc(String str, int i) {
        if (i > 0) {
            return new StringBuilder(String.valueOf(str).length() + 11).append(str).append(i).toString();
        }
        zzaob.zzf("index out of range for prefix", str);
        return "";
    }
}
