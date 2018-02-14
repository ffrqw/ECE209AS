package com.google.android.gms.internal;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

final class zzbcu extends Handler {
    private /* synthetic */ zzbcp zzaDN;

    zzbcu(zzbcp zzbcp, Looper looper) {
        this.zzaDN = zzbcp;
        super(looper);
    }

    public final void handleMessage(Message message) {
        switch (message.what) {
            case 1:
                zzbcp.zzb(this.zzaDN);
                return;
            case 2:
                zzbcp.zza(this.zzaDN);
                return;
            default:
                Log.w("GoogleApiClientImpl", "Unknown message id: " + message.what);
                return;
        }
    }
}
