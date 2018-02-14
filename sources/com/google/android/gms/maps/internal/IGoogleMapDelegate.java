package com.google.android.gms.maps.internal;

import android.os.IInterface;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.internal.zzp;

public interface IGoogleMapDelegate extends IInterface {
    zzp addMarker(MarkerOptions markerOptions) throws RemoteException;

    void animateCamera(IObjectWrapper iObjectWrapper) throws RemoteException;

    void animateCameraWithDurationAndCallback(IObjectWrapper iObjectWrapper, int i, zzc zzc) throws RemoteException;

    void clear() throws RemoteException;

    IUiSettingsDelegate getUiSettings() throws RemoteException;

    void moveCamera(IObjectWrapper iObjectWrapper) throws RemoteException;

    void setMyLocationEnabled(boolean z) throws RemoteException;

    void setOnMarkerClickListener(zzar zzar) throws RemoteException;

    void snapshot(zzbq zzbq, IObjectWrapper iObjectWrapper) throws RemoteException;
}
