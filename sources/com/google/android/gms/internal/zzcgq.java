package com.google.android.gms.internal;

import com.google.android.gms.common.internal.zzbo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

public final class zzcgq extends zzcfe {
    private final zzcgl zzboe;
    private Boolean zzbtc;
    private String zzbtd;

    public zzcgq(zzcgl zzcgl) {
        this(zzcgl, (byte) 0);
    }

    private zzcgq(zzcgl zzcgl, byte b) {
        zzbo.zzu(zzcgl);
        this.zzboe = zzcgl;
        this.zzbtd = null;
    }

    private final void zzb$5399953a(zzceh zzceh) {
        zzbo.zzu(zzceh);
        zzh(zzceh.packageName, false);
        this.zzboe.zzwB().zzev(zzceh.zzboQ);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final void zzh(java.lang.String r5, boolean r6) {
        /*
        r4 = this;
        r1 = 1;
        r0 = 0;
        r2 = android.text.TextUtils.isEmpty(r5);
        if (r2 == 0) goto L_0x001f;
    L_0x0008:
        r0 = r4.zzboe;
        r0 = r0.zzwF();
        r0 = r0.zzyx();
        r1 = "Measurement Service called without app package";
        r0.log(r1);
        r0 = new java.lang.SecurityException;
        r1 = "Measurement Service called without app package";
        r0.<init>(r1);
        throw r0;
    L_0x001f:
        if (r6 == 0) goto L_0x006b;
    L_0x0021:
        r2 = r4.zzbtc;	 Catch:{ SecurityException -> 0x009b }
        if (r2 != 0) goto L_0x0063;
    L_0x0025:
        r2 = "com.google.android.gms";
        r3 = r4.zzbtd;	 Catch:{ SecurityException -> 0x009b }
        r2 = r2.equals(r3);	 Catch:{ SecurityException -> 0x009b }
        if (r2 != 0) goto L_0x005c;
    L_0x002f:
        r2 = r4.zzboe;	 Catch:{ SecurityException -> 0x009b }
        r2 = r2.getContext();	 Catch:{ SecurityException -> 0x009b }
        r3 = android.os.Binder.getCallingUid();	 Catch:{ SecurityException -> 0x009b }
        r2 = com.google.android.gms.common.util.zzw.zzf(r2, r3);	 Catch:{ SecurityException -> 0x009b }
        if (r2 != 0) goto L_0x005c;
    L_0x003f:
        r2 = r4.zzboe;	 Catch:{ SecurityException -> 0x009b }
        r2 = r2.getContext();	 Catch:{ SecurityException -> 0x009b }
        r2 = com.google.android.gms.common.zzp.zzax(r2);	 Catch:{ SecurityException -> 0x009b }
        r3 = r4.zzboe;	 Catch:{ SecurityException -> 0x009b }
        r3 = r3.getContext();	 Catch:{ SecurityException -> 0x009b }
        r3.getPackageManager();	 Catch:{ SecurityException -> 0x009b }
        r3 = android.os.Binder.getCallingUid();	 Catch:{ SecurityException -> 0x009b }
        r2 = r2.zza$1285f85e(r3);	 Catch:{ SecurityException -> 0x009b }
        if (r2 == 0) goto L_0x005d;
    L_0x005c:
        r0 = r1;
    L_0x005d:
        r0 = java.lang.Boolean.valueOf(r0);	 Catch:{ SecurityException -> 0x009b }
        r4.zzbtc = r0;	 Catch:{ SecurityException -> 0x009b }
    L_0x0063:
        r0 = r4.zzbtc;	 Catch:{ SecurityException -> 0x009b }
        r0 = r0.booleanValue();	 Catch:{ SecurityException -> 0x009b }
        if (r0 != 0) goto L_0x00b0;
    L_0x006b:
        r0 = r4.zzbtd;	 Catch:{ SecurityException -> 0x009b }
        if (r0 != 0) goto L_0x0081;
    L_0x006f:
        r0 = r4.zzboe;	 Catch:{ SecurityException -> 0x009b }
        r0 = r0.getContext();	 Catch:{ SecurityException -> 0x009b }
        r1 = android.os.Binder.getCallingUid();	 Catch:{ SecurityException -> 0x009b }
        r0 = com.google.android.gms.common.zzo.zzb(r0, r1, r5);	 Catch:{ SecurityException -> 0x009b }
        if (r0 == 0) goto L_0x0081;
    L_0x007f:
        r4.zzbtd = r5;	 Catch:{ SecurityException -> 0x009b }
    L_0x0081:
        r0 = r4.zzbtd;	 Catch:{ SecurityException -> 0x009b }
        r0 = r5.equals(r0);	 Catch:{ SecurityException -> 0x009b }
        if (r0 != 0) goto L_0x00b0;
    L_0x0089:
        r0 = new java.lang.SecurityException;	 Catch:{ SecurityException -> 0x009b }
        r1 = "Unknown calling package name '%s'.";
        r2 = 1;
        r2 = new java.lang.Object[r2];	 Catch:{ SecurityException -> 0x009b }
        r3 = 0;
        r2[r3] = r5;	 Catch:{ SecurityException -> 0x009b }
        r1 = java.lang.String.format(r1, r2);	 Catch:{ SecurityException -> 0x009b }
        r0.<init>(r1);	 Catch:{ SecurityException -> 0x009b }
        throw r0;	 Catch:{ SecurityException -> 0x009b }
    L_0x009b:
        r0 = move-exception;
        r1 = r4.zzboe;
        r1 = r1.zzwF();
        r1 = r1.zzyx();
        r2 = "Measurement Service called with invalid calling package. appId";
        r3 = com.google.android.gms.internal.zzcfl.zzdZ(r5);
        r1.zzj(r2, r3);
        throw r0;
    L_0x00b0:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.zzcgq.zzh(java.lang.String, boolean):void");
    }

    public final List<zzcji> zza(zzceh zzceh, boolean z) {
        Object e;
        zzb$5399953a(zzceh);
        try {
            List<zzcjk> list = (List) this.zzboe.zzwE().zze(new zzchf(this, zzceh)).get();
            List<zzcji> arrayList = new ArrayList(list.size());
            for (zzcjk zzcjk : list) {
                if (z || !zzcjl.zzex(zzcjk.mName)) {
                    arrayList.add(new zzcji(zzcjk));
                }
            }
            return arrayList;
        } catch (InterruptedException e2) {
            e = e2;
            this.zzboe.zzwF().zzyx().zze("Failed to get user attributes. appId", zzcfl.zzdZ(zzceh.packageName), e);
            return null;
        } catch (ExecutionException e3) {
            e = e3;
            this.zzboe.zzwF().zzyx().zze("Failed to get user attributes. appId", zzcfl.zzdZ(zzceh.packageName), e);
            return null;
        }
    }

    public final List<zzcek> zza(String str, String str2, zzceh zzceh) {
        Object e;
        zzb$5399953a(zzceh);
        try {
            return (List) this.zzboe.zzwE().zze(new zzcgy(this, zzceh, str, str2)).get();
        } catch (InterruptedException e2) {
            e = e2;
        } catch (ExecutionException e3) {
            e = e3;
        }
        this.zzboe.zzwF().zzyx().zzj("Failed to get conditional user properties", e);
        return Collections.emptyList();
    }

    public final List<zzcji> zza(String str, String str2, String str3, boolean z) {
        Object e;
        zzh(str, true);
        try {
            List<zzcjk> list = (List) this.zzboe.zzwE().zze(new zzcgx(this, str, str2, str3)).get();
            List<zzcji> arrayList = new ArrayList(list.size());
            for (zzcjk zzcjk : list) {
                if (z || !zzcjl.zzex(zzcjk.mName)) {
                    arrayList.add(new zzcji(zzcjk));
                }
            }
            return arrayList;
        } catch (InterruptedException e2) {
            e = e2;
            this.zzboe.zzwF().zzyx().zze("Failed to get user attributes. appId", zzcfl.zzdZ(str), e);
            return Collections.emptyList();
        } catch (ExecutionException e3) {
            e = e3;
            this.zzboe.zzwF().zzyx().zze("Failed to get user attributes. appId", zzcfl.zzdZ(str), e);
            return Collections.emptyList();
        }
    }

    public final List<zzcji> zza(String str, String str2, boolean z, zzceh zzceh) {
        Object e;
        zzb$5399953a(zzceh);
        try {
            List<zzcjk> list = (List) this.zzboe.zzwE().zze(new zzcgw(this, zzceh, str, str2)).get();
            List<zzcji> arrayList = new ArrayList(list.size());
            for (zzcjk zzcjk : list) {
                if (z || !zzcjl.zzex(zzcjk.mName)) {
                    arrayList.add(new zzcji(zzcjk));
                }
            }
            return arrayList;
        } catch (InterruptedException e2) {
            e = e2;
            this.zzboe.zzwF().zzyx().zze("Failed to get user attributes. appId", zzcfl.zzdZ(zzceh.packageName), e);
            return Collections.emptyList();
        } catch (ExecutionException e3) {
            e = e3;
            this.zzboe.zzwF().zzyx().zze("Failed to get user attributes. appId", zzcfl.zzdZ(zzceh.packageName), e);
            return Collections.emptyList();
        }
    }

    public final void zza(long j, String str, String str2, String str3) {
        this.zzboe.zzwE().zzj(new zzchh(this, str2, str3, str, j));
    }

    public final void zza(zzceh zzceh) {
        zzb$5399953a(zzceh);
        Runnable zzchg = new zzchg(this, zzceh);
        if (this.zzboe.zzwE().zzyM()) {
            zzchg.run();
        } else {
            this.zzboe.zzwE().zzj(zzchg);
        }
    }

    public final void zza(zzcek zzcek, zzceh zzceh) {
        zzbo.zzu(zzcek);
        zzbo.zzu(zzcek.zzbpd);
        zzb$5399953a(zzceh);
        zzcek zzcek2 = new zzcek(zzcek);
        zzcek2.packageName = zzceh.packageName;
        if (zzcek.zzbpd.getValue() == null) {
            this.zzboe.zzwE().zzj(new zzcgs(this, zzcek2, zzceh));
        } else {
            this.zzboe.zzwE().zzj(new zzcgt(this, zzcek2, zzceh));
        }
    }

    public final void zza(zzcez zzcez, zzceh zzceh) {
        zzbo.zzu(zzcez);
        zzb$5399953a(zzceh);
        this.zzboe.zzwE().zzj(new zzcha(this, zzcez, zzceh));
    }

    public final void zza(zzcez zzcez, String str, String str2) {
        zzbo.zzu(zzcez);
        zzbo.zzcF(str);
        zzh(str, true);
        this.zzboe.zzwE().zzj(new zzchb(this, zzcez, str));
    }

    public final void zza(zzcji zzcji, zzceh zzceh) {
        zzbo.zzu(zzcji);
        zzb$5399953a(zzceh);
        if (zzcji.getValue() == null) {
            this.zzboe.zzwE().zzj(new zzchd(this, zzcji, zzceh));
        } else {
            this.zzboe.zzwE().zzj(new zzche(this, zzcji, zzceh));
        }
    }

    public final byte[] zza(zzcez zzcez, String str) {
        Object e;
        zzbo.zzcF(str);
        zzbo.zzu(zzcez);
        zzh(str, true);
        this.zzboe.zzwF().zzyC().zzj("Log and bundle. event", this.zzboe.zzwA().zzdW(zzcez.name));
        long nanoTime = this.zzboe.zzkq().nanoTime() / 1000000;
        try {
            byte[] bArr = (byte[]) this.zzboe.zzwE().zzf(new zzchc(this, zzcez, str)).get();
            if (bArr == null) {
                this.zzboe.zzwF().zzyx().zzj("Log and bundle returned null. appId", zzcfl.zzdZ(str));
                bArr = new byte[0];
            }
            this.zzboe.zzwF().zzyC().zzd("Log and bundle processed. event, size, time_ms", this.zzboe.zzwA().zzdW(zzcez.name), Integer.valueOf(bArr.length), Long.valueOf((this.zzboe.zzkq().nanoTime() / 1000000) - nanoTime));
            return bArr;
        } catch (InterruptedException e2) {
            e = e2;
            this.zzboe.zzwF().zzyx().zzd("Failed to log and bundle. appId, event, error", zzcfl.zzdZ(str), this.zzboe.zzwA().zzdW(zzcez.name), e);
            return null;
        } catch (ExecutionException e3) {
            e = e3;
            this.zzboe.zzwF().zzyx().zzd("Failed to log and bundle. appId, event, error", zzcfl.zzdZ(str), this.zzboe.zzwA().zzdW(zzcez.name), e);
            return null;
        }
    }

    public final void zzb(zzceh zzceh) {
        zzb$5399953a(zzceh);
        this.zzboe.zzwE().zzj(new zzcgr(this, zzceh));
    }

    public final void zzb(zzcek zzcek) {
        zzbo.zzu(zzcek);
        zzbo.zzu(zzcek.zzbpd);
        zzh(zzcek.packageName, true);
        zzcek zzcek2 = new zzcek(zzcek);
        if (zzcek.zzbpd.getValue() == null) {
            this.zzboe.zzwE().zzj(new zzcgu(this, zzcek2));
        } else {
            this.zzboe.zzwE().zzj(new zzcgv(this, zzcek2));
        }
    }

    public final String zzc(zzceh zzceh) {
        zzb$5399953a(zzceh);
        return this.zzboe.zzem(zzceh.packageName);
    }

    public final List<zzcek> zzk(String str, String str2, String str3) {
        Object e;
        zzh(str, true);
        try {
            return (List) this.zzboe.zzwE().zze(new zzcgz(this, str, str2, str3)).get();
        } catch (InterruptedException e2) {
            e = e2;
        } catch (ExecutionException e3) {
            e = e3;
        }
        this.zzboe.zzwF().zzyx().zzj("Failed to get conditional user properties", e);
        return Collections.emptyList();
    }
}
