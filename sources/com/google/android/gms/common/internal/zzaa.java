package com.google.android.gms.common.internal;

import android.os.Bundle;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;

final class zzaa implements zzf {
    private /* synthetic */ ConnectionCallbacks zzaHC;

    zzaa(ConnectionCallbacks connectionCallbacks) {
        this.zzaHC = connectionCallbacks;
    }

    public final void onConnected(Bundle bundle) {
        this.zzaHC.onConnected(bundle);
    }

    public final void onConnectionSuspended(int i) {
        this.zzaHC.onConnectionSuspended(i);
    }
}
