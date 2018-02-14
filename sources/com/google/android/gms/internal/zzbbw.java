package com.google.android.gms.internal;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.util.zza;

public final class zzbbw extends zzbba {
    private final zza<zzbat<?>> zzaCW;

    private final void zzpS() {
        if (!this.zzaCW.isEmpty()) {
            zzbdb zzbdb = null;
            zzbdb.zza(this);
        }
    }

    public final void onResume() {
        super.onResume();
        zzpS();
    }

    public final void onStart() {
        super.onStart();
        zzpS();
    }

    public final void onStop() {
        super.onStop();
        zzbdb zzbdb = null;
        zzbdb.zzb(this);
    }

    protected final void zza(ConnectionResult connectionResult, int i) {
        zzbdb zzbdb = null;
        zzbdb.zza(connectionResult, i);
    }

    final zza<zzbat<?>> zzpR() {
        return this.zzaCW;
    }

    protected final void zzps() {
        zzbdb zzbdb = null;
        zzbdb.zzps();
    }
}
