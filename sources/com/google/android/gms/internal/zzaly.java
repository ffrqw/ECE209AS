package com.google.android.gms.internal;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import com.google.android.gms.analytics.zzl;
import com.google.android.gms.common.internal.zzbo;

public final class zzaly extends zzamh {
    private final zzamv zzafB;

    public zzaly(zzamj zzamj, zzaml zzaml) {
        super(zzamj);
        zzbo.zzu(zzaml);
        this.zzafB = new zzamv(zzamj, zzaml);
    }

    final void onServiceConnected() {
        zzl.zzjC();
        this.zzafB.onServiceConnected();
    }

    public final void start() {
        this.zzafB.start();
    }

    public final long zza(zzamm zzamm) {
        zzkD();
        zzbo.zzu(zzamm);
        zzl.zzjC();
        long zza$50872eb5 = this.zzafB.zza$50872eb5(zzamm);
        if (zza$50872eb5 == 0) {
            this.zzafB.zzb(zzamm);
        }
        return zza$50872eb5;
    }

    public final void zza(zzanx zzanx) {
        zzbo.zzu(zzanx);
        zzkD();
        zzb("Hit delivery requested", zzanx);
        zzkt().zzf(new zzamc(this, zzanx));
    }

    public final void zza(String str, Runnable runnable) {
        zzbo.zzh(str, "campaign param can't be empty");
        zzkt().zzf(new zzamb(this, str, runnable));
    }

    protected final void zzjD() {
        this.zzafB.initialize();
    }

    public final void zzkl() {
        zzkD();
        Context context = getContext();
        if (zzaoj.zzac(context) && zzaok.zzad(context)) {
            Intent intent = new Intent("com.google.android.gms.analytics.ANALYTICS_DISPATCH");
            intent.setComponent(new ComponentName(context, "com.google.android.gms.analytics.AnalyticsService"));
            context.startService(intent);
            return;
        }
        zzkD();
        zzkt().zzf(new zzame(this, null));
    }

    public final void zzkn() {
        zzkD();
        zzl.zzjC();
        zzamv zzamv = this.zzafB;
        zzl.zzjC();
        zzamv.zzkD();
        zzamv.zzbo("Service disconnected");
    }

    final void zzko() {
        zzl.zzjC();
        this.zzafB.zzko();
    }
}
