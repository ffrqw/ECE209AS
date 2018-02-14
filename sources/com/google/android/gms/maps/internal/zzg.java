package com.google.android.gms.maps.internal;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.internal.zzed;
import com.google.android.gms.internal.zzef;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.internal.zzp;
import com.google.android.gms.maps.model.internal.zzq;

public final class zzg extends zzed implements IGoogleMapDelegate {
    zzg(IBinder iBinder) {
        super(iBinder, "com.google.android.gms.maps.internal.IGoogleMapDelegate");
    }

    public final zzp addMarker(MarkerOptions markerOptions) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (Parcelable) markerOptions);
        zzZ = zza(11, zzZ);
        zzp zzaf = zzq.zzaf(zzZ.readStrongBinder());
        zzZ.recycle();
        return zzaf;
    }

    public final void animateCamera(IObjectWrapper iObjectWrapper) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (IInterface) iObjectWrapper);
        zzb(5, zzZ);
    }

    public final void animateCameraWithDurationAndCallback(IObjectWrapper iObjectWrapper, int i, zzc zzc) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (IInterface) iObjectWrapper);
        zzZ.writeInt(i);
        zzef.zza(zzZ, (IInterface) zzc);
        zzb(7, zzZ);
    }

    public final void clear() throws RemoteException {
        zzb(14, zzZ());
    }

    public final IUiSettingsDelegate getUiSettings() throws RemoteException {
        IUiSettingsDelegate iUiSettingsDelegate;
        Parcel zza = zza(25, zzZ());
        IBinder readStrongBinder = zza.readStrongBinder();
        if (readStrongBinder == null) {
            iUiSettingsDelegate = null;
        } else {
            IInterface queryLocalInterface = readStrongBinder.queryLocalInterface("com.google.android.gms.maps.internal.IUiSettingsDelegate");
            iUiSettingsDelegate = queryLocalInterface instanceof IUiSettingsDelegate ? (IUiSettingsDelegate) queryLocalInterface : new zzbv(readStrongBinder);
        }
        zza.recycle();
        return iUiSettingsDelegate;
    }

    public final void moveCamera(IObjectWrapper iObjectWrapper) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (IInterface) iObjectWrapper);
        zzb(4, zzZ);
    }

    public final void setMyLocationEnabled(boolean z) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, z);
        zzb(22, zzZ);
    }

    public final void setOnMarkerClickListener(zzar zzar) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (IInterface) zzar);
        zzb(30, zzZ);
    }

    public final void snapshot(zzbq zzbq, IObjectWrapper iObjectWrapper) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (IInterface) zzbq);
        zzef.zza(zzZ, (IInterface) iObjectWrapper);
        zzb(38, zzZ);
    }
}
