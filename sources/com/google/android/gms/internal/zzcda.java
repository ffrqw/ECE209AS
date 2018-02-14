package com.google.android.gms.internal;

import android.location.Location;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;

public final class zzcda extends zzed implements zzccz {
    zzcda(IBinder iBinder) {
        super(iBinder, "com.google.android.gms.location.internal.IGoogleLocationManagerService");
    }

    public final void zza(zzcdp zzcdp) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (Parcelable) zzcdp);
        zzb(59, zzZ);
    }

    public final void zzai(boolean z) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, z);
        zzb(12, zzZ);
    }

    public final Location zzdv(String str) throws RemoteException {
        Parcel zzZ = zzZ();
        zzZ.writeString(str);
        Parcel zza = zza(21, zzZ);
        Location location = (Location) zzef.zza(zza, Location.CREATOR);
        zza.recycle();
        return location;
    }
}
