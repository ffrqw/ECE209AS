package com.google.android.gms.maps.internal;

import android.os.IInterface;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public interface ICameraUpdateFactoryDelegate extends IInterface {
    IObjectWrapper newCameraPosition(CameraPosition cameraPosition) throws RemoteException;

    IObjectWrapper newLatLng(LatLng latLng) throws RemoteException;

    IObjectWrapper newLatLngBounds(LatLngBounds latLngBounds, int i) throws RemoteException;
}
