package com.google.android.gms.common.internal;

import android.content.Context;
import android.content.ServiceConnection;

public abstract class zzae {
    private static final Object zzaHL = new Object();
    private static zzae zzaHM;

    public static zzae zzaC(Context context) {
        synchronized (zzaHL) {
            if (zzaHM == null) {
                zzaHM = new zzag(context.getApplicationContext());
            }
        }
        return zzaHM;
    }

    public final void zza(String str, String str2, ServiceConnection serviceConnection, String str3) {
        zzb(new zzaf(str, str2), serviceConnection, str3);
    }

    protected abstract boolean zza(zzaf zzaf, ServiceConnection serviceConnection, String str);

    protected abstract void zzb(zzaf zzaf, ServiceConnection serviceConnection, String str);
}
