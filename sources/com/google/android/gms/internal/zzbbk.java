package com.google.android.gms.internal;

import android.app.PendingIntent;
import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.zza;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.Api.zzc;
import com.google.android.gms.common.api.Api.zze;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.internal.zzq;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.locks.Lock;

final class zzbbk implements zzbdp {
    private final Context mContext;
    private final zzbcp zzaCl;
    private final zzbcx zzaCm;
    private final zzbcx zzaCn;
    private final Map<zzc<?>, zzbcx> zzaCo;
    private final Set<Object> zzaCp = Collections.newSetFromMap(new WeakHashMap());
    private final zze zzaCq;
    private Bundle zzaCr;
    private ConnectionResult zzaCs = null;
    private ConnectionResult zzaCt = null;
    private boolean zzaCu = false;
    private final Lock zzaCv;
    private int zzaCw = 0;
    private final Looper zzrM;

    private zzbbk(Context context, zzbcp zzbcp, Lock lock, Looper looper, com.google.android.gms.common.zze zze, Map<zzc<?>, zze> map, Map<zzc<?>, zze> map2, zzq zzq, zza<? extends zzctk, zzctl> zza, zze zze2, ArrayList<zzbbi> arrayList, ArrayList<zzbbi> arrayList2, Map<Api<?>, Boolean> map3, Map<Api<?>, Boolean> map4) {
        this.mContext = context;
        this.zzaCl = zzbcp;
        this.zzaCv = lock;
        this.zzrM = looper;
        this.zzaCq = zze2;
        this.zzaCm = new zzbcx(context, this.zzaCl, lock, looper, zze, map2, null, map4, null, arrayList2, new zzbbm());
        this.zzaCn = new zzbcx(context, this.zzaCl, lock, looper, zze, map, zzq, map3, zza, arrayList, new zzbbn());
        Map arrayMap = new ArrayMap();
        for (zzc put : map2.keySet()) {
            arrayMap.put(put, this.zzaCm);
        }
        for (zzc put2 : map.keySet()) {
            arrayMap.put(put2, this.zzaCn);
        }
        this.zzaCo = Collections.unmodifiableMap(arrayMap);
    }

    public static zzbbk zza(Context context, zzbcp zzbcp, Lock lock, Looper looper, com.google.android.gms.common.zze zze, Map<zzc<?>, zze> map, zzq zzq, Map<Api<?>, Boolean> map2, zza<? extends zzctk, zzctl> zza, ArrayList<zzbbi> arrayList) {
        Map arrayMap = new ArrayMap();
        Map arrayMap2 = new ArrayMap();
        for (Entry entry : map.entrySet()) {
            zze zze2 = (zze) entry.getValue();
            if (zze2.zzmv()) {
                arrayMap.put((zzc) entry.getKey(), zze2);
            } else {
                arrayMap2.put((zzc) entry.getKey(), zze2);
            }
        }
        zzbo.zza(!arrayMap.isEmpty(), "CompositeGoogleApiClient should not be used without any APIs that require sign-in.");
        Map arrayMap3 = new ArrayMap();
        Map arrayMap4 = new ArrayMap();
        for (Api api : map2.keySet()) {
            zzc zzpd = api.zzpd();
            if (arrayMap.containsKey(zzpd)) {
                arrayMap3.put(api, (Boolean) map2.get(api));
            } else if (arrayMap2.containsKey(zzpd)) {
                arrayMap4.put(api, (Boolean) map2.get(api));
            } else {
                throw new IllegalStateException("Each API in the isOptionalMap must have a corresponding client in the clients map.");
            }
        }
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        ArrayList arrayList4 = arrayList;
        int size = arrayList4.size();
        int i = 0;
        while (i < size) {
            Object obj = arrayList4.get(i);
            i++;
            zzbbi zzbbi = (zzbbi) obj;
            if (arrayMap3.containsKey(zzbbi.zzayW)) {
                arrayList2.add(zzbbi);
            } else if (arrayMap4.containsKey(zzbbi.zzayW)) {
                arrayList3.add(zzbbi);
            } else {
                throw new IllegalStateException("Each ClientCallbacks must have a corresponding API in the isOptionalMap");
            }
        }
        return new zzbbk(context, zzbcp, lock, looper, zze, arrayMap, arrayMap2, zzq, zza, null, arrayList2, arrayList3, arrayMap3, arrayMap4);
    }

    private final void zza(ConnectionResult connectionResult) {
        switch (this.zzaCw) {
            case 1:
                break;
            case 2:
                this.zzaCl.zzc(connectionResult);
                break;
            default:
                Log.wtf("CompositeGAC", "Attempted to call failure callbacks in CONNECTION_MODE_NONE. Callbacks should be disabled via GmsClientSupervisor", new Exception());
                break;
        }
        zzpG();
        this.zzaCw = 0;
    }

