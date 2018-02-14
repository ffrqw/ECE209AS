package com.google.android.gms.internal;

import android.text.TextUtils;
import com.google.android.gms.analytics.zzj;
import java.util.HashMap;
import java.util.Map;

public final class zzals extends zzj<zzals> {
    public String zzafa;
    public boolean zzafb;

    public final String toString() {
        Map hashMap = new HashMap();
        hashMap.put("description", this.zzafa);
        hashMap.put("fatal", Boolean.valueOf(this.zzafb));
        return zzj.zzh(hashMap);
    }

    public final /* synthetic */ void zzb(zzj zzj) {
        zzals zzals = (zzals) zzj;
        if (!TextUtils.isEmpty(this.zzafa)) {
            zzals.zzafa = this.zzafa;
        }
        if (this.zzafb) {
            zzals.zzafb = this.zzafb;
        }
    }
}
