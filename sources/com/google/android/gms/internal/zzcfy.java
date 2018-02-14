package com.google.android.gms.internal;

import android.content.SharedPreferences.Editor;
import com.google.android.gms.common.internal.zzbo;

public final class zzcfy {
    private final String zzBN;
    private boolean zzaAI;
    private final boolean zzbrD = true;
    private boolean zzbrE;
    private /* synthetic */ zzcfw zzbrF;

    public zzcfy(zzcfw zzcfw, String str) {
        this.zzbrF = zzcfw;
        zzbo.zzcF(str);
        this.zzBN = str;
    }

    public final boolean get() {
        if (!this.zzbrE) {
            this.zzbrE = true;
            this.zzaAI = this.zzbrF.zzaix.getBoolean(this.zzBN, true);
        }
        return this.zzaAI;
    }

    public final void set(boolean z) {
        Editor edit = this.zzbrF.zzaix.edit();
        edit.putBoolean(this.zzBN, z);
        edit.apply();
        this.zzaAI = z;
    }
}
