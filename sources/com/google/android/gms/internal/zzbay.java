package com.google.android.gms.internal;

import android.os.DeadObjectException;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.Api.zzc;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzbo;

public abstract class zzbay<R extends Result, A extends zzb> extends zzbbe<R> {
    private final zzc<A> zzaBM;
    private final Api<?> zzayW;

    public final void zzb(A a) throws DeadObjectException {
    }

    public final zzc<A> zzpd() {
        return this.zzaBM;
    }

    public final Api<?> zzpg() {
        return this.zzayW;
    }

    public final void zzr(Status status) {
        zzbo.zzb(!status.isSuccess(), (Object) "Failed result must not be success");
        setResult(zzb$34dcf236());
    }
}
