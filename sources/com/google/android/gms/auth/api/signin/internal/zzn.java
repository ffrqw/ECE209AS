package com.google.android.gms.auth.api.signin.internal;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;

public final class zzn extends zza {
    public static final Creator<zzn> CREATOR = new zzm();
    private Bundle mBundle;
    private int versionCode;
    private int zzamr;

    zzn(int i, int i2, Bundle bundle) {
        this.versionCode = i;
        this.zzamr = i2;
        this.mBundle = bundle;
    }

    public final int getType() {
        return this.zzamr;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zzc(parcel, 1, this.versionCode);
        zzd.zzc(parcel, 2, this.zzamr);
        zzd.zza$f7bef55(parcel, 3, this.mBundle);
        zzd.zzI(parcel, zze);
    }
}
