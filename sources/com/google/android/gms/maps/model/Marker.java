package com.google.android.gms.maps.model;

import android.os.RemoteException;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.maps.model.internal.zzp;

public final class Marker {
    private final zzp zzbnD;

    public Marker(zzp zzp) {
        this.zzbnD = (zzp) zzbo.zzu(zzp);
    }

    public final boolean equals(Object obj) {
        if (!(obj instanceof Marker)) {
            return false;
        }
        try {
            return this.zzbnD.zzj(((Marker) obj).zzbnD);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final LatLng getPosition() {
        try {
            return this.zzbnD.getPosition();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final int hashCode() {
        try {
            return this.zzbnD.hashCodeRemote();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final void hideInfoWindow() {
        try {
            this.zzbnD.hideInfoWindow();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final void setAnchor(float f, float f2) {
        try {
            this.zzbnD.setAnchor(0.5f, f2);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final void setIcon(BitmapDescriptor bitmapDescriptor) {
        if (bitmapDescriptor == null) {
            try {
                this.zzbnD.zzK(null);
                return;
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
        this.zzbnD.zzK(bitmapDescriptor.zzwe());
    }

    public final void showInfoWindow() {
        try {
            this.zzbnD.showInfoWindow();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }
}
