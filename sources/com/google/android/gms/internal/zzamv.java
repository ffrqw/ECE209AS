package com.google.android.gms.internal;

import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.text.TextUtils;
import android.util.Pair;
import com.google.android.gms.analytics.CampaignTrackingReceiver;
import com.google.android.gms.analytics.CampaignTrackingService;
import com.google.android.gms.analytics.zza;
import com.google.android.gms.analytics.zzi;
import com.google.android.gms.analytics.zzj;
import com.google.android.gms.analytics.zzl;
import com.google.android.gms.common.internal.zzbo;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

final class zzamv extends zzamh {
    private boolean mStarted;
    private final zzanm zzagA;
    private final zzanm zzagB;
    private final zzaoo zzagC;
    private long zzagD;
    private boolean zzagE;
    private final zzams zzagv;
    private final zzaoe zzagw;
    private final zzaod zzagx;
    private final zzamn zzagy;
    private long zzagz = Long.MIN_VALUE;

    protected zzamv(zzamj zzamj, zzaml zzaml) {
        super(zzamj);
        zzbo.zzu(zzaml);
        this.zzagx = new zzaod(zzamj);
        this.zzagv = new zzams(zzamj);
        this.zzagw = new zzaoe(zzamj);
        this.zzagy = new zzamn(zzamj);
        this.zzagC = new zzaoo(zzkq());
        this.zzagA = new zzamw(this, zzamj);
        this.zzagB = new zzamx(this, zzamj);
    }

    private final void zza(zzamm zzamm, zzall zzall) {
        zzbo.zzu(zzamm);
        zzbo.zzu(zzall);
        zza zza = new zza(zzkp());
        zza.zzaY(zzamm.zzkL());
        zza.enableAdvertisingIdCollection(zzamm.zzkM());
        zzi zzjj = zza.zzjj();
        zzalt zzalt = (zzalt) zzjj.zzb(zzalt.class);
        zzalt.zzbj("data");
        zzalt.zzH$1385ff();
        zzjj.zza((zzj) zzall);
        zzalo zzalo = (zzalo) zzjj.zzb(zzalo.class);
        zzalk zzalk = (zzalk) zzjj.zzb(zzalk.class);
        for (Entry entry : zzamm.zzdV().entrySet()) {
            String str = (String) entry.getKey();
            String str2 = (String) entry.getValue();
            if ("an".equals(str)) {
                zzalk.setAppName(str2);
            } else if ("av".equals(str)) {
                zzalk.setAppVersion(str2);
            } else if ("aid".equals(str)) {
                zzalk.setAppId(str2);
            } else if ("aiid".equals(str)) {
                zzalk.setAppInstallerId(str2);
            } else if ("uid".equals(str)) {
                zzalt.setUserId(str2);
            } else {
                zzalo.set(str, str2);
            }
        }
        zzb("Sending installation campaign to", zzamm.zzkL(), zzall);
        zzjj.zzl(zzky().zzlU());
        zzjj.zzjt();
    }

    private final boolean zzbv(String str) {
        return zzbha.zzaP(getContext()).checkCallingOrSelfPermission(str) == 0;
    }

    private final long zzkT() {
        zzl.zzjC();
        zzkD();
        try {
            return this.zzagv.zzkT();
        } catch (SQLiteException e) {
            zze("Failed to get min/max hit times from local store", e);
            return 0;
        }
    }

