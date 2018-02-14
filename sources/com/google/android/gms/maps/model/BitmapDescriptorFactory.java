package com.google.android.gms.maps.model;

import android.os.RemoteException;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.maps.model.internal.zza;

public final class BitmapDescriptorFactory {
    private static zza zzbnb;

    public static BitmapDescriptor fromResource(int i) {
        try {
            return new BitmapDescriptor(((zza) zzbo.zzb(zzbnb, (Object) "IBitmapDescriptorFactory is not initialized")).zzbo(i));
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public static void zza(zza zza) {
        if (zzbnb == null) {
            zzbnb = (zza) zzbo.zzu(zza);
        }
    }
}
