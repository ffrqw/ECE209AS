package com.google.android.gms.internal;

import android.app.PendingIntent;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import com.google.android.gms.location.zzj;
import com.google.android.gms.location.zzk;
import com.google.android.gms.location.zzm;
import com.google.android.gms.location.zzn;

public final class zzcdp extends zza {
    public static final Creator<zzcdp> CREATOR = new zzcdq();
    private PendingIntent mPendingIntent;
    private int zzbja;
    private zzcdn zzbjb;
    private zzm zzbjc;
    private zzj zzbjd;
    private zzccu zzbje;

    zzcdp(int i, zzcdn zzcdn, IBinder iBinder, PendingIntent pendingIntent, IBinder iBinder2, IBinder iBinder3) {
        zzccu zzccu = null;
        this.zzbja = i;
        this.zzbjb = zzcdn;
        this.zzbjc = iBinder == null ? null : zzn.zzZ(iBinder);
        this.mPendingIntent = pendingIntent;
        this.zzbjd = iBinder2 == null ? null : zzk.zzY(iBinder2);
        if (!(iBinder3 == null || iBinder3 == null)) {
            IInterface queryLocalInterface = iBinder3.queryLocalInterface("com.google.android.gms.location.internal.IFusedLocationProviderCallback");
            zzccu = queryLocalInterface instanceof zzccu ? (zzccu) queryLocalInterface : new zzccw(iBinder3);
        }
        this.zzbje = zzccu;
    }

    public static zzcdp zza(zzj zzj, zzccu zzccu) {
        return new zzcdp(2, null, null, null, zzj.asBinder(), null);
    }

    public static zzcdp zza(zzm zzm, zzccu zzccu) {
        return new zzcdp(2, null, zzm.asBinder(), null, null, null);
    }

    public final void writeToParcel(Parcel parcel, int i) {
        IBinder iBinder = null;
        int zze = zzd.zze(parcel);
        zzd.zzc(parcel, 1, this.zzbja);
        zzd.zza(parcel, 2, this.zzbjb, i, false);
        zzd.zza$cdac282(parcel, 3, this.zzbjc == null ? null : this.zzbjc.asBinder());
        zzd.zza(parcel, 4, this.mPendingIntent, i, false);
        zzd.zza$cdac282(parcel, 5, this.zzbjd == null ? null : this.zzbjd.asBinder());
        if (this.zzbje != null) {
            iBinder = this.zzbje.asBinder();
        }
        zzd.zza$cdac282(parcel, 6, iBinder);
        zzd.zzI(parcel, zze);
    }
}
