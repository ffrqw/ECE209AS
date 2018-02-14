package com.google.android.gms.internal;

import android.os.Handler;
import android.os.Message;
import com.google.android.gms.common.internal.zzbo;

final class zzbdx extends Handler {
    public final void handleMessage(Message message) {
        boolean z = true;
        if (message.what != 1) {
            z = false;
        }
        zzbo.zzaf(z);
        zzbdw zzbdw = null;
        zzbdw.zzb((zzbdz) message.obj);
    }
}
