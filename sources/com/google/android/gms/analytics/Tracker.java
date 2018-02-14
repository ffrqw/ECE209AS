package com.google.android.gms.analytics;

import android.text.TextUtils;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.internal.zzamh;
import com.google.android.gms.internal.zzamj;
import com.google.android.gms.internal.zzaoa;
import com.google.android.gms.internal.zzaos;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

public final class Tracker extends zzamh {
    private final Map<String, String> zzHa = new HashMap();
    private final Map<String, String> zzaep = new HashMap();
    private final zzaoa zzaeq;
    private final zza zzaer;

    class zza extends zzamh {
        private /* synthetic */ Tracker zzaeB;
        private long zzaeE = -1;
        private boolean zzaeF;

        protected zza(Tracker tracker, zzamj zzamj) {
            this.zzaeB = tracker;
            super(zzamj);
        }

        protected final void zzjD() {
        }

        public final synchronized boolean zzjE() {
            this.zzaeF = false;
            return false;
        }
    }

    Tracker(zzamj zzamj, String str) {
        super(zzamj);
        if (str != null) {
            this.zzHa.put("&tid", str);
        }
        this.zzHa.put("useSecure", "1");
        this.zzHa.put("&a", Integer.toString(new Random().nextInt(ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED) + 1));
        this.zzaeq = new zzaoa("tracking", zzkq(), (byte) 0);
        this.zzaer = new zza(this, zzamj);
    }

    private void set(String str, String str2) {
        zzbo.zzb((Object) str, (Object) "Key should be non-null");
        if (!TextUtils.isEmpty(str)) {
            this.zzHa.put(str, str2);
        }
    }

    private static String zza(Entry<String, String> entry) {
        String str = (String) entry.getKey();
        entry.getValue();
        int i = (!str.startsWith("&") || str.length() < 2) ? 0 : 1;
        return i == 0 ? null : ((String) entry.getKey()).substring(1);
    }

    private static void zzb(Map<String, String> map, Map<String, String> map2) {
        zzbo.zzu(map2);
        if (map != null) {
            for (Entry entry : map.entrySet()) {
                String zza = zza(entry);
                if (zza != null) {
                    map2.put(zza, (String) entry.getValue());
                }
            }
        }
    }

    private static void zzc(Map<String, String> map, Map<String, String> map2) {
        zzbo.zzu(map2);
        if (map != null) {
            for (Entry entry : map.entrySet()) {
                String zza = zza(entry);
                if (!(zza == null || map2.containsKey(zza))) {
                    map2.put(zza, (String) entry.getValue());
                }
            }
        }
    }

    public final void send(Map<String, String> map) {
        long currentTimeMillis = zzkq().currentTimeMillis();
        if (zzku().getAppOptOut()) {
            zzbp("AppOptOut is set to true. Not sending Google Analytics hit");
            return;
        }
        boolean isDryRunEnabled = zzku().isDryRunEnabled();
        Map hashMap = new HashMap();
        zzb(this.zzHa, hashMap);
        zzb(map, hashMap);
        boolean zzf$505cbf47 = zzaos.zzf$505cbf47((String) this.zzHa.get("useSecure"));
        zzc(this.zzaep, hashMap);
        this.zzaep.clear();
        String str = (String) hashMap.get("t");
        if (TextUtils.isEmpty(str)) {
            zzkr().zze(hashMap, "Missing hit type parameter");
            return;
        }
        String str2 = (String) hashMap.get("tid");
        if (TextUtils.isEmpty(str2)) {
            zzkr().zze(hashMap, "Missing tracking id parameter");
            return;
        }
        synchronized (this) {
            if ("screenview".equalsIgnoreCase(str) || "pageview".equalsIgnoreCase(str) || "appview".equalsIgnoreCase(str) || TextUtils.isEmpty(str)) {
                int parseInt = Integer.parseInt((String) this.zzHa.get("&a")) + 1;
                if (parseInt >= ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED) {
                    parseInt = 1;
                }
                this.zzHa.put("&a", Integer.toString(parseInt));
            }
        }
        zzkt().zzf(new zzp(this, hashMap, false, str, currentTimeMillis, isDryRunEnabled, zzf$505cbf47, str2));
    }

    public final void setScreenName(String str) {
        set("&cd", str);
    }

    protected final void zzjD() {
        this.zzaer.initialize();
        String zzjG = zzkx().zzjG();
        if (zzjG != null) {
            set("&an", zzjG);
        }
        zzjG = zzkx().zzjH();
        if (zzjG != null) {
            set("&av", zzjG);
        }
    }
}
