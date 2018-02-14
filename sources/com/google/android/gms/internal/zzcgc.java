package com.google.android.gms.internal;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.gms.common.internal.zzbo;

public final class zzcgc {
    private final zzcge zzbrL;

    public zzcgc(zzcge zzcge) {
        zzbo.zzu(zzcge);
        this.zzbrL = zzcge;
    }

    public static boolean zzj$1a552345(Context context) {
        zzbo.zzu(context);
        return zzcjl.zza$607b2e85(context, "com.google.android.gms.measurement.AppMeasurementReceiver");
    }

    public final void onReceive(Context context, Intent intent) {
        zzcgl zzbj = zzcgl.zzbj(context);
        zzcfl zzwF = zzbj.zzwF();
        if (intent == null) {
            zzwF.zzyz().log("Receiver called with null intent");
            return;
        }
        zzcem.zzxE();
        String action = intent.getAction();
        zzwF.zzyD().zzj("Local receiver got", action);
        if ("com.google.android.gms.measurement.UPLOAD".equals(action)) {
            zzciw.zzk$1a552345(context);
            Intent className = new Intent().setClassName(context, "com.google.android.gms.measurement.AppMeasurementService");
            className.setAction("com.google.android.gms.measurement.UPLOAD");
            this.zzbrL.doStartService(context, className);
        } else if ("com.android.vending.INSTALL_REFERRER".equals(action)) {
            action = intent.getStringExtra("referrer");
            if (action == null) {
                zzwF.zzyD().log("Install referrer extras are null");
                return;
            }
            zzwF.zzyB().zzj("Install referrer extras are", action);
            if (!action.contains("?")) {
                String str = "?";
                action = String.valueOf(action);
                action = action.length() != 0 ? str.concat(action) : new String(str);
            }
            Bundle zzq = zzbj.zzwB().zzq(Uri.parse(action));
            if (zzq == null) {
                zzwF.zzyD().log("No campaign defined in install referrer broadcast");
                return;
            }
            long longExtra = intent.getLongExtra("referrer_timestamp_seconds", 0) * 1000;
            if (longExtra == 0) {
                zzwF.zzyz().log("Install referrer is missing timestamp");
            }
            zzbj.zzwE().zzj(new zzcgd(zzbj, longExtra, zzq, context, zzwF));
        }
    }
}
