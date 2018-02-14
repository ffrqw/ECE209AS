package com.google.android.gms.analytics;

import android.util.Log;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.FutureTask;

final class zzn extends FutureTask<T> {
    private /* synthetic */ zza zzael;

    zzn(zza zza, Runnable runnable, Object obj) {
        this.zzael = zza;
        super(runnable, obj);
    }

    protected final void setException(Throwable th) {
        UncaughtExceptionHandler zzb = this.zzael.zzaek.zzaei;
        if (zzb != null) {
            zzb.uncaughtException(Thread.currentThread(), th);
        } else if (Log.isLoggable("GAv4", 6)) {
            String valueOf = String.valueOf(th);
            Log.e("GAv4", new StringBuilder(String.valueOf(valueOf).length() + 37).append("MeasurementExecutor: job failed with ").append(valueOf).toString());
        }
        super.setException(th);
    }
}
