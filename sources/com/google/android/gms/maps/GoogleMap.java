package com.google.android.gms.maps;

import android.graphics.Bitmap;
import android.os.RemoteException;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.maps.internal.IGoogleMapDelegate;
import com.google.android.gms.maps.internal.zzd;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.RuntimeRemoteException;
import com.google.android.gms.maps.model.internal.zzp;

public final class GoogleMap {
    private final IGoogleMapDelegate zzblx;
    private UiSettings zzbly;

    public interface CancelableCallback {
        void onFinish();
    }

    public interface OnMarkerClickListener {
        boolean onMarkerClick(Marker marker);
    }

    public interface SnapshotReadyCallback {
        void onSnapshotReady(Bitmap bitmap);
    }

    static final class zza extends zzd {
        private final CancelableCallback zzblY;

        zza(CancelableCallback cancelableCallback) {
            this.zzblY = cancelableCallback;
        }

        public final void onFinish() {
            this.zzblY.onFinish();
        }
    }

    public GoogleMap(IGoogleMapDelegate iGoogleMapDelegate) {
        this.zzblx = (IGoogleMapDelegate) zzbo.zzu(iGoogleMapDelegate);
    }

    public final Marker addMarker(MarkerOptions markerOptions) {
        try {
            zzp addMarker = this.zzblx.addMarker(markerOptions);
            return addMarker != null ? new Marker(addMarker) : null;
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final void animateCamera(CameraUpdate cameraUpdate) {
        try {
            this.zzblx.animateCamera(cameraUpdate.zzwe());
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final void animateCamera(CameraUpdate cameraUpdate, int i, CancelableCallback cancelableCallback) {
        try {
            this.zzblx.animateCameraWithDurationAndCallback(cameraUpdate.zzwe(), i, new zza(cancelableCallback));
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final void clear() {
        try {
            this.zzblx.clear();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final UiSettings getUiSettings() {
        try {
            if (this.zzbly == null) {
                this.zzbly = new UiSettings(this.zzblx.getUiSettings());
            }
            return this.zzbly;
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final void moveCamera(CameraUpdate cameraUpdate) {
        try {
            this.zzblx.moveCamera(cameraUpdate.zzwe());
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final void setMyLocationEnabled(boolean z) {
        try {
            this.zzblx.setMyLocationEnabled(z);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final void setOnMarkerClickListener(OnMarkerClickListener onMarkerClickListener) {
        try {
            this.zzblx.setOnMarkerClickListener(new zzb(onMarkerClickListener));
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final void snapshot(SnapshotReadyCallback snapshotReadyCallback) {
        try {
            this.zzblx.snapshot(new zzq(snapshotReadyCallback), null);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }
}
