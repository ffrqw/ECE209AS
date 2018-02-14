package com.google.android.gms.internal;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.util.zze;
import com.google.android.gms.measurement.AppMeasurement.zza;
import com.google.android.gms.measurement.AppMeasurement.zzb;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public final class zzchz extends zzchj {
    protected zzcic zzbtE;
    private volatile zzb zzbtF;
    private zzb zzbtG;
    private long zzbtH;
    private final Map<Activity, zzcic> zzbtI = new ArrayMap();
    private final CopyOnWriteArrayList<zza> zzbtJ = new CopyOnWriteArrayList();
    private boolean zzbtK;
    private zzb zzbtL;
    private String zzbtM;

    public zzchz(zzcgl zzcgl) {
        super(zzcgl);
    }

    private final void zza(Activity activity, zzcic zzcic, boolean z) {
        boolean hasNext;
        boolean z2 = true;
        zzb zzb = null;
        if (this.zzbtF != null) {
            zzb = this.zzbtF;
        } else if (this.zzbtG != null && Math.abs(super.zzkq().elapsedRealtime() - this.zzbtH) < 1000) {
            zzb = this.zzbtG;
        }
        if (zzb != null) {
            zzb zzb2 = new zzb(zzb);
        }
        this.zzbtK = true;
        try {
            Iterator it = this.zzbtJ.iterator();
            while (true) {
                hasNext = it.hasNext();
                if (!hasNext) {
                    break;
                }
                try {
                    z2 &= ((zza) it.next()).zza$5a299da7();
                } catch (Exception e) {
                    super.zzwF().zzyx().zzj("onScreenChangeCallback threw exception", e);
                }
            }
            hasNext = z2;
        } catch (Exception e2) {
            Exception exception = e2;
            hasNext = z2;
            z2 = exception;
            super.zzwF().zzyx().zzj("onScreenChangeCallback loop threw exception", z2);
        } finally {
            this.zzbtK = false;
        }
        zzb zzb3 = this.zzbtF == null ? this.zzbtG : this.zzbtF;
        if (hasNext) {
            if (zzcic.zzbok == null) {
                zzcic.zzbok = zzen(activity.getClass().getCanonicalName());
            }
            zzb = new zzcic(zzcic);
            this.zzbtG = this.zzbtF;
            this.zzbtH = super.zzkq().elapsedRealtime();
            this.zzbtF = zzb;
            super.zzwE().zzj(new zzcia(this, z, zzb3, zzb));
        }
    }

    public static void zza(zzb zzb, Bundle bundle) {
        if (bundle != null && zzb != null && !bundle.containsKey("_sc")) {
            if (zzb.zzboj != null) {
                bundle.putString("_sn", zzb.zzboj);
            }
            bundle.putString("_sc", zzb.zzbok);
            bundle.putLong("_si", zzb.zzbol);
        }
    }

    private static String zzen(String str) {
        String[] split = str.split("\\.");
        if (split.length == 0) {
            return str.substring(0, 36);
        }
        String str2 = split[split.length - 1];
        return str2.length() > 36 ? str2.substring(0, 36) : str2;
    }

    public final /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    public final void onActivityDestroyed(Activity activity) {
        this.zzbtI.remove(activity);
    }

    public final void onActivityPaused(Activity activity) {
        zzcic zzq = zzq(activity);
        this.zzbtG = this.zzbtF;
        this.zzbtH = super.zzkq().elapsedRealtime();
        this.zzbtF = null;
        super.zzwE().zzj(new zzcib(this, zzq));
    }

    public final void onActivityResumed(Activity activity) {
        zza(activity, zzq(activity), false);
        super.zzwr().zzwn();
    }

    public final void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
        if (bundle != null) {
            zzcic zzcic = (zzcic) this.zzbtI.get(activity);
            if (zzcic != null) {
                Bundle bundle2 = new Bundle();
                bundle2.putLong("id", zzcic.zzbol);
                bundle2.putString("name", zzcic.zzboj);
                bundle2.putString("referrer_name", zzcic.zzbok);
                bundle.putBundle("com.google.firebase.analytics.screen_service", bundle2);
            }
        }
    }

    public final void registerOnScreenChangeCallback(zza zza) {
        super.zzwp();
        if (zza == null) {
            super.zzwF().zzyz().log("Attempting to register null OnScreenChangeCallback");
            return;
        }
        this.zzbtJ.remove(zza);
        this.zzbtJ.add(zza);
    }

    public final void setCurrentScreen(Activity activity, String str, String str2) {
        if (activity == null) {
            super.zzwF().zzyz().log("setCurrentScreen must be called with a non-null activity");
            return;
        }
        super.zzwE();
        if (!zzcgg.zzS()) {
            super.zzwF().zzyz().log("setCurrentScreen must be called from the main thread");
        } else if (this.zzbtK) {
            super.zzwF().zzyz().log("Cannot call setCurrentScreen from onScreenChangeCallback");
        } else if (this.zzbtF == null) {
            super.zzwF().zzyz().log("setCurrentScreen cannot be called while no activity active");
        } else if (this.zzbtI.get(activity) == null) {
            super.zzwF().zzyz().log("setCurrentScreen must be called with an activity in the activity lifecycle");
        } else {
            if (str2 == null) {
                str2 = zzen(activity.getClass().getCanonicalName());
            }
            boolean equals = this.zzbtF.zzbok.equals(str2);
            boolean zzR = zzcjl.zzR(this.zzbtF.zzboj, str);
            if (equals && zzR) {
                super.zzwF().zzyA().log("setCurrentScreen cannot be called with the same class and name");
            } else if (str != null && (str.length() <= 0 || str.length() > zzcem.zzxk())) {
                super.zzwF().zzyz().zzj("Invalid screen name length in setCurrentScreen. Length", Integer.valueOf(str.length()));
            } else if (str2 == null || (str2.length() > 0 && str2.length() <= zzcem.zzxk())) {
                Object obj;
                zzcfn zzyD = super.zzwF().zzyD();
                String str3 = "Setting current screen to name, class";
                if (str == null) {
                    obj = "null";
                } else {
                    String str4 = str;
                }
                zzyD.zze(str3, obj, str2);
                zzcic zzcic = new zzcic(str, str2, super.zzwB().zzzs());
                this.zzbtI.put(activity, zzcic);
                zza(activity, zzcic, true);
            } else {
                super.zzwF().zzyz().zzj("Invalid class name length in setCurrentScreen. Length", Integer.valueOf(str2.length()));
            }
        }
    }

    public final void unregisterOnScreenChangeCallback(zza zza) {
        super.zzwp();
        this.zzbtJ.remove(zza);
    }

    public final void zza(String str, zzb zzb) {
        super.zzjC();
        synchronized (this) {
            if (this.zzbtM == null || this.zzbtM.equals(str) || zzb != null) {
                this.zzbtM = str;
                this.zzbtL = zzb;
            }
        }
    }

    public final /* bridge */ /* synthetic */ void zzjC() {
        super.zzjC();
    }

    protected final void zzjD() {
    }

    public final /* bridge */ /* synthetic */ zze zzkq() {
        return super.zzkq();
    }

    final zzcic zzq(Activity activity) {
        zzbo.zzu(activity);
        zzcic zzcic = (zzcic) this.zzbtI.get(activity);
        if (zzcic != null) {
            return zzcic;
        }
        zzcic = new zzcic(null, zzen(activity.getClass().getCanonicalName()), super.zzwB().zzzs());
        this.zzbtI.put(activity, zzcic);
        return zzcic;
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

    public final zzcic zzzh() {
        zzkD();
        super.zzjC();
        return this.zzbtE;
    }

    public final zzb zzzi() {
        super.zzwp();
        zzb zzb = this.zzbtF;
        return zzb == null ? null : new zzb(zzb);
    }

    static /* synthetic */ void zza(zzchz zzchz, zzcic zzcic) {
        super.zzwr().zzJ(super.zzkq().elapsedRealtime());
        if (super.zzwD().zzap(zzcic.zzbtS)) {
            zzcic.zzbtS = false;
        }
    }
}
