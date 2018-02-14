package com.google.android.gms.internal;

import android.os.DeadObjectException;
import android.os.RemoteException;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.TaskCompletionSource;

abstract class zzban extends zzbam {
    protected final TaskCompletionSource<Void> zzalE;

    public zzban(int i, TaskCompletionSource<Void> taskCompletionSource) {
        super(4);
        this.zzalE = taskCompletionSource;
    }

    public void zza(zzbbt zzbbt, boolean z) {
    }

    public final void zza(zzbdd<?> zzbdd) throws DeadObjectException {
        try {
            zzb(zzbdd);
        } catch (RemoteException e) {
            zzp(zzbam.zzb(e));
            throw e;
        } catch (RemoteException e2) {
            zzp(zzbam.zzb(e2));
        }
    }

    protected abstract void zzb(zzbdd<?> zzbdd) throws RemoteException;

    public void zzp(Status status) {
        this.zzalE.trySetException(new ApiException(status));
    }
}
