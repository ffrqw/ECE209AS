package com.google.android.gms.analytics;

import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.util.zze;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class zzk<T extends zzk> {
    private final zzl zzaea;
    protected final zzi zzaeb;
    private final List<Object> zzaec = new ArrayList();

    protected zzk(zzl zzl, zze zze) {
        zzbo.zzu(zzl);
        this.zzaea = zzl;
        zzi zzi = new zzi(this, zze);
        zzi.zzjy();
        this.zzaeb = zzi;
    }

    protected void zza(zzi zzi) {
    }

    protected final void zzd$6dd0e1ae() {
        Iterator it = this.zzaec.iterator();
        while (it.hasNext()) {
            it.next();
        }
    }

    public zzi zzjj() {
        zzi zzjp = this.zzaeb.zzjp();
        zzd$6dd0e1ae();
        return zzjp;
    }

    protected final zzl zzjz() {
        return this.zzaea;
    }
}
