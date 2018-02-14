package com.google.android.gms.common.internal;

import com.google.android.gms.common.ConnectionResult;

public final class zzo extends zze {
    private /* synthetic */ zzd zzaHe;

    public zzo(zzd zzd, int i) {
        this.zzaHe = zzd;
        super(zzd, i, null);
    }

    protected final void zzj(ConnectionResult connectionResult) {
        this.zzaHe.zzaGQ.zzf(connectionResult);
        this.zzaHe.onConnectionFailed(connectionResult);
    }

    protected final boolean zzrj() {
        this.zzaHe.zzaGQ.zzf(ConnectionResult.zzazX);
        return true;
    }
}
