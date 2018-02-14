package com.google.android.gms.internal;

abstract class zzbcn implements Runnable {
    private /* synthetic */ zzbcd zzaDp;

    private zzbcn(zzbcd zzbcd) {
        this.zzaDp = zzbcd;
    }

    public void run() {
        this.zzaDp.zzaCv.lock();
        try {
            if (!Thread.interrupted()) {
                zzpV();
                this.zzaDp.zzaCv.unlock();
            }
        } catch (RuntimeException e) {
            this.zzaDp.zzaCZ.zza(e);
        } finally {
            this.zzaDp.zzaCv.unlock();
        }
    }

    protected abstract void zzpV();
}
