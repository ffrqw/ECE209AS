package com.google.android.gms.internal;

final class zzbek implements Runnable {
    private /* synthetic */ zzctx zzaDx;
    private /* synthetic */ zzbej zzaEY;

    zzbek(zzbej zzbej, zzctx zzctx) {
        this.zzaEY = zzbej;
        this.zzaDx = zzctx;
    }

    public final void run() {
        zzbej.zza(this.zzaEY, this.zzaDx);
    }
}
