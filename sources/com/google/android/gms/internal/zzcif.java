package com.google.android.gms.internal;

import java.util.concurrent.atomic.AtomicReference;

final class zzcif implements Runnable {
    private /* synthetic */ zzcid zzbua;
    private /* synthetic */ AtomicReference zzbub;

    zzcif(zzcid zzcid, AtomicReference atomicReference) {
        this.zzbua = zzcid;
        this.zzbub = atomicReference;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void run() {
        /*
        r5 = this;
        r1 = r5.zzbub;
        monitor-enter(r1);
        r0 = r5.zzbua;	 Catch:{ RemoteException -> 0x0062 }
        r0 = r0.zzbtU;	 Catch:{ RemoteException -> 0x0062 }
        if (r0 != 0) goto L_0x0021;
    L_0x000b:
        r0 = r5.zzbua;	 Catch:{ RemoteException -> 0x0062 }
        r0 = r0.zzwF();	 Catch:{ RemoteException -> 0x0062 }
        r0 = r0.zzyx();	 Catch:{ RemoteException -> 0x0062 }
        r2 = "Failed to get app instance id";
        r0.log(r2);	 Catch:{ RemoteException -> 0x0062 }
        r0 = r5.zzbub;	 Catch:{ all -> 0x005f }
        r0.notify();	 Catch:{ all -> 0x005f }
        monitor-exit(r1);	 Catch:{ all -> 0x005f }
    L_0x0020:
        return;
    L_0x0021:
        r2 = r5.zzbub;	 Catch:{ RemoteException -> 0x0062 }
        r3 = r5.zzbua;	 Catch:{ RemoteException -> 0x0062 }
        r3 = r3.zzwu();	 Catch:{ RemoteException -> 0x0062 }
        r4 = 0;
        r3 = r3.zzdV(r4);	 Catch:{ RemoteException -> 0x0062 }
        r0 = r0.zzc(r3);	 Catch:{ RemoteException -> 0x0062 }
        r2.set(r0);	 Catch:{ RemoteException -> 0x0062 }
        r0 = r5.zzbub;	 Catch:{ RemoteException -> 0x0062 }
        r0 = r0.get();	 Catch:{ RemoteException -> 0x0062 }
        r0 = (java.lang.String) r0;	 Catch:{ RemoteException -> 0x0062 }
        if (r0 == 0) goto L_0x0053;
    L_0x003f:
        r2 = r5.zzbua;	 Catch:{ RemoteException -> 0x0062 }
        r2 = r2.zzwt();	 Catch:{ RemoteException -> 0x0062 }
        r2.zzee(r0);	 Catch:{ RemoteException -> 0x0062 }
        r2 = r5.zzbua;	 Catch:{ RemoteException -> 0x0062 }
        r2 = r2.zzwG();	 Catch:{ RemoteException -> 0x0062 }
        r2 = r2.zzbrq;	 Catch:{ RemoteException -> 0x0062 }
        r2.zzef(r0);	 Catch:{ RemoteException -> 0x0062 }
    L_0x0053:
        r0 = r5.zzbua;	 Catch:{ RemoteException -> 0x0062 }
        r0.zzkP();	 Catch:{ RemoteException -> 0x0062 }
        r0 = r5.zzbub;	 Catch:{ all -> 0x005f }
        r0.notify();	 Catch:{ all -> 0x005f }
    L_0x005d:
        monitor-exit(r1);	 Catch:{ all -> 0x005f }
        goto L_0x0020;
    L_0x005f:
        r0 = move-exception;
        monitor-exit(r1);	 Catch:{ all -> 0x005f }
        throw r0;
    L_0x0062:
        r0 = move-exception;
        r2 = r5.zzbua;	 Catch:{ all -> 0x0078 }
        r2 = r2.zzwF();	 Catch:{ all -> 0x0078 }
        r2 = r2.zzyx();	 Catch:{ all -> 0x0078 }
        r3 = "Failed to get app instance id";
        r2.zzj(r3, r0);	 Catch:{ all -> 0x0078 }
        r0 = r5.zzbub;	 Catch:{ all -> 0x005f }
        r0.notify();	 Catch:{ all -> 0x005f }
        goto L_0x005d;
    L_0x0078:
        r0 = move-exception;
        r2 = r5.zzbub;	 Catch:{ all -> 0x005f }
        r2.notify();	 Catch:{ all -> 0x005f }
        throw r0;	 Catch:{ all -> 0x005f }
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.zzcif.run():void");
    }
}
