package com.google.android.gms.internal;

import android.os.Build.VERSION;
import android.os.DeadObjectException;
import android.os.RemoteException;
import android.os.TransactionTooLargeException;
import com.google.android.gms.common.api.Status;

public abstract class zzbam {
    private int zzamr;

    public zzbam(int i) {
        this.zzamr = i;
    }

    public abstract void zza(zzbbt zzbbt, boolean z);

    public abstract void zza(zzbdd<?> zzbdd) throws DeadObjectException;

    public abstract void zzp(Status status);

    static /* synthetic */ Status zzb(RemoteException remoteException) {
        StringBuilder stringBuilder = new StringBuilder();
        if (VERSION.SDK_INT >= 15 && (remoteException instanceof TransactionTooLargeException)) {
            stringBuilder.append("TransactionTooLargeException: ");
        }
        stringBuilder.append(remoteException.getLocalizedMessage());
        return new Status(8, stringBuilder.toString());
    }
}
