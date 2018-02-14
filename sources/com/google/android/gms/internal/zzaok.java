package com.google.android.gms.internal;

import android.content.Context;
import com.google.android.gms.common.internal.zzbo;

public final class zzaok {
    private static Boolean zzadu;

    public static boolean zzad(Context context) {
        zzbo.zzu(context);
        if (zzadu != null) {
            return zzadu.booleanValue();
        }
        boolean zzw = zzaos.zzw(context, "com.google.android.gms.analytics.AnalyticsService");
        zzadu = Boolean.valueOf(zzw);
        return zzw;
    }
}
