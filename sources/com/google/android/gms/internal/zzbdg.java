package com.google.android.gms.internal;

import com.google.android.gms.common.ConnectionResult;

final class zzbdg implements Runnable {
    private /* synthetic */ zzbdd zzaEv;
    private /* synthetic */ ConnectionResult zzaEw;

    zzbdg(zzbdd zzbdd, ConnectionResult connectionResult) {
        this.zzaEv = zzbdd;
        this.zzaEw = connectionResult;
    }

    public final void run() {
        this.zzaEv.onConnectionFailed(this.zzaEw);
    }
}
