package com.google.android.gms.maps;

import android.os.RemoteException;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.maps.internal.ICameraUpdateFactoryDelegate;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.RuntimeRemoteException;

public final class CameraUpdateFactory {
    private static ICameraUpdateFactoryDelegate zzblw;

    public static CameraUpdate newCameraPosition(CameraPosition cameraPosition) {
        try {
            return new CameraUpdate(zzwf().newCameraPosition(cameraPosition));
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public static CameraUpdate newLatLng(LatLng latLng) {
        try {
            return new CameraUpdate(zzwf().newLatLng(latLng));
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public static CameraUpdate newLatLngBounds(LatLngBounds latLngBounds, int i) {
        try {
            return new CameraUpdate(zzwf().newLatLngBounds(latLngBounds, i));
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public static void zza(ICameraUpdateFactoryDelegate iCameraUpdateFactoryDelegate) {
        zzblw = (ICameraUpdateFactoryDelegate) zzbo.zzu(iCameraUpdateFactoryDelegate);
    }

    private static ICameraUpdateFactoryDelegate zzwf() {
        return (ICameraUpdateFactoryDelegate) zzbo.zzb(zzblw, (Object) "CameraUpdateFactory is not initialized");
    }
}
