package com.google.android.gms.internal;

import android.content.ComponentName;
import android.content.Context;
import android.os.RemoteException;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.stats.zza;
import com.google.android.gms.common.util.zze;
import com.google.android.gms.measurement.AppMeasurement.zzb;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public final class zzcid extends zzchj {
    private final zzciq zzbtT;
    private zzcfd zzbtU;
    private Boolean zzbtV;
    private final zzcer zzbtW;
    private final zzcjf zzbtX;
    private final List<Runnable> zzbtY = new ArrayList();
    private final zzcer zzbtZ;

    protected zzcid(zzcgl zzcgl) {
        super(zzcgl);
        this.zzbtX = new zzcjf(zzcgl.zzkq());
        this.zzbtT = new zzciq(this);
        this.zzbtW = new zzcie(this, zzcgl);
        this.zzbtZ = new zzcii(this, zzcgl);
    }

    private final void zzkP() {
        super.zzjC();
        this.zzbtX.start();
        this.zzbtW.zzs(zzcem.zzxB());
    }

    private final void zzm(Runnable runnable) throws IllegalStateException {
        super.zzjC();
        if (isConnected()) {
            runnable.run();
        } else if (((long) this.zzbtY.size()) >= zzcem.zzxJ()) {
            super.zzwF().zzyx().log("Discarding data. Max runnable queue size reached");
        } else {
            this.zzbtY.add(runnable);
            this.zzbtZ.zzs(60000);
            zzla();
        }
    }

    private final void zzzl() {
        super.zzjC();
        super.zzwF().zzyD().zzj("Processing queued up service tasks", Integer.valueOf(this.zzbtY.size()));
        for (Runnable run : this.zzbtY) {
            try {
                run.run();
            } catch (Throwable th) {
                super.zzwF().zzyx().zzj("Task exception while flushing queue", th);
            }
        }
        this.zzbtY.clear();
        this.zzbtZ.cancel();
    }

    public final void disconnect() {
        super.zzjC();
        zzkD();
        try {
            zza.zzrU();
            super.getContext().unbindService(this.zzbtT);
        } catch (IllegalStateException e) {
        } catch (IllegalArgumentException e2) {
        }
        this.zzbtU = null;
    }

    public final /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    public final boolean isConnected() {
        super.zzjC();
        zzkD();
        return this.zzbtU != null;
    }

    protected final void zza(zzcfd zzcfd) {
        super.zzjC();
        zzbo.zzu(zzcfd);
        this.zzbtU = zzcfd;
        zzkP();
        zzzl();
    }

    final void zza(zzcfd zzcfd, com.google.android.gms.common.internal.safeparcel.zza zza) {
        super.zzjC();
        super.zzwp();
        zzkD();
        zzcem.zzxE();
        List arrayList = new ArrayList();
        zzcem.zzxN();
        int i = 100;
        for (int i2 = 0; i2 < 1001 && r5 == 100; i2++) {
            Object zzbp$22f3aa59 = super.zzwy().zzbp$22f3aa59();
            if (zzbp$22f3aa59 != null) {
                arrayList.addAll(zzbp$22f3aa59);
                i = zzbp$22f3aa59.size();
            } else {
                i = 0;
            }
            if (zza != null && r5 < 100) {
                arrayList.add(zza);
            }
            ArrayList arrayList2 = (ArrayList) arrayList;
            int size = arrayList2.size();
            int i3 = 0;
            while (i3 < size) {
                Object obj = arrayList2.get(i3);
                i3++;
                com.google.android.gms.common.internal.safeparcel.zza zza2 = (com.google.android.gms.common.internal.safeparcel.zza) obj;
                if (zza2 instanceof zzcez) {
                    try {
                        zzcfd.zza((zzcez) zza2, super.zzwu().zzdV(super.zzwF().zzyE()));
                    } catch (RemoteException e) {
                        super.zzwF().zzyx().zzj("Failed to send event to the service", e);
                    }
                } else if (zza2 instanceof zzcji) {
                    try {
                        zzcfd.zza((zzcji) zza2, super.zzwu().zzdV(super.zzwF().zzyE()));
                    } catch (RemoteException e2) {
                        super.zzwF().zzyx().zzj("Failed to send attribute to the service", e2);
                    }
                } else if (zza2 instanceof zzcek) {
                    try {
                        zzcfd.zza((zzcek) zza2, super.zzwu().zzdV(super.zzwF().zzyE()));
                    } catch (RemoteException e22) {
                        super.zzwF().zzyx().zzj("Failed to send conditional property to the service", e22);
                    }
                } else {
                    super.zzwF().zzyx().log("Discarding data. Unrecognized parcel type.");
                }
            }
        }
    }

    protected final void zza(zzb zzb) {
        super.zzjC();
        zzkD();
        zzm(new zzcih(this, zzb));
    }

    public final void zza(AtomicReference<String> atomicReference) {
        super.zzjC();
        zzkD();
        zzm(new zzcif(this, atomicReference));
    }

    protected final void zza(AtomicReference<List<zzcek>> atomicReference, String str, String str2, String str3) {
        super.zzjC();
        zzkD();
        zzm(new zzcim(this, atomicReference, str, str2, str3));
    }

    protected final void zza(AtomicReference<List<zzcji>> atomicReference, String str, String str2, String str3, boolean z) {
        super.zzjC();
        zzkD();
        zzm(new zzcin(this, atomicReference, str, str2, str3, z));
    }

    protected final void zzb(zzcji zzcji) {
        super.zzjC();
        zzkD();
        zzcem.zzxE();
        zzm(new zzcio(this, super.zzwy().zza(zzcji), zzcji));
    }

    protected final void zzc(zzcez zzcez, String str) {
        zzbo.zzu(zzcez);
        super.zzjC();
        zzkD();
        zzcem.zzxE();
        zzm(new zzcik(this, super.zzwy().zza(zzcez), zzcez, str));
    }

    protected final void zzf(zzcek zzcek) {
        zzbo.zzu(zzcek);
        super.zzjC();
        zzkD();
        zzcem.zzxE();
        zzm(new zzcil(this, super.zzwy().zzc(zzcek), new zzcek(zzcek), zzcek));
    }

    public final /* bridge */ /* synthetic */ void zzjC() {
        super.zzjC();
    }

    protected final void zzjD() {
    }

    public final /* bridge */ /* synthetic */ zze zzkq() {
        return super.zzkq();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    final void zzla() {
        /*
        r6 = this;
        r2 = 0;
        r1 = 1;
        super.zzjC();
        r6.zzkD();
        r0 = r6.isConnected();
        if (r0 == 0) goto L_0x000f;
    L_0x000e:
        return;
    L_0x000f:
        r0 = r6.zzbtV;
        if (r0 != 0) goto L_0x0067;
    L_0x0013:
        r0 = super.zzwG();
        r0 = r0.zzyI();
        r6.zzbtV = r0;
        r0 = r6.zzbtV;
        if (r0 != 0) goto L_0x0067;
    L_0x0021:
        r0 = super.zzwF();
        r0 = r0.zzyD();
        r3 = "State of service unknown";
        r0.log(r3);
        super.zzjC();
        r6.zzkD();
        com.google.android.gms.internal.zzcem.zzxE();
        r0 = super.zzwF();
        r0 = r0.zzyD();
        r3 = "Checking service availability";
        r0.log(r3);
        r0 = com.google.android.gms.common.zze.zzoW();
        r3 = super.getContext();
        r0 = r0.isGooglePlayServicesAvailable(r3);
        switch(r0) {
            case 0: goto L_0x0082;
            case 1: goto L_0x0091;
            case 2: goto L_0x00ae;
            case 3: goto L_0x00bd;
            case 9: goto L_0x00cb;
            case 18: goto L_0x009f;
            default: goto L_0x0053;
        };
    L_0x0053:
        r0 = r2;
    L_0x0054:
        r0 = java.lang.Boolean.valueOf(r0);
        r6.zzbtV = r0;
        r0 = super.zzwG();
        r3 = r6.zzbtV;
        r3 = r3.booleanValue();
        r0.zzak(r3);
    L_0x0067:
        r0 = r6.zzbtV;
        r0 = r0.booleanValue();
        if (r0 == 0) goto L_0x00da;
    L_0x006f:
        r0 = super.zzwF();
        r0 = r0.zzyD();
        r1 = "Using measurement service";
        r0.log(r1);
        r0 = r6.zzbtT;
        r0.zzzm();
        goto L_0x000e;
    L_0x0082:
        r0 = super.zzwF();
        r0 = r0.zzyD();
        r3 = "Service available";
        r0.log(r3);
        r0 = r1;
        goto L_0x0054;
    L_0x0091:
        r0 = super.zzwF();
        r0 = r0.zzyD();
        r3 = "Service missing";
        r0.log(r3);
        goto L_0x0053;
    L_0x009f:
        r0 = super.zzwF();
        r0 = r0.zzyz();
        r3 = "Service updating";
        r0.log(r3);
        r0 = r1;
        goto L_0x0054;
    L_0x00ae:
        r0 = super.zzwF();
        r0 = r0.zzyC();
        r3 = "Service container out of date";
        r0.log(r3);
        r0 = r1;
        goto L_0x0054;
    L_0x00bd:
        r0 = super.zzwF();
        r0 = r0.zzyz();
        r3 = "Service disabled";
        r0.log(r3);
        goto L_0x0053;
    L_0x00cb:
        r0 = super.zzwF();
        r0 = r0.zzyz();
        r3 = "Service invalid";
        r0.log(r3);
        goto L_0x0053;
    L_0x00da:
        com.google.android.gms.internal.zzcem.zzxE();
        r0 = super.getContext();
        r0 = r0.getPackageManager();
        r3 = new android.content.Intent;
        r3.<init>();
        r4 = super.getContext();
        r5 = "com.google.android.gms.measurement.AppMeasurementService";
        r3 = r3.setClassName(r4, r5);
        r4 = 65536; // 0x10000 float:9.18355E-41 double:3.2379E-319;
        r0 = r0.queryIntentServices(r3, r4);
        if (r0 == 0) goto L_0x0130;
    L_0x00fc:
        r0 = r0.size();
        if (r0 <= 0) goto L_0x0130;
    L_0x0102:
        if (r1 == 0) goto L_0x0132;
    L_0x0104:
        r0 = super.zzwF();
        r0 = r0.zzyD();
        r1 = "Using local app measurement service";
        r0.log(r1);
        r0 = new android.content.Intent;
        r1 = "com.google.android.gms.measurement.START";
        r0.<init>(r1);
        r1 = new android.content.ComponentName;
        r2 = super.getContext();
        com.google.android.gms.internal.zzcem.zzxE();
        r3 = "com.google.android.gms.measurement.AppMeasurementService";
        r1.<init>(r2, r3);
        r0.setComponent(r1);
        r1 = r6.zzbtT;
        r1.zzk(r0);
        goto L_0x000e;
    L_0x0130:
        r1 = r2;
        goto L_0x0102;
    L_0x0132:
        r0 = super.zzwF();
        r0 = r0.zzyx();
        r1 = "Unable to use remote or local measurement implementation. Please register the AppMeasurementService service in the app manifest";
        r0.log(r1);
        goto L_0x000e;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.zzcid.zzla():void");
    }

    public final /* bridge */ /* synthetic */ zzcfj zzwA() {
        return super.zzwA();
    }

    public final /* bridge */ /* synthetic */ zzcjl zzwB() {
        return super.zzwB();
    }

    public final /* bridge */ /* synthetic */ zzcgf zzwC() {
        return super.zzwC();
    }

    public final /* bridge */ /* synthetic */ zzcja zzwD() {
        return super.zzwD();
    }

    public final /* bridge */ /* synthetic */ zzcgg zzwE() {
        return super.zzwE();
    }

    public final /* bridge */ /* synthetic */ zzcfl zzwF() {
        return super.zzwF();
    }

    public final /* bridge */ /* synthetic */ zzcfw zzwG() {
        return super.zzwG();
    }

    public final /* bridge */ /* synthetic */ zzcem zzwH() {
        return super.zzwH();
    }

    public final /* bridge */ /* synthetic */ void zzwo() {
        super.zzwo();
    }

    public final /* bridge */ /* synthetic */ void zzwp() {
        super.zzwp();
    }

    public final /* bridge */ /* synthetic */ void zzwq() {
        super.zzwq();
    }

    public final /* bridge */ /* synthetic */ zzcec zzwr() {
        return super.zzwr();
    }

    public final /* bridge */ /* synthetic */ zzcej zzws() {
        return super.zzws();
    }

    public final /* bridge */ /* synthetic */ zzchl zzwt() {
        return super.zzwt();
    }

    public final /* bridge */ /* synthetic */ zzcfg zzwu() {
        return super.zzwu();
    }

    public final /* bridge */ /* synthetic */ zzcet zzwv() {
        return super.zzwv();
    }

    public final /* bridge */ /* synthetic */ zzcid zzww() {
        return super.zzww();
    }

    public final /* bridge */ /* synthetic */ zzchz zzwx() {
        return super.zzwx();
    }

    public final /* bridge */ /* synthetic */ zzcfh zzwy() {
        return super.zzwy();
    }

    public final /* bridge */ /* synthetic */ zzcen zzwz() {
        return super.zzwz();
    }

    protected final void zzzk() {
        super.zzjC();
        zzkD();
        zzm(new zzcig(this));
    }

    static /* synthetic */ void zza(zzcid zzcid, ComponentName componentName) {
        super.zzjC();
        if (zzcid.zzbtU != null) {
            zzcid.zzbtU = null;
            super.zzwF().zzyD().zzj("Disconnected from device MeasurementService", componentName);
            super.zzjC();
            zzcid.zzla();
        }
    }

    static /* synthetic */ void zzc(zzcid zzcid) {
        super.zzjC();
        if (zzcid.isConnected()) {
            super.zzwF().zzyD().log("Inactivity, disconnecting from the service");
            zzcid.disconnect();
        }
    }
}
