package com.google.android.gms.internal;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import com.google.android.gms.analytics.zzl;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.stats.zza;

public final class zzamp implements ServiceConnection {
    final /* synthetic */ zzamn zzagk;
    private volatile zzany zzagl;
    private volatile boolean zzagm;

    protected zzamp(zzamn zzamn) {
        this.zzagk = zzamn;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void onServiceConnected(android.content.ComponentName r5, android.os.IBinder r6) {
        /*
        r4 = this;
        r1 = 0;
        r0 = "AnalyticsServiceConnection.onServiceConnected";
        com.google.android.gms.common.internal.zzbo.zzcz(r0);
        monitor-enter(r4);
        if (r6 != 0) goto L_0x0015;
    L_0x0009:
        r0 = r4.zzagk;	 Catch:{ all -> 0x0064 }
        r1 = "Service connected with null binder";
        r0.zzbs(r1);	 Catch:{ all -> 0x0064 }
        r4.notifyAll();	 Catch:{ all -> 0x0044 }
        monitor-exit(r4);	 Catch:{ all -> 0x0044 }
    L_0x0014:
        return;
    L_0x0015:
        r0 = r6.getInterfaceDescriptor();	 Catch:{ RemoteException -> 0x005a }
        r2 = "com.google.android.gms.analytics.internal.IAnalyticsService";
        r2 = r2.equals(r0);	 Catch:{ RemoteException -> 0x005a }
        if (r2 == 0) goto L_0x0069;
    L_0x0021:
        if (r6 != 0) goto L_0x0047;
    L_0x0023:
        r0 = r1;
    L_0x0024:
        r1 = r4.zzagk;	 Catch:{ RemoteException -> 0x0091 }
        r2 = "Bound to IAnalyticsService interface";
        r1.zzbo(r2);	 Catch:{ RemoteException -> 0x0091 }
    L_0x002b:
        if (r0 != 0) goto L_0x0072;
    L_0x002d:
        com.google.android.gms.common.stats.zza.zzrU();	 Catch:{ IllegalArgumentException -> 0x008f }
        r0 = r4.zzagk;	 Catch:{ IllegalArgumentException -> 0x008f }
        r0 = r0.getContext();	 Catch:{ IllegalArgumentException -> 0x008f }
        r1 = r4.zzagk;	 Catch:{ IllegalArgumentException -> 0x008f }
        r1 = r1.zzagg;	 Catch:{ IllegalArgumentException -> 0x008f }
        r0.unbindService(r1);	 Catch:{ IllegalArgumentException -> 0x008f }
    L_0x003f:
        r4.notifyAll();	 Catch:{ all -> 0x0044 }
        monitor-exit(r4);	 Catch:{ all -> 0x0044 }
        goto L_0x0014;
    L_0x0044:
        r0 = move-exception;
        monitor-exit(r4);	 Catch:{ all -> 0x0044 }
        throw r0;
    L_0x0047:
        r0 = "com.google.android.gms.analytics.internal.IAnalyticsService";
        r0 = r6.queryLocalInterface(r0);	 Catch:{ RemoteException -> 0x005a }
        r2 = r0 instanceof com.google.android.gms.internal.zzany;	 Catch:{ RemoteException -> 0x005a }
        if (r2 == 0) goto L_0x0054;
    L_0x0051:
        r0 = (com.google.android.gms.internal.zzany) r0;	 Catch:{ RemoteException -> 0x005a }
        goto L_0x0024;
    L_0x0054:
        r0 = new com.google.android.gms.internal.zzanz;	 Catch:{ RemoteException -> 0x005a }
        r0.<init>(r6);	 Catch:{ RemoteException -> 0x005a }
        goto L_0x0024;
    L_0x005a:
        r0 = move-exception;
        r0 = r1;
    L_0x005c:
        r1 = r4.zzagk;	 Catch:{ all -> 0x0064 }
        r2 = "Service connect failed to get IAnalyticsService";
        r1.zzbs(r2);	 Catch:{ all -> 0x0064 }
        goto L_0x002b;
    L_0x0064:
        r0 = move-exception;
        r4.notifyAll();	 Catch:{ all -> 0x0044 }
        throw r0;	 Catch:{ all -> 0x0044 }
    L_0x0069:
        r2 = r4.zzagk;	 Catch:{ RemoteException -> 0x005a }
        r3 = "Got binder with a wrong descriptor";
        r2.zze(r3, r0);	 Catch:{ RemoteException -> 0x005a }
        r0 = r1;
        goto L_0x002b;
    L_0x0072:
        r1 = r4.zzagm;	 Catch:{ all -> 0x0064 }
        if (r1 != 0) goto L_0x008c;
    L_0x0076:
        r1 = r4.zzagk;	 Catch:{ all -> 0x0064 }
        r2 = "onServiceConnected received after the timeout limit";
        r1.zzbr(r2);	 Catch:{ all -> 0x0064 }
        r1 = r4.zzagk;	 Catch:{ all -> 0x0064 }
        r1 = r1.zzkt();	 Catch:{ all -> 0x0064 }
        r2 = new com.google.android.gms.internal.zzamq;	 Catch:{ all -> 0x0064 }
        r2.<init>(r4, r0);	 Catch:{ all -> 0x0064 }
        r1.zzf(r2);	 Catch:{ all -> 0x0064 }
        goto L_0x003f;
    L_0x008c:
        r4.zzagl = r0;	 Catch:{ all -> 0x0064 }
        goto L_0x003f;
    L_0x008f:
        r0 = move-exception;
        goto L_0x003f;
    L_0x0091:
        r1 = move-exception;
        goto L_0x005c;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.zzamp.onServiceConnected(android.content.ComponentName, android.os.IBinder):void");
    }

    public final void onServiceDisconnected(ComponentName componentName) {
        zzbo.zzcz("AnalyticsServiceConnection.onServiceDisconnected");
        this.zzagk.zzkt().zzf(new zzamr(this, componentName));
    }

    public final zzany zzkR() {
        zzany zzany = null;
        zzl.zzjC();
        Intent intent = new Intent("com.google.android.gms.analytics.service.START");
        intent.setComponent(new ComponentName("com.google.android.gms", "com.google.android.gms.analytics.service.AnalyticsService"));
        Context context = this.zzagk.getContext();
        intent.putExtra("app_package_name", context.getPackageName());
        zza.zzrU();
        synchronized (this) {
            this.zzagl = null;
            this.zzagm = true;
            boolean zza = zza.zza(context, intent, this.zzagk.zzagg, 129);
            this.zzagk.zza("Bind to service requested", Boolean.valueOf(zza));
            if (zza) {
                try {
                    wait(((Long) zzans.zzahP.get()).longValue());
                } catch (InterruptedException e) {
                    this.zzagk.zzbr("Wait for service connect was interrupted");
                }
                this.zzagm = false;
                zzany = this.zzagl;
                this.zzagl = null;
                if (zzany == null) {
                    this.zzagk.zzbs("Successfully bound to service but never got onServiceConnected callback");
                }
            } else {
                this.zzagm = false;
            }
        }
        return zzany;
    }
}
