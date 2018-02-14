package com.google.android.gms.internal;

import java.lang.ref.WeakReference;

final class zzbck extends zzctp {
    private final WeakReference<zzbcd> zzaDq;

    zzbck(zzbcd zzbcd) {
        this.zzaDq = new WeakReference(zzbcd);
    }

    public final void zzb(zzctx zzctx) {
        zzbcd zzbcd = (zzbcd) this.zzaDq.get();
        if (zzbcd != null) {
            zzbcd.zzaCZ.zza(new zzbcl(zzbcd, zzbcd, zzctx));
        }
    }
}
