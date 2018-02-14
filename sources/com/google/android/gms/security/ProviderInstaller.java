package com.google.android.gms.security;

import android.content.Context;
import android.util.Log;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.zze;
import com.google.android.gms.common.zzo;
import java.lang.reflect.Method;

public final class ProviderInstaller {
    private static final zze zzbCG = zze.zzoW();
    private static Method zzbCH = null;
    private static final Object zzuF = new Object();

    public static void installIfNeeded(Context context) throws GooglePlayServicesRepairableException, GooglePlayServicesNotAvailableException {
        zzbo.zzb((Object) context, (Object) "Context must not be null");
        zze.zzas(context);
        Context remoteContext = zzo.getRemoteContext(context);
        if (remoteContext == null) {
            Log.e("ProviderInstaller", "Failed to get remote context");
            throw new GooglePlayServicesNotAvailableException(8);
        }
        synchronized (zzuF) {
            try {
                if (zzbCH == null) {
                    zzbCH = remoteContext.getClassLoader().loadClass("com.google.android.gms.common.security.ProviderInstallerImpl").getMethod("insertProvider", new Class[]{Context.class});
                }
                zzbCH.invoke(null, new Object[]{remoteContext});
            } catch (Exception e) {
                String str = "ProviderInstaller";
                String str2 = "Failed to install provider: ";
                String valueOf = String.valueOf(e.getMessage());
                Log.e(str, valueOf.length() != 0 ? str2.concat(valueOf) : new String(str2));
                throw new GooglePlayServicesNotAvailableException(8);
            }
        }
    }
}
