package com.google.android.gms.analytics;

import android.text.TextUtils;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.internal.zzalk;
import com.google.android.gms.internal.zzalx;
import com.google.android.gms.internal.zzami;
import com.google.android.gms.internal.zzamm;
import com.google.android.gms.internal.zzanx;
import com.google.android.gms.internal.zzaos;
import java.util.HashMap;
import java.util.Map;

final class zzp implements Runnable {
    private /* synthetic */ String zzaeA;
    private /* synthetic */ Tracker zzaeB;
    private /* synthetic */ Map zzaeu;
    private /* synthetic */ boolean zzaev;
    private /* synthetic */ String zzaew;
    private /* synthetic */ long zzaex;
    private /* synthetic */ boolean zzaey;
    private /* synthetic */ boolean zzaez;

    zzp(Tracker tracker, Map map, boolean z, String str, long j, boolean z2, boolean z3, String str2) {
        this.zzaeB = tracker;
        this.zzaeu = map;
        this.zzaev = z;
        this.zzaew = str;
        this.zzaex = j;
        this.zzaey = z2;
        this.zzaez = z3;
        this.zzaeA = str2;
    }

    public final void run() {
        boolean z = true;
        if (this.zzaeB.zzaer.zzjE()) {
            this.zzaeu.put("sc", "start");
        }
        GoogleAnalytics zzku = this.zzaeB.zzku();
        zzbo.zzcG("getClientId can not be called from the main thread");
        zzaos.zzc(this.zzaeu, "cid", zzku.zzji().zzkJ().zzli());
        String str = (String) this.zzaeu.get("sf");
        if (str != null) {
            double zza$505d11f3 = zzaos.zza$505d11f3(str);
            if (zzaos.zza(zza$505d11f3, (String) this.zzaeu.get("cid"))) {
                this.zzaeB.zzb("Sampling enabled. Hit sampled out. sample rate", Double.valueOf(zza$505d11f3));
                return;
            }
        }
        zzalx zzb = this.zzaeB.zzkA();
        if (this.zzaev) {
            zzaos.zzb(this.zzaeu, "ate", zzb.zzjZ());
            zzaos.zzb(this.zzaeu, "adid", zzb.zzkg());
        } else {
            this.zzaeu.remove("ate");
            this.zzaeu.remove("adid");
        }
        zzalk zzkW = this.zzaeB.zzkB().zzkW();
        zzaos.zzb(this.zzaeu, "an", zzkW.zzjG());
        zzaos.zzb(this.zzaeu, "av", zzkW.zzjH());
        zzaos.zzb(this.zzaeu, "aid", zzkW.zzhl());
        zzaos.zzb(this.zzaeu, "aiid", zzkW.zzjI());
        this.zzaeu.put("v", "1");
        this.zzaeu.put("_v", zzami.zzafL);
        zzaos.zzb(this.zzaeu, "ul", this.zzaeB.zzkC().zzlA().getLanguage());
        zzaos.zzb(this.zzaeu, "sr", this.zzaeB.zzkC().zzlB());
        boolean z2 = this.zzaew.equals("transaction") || this.zzaew.equals("item");
        if (z2 || this.zzaeB.zzaeq.zzlL()) {
            long zzbC = zzaos.zzbC((String) this.zzaeu.get("ht"));
            if (zzbC == 0) {
                zzbC = this.zzaex;
            }
            if (this.zzaey) {
                this.zzaeB.zzkr().zzc("Dry run enabled. Would have sent hit", new zzanx(this.zzaeB, this.zzaeu, zzbC, this.zzaez));
                return;
            }
            String str2 = (String) this.zzaeu.get("cid");
            Map hashMap = new HashMap();
            zzaos.zza(hashMap, "uid", this.zzaeu);
            zzaos.zza(hashMap, "an", this.zzaeu);
            zzaos.zza(hashMap, "aid", this.zzaeu);
            zzaos.zza(hashMap, "av", this.zzaeu);
            zzaos.zza(hashMap, "aiid", this.zzaeu);
            String str3 = this.zzaeA;
            if (TextUtils.isEmpty((CharSequence) this.zzaeu.get("adid"))) {
                z = false;
            }
            this.zzaeu.put("_s", String.valueOf(this.zzaeB.zzkv().zza(new zzamm(str2, str3, z, 0, hashMap))));
            this.zzaeB.zzkv().zza(new zzanx(this.zzaeB, this.zzaeu, zzbC, this.zzaez));
            return;
        }
        this.zzaeB.zzkr().zze(this.zzaeu, "Too many hits sent too quickly, rate limiting invoked");
    }
}
