package com.google.android.gms.internal;

import android.support.v4.util.ArrayMap;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.zza;
import com.google.android.gms.tasks.TaskCompletionSource;
import java.util.Set;

public final class zzbav {
    private final ArrayMap<zzbat<?>, ConnectionResult> zzaAB;
    private final TaskCompletionSource<Void> zzaBG;
    private int zzaBH;
    private boolean zzaBI;

    public final void zza(zzbat<?> zzbat, ConnectionResult connectionResult) {
        this.zzaAB.put(zzbat, connectionResult);
        this.zzaBH--;
        if (!connectionResult.isSuccess()) {
            this.zzaBI = true;
        }
        if (this.zzaBH != 0) {
            return;
        }
        if (this.zzaBI) {
            this.zzaBG.setException(new zza(this.zzaAB));
            return;
        }
        this.zzaBG.setResult(null);
    }

    public final Set<zzbat<?>> zzpt() {
        return this.zzaAB.keySet();
    }
}
