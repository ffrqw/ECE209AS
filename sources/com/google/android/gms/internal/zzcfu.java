package com.google.android.gms.internal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.google.android.gms.common.internal.zzbo;

class zzcfu extends BroadcastReceiver {
    private static String zzaiq = zzcfu.class.getName();
    private boolean mRegistered;
    private boolean zzair;
    private final zzcgl zzboe;

    zzcfu(zzcgl zzcgl) {
        zzbo.zzu(zzcgl);
        this.zzboe = zzcgl;
    }

    public void onReceive(Context context, Intent intent) {
        zzcgl zzcgl = this.zzboe;
        String action = intent.getAction();
        this.zzboe.zzwF().zzyD().zzj("NetworkBroadcastReceiver received action", action);
        if ("android.net.conn.CONNECTIVITY_CHANGE".equals(action)) {
            boolean zzlQ = this.zzboe.zzyU().zzlQ();
            if (this.zzair != zzlQ) {
                this.zzair = zzlQ;
                this.zzboe.zzwE().zzj(new zzcfv(this, zzlQ));
                return;
            }
            return;
        }
        this.zzboe.zzwF().zzyz().zzj("NetworkBroadcastReceiver received unknown action", action);
    }

    public final void unregister() {
        zzcgl zzcgl = this.zzboe;
        this.zzboe.zzwE().zzjC();
        this.zzboe.zzwE().zzjC();
        if (this.mRegistered) {
            this.zzboe.zzwF().zzyD().log("Unregistering connectivity change receiver");
            this.mRegistered = false;
            this.zzair = false;
            try {
                this.zzboe.getContext().unregisterReceiver(this);
            } catch (IllegalArgumentException e) {
                this.zzboe.zzwF().zzyx().zzj("Failed to unregister the network broadcast receiver", e);
            }
        }
    }

    public final void zzlN() {
        zzcgl zzcgl = this.zzboe;
        this.zzboe.zzwE().zzjC();
        if (!this.mRegistered) {
            this.zzboe.getContext().registerReceiver(this, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
            this.zzair = this.zzboe.zzyU().zzlQ();
            this.zzboe.zzwF().zzyD().zzj("Registering connectivity change receiver. Network connected", Boolean.valueOf(this.zzair));
            this.mRegistered = true;
        }
    }
}
