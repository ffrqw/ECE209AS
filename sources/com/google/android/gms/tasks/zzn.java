package com.google.android.gms.tasks;

import com.google.android.gms.common.internal.zzbo;

final class zzn<TResult> extends Task<TResult> {
    private final Object mLock = new Object();
    private final zzl<TResult> zzbMg = new zzl();
    private boolean zzbMh;
    private TResult zzbMi;
    private Exception zzbMj;

    zzn() {
    }

    private final void zzDH() {
        zzbo.zza(!this.zzbMh, "Task is already complete");
    }

    public final void setException(Exception exception) {
        zzbo.zzb((Object) exception, (Object) "Exception must not be null");
        synchronized (this.mLock) {
            zzDH();
            this.zzbMh = true;
            this.zzbMj = exception;
        }
        this.zzbMg.zza(this);
    }

    public final void setResult(TResult tResult) {
        synchronized (this.mLock) {
            zzDH();
            this.zzbMh = true;
            this.zzbMi = tResult;
        }
        this.zzbMg.zza(this);
    }

    public final boolean trySetException(Exception exception) {
        boolean z = true;
        zzbo.zzb((Object) exception, (Object) "Exception must not be null");
        synchronized (this.mLock) {
            if (this.zzbMh) {
                z = false;
            } else {
                this.zzbMh = true;
                this.zzbMj = exception;
                this.zzbMg.zza(this);
            }
        }
        return z;
    }
}
