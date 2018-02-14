package com.google.android.gms.internal;

import java.util.concurrent.atomic.AtomicReference;

final class zzcin implements Runnable {
    private /* synthetic */ String zzbjh;
    private /* synthetic */ String zzbth;
    private /* synthetic */ String zzbti;
    private /* synthetic */ boolean zzbtw;
    private /* synthetic */ zzcid zzbua;
    private /* synthetic */ AtomicReference zzbub;

    zzcin(zzcid zzcid, AtomicReference atomicReference, String str, String str2, String str3, boolean z) {
        this.zzbua = zzcid;
        this.zzbub = atomicReference;
        this.zzbjh = str;
        this.zzbth = str2;
        this.zzbti = str3;
        this.zzbtw = z;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void run() {
        /*
        r8 = this;
        r1 = r8.zzbub;
        monitor-enter(r1);
        r0 = r8.zzbua;	 Catch:{ RemoteException -> 0x0080 }
        r0 = r0.zzbtU;	 Catch:{ RemoteException -> 0x0080 }
        if (r0 != 0) goto L_0x0034;
    L_0x000b:
        r0 = r8.zzbua;	 Catch:{ RemoteException -> 0x0080 }
        r0 = r0.zzwF();	 Catch:{ RemoteException -> 0x0080 }
        r0 = r0.zzyx();	 Catch:{ RemoteException -> 0x0080 }
        r2 = "Failed to get user properties";
        r3 = r8.zzbjh;	 Catch:{ RemoteException -> 0x0080 }
        r3 = com.google.android.gms.internal.zzcfl.zzdZ(r3);	 Catch:{ RemoteException -> 0x0080 }
        r4 = r8.zzbth;	 Catch:{ RemoteException -> 0x0080 }
        r5 = r8.zzbti;	 Catch:{ RemoteException -> 0x0080 }
        r0.zzd(r2, r3, r4, r5);	 Catch:{ RemoteException -> 0x0080 }
        r0 = r8.zzbub;	 Catch:{ RemoteException -> 0x0080 }
        r2 = java.util.Collections.emptyList();	 Catch:{ RemoteException -> 0x0080 }
        r0.set(r2);	 Catch:{ RemoteException -> 0x0080 }
        r0 = r8.zzbub;	 Catch:{ all -> 0x006b }
        r0.notify();	 Catch:{ all -> 0x006b }
        monitor-exit(r1);	 Catch:{ all -> 0x006b }
    L_0x0033:
        return;
    L_0x0034:
        r2 = r8.zzbjh;	 Catch:{ RemoteException -> 0x0080 }
        r2 = android.text.TextUtils.isEmpty(r2);	 Catch:{ RemoteException -> 0x0080 }
        if (r2 == 0) goto L_0x006e;
    L_0x003c:
        r2 = r8.zzbub;	 Catch:{ RemoteException -> 0x0080 }
        r3 = r8.zzbth;	 Catch:{ RemoteException -> 0x0080 }
        r4 = r8.zzbti;	 Catch:{ RemoteException -> 0x0080 }
        r5 = r8.zzbtw;	 Catch:{ RemoteException -> 0x0080 }
        r6 = r8.zzbua;	 Catch:{ RemoteException -> 0x0080 }
        r6 = r6.zzwu();	 Catch:{ RemoteException -> 0x0080 }
        r7 = r8.zzbua;	 Catch:{ RemoteException -> 0x0080 }
        r7 = r7.zzwF();	 Catch:{ RemoteException -> 0x0080 }
        r7 = r7.zzyE();	 Catch:{ RemoteException -> 0x0080 }
        r6 = r6.zzdV(r7);	 Catch:{ RemoteException -> 0x0080 }
        r0 = r0.zza(r3, r4, r5, r6);	 Catch:{ RemoteException -> 0x0080 }
        r2.set(r0);	 Catch:{ RemoteException -> 0x0080 }
    L_0x005f:
        r0 = r8.zzbua;	 Catch:{ RemoteException -> 0x0080 }
        r0.zzkP();	 Catch:{ RemoteException -> 0x0080 }
        r0 = r8.zzbub;	 Catch:{ all -> 0x006b }
        r0.notify();	 Catch:{ all -> 0x006b }
    L_0x0069:
        monitor-exit(r1);	 Catch:{ all -> 0x006b }
        goto L_0x0033;
    L_0x006b:
        r0 = move-exception;
        monitor-exit(r1);	 Catch:{ all -> 0x006b }
        throw r0;
    L_0x006e:
        r2 = r8.zzbub;	 Catch:{ RemoteException -> 0x0080 }
        r3 = r8.zzbjh;	 Catch:{ RemoteException -> 0x0080 }
        r4 = r8.zzbth;	 Catch:{ RemoteException -> 0x0080 }
        r5 = r8.zzbti;	 Catch:{ RemoteException -> 0x0080 }
        r6 = r8.zzbtw;	 Catch:{ RemoteException -> 0x0080 }
        r0 = r0.zza(r3, r4, r5, r6);	 Catch:{ RemoteException -> 0x0080 }
        r2.set(r0);	 Catch:{ RemoteException -> 0x0080 }
        goto L_0x005f;
    L_0x0080:
        r0 = move-exception;
        r2 = r8.zzbua;	 Catch:{ all -> 0x00a7 }
        r2 = r2.zzwF();	 Catch:{ all -> 0x00a7 }
        r2 = r2.zzyx();	 Catch:{ all -> 0x00a7 }
        r3 = "Failed to get user properties";
        r4 = r8.zzbjh;	 Catch:{ all -> 0x00a7 }
        r4 = com.google.android.gms.internal.zzcfl.zzdZ(r4);	 Catch:{ all -> 0x00a7 }
        r5 = r8.zzbth;	 Catch:{ all -> 0x00a7 }
        r2.zzd(r3, r4, r5, r0);	 Catch:{ all -> 0x00a7 }
        r0 = r8.zzbub;	 Catch:{ all -> 0x00a7 }
        r2 = java.util.Collections.emptyList();	 Catch:{ all -> 0x00a7 }
        r0.set(r2);	 Catch:{ all -> 0x00a7 }
        r0 = r8.zzbub;	 Catch:{ all -> 0x006b }
        r0.notify();	 Catch:{ all -> 0x006b }
        goto L_0x0069;
    L_0x00a7:
        r0 = move-exception;
        r2 = r8.zzbub;	 Catch:{ all -> 0x006b }
        r2.notify();	 Catch:{ all -> 0x006b }
        throw r0;	 Catch:{ all -> 0x006b }
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.zzcin.run():void");
    }
}
