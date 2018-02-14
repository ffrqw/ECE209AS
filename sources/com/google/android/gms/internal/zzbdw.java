package com.google.android.gms.internal;

import com.google.android.gms.common.internal.zzbo;

public final class zzbdw<L> {
    private volatile L mListener;
    private final zzbdx zzaEM;

    public final void clear() {
        this.mListener = null;
    }

    public final void zza(zzbdz<? super L> zzbdz) {
        zzbo.zzb((Object) zzbdz, (Object) "Notifier must not be null");
        this.zzaEM.sendMessage(this.zzaEM.obtainMessage(1, zzbdz));
    }

    final void zzb(zzbdz<? super L> zzbdz) {
        Object obj = this.mListener;
    }
}
