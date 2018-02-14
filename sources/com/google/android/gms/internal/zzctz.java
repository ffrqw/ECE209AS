package com.google.android.gms.internal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.WorkSource;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.util.zzt;
import com.google.android.gms.common.util.zzx;

public final class zzctz {
    private static boolean DEBUG = false;
    private static String TAG = "WakeLock";
    private static String zzbCW = "*gcore*:";
    private final Context mContext;
    private final String zzaJp;
    private final String zzaJr;
    private final WakeLock zzbCX;
    private WorkSource zzbCY;
    private final int zzbCZ;
    private final String zzbDa;
    private boolean zzbDb;
    private int zzbDc;
    private int zzbDd;

    @SuppressLint({"UnwrappedWakeLock"})
    private zzctz(Context context, int i, String str, String str2) {
        this.zzbDb = true;
        zzbo.zzh(str, "Wake lock name can NOT be empty");
        this.zzbCZ = 1;
        this.zzbDa = null;
        this.zzaJr = null;
        this.mContext = context.getApplicationContext();
        if ("com.google.android.gms".equals(context.getPackageName())) {
            this.zzaJp = str;
        } else {
            String valueOf = String.valueOf(zzbCW);
            String valueOf2 = String.valueOf(str);
            this.zzaJp = valueOf2.length() != 0 ? valueOf.concat(valueOf2) : new String(valueOf);
        }
        this.zzbCX = ((PowerManager) context.getSystemService("power")).newWakeLock(1, str);
        if (zzx.zzaM(this.mContext)) {
            if (zzt.zzcL(str2)) {
                str2 = context.getPackageName();
            }
            this.zzbCY = zzx.zzD(context, str2);
            WorkSource workSource = this.zzbCY;
            if (workSource != null && zzx.zzaM(this.mContext)) {
                if (this.zzbCY != null) {
                    this.zzbCY.add(workSource);
                } else {
                    this.zzbCY = workSource;
                }
                try {
                    this.zzbCX.setWorkSource(this.zzbCY);
                } catch (IllegalArgumentException e) {
                    Log.wtf(TAG, e.toString());
                }
            }
        }
    }

    public zzctz(Context context, String str) {
        this(context, str, context == null ? null : context.getPackageName());
    }

    @SuppressLint({"UnwrappedWakeLock"})
    private zzctz(Context context, String str, String str2) {
        this(context, 1, str, str2);
    }

    private final boolean zzeV$552c4dfd() {
        Object obj = null;
        return (TextUtils.isEmpty(obj) || obj.equals(obj)) ? false : true;
    }

