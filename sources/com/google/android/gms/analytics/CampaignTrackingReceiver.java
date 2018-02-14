package com.google.android.gms.analytics;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.internal.zzamj;
import com.google.android.gms.internal.zzaoc;
import com.google.android.gms.internal.zzaos;
import com.google.android.gms.internal.zzctz;

public final class CampaignTrackingReceiver extends BroadcastReceiver {
    static zzctz zzads;
    private static Boolean zzadt;
    static Object zzuF = new Object();

    public static boolean zzac(Context context) {
        zzbo.zzu(context);
        if (zzadt != null) {
            return zzadt.booleanValue();
        }
        boolean zza = zzaos.zza(context, "com.google.android.gms.analytics.CampaignTrackingReceiver", true);
        zzadt = Boolean.valueOf(zza);
        return zza;
    }

    public final void onReceive(Context context, Intent intent) {
        zzaoc zzkr = zzamj.zzaf(context).zzkr();
        if (intent == null) {
            zzkr.zzbr("CampaignTrackingReceiver received null intent");
            return;
        }
        Object stringExtra = intent.getStringExtra("referrer");
        String action = intent.getAction();
        zzkr.zza("CampaignTrackingReceiver received", action);
        if (!"com.android.vending.INSTALL_REFERRER".equals(action) || TextUtils.isEmpty(stringExtra)) {
            zzkr.zzbr("CampaignTrackingReceiver received unexpected intent without referrer extra");
            return;
        }
        boolean zzad = CampaignTrackingService.zzad(context);
        if (!zzad) {
            zzkr.zzbr("CampaignTrackingService not registered or disabled. Installation tracking not possible. See http://goo.gl/8Rd3yj for instructions.");
        }
        Class cls = CampaignTrackingService.class;
        zzbo.zzu(cls);
        Intent intent2 = new Intent(context, cls);
        intent2.putExtra("referrer", stringExtra);
        synchronized (zzuF) {
            context.startService(intent2);
            if (zzad) {
                try {
                    if (zzads == null) {
                        zzctz zzctz = new zzctz(context, "Analytics campaign WakeLock");
                        zzads = zzctz;
                        zzctz.setReferenceCounted$1385ff();
                    }
                    zzads.acquire$1349ef();
                } catch (SecurityException e) {
                    zzkr.zzbr("CampaignTrackingService service at risk of not starting. For more reliable installation campaign reports, add the WAKE_LOCK permission to your manifest. See http://goo.gl/8Rd3yj for instructions.");
                }
                return;
            }
        }
    }
}
