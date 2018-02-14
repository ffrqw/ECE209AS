package com.google.android.gms.internal;

import android.content.ContentProviderClient;
import android.content.Context;
import android.location.Location;
import com.google.android.gms.location.zzj;
import com.google.android.gms.location.zzm;
import java.util.HashMap;
import java.util.Map;

public final class zzcdd {
    private final Context mContext;
    private final Map<zzbdy<Object>, zzcdh> zzaWU = new HashMap();
    private final zzcdt<zzccz> zzbiB;
    private ContentProviderClient zzbiM = null;
    private boolean zzbiN = false;
    private final Map<zzbdy<Object>, zzcde> zzbiO = new HashMap();

    public zzcdd(Context context, zzcdt<zzccz> zzcdt) {
        this.mContext = context;
        this.zzbiB = zzcdt;
    }

    public final Location getLastLocation() {
        this.zzbiB.zzre();
        try {
            return ((zzccz) this.zzbiB.zzrf()).zzdv(this.mContext.getPackageName());
        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }
    }

    public final void removeAllListeners() {
        try {
            synchronized (this.zzaWU) {
                for (zzm zzm : this.zzaWU.values()) {
                    if (zzm != null) {
                        ((zzccz) this.zzbiB.zzrf()).zza(zzcdp.zza(zzm, null));
                    }
                }
                this.zzaWU.clear();
            }
            synchronized (this.zzbiO) {
                for (zzj zzj : this.zzbiO.values()) {
                    if (zzj != null) {
                        ((zzccz) this.zzbiB.zzrf()).zza(zzcdp.zza(zzj, null));
                    }
                }
                this.zzbiO.clear();
            }
        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }
    }

    public final void zzvR() {
        if (this.zzbiN) {
            try {
                this.zzbiB.zzre();
                ((zzccz) this.zzbiB.zzrf()).zzai(false);
                this.zzbiN = false;
            } catch (Throwable e) {
                throw new IllegalStateException(e);
            }
        }
    }
}
