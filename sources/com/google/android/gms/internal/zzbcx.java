package com.google.android.gms.internal;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.zza;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.Api.zzc;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.internal.zzq;
import com.google.android.gms.common.zze;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public final class zzbcx implements zzbbj, zzbdp {
    private final Context mContext;
    private zza<? extends zzctk, zzctl> zzaBe;
    private zzq zzaCA;
    private Map<Api<?>, Boolean> zzaCD;
    private final zze zzaCF;
    final zzbcp zzaCl;
    private final Lock zzaCv;
    final Map<zzc<?>, Api.zze> zzaDF;
    private final Condition zzaDS;
    private final zzbcz zzaDT;
    final Map<zzc<?>, ConnectionResult> zzaDU = new HashMap();
    private volatile zzbcw zzaDV;
    private ConnectionResult zzaDW = null;
    int zzaDX;
    final zzbdq zzaDY;

    public zzbcx(Context context, zzbcp zzbcp, Lock lock, Looper looper, zze zze, Map<zzc<?>, Api.zze> map, zzq zzq, Map<Api<?>, Boolean> map2, zza<? extends zzctk, zzctl> zza, ArrayList<zzbbi> arrayList, zzbdq zzbdq) {
        this.mContext = context;
        this.zzaCv = lock;
        this.zzaCF = zze;
        this.zzaDF = map;
        this.zzaCA = zzq;
        this.zzaCD = map2;
        this.zzaBe = zza;
        this.zzaCl = zzbcp;
        this.zzaDY = zzbdq;
        ArrayList arrayList2 = arrayList;
        int size = arrayList2.size();
        int i = 0;
        while (i < size) {
            Object obj = arrayList2.get(i);
            i++;
            ((zzbbi) obj).zza(this);
        }
        this.zzaDT = new zzbcz(this, looper);
        this.zzaDS = lock.newCondition();
        this.zzaDV = new zzbco(this);
    }

    public final void connect() {
        this.zzaDV.connect();
    }

    public final void disconnect() {
        if (this.zzaDV.disconnect()) {
            this.zzaDU.clear();
        }
    }

    public final void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        String concat = String.valueOf(str).concat("  ");
        printWriter.append(str).append("mState=").println(this.zzaDV);
        for (Api api : this.zzaCD.keySet()) {
            printWriter.append(str).append(api.getName()).println(":");
            ((Api.zze) this.zzaDF.get(api.zzpd())).dump$ec96877(concat, printWriter);
        }
    }

    public final boolean isConnected() {
        return this.zzaDV instanceof zzbca;
    }

    public final void onConnected(Bundle bundle) {
        this.zzaCv.lock();
        try {
            this.zzaDV.onConnected(bundle);
        } finally {
            this.zzaCv.unlock();
        }
    }

    public final void onConnectionSuspended(int i) {
        this.zzaCv.lock();
        try {
            this.zzaDV.onConnectionSuspended(i);
        } finally {
            this.zzaCv.unlock();
        }
    }

    public final void zza(ConnectionResult connectionResult, Api<?> api, boolean z) {
        this.zzaCv.lock();
        try {
            this.zzaDV.zza(connectionResult, api, z);
        } finally {
            this.zzaCv.unlock();
        }
    }

    final void zza(zzbcy zzbcy) {
        this.zzaDT.sendMessage(this.zzaDT.obtainMessage(1, zzbcy));
    }

    final void zza(RuntimeException runtimeException) {
        this.zzaDT.sendMessage(this.zzaDT.obtainMessage(2, runtimeException));
    }

    public final <A extends zzb, T extends zzbay<? extends Result, A>> T zze(T t) {
        t.zzpC();
        return this.zzaDV.zze(t);
    }

    final void zzg(ConnectionResult connectionResult) {
        this.zzaCv.lock();
        try {
            this.zzaDW = connectionResult;
            this.zzaDV = new zzbco(this);
            this.zzaDV.begin();
            this.zzaDS.signalAll();
        } finally {
            this.zzaCv.unlock();
        }
    }

    final void zzqh() {
        this.zzaCv.lock();
        try {
            this.zzaDV = new zzbcd(this, this.zzaCA, this.zzaCD, this.zzaCF, this.zzaBe, this.zzaCv, this.mContext);
            this.zzaDV.begin();
            this.zzaDS.signalAll();
        } finally {
            this.zzaCv.unlock();
        }
    }

    final void zzqi() {
        this.zzaCv.lock();
        try {
            this.zzaCl.zzqe();
            this.zzaDV = new zzbca(this);
            this.zzaDV.begin();
            this.zzaDS.signalAll();
        } finally {
            this.zzaCv.unlock();
        }
    }
}
