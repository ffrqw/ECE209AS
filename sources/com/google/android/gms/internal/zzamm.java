package com.google.android.gms.internal;

import com.google.android.gms.common.internal.zzbo;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class zzamm {
    private final Map<String, String> zzHa;
    private final String zzafd;
    private final long zzagc = 0;
    private final String zzagd;
    private final boolean zzage;
    private long zzagf;

    public zzamm(String str, String str2, boolean z, long j, Map<String, String> map) {
        zzbo.zzcF(str);
        zzbo.zzcF(str2);
        this.zzafd = str;
        this.zzagd = str2;
        this.zzage = z;
        this.zzagf = j;
        if (map != null) {
            this.zzHa = new HashMap(map);
        } else {
            this.zzHa = Collections.emptyMap();
        }
    }

    public final Map<String, String> zzdV() {
        return this.zzHa;
    }

    public final String zzjX() {
        return this.zzafd;
    }

    public final String zzkL() {
        return this.zzagd;
    }

    public final boolean zzkM() {
        return this.zzage;
    }

    public final long zzkN() {
        return this.zzagf;
    }

    public final void zzm(long j) {
        this.zzagf = j;
    }
}
