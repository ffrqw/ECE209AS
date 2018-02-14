package com.google.android.gms.internal;

import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.zzk;

final class zzcde extends zzk {
    private final zzbdw<Object> zzaEU;

    public final void onLocationAvailability(LocationAvailability locationAvailability) {
        this.zzaEU.zza(new zzcdg(locationAvailability));
    }

    public final void onLocationResult(LocationResult locationResult) {
        this.zzaEU.zza(new zzcdf(locationResult));
    }
}
