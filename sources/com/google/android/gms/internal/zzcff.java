package com.google.android.gms.internal;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import java.util.List;

public final class zzcff extends zzed implements zzcfd {
    zzcff(IBinder iBinder) {
        super(iBinder, "com.google.android.gms.measurement.internal.IMeasurementService");
    }

    public final List<zzcji> zza(zzceh zzceh, boolean z) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (Parcelable) zzceh);
        zzef.zza(zzZ, z);
        zzZ = zza(7, zzZ);
        List createTypedArrayList = zzZ.createTypedArrayList(zzcji.CREATOR);
        zzZ.recycle();
        return createTypedArrayList;
    }

    public final List<zzcek> zza(String str, String str2, zzceh zzceh) throws RemoteException {
        Parcel zzZ = zzZ();
        zzZ.writeString(str);
        zzZ.writeString(str2);
        zzef.zza(zzZ, (Parcelable) zzceh);
        zzZ = zza(16, zzZ);
        List createTypedArrayList = zzZ.createTypedArrayList(zzcek.CREATOR);
        zzZ.recycle();
        return createTypedArrayList;
    }

    public final List<zzcji> zza(String str, String str2, String str3, boolean z) throws RemoteException {
        Parcel zzZ = zzZ();
        zzZ.writeString(str);
        zzZ.writeString(str2);
        zzZ.writeString(str3);
        zzef.zza(zzZ, z);
        zzZ = zza(15, zzZ);
        List createTypedArrayList = zzZ.createTypedArrayList(zzcji.CREATOR);
        zzZ.recycle();
        return createTypedArrayList;
    }

    public final List<zzcji> zza(String str, String str2, boolean z, zzceh zzceh) throws RemoteException {
        Parcel zzZ = zzZ();
        zzZ.writeString(str);
        zzZ.writeString(str2);
        zzef.zza(zzZ, z);
        zzef.zza(zzZ, (Parcelable) zzceh);
        zzZ = zza(14, zzZ);
        List createTypedArrayList = zzZ.createTypedArrayList(zzcji.CREATOR);
        zzZ.recycle();
        return createTypedArrayList;
    }

    public final void zza(long j, String str, String str2, String str3) throws RemoteException {
        Parcel zzZ = zzZ();
        zzZ.writeLong(j);
        zzZ.writeString(str);
        zzZ.writeString(str2);
        zzZ.writeString(str3);
        zzb(10, zzZ);
    }

    public final void zza(zzceh zzceh) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (Parcelable) zzceh);
        zzb(4, zzZ);
    }

    public final void zza(zzcek zzcek, zzceh zzceh) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (Parcelable) zzcek);
        zzef.zza(zzZ, (Parcelable) zzceh);
        zzb(12, zzZ);
    }

    public final void zza(zzcez zzcez, zzceh zzceh) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (Parcelable) zzcez);
        zzef.zza(zzZ, (Parcelable) zzceh);
        zzb(1, zzZ);
    }

    public final void zza(zzcez zzcez, String str, String str2) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (Parcelable) zzcez);
        zzZ.writeString(str);
        zzZ.writeString(str2);
        zzb(5, zzZ);
    }

    public final void zza(zzcji zzcji, zzceh zzceh) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (Parcelable) zzcji);
        zzef.zza(zzZ, (Parcelable) zzceh);
        zzb(2, zzZ);
    }

    public final byte[] zza(zzcez zzcez, String str) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (Parcelable) zzcez);
        zzZ.writeString(str);
        zzZ = zza(9, zzZ);
        byte[] createByteArray = zzZ.createByteArray();
        zzZ.recycle();
        return createByteArray;
    }

    public final void zzb(zzceh zzceh) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (Parcelable) zzceh);
        zzb(6, zzZ);
    }

    public final void zzb(zzcek zzcek) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (Parcelable) zzcek);
        zzb(13, zzZ);
    }

    public final String zzc(zzceh zzceh) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (Parcelable) zzceh);
        zzZ = zza(11, zzZ);
        String readString = zzZ.readString();
        zzZ.recycle();
        return readString;
    }

    public final List<zzcek> zzk(String str, String str2, String str3) throws RemoteException {
        Parcel zzZ = zzZ();
        zzZ.writeString(str);
        zzZ.writeString(str2);
        zzZ.writeString(str3);
        zzZ = zza(17, zzZ);
        List createTypedArrayList = zzZ.createTypedArrayList(zzcek.CREATOR);
        zzZ.recycle();
        return createTypedArrayList;
    }
}