    static /* synthetic */ void zza(zzbbk zzbbk, int i, boolean z) {
        zzbbk.zzaCl.zze(i, z);
        zzbbk.zzaCt = null;
        zzbbk.zzaCs = null;
    }

    private static boolean zzb(ConnectionResult connectionResult) {
        return connectionResult != null && connectionResult.isSuccess();
    }

    private final void zzpG() {
        Iterator it = this.zzaCp.iterator();
        while (it.hasNext()) {
            it.next();
        }
        this.zzaCp.clear();
    }

    private final boolean zzpH() {
        return this.zzaCt != null && this.zzaCt.getErrorCode() == 4;
    }

    public final void connect() {
        this.zzaCw = 2;
        this.zzaCu = false;
        this.zzaCt = null;
        this.zzaCs = null;
        this.zzaCm.connect();
        this.zzaCn.connect();
    }

    public final void disconnect() {
        this.zzaCt = null;
        this.zzaCs = null;
        this.zzaCw = 0;
        this.zzaCm.disconnect();
        this.zzaCn.disconnect();
        zzpG();
    }

    public final void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.append(str).append("authClient").println(":");
        this.zzaCn.dump(String.valueOf(str).concat("  "), fileDescriptor, printWriter, strArr);
        printWriter.append(str).append("anonClient").println(":");
        this.zzaCm.dump(String.valueOf(str).concat("  "), fileDescriptor, printWriter, strArr);
    }

    public final boolean isConnected() {
        boolean z = true;
        this.zzaCv.lock();
        try {
            if (!(this.zzaCm.isConnected() && (this.zzaCn.isConnected() || zzpH() || this.zzaCw == 1))) {
                z = false;
            }
            this.zzaCv.unlock();
            return z;
        } catch (Throwable th) {
            this.zzaCv.unlock();
        }
    }

    public final <A extends zzb, T extends zzbay<? extends Result, A>> T zze(T t) {
        zzc zzpd = t.zzpd();
        zzbo.zzb(this.zzaCo.containsKey(zzpd), (Object) "GoogleApiClient is not configured to use the API required for this call.");
        if (!((zzbcx) this.zzaCo.get(zzpd)).equals(this.zzaCn)) {
            return this.zzaCm.zze(t);
        }
        if (!zzpH()) {
            return this.zzaCn.zze(t);
        }
        t.zzr(new Status(4, null, this.zzaCq == null ? null : PendingIntent.getActivity(this.mContext, System.identityHashCode(this.zzaCl), this.zzaCq.zzmH(), 134217728)));
        return t;
    }

    static /* synthetic */ void zzb(zzbbk zzbbk) {
        if (zzb(zzbbk.zzaCs)) {
            if (zzb(zzbbk.zzaCt) || zzbbk.zzpH()) {
                switch (zzbbk.zzaCw) {
                    case 1:
                        break;
                    case 2:
                        zzbbk.zzaCl.zzm(zzbbk.zzaCr);
                        break;
                    default:
                        Log.wtf("CompositeGAC", "Attempted to call success callbacks in CONNECTION_MODE_NONE. Callbacks should be disabled via GmsClientSupervisor", new AssertionError());
                        break;
                }
                zzbbk.zzpG();
                zzbbk.zzaCw = 0;
            } else if (zzbbk.zzaCt == null) {
            } else {
                if (zzbbk.zzaCw == 1) {
                    zzbbk.zzpG();
                    return;
                }
                zzbbk.zza(zzbbk.zzaCt);
                zzbbk.zzaCm.disconnect();
            }
        } else if (zzbbk.zzaCs != null && zzb(zzbbk.zzaCt)) {
            zzbbk.zzaCn.disconnect();
            zzbbk.zza(zzbbk.zzaCs);
        } else if (zzbbk.zzaCs != null && zzbbk.zzaCt != null) {
            ConnectionResult connectionResult = zzbbk.zzaCs;
            if (zzbbk.zzaCn.zzaDX < zzbbk.zzaCm.zzaDX) {
                connectionResult = zzbbk.zzaCt;
            }
            zzbbk.zza(connectionResult);
        }
    }

    static /* synthetic */ void zza(zzbbk zzbbk, Bundle bundle) {
        if (zzbbk.zzaCr == null) {
            zzbbk.zzaCr = bundle;
        } else if (bundle != null) {
            zzbbk.zzaCr.putAll(bundle);
        }
    }
}
