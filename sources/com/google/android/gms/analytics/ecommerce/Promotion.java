package com.google.android.gms.analytics.ecommerce;

import com.google.android.gms.analytics.zzj;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public final class Promotion {
    private Map<String, String> zzafu = new HashMap();

    public final String toString() {
        return zzj.zzk(this.zzafu);
    }

    public final Map<String, String> zzbl(String str) {
        Map<String, String> hashMap = new HashMap();
        for (Entry entry : this.zzafu.entrySet()) {
            String valueOf = String.valueOf(str);
            String valueOf2 = String.valueOf((String) entry.getKey());
            hashMap.put(valueOf2.length() != 0 ? valueOf.concat(valueOf2) : new String(valueOf), (String) entry.getValue());
        }
        return hashMap;
    }
}
