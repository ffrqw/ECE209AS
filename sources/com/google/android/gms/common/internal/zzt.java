package com.google.android.gms.common.internal;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;
import com.google.android.gms.internal.zzbdt;

public abstract class zzt implements OnClickListener {
    public static zzt zza(Activity activity, Intent intent, int i) {
        return new zzu(intent, activity, i);
    }

    public static zzt zza(Fragment fragment, Intent intent, int i) {
        return new zzv(intent, fragment, i);
    }

    public static zzt zza$53204fe5(zzbdt zzbdt, Intent intent) {
        return new zzw(intent, zzbdt, 2);
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        try {
            zzrv();
        } catch (Throwable e) {
            Log.e("DialogRedirect", "Failed to start resolution intent", e);
        } finally {
            dialogInterface.dismiss();
        }
    }

    protected abstract void zzrv();
}
