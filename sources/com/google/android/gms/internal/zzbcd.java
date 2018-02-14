package com.google.android.gms.internal;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.zza;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.Api.zzc;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.internal.zzal;
import com.google.android.gms.common.internal.zzbr;
import com.google.android.gms.common.internal.zzq;
import com.google.android.gms.common.internal.zzr;
import com.google.android.gms.common.zze;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;

public final class zzbcd implements zzbcw {
    private final Context mContext;
    private final zza<? extends zzctk, zzctl> zzaBe;
    private final zzq zzaCA;
    private final Map<Api<?>, Boolean> zzaCD;
    private final zze zzaCF;
    private ConnectionResult zzaCO;
    private final zzbcx zzaCZ;
    private final Lock zzaCv;
    private int zzaDc;
    private int zzaDd = 0;
    private int zzaDe;
    private final Bundle zzaDf = new Bundle();
    private final Set<zzc> zzaDg = new HashSet();
    private zzctk zzaDh;
    private boolean zzaDi;
    private boolean zzaDj;
    private boolean zzaDk;
    private zzal zzaDl;
    private boolean zzaDm;
    private boolean zzaDn;
    private ArrayList<Future<?>> zzaDo = new ArrayList();

    public zzbcd(zzbcx zzbcx, zzq zzq, Map<Api<?>, Boolean> map, zze zze, zza<? extends zzctk, zzctl> zza, Lock lock, Context context) {
        this.zzaCZ = zzbcx;
        this.zzaCA = zzq;
        this.zzaCD = map;
        this.zzaCF = zze;
        this.zzaBe = zza;
        this.zzaCv = lock;
        this.mContext = context;
    }

    private final void zzad(boolean z) {
        if (this.zzaDh != null) {
            if (this.zzaDh.isConnected() && z) {
                this.zzaDh.zzAq();
            }
            this.zzaDh.disconnect();
            this.zzaDl = null;
        }
    }

    private final boolean zzan(int i) {
        if (this.zzaDd == i) {
            return true;
        }
        Log.w("GoogleApiClientConnecting", this.zzaCZ.zzaCl.zzqg());
        String valueOf = String.valueOf(this);
        Log.w("GoogleApiClientConnecting", new StringBuilder(String.valueOf(valueOf).length() + 23).append("Unexpected callback in ").append(valueOf).toString());
        Log.w("GoogleApiClientConnecting", "mRemainingConnections=" + this.zzaDe);
        valueOf = String.valueOf(zzao(this.zzaDd));
        String valueOf2 = String.valueOf(zzao(i));
        Log.wtf("GoogleApiClientConnecting", new StringBuilder((String.valueOf(valueOf).length() + 70) + String.valueOf(valueOf2).length()).append("GoogleApiClient connecting is in step ").append(valueOf).append(" but received callback for step ").append(valueOf2).toString(), new Exception());
        zze(new ConnectionResult(8, null));
        return false;
    }

