package com.google.android.gms.internal;

import com.google.android.gms.common.api.Api.zze;
import java.util.ArrayList;

final class zzbcj extends zzbcn {
    private /* synthetic */ zzbcd zzaDp;
    private final ArrayList<zze> zzaDv;

    public zzbcj(zzbcd zzbcd, ArrayList<zze> arrayList) {
        this.zzaDp = zzbcd;
        super(zzbcd);
        this.zzaDv = arrayList;
    }

    public final void zzpV() {
        this.zzaDp.zzaCZ.zzaCl.zzaDG = zzbcd.zzg(this.zzaDp);
        ArrayList arrayList = this.zzaDv;
        int size = arrayList.size();
        int i = 0;
        while (i < size) {
            Object obj = arrayList.get(i);
            i++;
            ((zze) obj).zza(this.zzaDp.zzaDl, this.zzaDp.zzaCZ.zzaCl.zzaDG);
        }
    }
}