    private final void zzla() {
        if (!this.zzagE && zzank.zzlo() && !this.zzagy.isConnected()) {
            if (this.zzagC.zzu(((Long) zzans.zzahS.get()).longValue())) {
                this.zzagC.start();
                zzbo("Connecting to service");
                if (this.zzagy.connect()) {
                    zzbo("Connected to service");
                    this.zzagC.clear();
                    onServiceConnected();
                }
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final boolean zzlb() {
        /*
        r12 = this;
        r1 = 1;
        r2 = 0;
        com.google.android.gms.analytics.zzl.zzjC();
        r12.zzkD();
        r0 = "Dispatching a batch of local hits";
        r12.zzbo(r0);
        r0 = r12.zzagy;
        r0 = r0.isConnected();
        if (r0 != 0) goto L_0x0028;
    L_0x0015:
        r0 = r1;
    L_0x0016:
        r3 = r12.zzagw;
        r3 = r3.zzlQ();
        if (r3 != 0) goto L_0x002a;
    L_0x001e:
        if (r0 == 0) goto L_0x002c;
    L_0x0020:
        if (r1 == 0) goto L_0x002c;
    L_0x0022:
        r0 = "No network or service available. Will retry later";
        r12.zzbo(r0);
    L_0x0027:
        return r2;
    L_0x0028:
        r0 = r2;
        goto L_0x0016;
    L_0x002a:
        r1 = r2;
        goto L_0x001e;
    L_0x002c:
        r0 = com.google.android.gms.internal.zzank.zzls();
        r1 = com.google.android.gms.internal.zzank.zzlt();
        r0 = java.lang.Math.max(r0, r1);
        r6 = (long) r0;
        r3 = new java.util.ArrayList;
        r3.<init>();
        r4 = 0;
    L_0x0040:
        r0 = r12.zzagv;	 Catch:{ all -> 0x01cf }
        r0.beginTransaction();	 Catch:{ all -> 0x01cf }
        r3.clear();	 Catch:{ all -> 0x01cf }
        r0 = r12.zzagv;	 Catch:{ SQLiteException -> 0x00c1 }
        r8 = r0.zzo(r6);	 Catch:{ SQLiteException -> 0x00c1 }
        r0 = r8.isEmpty();	 Catch:{ SQLiteException -> 0x00c1 }
        if (r0 == 0) goto L_0x0071;
    L_0x0054:
        r0 = "Store is empty, nothing to dispatch";
        r12.zzbo(r0);	 Catch:{ SQLiteException -> 0x00c1 }
        r12.zzlf();	 Catch:{ SQLiteException -> 0x00c1 }
        r0 = r12.zzagv;	 Catch:{ SQLiteException -> 0x0067 }
        r0.setTransactionSuccessful();	 Catch:{ SQLiteException -> 0x0067 }
        r0 = r12.zzagv;	 Catch:{ SQLiteException -> 0x0067 }
        r0.endTransaction();	 Catch:{ SQLiteException -> 0x0067 }
        goto L_0x0027;
    L_0x0067:
        r0 = move-exception;
        r1 = "Failed to commit local dispatch transaction";
        r12.zze(r1, r0);
        r12.zzlf();
        goto L_0x0027;
    L_0x0071:
        r0 = "Hits loaded from store. count";
        r1 = r8.size();	 Catch:{ SQLiteException -> 0x00c1 }
        r1 = java.lang.Integer.valueOf(r1);	 Catch:{ SQLiteException -> 0x00c1 }
        r12.zza(r0, r1);	 Catch:{ SQLiteException -> 0x00c1 }
        r1 = r8.iterator();	 Catch:{ all -> 0x01cf }
    L_0x0082:
        r0 = r1.hasNext();	 Catch:{ all -> 0x01cf }
        if (r0 == 0) goto L_0x00e1;
    L_0x0088:
        r0 = r1.next();	 Catch:{ all -> 0x01cf }
        r0 = (com.google.android.gms.internal.zzanx) r0;	 Catch:{ all -> 0x01cf }
        r10 = r0.zzlF();	 Catch:{ all -> 0x01cf }
        r0 = (r10 > r4 ? 1 : (r10 == r4 ? 0 : -1));
        if (r0 != 0) goto L_0x0082;
    L_0x0096:
        r0 = "Database contains successfully uploaded hit";
        r1 = java.lang.Long.valueOf(r4);	 Catch:{ all -> 0x01cf }
        r3 = r8.size();	 Catch:{ all -> 0x01cf }
        r3 = java.lang.Integer.valueOf(r3);	 Catch:{ all -> 0x01cf }
        r12.zzd(r0, r1, r3);	 Catch:{ all -> 0x01cf }
        r12.zzlf();	 Catch:{ all -> 0x01cf }
        r0 = r12.zzagv;	 Catch:{ SQLiteException -> 0x00b6 }
        r0.setTransactionSuccessful();	 Catch:{ SQLiteException -> 0x00b6 }
        r0 = r12.zzagv;	 Catch:{ SQLiteException -> 0x00b6 }
        r0.endTransaction();	 Catch:{ SQLiteException -> 0x00b6 }
        goto L_0x0027;
    L_0x00b6:
        r0 = move-exception;
        r1 = "Failed to commit local dispatch transaction";
        r12.zze(r1, r0);
        r12.zzlf();
        goto L_0x0027;
    L_0x00c1:
        r0 = move-exception;
        r1 = "Failed to read hits from persisted store";
        r12.zzd(r1, r0);	 Catch:{ all -> 0x01cf }
        r12.zzlf();	 Catch:{ all -> 0x01cf }
        r0 = r12.zzagv;	 Catch:{ SQLiteException -> 0x00d6 }
        r0.setTransactionSuccessful();	 Catch:{ SQLiteException -> 0x00d6 }
        r0 = r12.zzagv;	 Catch:{ SQLiteException -> 0x00d6 }
        r0.endTransaction();	 Catch:{ SQLiteException -> 0x00d6 }
        goto L_0x0027;
    L_0x00d6:
        r0 = move-exception;
        r1 = "Failed to commit local dispatch transaction";
        r12.zze(r1, r0);
        r12.zzlf();
        goto L_0x0027;
    L_0x00e1:
        r0 = r12.zzagy;	 Catch:{ all -> 0x01cf }
        r0 = r0.isConnected();	 Catch:{ all -> 0x01cf }
        if (r0 == 0) goto L_0x0148;
    L_0x00e9:
        r0 = "Service connected, sending hits to the service";
        r12.zzbo(r0);	 Catch:{ all -> 0x01cf }
    L_0x00ee:
        r0 = r8.isEmpty();	 Catch:{ all -> 0x01cf }
        if (r0 != 0) goto L_0x0148;
    L_0x00f4:
        r0 = 0;
        r0 = r8.get(r0);	 Catch:{ all -> 0x01cf }
        r0 = (com.google.android.gms.internal.zzanx) r0;	 Catch:{ all -> 0x01cf }
        r1 = r12.zzagy;	 Catch:{ all -> 0x01cf }
        r1 = r1.zzb(r0);	 Catch:{ all -> 0x01cf }
        if (r1 == 0) goto L_0x0148;
    L_0x0103:
        r10 = r0.zzlF();	 Catch:{ all -> 0x01cf }
        r4 = java.lang.Math.max(r4, r10);	 Catch:{ all -> 0x01cf }
        r8.remove(r0);	 Catch:{ all -> 0x01cf }
        r1 = "Hit sent do device AnalyticsService for delivery";
        r12.zzb(r1, r0);	 Catch:{ all -> 0x01cf }
        r1 = r12.zzagv;	 Catch:{ SQLiteException -> 0x0128 }
        r10 = r0.zzlF();	 Catch:{ SQLiteException -> 0x0128 }
        r1.zzp(r10);	 Catch:{ SQLiteException -> 0x0128 }
        r0 = r0.zzlF();	 Catch:{ SQLiteException -> 0x0128 }
        r0 = java.lang.Long.valueOf(r0);	 Catch:{ SQLiteException -> 0x0128 }
        r3.add(r0);	 Catch:{ SQLiteException -> 0x0128 }
        goto L_0x00ee;
    L_0x0128:
        r0 = move-exception;
        r1 = "Failed to remove hit that was send for delivery";
        r12.zze(r1, r0);	 Catch:{ all -> 0x01cf }
        r12.zzlf();	 Catch:{ all -> 0x01cf }
        r0 = r12.zzagv;	 Catch:{ SQLiteException -> 0x013d }
        r0.setTransactionSuccessful();	 Catch:{ SQLiteException -> 0x013d }
        r0 = r12.zzagv;	 Catch:{ SQLiteException -> 0x013d }
        r0.endTransaction();	 Catch:{ SQLiteException -> 0x013d }
        goto L_0x0027;
    L_0x013d:
        r0 = move-exception;
        r1 = "Failed to commit local dispatch transaction";
        r12.zze(r1, r0);
        r12.zzlf();
        goto L_0x0027;
    L_0x0148:
        r0 = r4;
        r4 = r12.zzagw;	 Catch:{ all -> 0x01cf }
        r4 = r4.zzlQ();	 Catch:{ all -> 0x01cf }
        if (r4 == 0) goto L_0x017a;
    L_0x0151:
        r4 = r12.zzagw;	 Catch:{ all -> 0x01cf }
        r8 = r4.zzu(r8);	 Catch:{ all -> 0x01cf }
        r9 = r8.iterator();	 Catch:{ all -> 0x01cf }
        r4 = r0;
    L_0x015c:
        r0 = r9.hasNext();	 Catch:{ all -> 0x01cf }
        if (r0 == 0) goto L_0x0171;
    L_0x0162:
        r0 = r9.next();	 Catch:{ all -> 0x01cf }
        r0 = (java.lang.Long) r0;	 Catch:{ all -> 0x01cf }
        r0 = r0.longValue();	 Catch:{ all -> 0x01cf }
        r4 = java.lang.Math.max(r4, r0);	 Catch:{ all -> 0x01cf }
        goto L_0x015c;
    L_0x0171:
        r0 = r12.zzagv;	 Catch:{ SQLiteException -> 0x0197 }
        r0.zzs(r8);	 Catch:{ SQLiteException -> 0x0197 }
        r3.addAll(r8);	 Catch:{ SQLiteException -> 0x0197 }
        r0 = r4;
    L_0x017a:
        r4 = r3.isEmpty();	 Catch:{ all -> 0x01cf }
        if (r4 == 0) goto L_0x01b7;
    L_0x0180:
        r0 = r12.zzagv;	 Catch:{ SQLiteException -> 0x018c }
        r0.setTransactionSuccessful();	 Catch:{ SQLiteException -> 0x018c }
        r0 = r12.zzagv;	 Catch:{ SQLiteException -> 0x018c }
        r0.endTransaction();	 Catch:{ SQLiteException -> 0x018c }
        goto L_0x0027;
    L_0x018c:
        r0 = move-exception;
        r1 = "Failed to commit local dispatch transaction";
        r12.zze(r1, r0);
        r12.zzlf();
        goto L_0x0027;
    L_0x0197:
        r0 = move-exception;
        r1 = "Failed to remove successfully uploaded hits";
        r12.zze(r1, r0);	 Catch:{ all -> 0x01cf }
        r12.zzlf();	 Catch:{ all -> 0x01cf }
        r0 = r12.zzagv;	 Catch:{ SQLiteException -> 0x01ac }
        r0.setTransactionSuccessful();	 Catch:{ SQLiteException -> 0x01ac }
        r0 = r12.zzagv;	 Catch:{ SQLiteException -> 0x01ac }
        r0.endTransaction();	 Catch:{ SQLiteException -> 0x01ac }
        goto L_0x0027;
    L_0x01ac:
        r0 = move-exception;
        r1 = "Failed to commit local dispatch transaction";
        r12.zze(r1, r0);
        r12.zzlf();
        goto L_0x0027;
    L_0x01b7:
        r4 = r12.zzagv;	 Catch:{ SQLiteException -> 0x01c4 }
        r4.setTransactionSuccessful();	 Catch:{ SQLiteException -> 0x01c4 }
        r4 = r12.zzagv;	 Catch:{ SQLiteException -> 0x01c4 }
        r4.endTransaction();	 Catch:{ SQLiteException -> 0x01c4 }
        r4 = r0;
        goto L_0x0040;
    L_0x01c4:
        r0 = move-exception;
        r1 = "Failed to commit local dispatch transaction";
        r12.zze(r1, r0);
        r12.zzlf();
        goto L_0x0027;
    L_0x01cf:
        r0 = move-exception;
        r1 = r12.zzagv;	 Catch:{ SQLiteException -> 0x01db }
        r1.setTransactionSuccessful();	 Catch:{ SQLiteException -> 0x01db }
        r1 = r12.zzagv;	 Catch:{ SQLiteException -> 0x01db }
        r1.endTransaction();	 Catch:{ SQLiteException -> 0x01db }
        throw r0;
    L_0x01db:
        r0 = move-exception;
        r1 = "Failed to commit local dispatch transaction";
        r12.zze(r1, r0);
        r12.zzlf();
        goto L_0x0027;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.zzamv.zzlb():boolean");
    }

    private final void zzle() {
        zzanp zzkw = zzkw();
        if (zzkw.zzlC() && !zzkw.zzbo()) {
            long zzkT = zzkT();
            if (zzkT != 0 && Math.abs(zzkq().currentTimeMillis() - zzkT) <= ((Long) zzans.zzahr.get()).longValue()) {
                zza("Dispatch alarm scheduled (ms)", Long.valueOf(zzank.zzlr()));
                zzkw.schedule();
            }
        }
    }

    private final void zzlf() {
        if (this.zzagA.zzbo()) {
            zzbo("All hits dispatched or no network/service. Going to power save mode");
        }
        this.zzagA.cancel();
        zzanp zzkw = zzkw();
        if (zzkw.zzbo()) {
            zzkw.cancel();
        }
    }

    private final long zzlg() {
        if (this.zzagz != Long.MIN_VALUE) {
            return this.zzagz;
        }
        long longValue = ((Long) zzans.zzahm.get()).longValue();
        zzaot zzkx = zzkx();
        zzkx.zzkD();
        if (!zzkx.zzaiP) {
            return longValue;
        }
        zzaot zzkx2 = zzkx();
        zzkx2.zzkD();
        return ((long) zzkx2.zzahZ) * 1000;
    }

    private final void zzlh() {
        zzkD();
        zzl.zzjC();
        this.zzagE = true;
        this.zzagy.disconnect();
        zzld();
    }

    protected final void onServiceConnected() {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxOverflowException: Regions stack size limit reached
	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:36)
	at jadx.core.utils.ErrorsCounter.methodError(ErrorsCounter.java:60)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:33)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:56)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r6 = this;
        com.google.android.gms.analytics.zzl.zzjC();
        com.google.android.gms.analytics.zzl.zzjC();
        r6.zzkD();
        r0 = com.google.android.gms.internal.zzank.zzlo();
        if (r0 != 0) goto L_0x0014;
    L_0x000f:
        r0 = "Service client disabled. Can't dispatch local hits to device AnalyticsService";
        r6.zzbr(r0);
    L_0x0014:
        r0 = r6.zzagy;
        r0 = r0.isConnected();
        if (r0 != 0) goto L_0x0022;
    L_0x001c:
        r0 = "Service not connected";
        r6.zzbo(r0);
    L_0x0021:
        return;
    L_0x0022:
        r0 = r6.zzagv;
        r0 = r0.isEmpty();
        if (r0 != 0) goto L_0x0021;
    L_0x002a:
        r0 = "Dispatching local hits to device AnalyticsService";
        r6.zzbo(r0);
    L_0x002f:
        r0 = r6.zzagv;	 Catch:{ SQLiteException -> 0x0044 }
        r1 = com.google.android.gms.internal.zzank.zzls();	 Catch:{ SQLiteException -> 0x0044 }
        r2 = (long) r1;	 Catch:{ SQLiteException -> 0x0044 }
        r1 = r0.zzo(r2);	 Catch:{ SQLiteException -> 0x0044 }
        r0 = r1.isEmpty();	 Catch:{ SQLiteException -> 0x0044 }
        if (r0 == 0) goto L_0x005a;	 Catch:{ SQLiteException -> 0x0044 }
    L_0x0040:
        r6.zzld();	 Catch:{ SQLiteException -> 0x0044 }
        goto L_0x0021;
    L_0x0044:
        r0 = move-exception;
        r1 = "Failed to read hits from store";
        r6.zze(r1, r0);
        r6.zzlf();
        goto L_0x0021;
    L_0x004e:
        r1.remove(r0);
        r2 = r6.zzagv;	 Catch:{ SQLiteException -> 0x0073 }
        r4 = r0.zzlF();	 Catch:{ SQLiteException -> 0x0073 }
        r2.zzp(r4);	 Catch:{ SQLiteException -> 0x0073 }
    L_0x005a:
        r0 = r1.isEmpty();
        if (r0 != 0) goto L_0x002f;
    L_0x0060:
        r0 = 0;
        r0 = r1.get(r0);
        r0 = (com.google.android.gms.internal.zzanx) r0;
        r2 = r6.zzagy;
        r2 = r2.zzb(r0);
        if (r2 != 0) goto L_0x004e;
    L_0x006f:
        r6.zzld();
        goto L_0x0021;
    L_0x0073:
        r0 = move-exception;
        r1 = "Failed to remove hit that was send for delivery";
        r6.zze(r1, r0);
        r6.zzlf();
        goto L_0x0021;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.zzamv.onServiceConnected():void");
    }

    final void start() {
        zzkD();
        zzbo.zza(!this.mStarted, "Analytics backend already started");
        this.mStarted = true;
        zzkt().zzf(new zzamy(this));
    }

    public final void zza(zzanx zzanx) {
        zzbo.zzu(zzanx);
        zzl.zzjC();
        zzkD();
        if (this.zzagE) {
            zzbp("Hit delivery not possible. Missing network permissions. See http://goo.gl/8Rd3yj for instructions");
        } else {
            zza("Delivering hit", zzanx);
        }
        if (TextUtils.isEmpty(zzanx.zzlK())) {
            Pair zzmb = zzky().zzlZ().zzmb();
            if (zzmb != null) {
                Long l = (Long) zzmb.second;
                String str = (String) zzmb.first;
                String valueOf = String.valueOf(l);
                valueOf = new StringBuilder((String.valueOf(valueOf).length() + 1) + String.valueOf(str).length()).append(valueOf).append(":").append(str).toString();
                Map hashMap = new HashMap(zzanx.zzdV());
                hashMap.put("_m", valueOf);
                zzanx = new zzanx(this, hashMap, zzanx.zzlG(), zzanx.zzlI(), zzanx.zzlF(), zzanx.zzlE(), zzanx.zzlH());
            }
        }
        zzla();
        if (this.zzagy.zzb(zzanx)) {
            zzbp("Hit sent to the device AnalyticsService for delivery");
            return;
        }
        try {
            this.zzagv.zzc(zzanx);
            zzld();
        } catch (SQLiteException e) {
            zze("Delivery failed to save hit to a database", e);
            zzkr().zza(zzanx, "deliver: failed to insert hit to database");
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final long zza$50872eb5(com.google.android.gms.internal.zzamm r15) {
        /*
        r14 = this;
        r4 = -1;
        r6 = 1;
        r7 = 0;
        com.google.android.gms.common.internal.zzbo.zzu(r15);
        r14.zzkD();
        com.google.android.gms.analytics.zzl.zzjC();
        r0 = r14.zzagv;	 Catch:{ SQLiteException -> 0x009c }
        r0.beginTransaction();	 Catch:{ SQLiteException -> 0x009c }
        r0 = r14.zzagv;	 Catch:{ SQLiteException -> 0x009c }
        r2 = 0;
        r1 = r15.zzjX();	 Catch:{ SQLiteException -> 0x009c }
        com.google.android.gms.common.internal.zzbo.zzcF(r1);	 Catch:{ SQLiteException -> 0x009c }
        r0.zzkD();	 Catch:{ SQLiteException -> 0x009c }
        com.google.android.gms.analytics.zzl.zzjC();	 Catch:{ SQLiteException -> 0x009c }
        r8 = r0.getWritableDatabase();	 Catch:{ SQLiteException -> 0x009c }
        r9 = "properties";
        r10 = "app_uid=? AND cid<>?";
        r11 = 2;
        r11 = new java.lang.String[r11];	 Catch:{ SQLiteException -> 0x009c }
        r12 = 0;
        r2 = java.lang.String.valueOf(r2);	 Catch:{ SQLiteException -> 0x009c }
        r11[r12] = r2;	 Catch:{ SQLiteException -> 0x009c }
        r2 = 1;
        r11[r2] = r1;	 Catch:{ SQLiteException -> 0x009c }
        r1 = r8.delete(r9, r10, r11);	 Catch:{ SQLiteException -> 0x009c }
        if (r1 <= 0) goto L_0x0047;
    L_0x003e:
        r2 = "Deleted property records";
        r1 = java.lang.Integer.valueOf(r1);	 Catch:{ SQLiteException -> 0x009c }
        r0.zza(r2, r1);	 Catch:{ SQLiteException -> 0x009c }
    L_0x0047:
        r0 = r14.zzagv;	 Catch:{ SQLiteException -> 0x009c }
        r2 = 0;
        r1 = r15.zzjX();	 Catch:{ SQLiteException -> 0x009c }
        r8 = r15.zzkL();	 Catch:{ SQLiteException -> 0x009c }
        r2 = r0.zza(r2, r1, r8);	 Catch:{ SQLiteException -> 0x009c }
        r0 = 1;
        r0 = r0 + r2;
        r15.zzm(r0);	 Catch:{ SQLiteException -> 0x009c }
        r8 = r14.zzagv;	 Catch:{ SQLiteException -> 0x009c }
        com.google.android.gms.common.internal.zzbo.zzu(r15);	 Catch:{ SQLiteException -> 0x009c }
        r8.zzkD();	 Catch:{ SQLiteException -> 0x009c }
        com.google.android.gms.analytics.zzl.zzjC();	 Catch:{ SQLiteException -> 0x009c }
        r9 = r8.getWritableDatabase();	 Catch:{ SQLiteException -> 0x009c }
        r0 = r15.zzdV();	 Catch:{ SQLiteException -> 0x009c }
        com.google.android.gms.common.internal.zzbo.zzu(r0);	 Catch:{ SQLiteException -> 0x009c }
        r10 = new android.net.Uri$Builder;	 Catch:{ SQLiteException -> 0x009c }
        r10.<init>();	 Catch:{ SQLiteException -> 0x009c }
        r0 = r0.entrySet();	 Catch:{ SQLiteException -> 0x009c }
        r11 = r0.iterator();	 Catch:{ SQLiteException -> 0x009c }
    L_0x0080:
        r0 = r11.hasNext();	 Catch:{ SQLiteException -> 0x009c }
        if (r0 == 0) goto L_0x00a9;
    L_0x0086:
        r0 = r11.next();	 Catch:{ SQLiteException -> 0x009c }
        r0 = (java.util.Map.Entry) r0;	 Catch:{ SQLiteException -> 0x009c }
        r1 = r0.getKey();	 Catch:{ SQLiteException -> 0x009c }
        r1 = (java.lang.String) r1;	 Catch:{ SQLiteException -> 0x009c }
        r0 = r0.getValue();	 Catch:{ SQLiteException -> 0x009c }
        r0 = (java.lang.String) r0;	 Catch:{ SQLiteException -> 0x009c }
        r10.appendQueryParameter(r1, r0);	 Catch:{ SQLiteException -> 0x009c }
        goto L_0x0080;
    L_0x009c:
        r0 = move-exception;
        r1 = "Failed to update Analytics property";
        r14.zze(r1, r0);	 Catch:{ all -> 0x0122 }
        r0 = r14.zzagv;	 Catch:{ SQLiteException -> 0x0130 }
        r0.endTransaction();	 Catch:{ SQLiteException -> 0x0130 }
    L_0x00a7:
        r0 = r4;
    L_0x00a8:
        return r0;
    L_0x00a9:
        r0 = r10.build();	 Catch:{ SQLiteException -> 0x009c }
        r0 = r0.getEncodedQuery();	 Catch:{ SQLiteException -> 0x009c }
        if (r0 != 0) goto L_0x0117;
    L_0x00b3:
        r0 = "";
        r1 = r0;
    L_0x00b6:
        r10 = new android.content.ContentValues;	 Catch:{ SQLiteException -> 0x009c }
        r10.<init>();	 Catch:{ SQLiteException -> 0x009c }
        r0 = "app_uid";
        r12 = 0;
        r11 = java.lang.Long.valueOf(r12);	 Catch:{ SQLiteException -> 0x009c }
        r10.put(r0, r11);	 Catch:{ SQLiteException -> 0x009c }
        r0 = "cid";
        r11 = r15.zzjX();	 Catch:{ SQLiteException -> 0x009c }
        r10.put(r0, r11);	 Catch:{ SQLiteException -> 0x009c }
        r0 = "tid";
        r11 = r15.zzkL();	 Catch:{ SQLiteException -> 0x009c }
        r10.put(r0, r11);	 Catch:{ SQLiteException -> 0x009c }
        r11 = "adid";
        r0 = r15.zzkM();	 Catch:{ SQLiteException -> 0x009c }
        if (r0 == 0) goto L_0x0119;
    L_0x00e0:
        r0 = r6;
    L_0x00e1:
        r0 = java.lang.Integer.valueOf(r0);	 Catch:{ SQLiteException -> 0x009c }
        r10.put(r11, r0);	 Catch:{ SQLiteException -> 0x009c }
        r0 = "hits_count";
        r6 = r15.zzkN();	 Catch:{ SQLiteException -> 0x009c }
        r6 = java.lang.Long.valueOf(r6);	 Catch:{ SQLiteException -> 0x009c }
        r10.put(r0, r6);	 Catch:{ SQLiteException -> 0x009c }
        r0 = "params";
        r10.put(r0, r1);	 Catch:{ SQLiteException -> 0x009c }
        r0 = "properties";
        r1 = 0;
        r6 = 5;
        r0 = r9.insertWithOnConflict(r0, r1, r10, r6);	 Catch:{ SQLiteException -> 0x011b }
        r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1));
        if (r0 != 0) goto L_0x010b;
    L_0x0106:
        r0 = "Failed to insert/update a property (got -1)";
        r8.zzbs(r0);	 Catch:{ SQLiteException -> 0x011b }
    L_0x010b:
        r0 = r14.zzagv;	 Catch:{ SQLiteException -> 0x009c }
        r0.setTransactionSuccessful();	 Catch:{ SQLiteException -> 0x009c }
        r0 = r14.zzagv;	 Catch:{ SQLiteException -> 0x0129 }
        r0.endTransaction();	 Catch:{ SQLiteException -> 0x0129 }
    L_0x0115:
        r0 = r2;
        goto L_0x00a8;
    L_0x0117:
        r1 = r0;
        goto L_0x00b6;
    L_0x0119:
        r0 = r7;
        goto L_0x00e1;
    L_0x011b:
        r0 = move-exception;
        r1 = "Error storing a property";
        r8.zze(r1, r0);	 Catch:{ SQLiteException -> 0x009c }
        goto L_0x010b;
    L_0x0122:
        r0 = move-exception;
        r1 = r14.zzagv;	 Catch:{ SQLiteException -> 0x0138 }
        r1.endTransaction();	 Catch:{ SQLiteException -> 0x0138 }
    L_0x0128:
        throw r0;
    L_0x0129:
        r0 = move-exception;
        r1 = "Failed to end transaction";
        r14.zze(r1, r0);
        goto L_0x0115;
    L_0x0130:
        r0 = move-exception;
        r1 = "Failed to end transaction";
        r14.zze(r1, r0);
        goto L_0x00a7;
    L_0x0138:
        r1 = move-exception;
        r2 = "Failed to end transaction";
        r14.zze(r2, r1);
        goto L_0x0128;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.zzamv.zza$50872eb5(com.google.android.gms.internal.zzamm):long");
    }

    protected final void zzb(zzamm zzamm) {
        zzl.zzjC();
        zzb("Sending first hit to property", zzamm.zzkL());
        if (!zzky().zzlV().zzu(zzank.zzly())) {
            String zzlY = zzky().zzlY();
            if (!TextUtils.isEmpty(zzlY)) {
                zzall zza = zzaos.zza(zzkr(), zzlY);
                zzb("Found relevant installation campaign", zza);
                zza(zzamm, zza);
            }
        }
    }

    public final void zzb(zzanq zzanq) {
        long j = this.zzagD;
        zzl.zzjC();
        zzkD();
        long j2 = -1;
        long zzlW = zzky().zzlW();
        if (zzlW != 0) {
            j2 = Math.abs(zzkq().currentTimeMillis() - zzlW);
        }
        zzb("Dispatching local hits. Elapsed time since last dispatch (ms)", Long.valueOf(j2));
        zzla();
        try {
            zzlb();
            zzky().zzlX();
            zzld();
            if (zzanq != null) {
                zzanq.zzc$786b7c60();
            }
            if (this.zzagD != j) {
                this.zzagx.zzlP();
            }
        } catch (Throwable th) {
            zze("Local dispatch failed", th);
            zzky().zzlX();
            zzld();
            if (zzanq != null) {
                zzanq.zzc$786b7c60();
            }
        }
    }

    public final void zzbw(String str) {
        zzbo.zzcF(str);
        zzl.zzjC();
        zzall zza = zzaos.zza(zzkr(), str);
        if (zza == null) {
            zzd("Parsing failed. Ignoring invalid campaign data", str);
            return;
        }
        CharSequence zzlY = zzky().zzlY();
        if (str.equals(zzlY)) {
            zzbr("Ignoring duplicate install campaign");
        } else if (TextUtils.isEmpty(zzlY)) {
            zzky().zzbz(str);
            if (zzky().zzlV().zzu(zzank.zzly())) {
                zzd("Campaign received too late, ignoring", zza);
                return;
            }
            zzb("Received installation campaign", zza);
            for (zzamm zza2 : this.zzagv.zzq$187a7388()) {
                zza(zza2, zza);
            }
        } else {
            zzd("Ignoring multiple install campaigns. original, new", zzlY, str);
        }
    }

    protected final void zzjD() {
        this.zzagv.initialize();
        this.zzagw.initialize();
        this.zzagy.initialize();
    }

    protected final void zzkX() {
        zzkD();
        zzl.zzjC();
        Context context = zzkp().getContext();
        if (!zzaoj.zzac(context)) {
            zzbr("AnalyticsReceiver is not registered or is disabled. Register the receiver for reliable dispatching on non-Google Play devices. See http://goo.gl/8Rd3yj for instructions.");
        } else if (!zzaok.zzad(context)) {
            zzbs("AnalyticsService is not registered or is disabled. Analytics service at risk of not starting. See http://goo.gl/8Rd3yj for instructions.");
        }
        if (!CampaignTrackingReceiver.zzac(context)) {
            zzbr("CampaignTrackingReceiver is not registered, not exported or is disabled. Installation campaign tracking is not possible. See http://goo.gl/8Rd3yj for instructions.");
        } else if (!CampaignTrackingService.zzad(context)) {
            zzbr("CampaignTrackingService is not registered or is disabled. Installation campaign tracking is not possible. See http://goo.gl/8Rd3yj for instructions.");
        }
        zzky().zzlU();
        if (!zzbv("android.permission.ACCESS_NETWORK_STATE")) {
            zzbs("Missing required android.permission.ACCESS_NETWORK_STATE. Google Analytics disabled. See http://goo.gl/8Rd3yj for instructions");
            zzlh();
        }
        if (!zzbv("android.permission.INTERNET")) {
            zzbs("Missing required android.permission.INTERNET. Google Analytics disabled. See http://goo.gl/8Rd3yj for instructions");
            zzlh();
        }
        if (zzaok.zzad(getContext())) {
            zzbo("AnalyticsService registered in the app manifest and enabled");
        } else {
            zzbr("AnalyticsService not registered in the app manifest. Hits might not be delivered reliably. See http://goo.gl/8Rd3yj for instructions.");
        }
        if (!(this.zzagE || this.zzagv.isEmpty())) {
            zzla();
        }
        zzld();
    }

    final void zzko() {
        zzl.zzjC();
        this.zzagD = zzkq().currentTimeMillis();
    }

    public final void zzld() {
        zzl.zzjC();
        zzkD();
        Object obj = (this.zzagE || zzlg() <= 0) ? null : 1;
        if (obj == null) {
            this.zzagx.unregister();
            zzlf();
        } else if (this.zzagv.isEmpty()) {
            this.zzagx.unregister();
            zzlf();
        } else {
            boolean z;
            if (((Boolean) zzans.zzahN.get()).booleanValue()) {
                z = true;
            } else {
                this.zzagx.zzlN();
                z = this.zzagx.isConnected();
            }
            if (z) {
                zzle();
                long zzlg = zzlg();
                long zzlW = zzky().zzlW();
                if (zzlW != 0) {
                    zzlW = zzlg - Math.abs(zzkq().currentTimeMillis() - zzlW);
                    if (zzlW <= 0) {
                        zzlW = Math.min(zzank.zzlq(), zzlg);
                    }
                } else {
                    zzlW = Math.min(zzank.zzlq(), zzlg);
                }
                zza("Dispatch scheduled (ms)", Long.valueOf(zzlW));
                if (this.zzagA.zzbo()) {
                    this.zzagA.zzt(Math.max(1, zzlW + this.zzagA.zzlz()));
                    return;
                } else {
                    this.zzagA.zzs(zzlW);
                    return;
                }
            }
            zzlf();
            zzle();
        }
    }

    static /* synthetic */ void zzb(zzamv zzamv) {
        try {
            zzamv.zzagv.zzkS();
            zzamv.zzld();
        } catch (SQLiteException e) {
            zzamv.zzd("Failed to delete stale hits", e);
        }
        zzamv.zzagB.zzs(86400000);
    }
}
