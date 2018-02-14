package com.google.android.gms.maps.internal;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.internal.zzed;
import com.google.android.gms.internal.zzef;

public final class zzbv extends zzed implements IUiSettingsDelegate {
    zzbv(IBinder iBinder) {
        super(iBinder, "com.google.android.gms.maps.internal.IUiSettingsDelegate");
    }

    public final void setAllGesturesEnabled(boolean z) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, z);
        zzb(8, zzZ);
    }

    public final void setCompassEnabled(boolean z) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, z);
        zzb(2, zzZ);
    }

    public final void setMapToolbarEnabled(boolean z) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, z);
        zzb(18, zzZ);
    }

    public final void setMyLocationButtonEnabled(boolean z) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, z);
        zzb(3, zzZ);
    }

    public final void setZoomControlsEnabled(boolean z) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, z);
        zzb(1, zzZ);
    }
}
