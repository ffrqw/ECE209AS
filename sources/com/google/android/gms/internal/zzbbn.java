package com.google.android.gms.internal;

import android.os.Bundle;
import com.google.android.gms.common.ConnectionResult;

final class zzbbn implements zzbdq {
    private /* synthetic */ zzbbk zzaCx;

    private zzbbn(zzbbk zzbbk) {
        this.zzaCx = zzbbk;
    }

    public final void zzc(ConnectionResult connectionResult) {
        this.zzaCx.zzaCv.lock();
        try {
            this.zzaCx.zzaCt = connectionResult;
            zzbbk.zzb(this.zzaCx);
        } finally {
            this.zzaCx.zzaCv.unlock();
        }
    }

    public final void zze(int i, boolean z) {
        this.zzaCx.zzaCv.lock();
        try {
            if (this.zzaCx.zzaCu) {
                this.zzaCx.zzaCu = false;
                zzbbk.zza(this.zzaCx, i, z);
                return;
            }
            this.zzaCx.zzaCu = true;
            this.zzaCx.zzaCm.onConnectionSuspended(i);
            this.zzaCx.zzaCv.unlock();
        } finally {
            this.zzaCx.zzaCv.unlock();
        }
    }

    public final void zzm(Bundle bundle) {
        this.zzaCx.zzaCv.lock();
        try {
            this.zzaCx.zzaCt = ConnectionResult.zzazX;
            zzbbk.zzb(this.zzaCx);
        } finally {
            this.zzaCx.zzaCv.unlock();
        }
    }
}
