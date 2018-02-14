package com.google.android.gms.internal;

import android.os.Bundle;
import com.google.android.gms.common.ConnectionResult;

final class zzbbm implements zzbdq {
    private /* synthetic */ zzbbk zzaCx;

    private zzbbm(zzbbk zzbbk) {
        this.zzaCx = zzbbk;
    }

    public final void zzc(ConnectionResult connectionResult) {
        this.zzaCx.zzaCv.lock();
        try {
            this.zzaCx.zzaCs = connectionResult;
            zzbbk.zzb(this.zzaCx);
        } finally {
            this.zzaCx.zzaCv.unlock();
        }
    }

    public final void zze(int i, boolean z) {
        this.zzaCx.zzaCv.lock();
        try {
            if (this.zzaCx.zzaCu || this.zzaCx.zzaCt == null || !this.zzaCx.zzaCt.isSuccess()) {
                this.zzaCx.zzaCu = false;
                zzbbk.zza(this.zzaCx, i, z);
                return;
            }
            this.zzaCx.zzaCu = true;
            this.zzaCx.zzaCn.onConnectionSuspended(i);
            this.zzaCx.zzaCv.unlock();
        } finally {
            this.zzaCx.zzaCv.unlock();
        }
    }

    public final void zzm(Bundle bundle) {
        this.zzaCx.zzaCv.lock();
        try {
            zzbbk.zza(this.zzaCx, bundle);
            this.zzaCx.zzaCs = ConnectionResult.zzazX;
            zzbbk.zzb(this.zzaCx);
        } finally {
            this.zzaCx.zzaCv.unlock();
        }
    }
}
