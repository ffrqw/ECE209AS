package com.google.android.gms.measurement;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import com.google.android.gms.internal.zzciw;
import com.google.android.gms.internal.zzciz;

@TargetApi(24)
public final class AppMeasurementJobService extends JobService implements zzciz {
    private zzciw zzbop;

    private final zzciw zzwm() {
        if (this.zzbop == null) {
            this.zzbop = new zzciw(this);
        }
        return this.zzbop;
    }

    public final boolean callServiceStopSelfResult(int i) {
        throw new UnsupportedOperationException();
    }

    public final Context getContext() {
        return this;
    }

    public final void onCreate() {
        super.onCreate();
        zzwm().onCreate();
    }

    public final void onDestroy() {
        zzwm().onDestroy();
        super.onDestroy();
    }

    public final void onRebind(Intent intent) {
        zzwm().onRebind(intent);
    }

    public final boolean onStartJob(JobParameters jobParameters) {
        return zzwm().onStartJob(jobParameters);
    }

    public final boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

    public final boolean onUnbind(Intent intent) {
        return zzwm().onUnbind(intent);
    }

    @TargetApi(24)
    public final void zza$2d8b4c91(JobParameters jobParameters) {
        jobFinished(jobParameters, false);
    }
}
