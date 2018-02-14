package com.google.android.gms.internal;

import android.util.Log;
import com.google.android.gms.analytics.Logger;

@Deprecated
public final class zzaob {
    private static volatile Logger zzaim = new zzanl();

    public static void zzaT(String str) {
        zzaoc zzlM = zzaoc.zzlM();
        if (zzlM != null) {
            zzlM.zzbr(str);
        } else if (zzz(2)) {
            Log.w((String) zzans.zzahg.get(), str);
        }
        Logger logger = zzaim;
    }

    public static void zzf(String str, Object obj) {
        zzaoc zzlM = zzaoc.zzlM();
        if (zzlM != null) {
            zzlM.zze(str, obj);
        } else if (zzz(3)) {
            if (obj != null) {
                String valueOf = String.valueOf(obj);
                str = new StringBuilder((String.valueOf(str).length() + 1) + String.valueOf(valueOf).length()).append(str).append(":").append(valueOf).toString();
            }
            Log.e((String) zzans.zzahg.get(), str);
        }
        Logger logger = zzaim;
    }

    private static boolean zzz(int i) {
        return zzaim != null && zzaim.getLogLevel() <= i;
    }
}
