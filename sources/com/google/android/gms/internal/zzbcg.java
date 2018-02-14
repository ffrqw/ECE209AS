package com.google.android.gms.internal;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api.zze;
import com.google.android.gms.common.internal.zzj;
import java.util.Map;

final class zzbcg extends zzbcn {
    final /* synthetic */ zzbcd zzaDp;
    private final Map<zze, zzbcf> zzaDr;

    public zzbcg(zzbcd zzbcd, Map<zze, zzbcf> map) {
        this.zzaDp = zzbcd;
        super(zzbcd);
        this.zzaDr = map;
    }

    public final void zzpV() {
        Object obj;
        Object obj2 = null;
        for (zze zze : this.zzaDr.keySet()) {
            obj = 1;
            if (!((zzbcf) this.zzaDr.get(zze)).zzaCj) {
                break;
            }
            int i = 1;
        }
        obj = obj2;
        int isGooglePlayServicesAvailable = obj != null ? this.zzaDp.zzaCF.isGooglePlayServicesAvailable(this.zzaDp.mContext) : 0;
        if (isGooglePlayServicesAvailable != 0) {
            this.zzaDp.zzaCZ.zza(new zzbch(this, this.zzaDp, new ConnectionResult(isGooglePlayServicesAvailable, null)));
            return;
        }
        if (this.zzaDp.zzaDj) {
            this.zzaDp.zzaDh.connect();
        }
        for (zze zze2 : this.zzaDr.keySet()) {
            zzj zzj = (zzj) this.zzaDr.get(zze2);
            if (isGooglePlayServicesAvailable != 0) {
                this.zzaDp.zzaCZ.zza(new zzbci(this.zzaDp, zzj));
            } else {
                zze2.zza(zzj);
            }
        }
    }
}
