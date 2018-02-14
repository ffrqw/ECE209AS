package com.google.android.gms.common.internal;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

final class zzab implements zzg {
    private /* synthetic */ OnConnectionFailedListener zzaHD;

    zzab(OnConnectionFailedListener onConnectionFailedListener) {
        this.zzaHD = onConnectionFailedListener;
    }

    public final void onConnectionFailed(ConnectionResult connectionResult) {
        this.zzaHD.onConnectionFailed(connectionResult);
    }
}
