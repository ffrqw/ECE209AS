package com.google.android.gms.analytics.ecommerce;

import com.google.android.gms.analytics.zzj;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public final class ProductAction {
    public final String toString() {
        Map hashMap = new HashMap();
        Map map = null;
        for (Entry entry : map.entrySet()) {
            if (((String) entry.getKey()).startsWith("&")) {
                hashMap.put(((String) entry.getKey()).substring(1), (String) entry.getValue());
            } else {
                hashMap.put((String) entry.getKey(), (String) entry.getValue());
            }
        }
        return zzj.zzk(hashMap);
    }
}
