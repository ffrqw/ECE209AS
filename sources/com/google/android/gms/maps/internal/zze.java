package com.google.android.gms.maps.internal;

import android.os.IInterface;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.maps.model.internal.zza;

public interface zze extends IInterface {
    IMapFragmentDelegate zzH(IObjectWrapper iObjectWrapper) throws RemoteException;

    void zzi(IObjectWrapper iObjectWrapper, int i) throws RemoteException;

    ICameraUpdateFactoryDelegate zzwh() throws RemoteException;

    zza zzwi() throws RemoteException;
}
