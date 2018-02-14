package com.google.android.gms.internal;

import android.content.SharedPreferences.Editor;
import com.google.android.gms.common.internal.zzbo;

public final class zzcgb {
    private String mValue;
    private final String zzBN;
    private boolean zzbrE;
    private /* synthetic */ zzcfw zzbrF;
    private final String zzbrK = null;

    public zzcgb(zzcfw zzcfw, String str) {
        this.zzbrF = zzcfw;
        zzbo.zzcF(str);
        this.zzBN = str;
    }

    public final void zzef(String str) {
        if (!zzcjl.zzR(str, this.mValue)) {
            Editor edit = this.zzbrF.zzaix.edit();
            edit.putString(this.zzBN, str);
            edit.apply();
            this.mValue = str;
        }
    }

    public final String zzyL() {
        if (!this.zzbrE) {
            this.zzbrE = true;
            this.mValue = this.zzbrF.zzaix.getString(this.zzBN, null);
        }
        return this.mValue;
    }
}
