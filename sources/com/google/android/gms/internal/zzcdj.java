package com.google.android.gms.internal;

import android.content.Context;
import android.location.Location;
import android.os.Looper;
import android.util.Log;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.internal.zzq;

public final class zzcdj extends zzcbx {
    private final zzcdd zzbiS;

    public zzcdj(Context context, Looper looper, ConnectionCallbacks connectionCallbacks, OnConnectionFailedListener onConnectionFailedListener, String str, zzq zzq) {
        super(context, looper, connectionCallbacks, onConnectionFailedListener, str, zzq);
        this.zzbiS = new zzcdd(context, this.zzbiB);
    }

    public final void disconnect() {
        synchronized (this.zzbiS) {
            if (isConnected()) {
                try {
                    this.zzbiS.removeAllListeners();
                    this.zzbiS.zzvR();
                } catch (Throwable e) {
                    Log.e("LocationClientImpl", "Client disconnected before listeners could be cleaned up", e);
                }
            }
            super.disconnect();
        }
    }

    public final Location getLastLocation() {
        return this.zzbiS.getLastLocation();
    }
}
