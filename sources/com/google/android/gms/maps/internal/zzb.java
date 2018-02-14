package com.google.android.gms.maps.internal;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.dynamic.IObjectWrapper.zza;
import com.google.android.gms.internal.zzed;
import com.google.android.gms.internal.zzef;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public final class zzb extends zzed implements ICameraUpdateFactoryDelegate {
    zzb(IBinder iBinder) {
        super(iBinder, "com.google.android.gms.maps.internal.ICameraUpdateFactoryDelegate");
    }

    public final IObjectWrapper newCameraPosition(CameraPosition cameraPosition) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (Parcelable) cameraPosition);
        zzZ = zza(7, zzZ);
        IObjectWrapper zzM = zza.zzM(zzZ.readStrongBinder());
        zzZ.recycle();
        return zzM;
    }

    public final IObjectWrapper newLatLng(LatLng latLng) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (Parcelable) latLng);
        zzZ = zza(8, zzZ);
        IObjectWrapper zzM = zza.zzM(zzZ.readStrongBinder());
        zzZ.recycle();
        return zzM;
    }

    public final IObjectWrapper newLatLngBounds(LatLngBounds latLngBounds, int i) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (Parcelable) latLngBounds);
        zzZ.writeInt(i);
        zzZ = zza(10, zzZ);
        IObjectWrapper zzM = zza.zzM(zzZ.readStrongBinder());
        zzZ.recycle();
        return zzM;
    }
}
