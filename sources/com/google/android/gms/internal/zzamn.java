package com.google.android.gms.internal;

import android.content.ComponentName;
import android.os.RemoteException;
import com.google.android.gms.analytics.zzl;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.stats.zza;
import java.util.Collections;

public final class zzamn extends zzamh {
    private final zzamp zzagg = new zzamp(this);
    private zzany zzagh;
    private final zzanm zzagi;
    private zzaoo zzagj;

    protected zzamn(zzamj zzamj) {
        super(zzamj);
        this.zzagj = new zzaoo(zzamj.zzkq());
        this.zzagi = new zzamo(this, zzamj);
    }

    private final void zzkP() {
        this.zzagj.start();
        this.zzagi.zzs(((Long) zzans.zzahO.get()).longValue());
    }

    public final boolean connect() {
        zzl.zzjC();
        zzkD();
        if (this.zzagh != null) {
            return true;
        }
        zzany zzkR = this.zzagg.zzkR();
        if (zzkR == null) {
            return false;
        }
        this.zzagh = zzkR;
        zzkP();
        return true;
    }

    public final void disconnect() {
        zzl.zzjC();
        zzkD();
        try {
            zza.zzrU();
            getContext().unbindService(this.zzagg);
        } catch (IllegalStateException e) {
        } catch (IllegalArgumentException e2) {
        }
        if (this.zzagh != null) {
            this.zzagh = null;
            zzkv().zzkn();
        }
    }

    public final boolean isConnected() {
        zzl.zzjC();
        zzkD();
        return this.zzagh != null;
    }

    public final boolean zzb(zzanx zzanx) {
        zzbo.zzu(zzanx);
        zzl.zzjC();
        zzkD();
        zzany zzany = this.zzagh;
        if (zzany == null) {
            return false;
        }
        try {
            zzany.zza(zzanx.zzdV(), zzanx.zzlG(), zzanx.zzlI() ? zzank.zzlu() : zzank.zzlv(), Collections.emptyList());
            zzkP();
            return true;
        } catch (RemoteException e) {
            zzbo("Failed to send hits to AnalyticsService");
            return false;
        }
    }

    protected final void zzjD() {
    }

    static /* synthetic */ void zza(zzamn zzamn, zzany zzany) {
        zzl.zzjC();
        zzamn.zzagh = zzany;
        zzamn.zzkP();
        zzamn.zzkv().onServiceConnected();
    }

    static /* synthetic */ void zza(zzamn zzamn, ComponentName componentName) {
        zzl.zzjC();
        if (zzamn.zzagh != null) {
            zzamn.zzagh = null;
            zzamn.zza("Disconnected from device AnalyticsService", componentName);
            zzamn.zzkv().zzkn();
        }
    }

    static /* synthetic */ void zzb(zzamn zzamn) {
        zzl.zzjC();
        if (zzamn.isConnected()) {
            zzamn.zzbo("Inactivity, disconnecting from device AnalyticsService");
            zzamn.disconnect();
        }
    }
}
