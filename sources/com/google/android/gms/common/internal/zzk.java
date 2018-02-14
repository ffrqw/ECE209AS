package com.google.android.gms.common.internal;

import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public final class zzk extends zzav {
    private zzd zzaHg;
    private final int zzaHh;

    public zzk(zzd zzd, int i) {
        this.zzaHg = zzd;
        this.zzaHh = i;
    }

    public final void zza(int i, IBinder iBinder, Bundle bundle) {
        zzbo.zzb(this.zzaHg, (Object) "onPostInitComplete can be called only once per call to getRemoteService");
        this.zzaHg.zza(i, iBinder, bundle, this.zzaHh);
        this.zzaHg = null;
    }

    public final void zza$68e2e3e6() {
        Log.wtf("GmsClient", "received deprecated onAccountValidationComplete callback, ignoring", new Exception());
    }
}