    private final String zzi$185c6b75(boolean z) {
        return (!this.zzbDb || z) ? null : null;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void acquire$1349ef() {
        /*
        r12 = this;
        r10 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r0 = r12.zzeV$552c4dfd();
        r4 = r12.zzi$185c6b75(r0);
        monitor-enter(r12);
        r1 = r12.zzbDb;	 Catch:{ all -> 0x004a }
        if (r1 == 0) goto L_0x0019;
    L_0x000f:
        r1 = r12.zzbDc;	 Catch:{ all -> 0x004a }
        r2 = r1 + 1;
        r12.zzbDc = r2;	 Catch:{ all -> 0x004a }
        if (r1 == 0) goto L_0x0021;
    L_0x0017:
        if (r0 != 0) goto L_0x0021;
    L_0x0019:
        r0 = r12.zzbDb;	 Catch:{ all -> 0x004a }
        if (r0 != 0) goto L_0x0043;
    L_0x001d:
        r0 = r12.zzbDd;	 Catch:{ all -> 0x004a }
        if (r0 != 0) goto L_0x0043;
    L_0x0021:
        com.google.android.gms.common.stats.zze.zzrX();	 Catch:{ all -> 0x004a }
        r0 = r12.mContext;	 Catch:{ all -> 0x004a }
        r1 = r12.zzbCX;	 Catch:{ all -> 0x004a }
        r1 = com.google.android.gms.common.stats.zzc.zza(r1, r4);	 Catch:{ all -> 0x004a }
        r2 = 7;
        r3 = r12.zzaJp;	 Catch:{ all -> 0x004a }
        r5 = 0;
        r6 = r12.zzbCZ;	 Catch:{ all -> 0x004a }
        r7 = r12.zzbCY;	 Catch:{ all -> 0x004a }
        r7 = com.google.android.gms.common.util.zzx.zzb(r7);	 Catch:{ all -> 0x004a }
        r8 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        com.google.android.gms.common.stats.zze.zza(r0, r1, r2, r3, r4, r5, r6, r7, r8);	 Catch:{ all -> 0x004a }
        r0 = r12.zzbDd;	 Catch:{ all -> 0x004a }
        r0 = r0 + 1;
        r12.zzbDd = r0;	 Catch:{ all -> 0x004a }
    L_0x0043:
        monitor-exit(r12);	 Catch:{ all -> 0x004a }
        r0 = r12.zzbCX;
        r0.acquire(r10);
        return;
    L_0x004a:
        r0 = move-exception;
        monitor-exit(r12);	 Catch:{ all -> 0x004a }
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.zzctz.acquire$1349ef():void");
    }

    public final boolean isHeld() {
        return this.zzbCX.isHeld();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void release() {
        /*
        r7 = this;
        r0 = r7.zzeV$552c4dfd();
        r3 = r7.zzi$185c6b75(r0);
        monitor-enter(r7);
        r1 = r7.zzbDb;	 Catch:{ all -> 0x0046 }
        if (r1 == 0) goto L_0x0017;
    L_0x000d:
        r1 = r7.zzbDc;	 Catch:{ all -> 0x0046 }
        r1 = r1 + -1;
        r7.zzbDc = r1;	 Catch:{ all -> 0x0046 }
        if (r1 == 0) goto L_0x0020;
    L_0x0015:
        if (r0 != 0) goto L_0x0020;
    L_0x0017:
        r0 = r7.zzbDb;	 Catch:{ all -> 0x0046 }
        if (r0 != 0) goto L_0x003f;
    L_0x001b:
        r0 = r7.zzbDd;	 Catch:{ all -> 0x0046 }
        r1 = 1;
        if (r0 != r1) goto L_0x003f;
    L_0x0020:
        com.google.android.gms.common.stats.zze.zzrX();	 Catch:{ all -> 0x0046 }
        r0 = r7.mContext;	 Catch:{ all -> 0x0046 }
        r1 = r7.zzbCX;	 Catch:{ all -> 0x0046 }
        r1 = com.google.android.gms.common.stats.zzc.zza(r1, r3);	 Catch:{ all -> 0x0046 }
        r2 = r7.zzaJp;	 Catch:{ all -> 0x0046 }
        r4 = 0;
        r5 = r7.zzbCZ;	 Catch:{ all -> 0x0046 }
        r6 = r7.zzbCY;	 Catch:{ all -> 0x0046 }
        r6 = com.google.android.gms.common.util.zzx.zzb(r6);	 Catch:{ all -> 0x0046 }
        com.google.android.gms.common.stats.zze.zza$5d0da770(r0, r1, r2, r3, r4, r5, r6);	 Catch:{ all -> 0x0046 }
        r0 = r7.zzbDd;	 Catch:{ all -> 0x0046 }
        r0 = r0 + -1;
        r7.zzbDd = r0;	 Catch:{ all -> 0x0046 }
    L_0x003f:
        monitor-exit(r7);	 Catch:{ all -> 0x0046 }
        r0 = r7.zzbCX;
        r0.release();
        return;
    L_0x0046:
        r0 = move-exception;
        monitor-exit(r7);	 Catch:{ all -> 0x0046 }
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.zzctz.release():void");
    }

    public final void setReferenceCounted$1385ff() {
        this.zzbCX.setReferenceCounted(false);
        this.zzbDb = false;
    }
}
