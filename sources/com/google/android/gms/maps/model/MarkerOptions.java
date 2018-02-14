package com.google.android.gms.maps.model;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import com.google.android.gms.dynamic.IObjectWrapper;

public final class MarkerOptions extends zza {
    public static final Creator<MarkerOptions> CREATOR = new zzh();
    private float mAlpha = 1.0f;
    private String zzaoy;
    private LatLng zzbmN;
    private String zzbnE;
    private BitmapDescriptor zzbnF;
    private boolean zzbnG;
    private boolean zzbnH = false;
    private float zzbnI = 0.0f;
    private float zzbnJ = 0.5f;
    private float zzbnK = 0.0f;
    private float zzbnk;
    private boolean zzbnl = true;
    private float zzbnu = 0.5f;
    private float zzbnv = 1.0f;

    MarkerOptions(LatLng latLng, String str, String str2, IBinder iBinder, float f, float f2, boolean z, boolean z2, boolean z3, float f3, float f4, float f5, float f6, float f7) {
        this.zzbmN = latLng;
        this.zzaoy = str;
        this.zzbnE = str2;
        if (iBinder == null) {
            this.zzbnF = null;
        } else {
            this.zzbnF = new BitmapDescriptor(IObjectWrapper.zza.zzM(iBinder));
        }
        this.zzbnu = f;
        this.zzbnv = f2;
        this.zzbnG = z;
        this.zzbnl = z2;
        this.zzbnH = z3;
        this.zzbnI = f3;
        this.zzbnJ = f4;
        this.zzbnK = f5;
        this.mAlpha = f6;
        this.zzbnk = f7;
    }

    public final MarkerOptions anchor(float f, float f2) {
        this.zzbnu = 0.5f;
        this.zzbnv = 1.0f;
        return this;
    }

    public final MarkerOptions icon(BitmapDescriptor bitmapDescriptor) {
        this.zzbnF = bitmapDescriptor;
        return this;
    }

    public final MarkerOptions position(LatLng latLng) {
        this.zzbmN = latLng;
        return this;
    }

    public final MarkerOptions snippet(String str) {
        this.zzbnE = str;
        return this;
    }

    public final MarkerOptions title(String str) {
        this.zzaoy = str;
        return this;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zza(parcel, 2, this.zzbmN, i, false);
        zzd.zza(parcel, 3, this.zzaoy, false);
        zzd.zza(parcel, 4, this.zzbnE, false);
        zzd.zza$cdac282(parcel, 5, this.zzbnF == null ? null : this.zzbnF.zzwe().asBinder());
        zzd.zza(parcel, 6, this.zzbnu);
        zzd.zza(parcel, 7, this.zzbnv);
        zzd.zza(parcel, 8, this.zzbnG);
        zzd.zza(parcel, 9, this.zzbnl);
        zzd.zza(parcel, 10, this.zzbnH);
        zzd.zza(parcel, 11, this.zzbnI);
        zzd.zza(parcel, 12, this.zzbnJ);
        zzd.zza(parcel, 13, this.zzbnK);
        zzd.zza(parcel, 14, this.mAlpha);
        zzd.zza(parcel, 15, this.zzbnk);
        zzd.zzI(parcel, zze);
    }
}
