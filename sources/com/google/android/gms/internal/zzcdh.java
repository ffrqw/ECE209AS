package com.google.android.gms.internal;

import android.location.Location;
import com.google.android.gms.location.zzn;

final class zzcdh extends zzn {
    private final zzbdw<Object> zzaEU;

    public final synchronized void onLocationChanged(Location location) {
        this.zzaEU.zza(new zzcdi(location));
    }
}
