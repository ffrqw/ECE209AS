package com.google.android.gms.internal;

import android.content.Context;
import com.google.android.gms.common.internal.zzbo;

public final class zzaoj {
    private static Boolean zzadt;
    static Object zzuF = new Object();

    public static boolean zzac(Context context) {
        zzbo.zzu(context);
        if (zzadt != null) {
            return zzadt.booleanValue();
        }
        boolean zza = zzaos.zza(context, "com.google.android.gms.analytics.AnalyticsReceiver", false);
        zzadt = Boolean.valueOf(zza);
        return zza;
    }
}
