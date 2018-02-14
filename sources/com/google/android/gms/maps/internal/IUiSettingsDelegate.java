package com.google.android.gms.maps.internal;

import android.os.IInterface;
import android.os.RemoteException;

public interface IUiSettingsDelegate extends IInterface {
    void setAllGesturesEnabled(boolean z) throws RemoteException;

    void setCompassEnabled(boolean z) throws RemoteException;

    void setMapToolbarEnabled(boolean z) throws RemoteException;

    void setMyLocationButtonEnabled(boolean z) throws RemoteException;

    void setZoomControlsEnabled(boolean z) throws RemoteException;
}