    private static String zzao(int i) {
        switch (i) {
            case 0:
                return "STEP_SERVICE_BINDINGS_AND_SIGN_IN";
            case 1:
                return "STEP_GETTING_REMOTE_SERVICE";
            default:
                return "UNKNOWN";
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final void zzb(com.google.android.gms.common.ConnectionResult r5, com.google.android.gms.common.api.Api<?> r6, boolean r7) {
        /*
        r4 = this;
        r3 = 2147483647; // 0x7fffffff float:NaN double:1.060997895E-314;
        r1 = 0;
        r0 = 1;
        if (r7 == 0) goto L_0x0010;
    L_0x0007:
        r2 = r5.hasResolution();
        if (r2 == 0) goto L_0x002a;
    L_0x000d:
        r2 = r0;
    L_0x000e:
        if (r2 == 0) goto L_0x0038;
    L_0x0010:
        r2 = r4.zzaCO;
        if (r2 == 0) goto L_0x0018;
    L_0x0014:
        r2 = r4.zzaDc;
        if (r3 >= r2) goto L_0x0038;
    L_0x0018:
        if (r0 == 0) goto L_0x001e;
    L_0x001a:
        r4.zzaCO = r5;
        r4.zzaDc = r3;
    L_0x001e:
        r0 = r4.zzaCZ;
        r0 = r0.zzaDU;
        r1 = r6.zzpd();
        r0.put(r1, r5);
        return;
    L_0x002a:
        r2 = r5.getErrorCode();
        r2 = com.google.android.gms.common.zze.zzak(r2);
        if (r2 == 0) goto L_0x0036;
    L_0x0034:
        r2 = r0;
        goto L_0x000e;
    L_0x0036:
        r2 = r1;
        goto L_0x000e;
    L_0x0038:
        r0 = r1;
        goto L_0x0018;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.zzbcd.zzb(com.google.android.gms.common.ConnectionResult, com.google.android.gms.common.api.Api, boolean):void");
    }

    private final boolean zzd(ConnectionResult connectionResult) {
        return this.zzaDi && !connectionResult.hasResolution();
    }

    private final void zze(ConnectionResult connectionResult) {
        zzqa();
        zzad(!connectionResult.hasResolution());
        this.zzaCZ.zzg(connectionResult);
        this.zzaCZ.zzaDY.zzc(connectionResult);
    }

    private final boolean zzpW() {
        this.zzaDe--;
        if (this.zzaDe > 0) {
            return false;
        }
        if (this.zzaDe < 0) {
            Log.w("GoogleApiClientConnecting", this.zzaCZ.zzaCl.zzqg());
            Log.wtf("GoogleApiClientConnecting", "GoogleApiClient received too many callbacks for the given step. Clients may be in an unexpected state; GoogleApiClient will now disconnect.", new Exception());
            zze(new ConnectionResult(8, null));
            return false;
        } else if (this.zzaCO == null) {
            return true;
        } else {
            this.zzaCZ.zzaDX = this.zzaDc;
            zze(this.zzaCO);
            return false;
        }
    }

    private final void zzpX() {
        if (this.zzaDe == 0) {
            if (!this.zzaDj || this.zzaDk) {
                ArrayList arrayList = new ArrayList();
                this.zzaDd = 1;
                this.zzaDe = this.zzaCZ.zzaDF.size();
                for (zzc zzc : this.zzaCZ.zzaDF.keySet()) {
                    if (!this.zzaCZ.zzaDU.containsKey(zzc)) {
                        arrayList.add((Api.zze) this.zzaCZ.zzaDF.get(zzc));
                    } else if (zzpW()) {
                        zzpY();
                    }
                }
                if (!arrayList.isEmpty()) {
                    this.zzaDo.add(zzbda.zzqj().submit(new zzbcj(this, arrayList)));
                }
            }
        }
    }

    private final void zzpY() {
        this.zzaCZ.zzqi();
        zzbda.zzqj().execute(new zzbce(this));
        if (this.zzaDh != null) {
            if (this.zzaDm) {
                this.zzaDh.zza(this.zzaDl, this.zzaDn);
            }
            zzad(false);
        }
        for (zzc zzc : this.zzaCZ.zzaDU.keySet()) {
            ((Api.zze) this.zzaCZ.zzaDF.get(zzc)).disconnect();
        }
        this.zzaCZ.zzaDY.zzm(this.zzaDf.isEmpty() ? null : this.zzaDf);
    }

    private final void zzpZ() {
        this.zzaDj = false;
        this.zzaCZ.zzaCl.zzaDG = Collections.emptySet();
        for (zzc zzc : this.zzaDg) {
            if (!this.zzaCZ.zzaDU.containsKey(zzc)) {
                this.zzaCZ.zzaDU.put(zzc, new ConnectionResult(17, null));
            }
        }
    }

    private final void zzqa() {
        ArrayList arrayList = this.zzaDo;
        int size = arrayList.size();
        int i = 0;
        while (i < size) {
            Object obj = arrayList.get(i);
            i++;
            ((Future) obj).cancel(true);
        }
        this.zzaDo.clear();
    }

    public final void begin() {
        this.zzaCZ.zzaDU.clear();
        this.zzaDj = false;
        this.zzaCO = null;
        this.zzaDd = 0;
        this.zzaDi = true;
        this.zzaDk = false;
        this.zzaDm = false;
        Map hashMap = new HashMap();
        for (Api api : this.zzaCD.keySet()) {
            Api.zze zze = (Api.zze) this.zzaCZ.zzaDF.get(api.zzpd());
            boolean booleanValue = ((Boolean) this.zzaCD.get(api)).booleanValue();
            if (zze.zzmv()) {
                this.zzaDj = true;
                if (booleanValue) {
                    this.zzaDg.add(api.zzpd());
                } else {
                    this.zzaDi = false;
                }
            }
            hashMap.put(zze, new zzbcf(this, api, booleanValue));
        }
        if (this.zzaDj) {
            this.zzaCA.zzc(Integer.valueOf(System.identityHashCode(this.zzaCZ.zzaCl)));
            ConnectionCallbacks zzbcm = new zzbcm();
            this.zzaDh = (zzctk) this.zzaBe.zza(this.mContext, this.zzaCZ.zzaCl.getLooper(), this.zzaCA, this.zzaCA.zzrt(), zzbcm, zzbcm);
        }
        this.zzaDe = this.zzaCZ.zzaDF.size();
        this.zzaDo.add(zzbda.zzqj().submit(new zzbcg(this, hashMap)));
    }

    public final void connect() {
    }

    public final boolean disconnect() {
        zzqa();
        zzad(true);
        this.zzaCZ.zzg(null);
        return true;
    }

    public final void onConnected(Bundle bundle) {
        if (zzan(1)) {
            if (bundle != null) {
                this.zzaDf.putAll(bundle);
            }
            if (zzpW()) {
                zzpY();
            }
        }
    }

    public final void onConnectionSuspended(int i) {
        zze(new ConnectionResult(8, null));
    }

    public final void zza(ConnectionResult connectionResult, Api<?> api, boolean z) {
        if (zzan(1)) {
            zzb(connectionResult, api, z);
            if (zzpW()) {
                zzpY();
            }
        }
    }

    public final <A extends zzb, T extends zzbay<? extends Result, A>> T zze(T t) {
        throw new IllegalStateException("GoogleApiClient is not connected yet.");
    }

    static /* synthetic */ Set zzg(zzbcd zzbcd) {
        if (zzbcd.zzaCA == null) {
            return Collections.emptySet();
        }
        Set hashSet = new HashSet(zzbcd.zzaCA.zzrn());
        Map zzrp = zzbcd.zzaCA.zzrp();
        for (Api api : zzrp.keySet()) {
            if (!zzbcd.zzaCZ.zzaDU.containsKey(api.zzpd())) {
                hashSet.addAll(((zzr) zzrp.get(api)).zzame);
            }
        }
        return hashSet;
    }

    static /* synthetic */ void zza(zzbcd zzbcd, zzctx zzctx) {
        if (zzbcd.zzan(0)) {
            ConnectionResult zzpz = zzctx.zzpz();
            if (zzpz.isSuccess()) {
                zzbr zzAx = zzctx.zzAx();
                ConnectionResult zzpz2 = zzAx.zzpz();
                if (zzpz2.isSuccess()) {
                    zzbcd.zzaDk = true;
                    zzbcd.zzaDl = zzAx.zzrH();
                    zzbcd.zzaDm = zzAx.zzrI();
                    zzbcd.zzaDn = zzAx.zzrJ();
                    zzbcd.zzpX();
                    return;
                }
                String valueOf = String.valueOf(zzpz2);
                Log.wtf("GoogleApiClientConnecting", new StringBuilder(String.valueOf(valueOf).length() + 48).append("Sign-in succeeded with resolve account failure: ").append(valueOf).toString(), new Exception());
                zzbcd.zze(zzpz2);
            } else if (zzbcd.zzd(zzpz)) {
                zzbcd.zzpZ();
                zzbcd.zzpX();
            } else {
                zzbcd.zze(zzpz);
            }
        }
    }
}
