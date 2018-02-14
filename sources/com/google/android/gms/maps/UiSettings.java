package com.google.android.gms.maps;

import android.os.RemoteException;
import com.google.android.gms.maps.internal.IUiSettingsDelegate;
import com.google.android.gms.maps.model.RuntimeRemoteException;

public final class UiSettings {
    private final IUiSettingsDelegate zzbmY;

    UiSettings(IUiSettingsDelegate iUiSettingsDelegate) {
        this.zzbmY = iUiSettingsDelegate;
    }

    public final void setAllGesturesEnabled(boolean z) {
        try {
            this.zzbmY.setAllGesturesEnabled(true);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final void setCompassEnabled(boolean z) {
        try {
            this.zzbmY.setCompassEnabled(true);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final void setMapToolbarEnabled(boolean z) {
        try {
            this.zzbmY.setMapToolbarEnabled(false);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final void setMyLocationButtonEnabled(boolean z) {
        try {
            this.zzbmY.setMyLocationButtonEnabled(z);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final void setZoomControlsEnabled(boolean z) {
        try {
            this.zzbmY.setZoomControlsEnabled(true);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }
}
