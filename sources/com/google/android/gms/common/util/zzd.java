package com.google.android.gms.common.util;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import com.google.android.gms.internal.zzbha;

public final class zzd {
    public static boolean zzC(Context context, String str) {
        "com.google.android.gms".equals(str);
        try {
            return (zzbha.zzaP(context).getApplicationInfo(str, 0).flags & 2097152) != 0;
        } catch (NameNotFoundException e) {
            return false;
        }
    }
}
