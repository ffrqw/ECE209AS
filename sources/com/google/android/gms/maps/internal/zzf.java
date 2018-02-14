package com.google.android.gms.maps.internal;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.internal.zzed;
import com.google.android.gms.internal.zzef;
import com.google.android.gms.maps.model.internal.zza;
import com.google.android.gms.maps.model.internal.zzb;

public final class zzf extends zzed implements zze {
    zzf(IBinder iBinder) {
        super(iBinder, "com.google.android.gms.maps.internal.ICreator");
    }

    public final IMapFragmentDelegate zzH(IObjectWrapper iObjectWrapper) throws RemoteException {
        IMapFragmentDelegate iMapFragmentDelegate;
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (IInterface) iObjectWrapper);
        Parcel zza = zza(2, zzZ);
        IBinder readStrongBinder = zza.readStrongBinder();
        if (readStrongBinder == null) {
            iMapFragmentDelegate = null;
        } else {
            IInterface queryLocalInterface = readStrongBinder.queryLocalInterface("com.google.android.gms.maps.internal.IMapFragmentDelegate");
            iMapFragmentDelegate = queryLocalInterface instanceof IMapFragmentDelegate ? (IMapFragmentDelegate) queryLocalInterface : new zzj(readStrongBinder);
        }
        zza.recycle();
        return iMapFragmentDelegate;
    }

    public final void zzi(IObjectWrapper iObjectWrapper, int i) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (IInterface) iObjectWrapper);
        zzZ.writeInt(i);
        zzb(6, zzZ);
    }

    public final ICameraUpdateFactoryDelegate zzwh() throws RemoteException {
        ICameraUpdateFactoryDelegate iCameraUpdateFactoryDelegate;
        Parcel zza = zza(4, zzZ());
        IBinder readStrongBinder = zza.readStrongBinder();
        if (readStrongBinder == null) {
            iCameraUpdateFactoryDelegate = null;
        } else {
            IInterface queryLocalInterface = readStrongBinder.queryLocalInterface("com.google.android.gms.maps.internal.ICameraUpdateFactoryDelegate");
            iCameraUpdateFactoryDelegate = queryLocalInterface instanceof ICameraUpdateFactoryDelegate ? (ICameraUpdateFactoryDelegate) queryLocalInterface : new zzb(readStrongBinder);
        }
        zza.recycle();
        return iCameraUpdateFactoryDelegate;
    }

    public final zza zzwi() throws RemoteException {
        Parcel zza = zza(5, zzZ());
        zza zzaa = zzb.zzaa(zza.readStrongBinder());
        zza.recycle();
        return zzaa;
    }
}
