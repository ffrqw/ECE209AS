package com.google.android.gms.internal;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Releasable;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.ResultCallbacks;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.TransformedResult;
import java.lang.ref.WeakReference;

public final class zzbes<R extends Result> extends TransformedResult<R> implements ResultCallback<R> {
    private final Object zzaBW;
    private final WeakReference<GoogleApiClient> zzaBY;
    private volatile ResultCallbacks<? super R> zzaFc;
    private Status zzaFe;

    private static void zzc(Result result) {
        if (!(result instanceof Releasable)) {
        }
    }

    private final boolean zzqL() {
        this.zzaBY.get();
        ResultCallbacks resultCallbacks = this.zzaFc;
        return false;
    }

    private final void zzv(Status status) {
        synchronized (this.zzaBW) {
            this.zzaFe = status;
            Status status2 = this.zzaFe;
            synchronized (this.zzaBW) {
                if (zzqL()) {
                    ResultCallbacks resultCallbacks = this.zzaFc;
                }
            }
        }
    }

    public final void onResult(R r) {
        synchronized (this.zzaBW) {
            if (!r.getStatus().isSuccess()) {
                zzv(r.getStatus());
                zzc(r);
            } else if (zzqL()) {
                ResultCallbacks resultCallbacks = this.zzaFc;
            }
        }
    }

    final void zzqK() {
        this.zzaFc = null;
    }
}
