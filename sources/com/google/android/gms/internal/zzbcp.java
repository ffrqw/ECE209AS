package com.google.android.gms.internal;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.zza;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.Api.zzc;
import com.google.android.gms.common.api.Api.zze;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.internal.zzad;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.internal.zzq;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.locks.Lock;

public final class zzbcp extends GoogleApiClient implements zzbdq {
    private final Context mContext;
    private final int zzaBb;
    private final GoogleApiAvailability zzaBd;
    private zza<? extends zzctk, zzctl> zzaBe;
    private boolean zzaBh;
    private zzq zzaCA;
    private Map<Api<?>, Boolean> zzaCD;
    final Queue<zzbay<?, ?>> zzaCJ = new LinkedList();
    private final Lock zzaCv;
    private volatile boolean zzaDA;
    private long zzaDB = 120000;
    private long zzaDC = 5000;
    private final zzbcu zzaDD;
    private zzbdk zzaDE;
    final Map<zzc<?>, zze> zzaDF;
    Set<Scope> zzaDG = new HashSet();
    private final zzbea zzaDH = new zzbea();
    private final ArrayList<zzbbi> zzaDI;
    private Integer zzaDJ = null;
    Set<zzbes> zzaDK = null;
    final zzbev zzaDL;
    private final zzad zzaDM = new zzbcq(this);
    private final zzac zzaDy;
    private zzbdp zzaDz = null;
    private final Looper zzrM;

    public zzbcp(Context context, Lock lock, Looper looper, zzq zzq, GoogleApiAvailability googleApiAvailability, zza<? extends zzctk, zzctl> zza, Map<Api<?>, Boolean> map, List<ConnectionCallbacks> list, List<OnConnectionFailedListener> list2, Map<zzc<?>, zze> map2, int i, int i2, ArrayList<zzbbi> arrayList) {
        this.mContext = context;
        this.zzaCv = lock;
        this.zzaBh = false;
        this.zzaDy = new zzac(looper, this.zzaDM);
        this.zzrM = looper;
        this.zzaDD = new zzbcu(this, looper);
        this.zzaBd = googleApiAvailability;
        this.zzaBb = i;
        if (this.zzaBb >= 0) {
            this.zzaDJ = Integer.valueOf(i2);
        }
        this.zzaCD = map;
        this.zzaDF = map2;
        this.zzaDI = arrayList;
        this.zzaDL = new zzbev(this.zzaDF);
        for (ConnectionCallbacks registerConnectionCallbacks : list) {
            this.zzaDy.registerConnectionCallbacks(registerConnectionCallbacks);
        }
        for (OnConnectionFailedListener registerConnectionFailedListener : list2) {
            this.zzaDy.registerConnectionFailedListener(registerConnectionFailedListener);
        }
        this.zzaCA = zzq;
        this.zzaBe = zza;
    }

    public static int zza(Iterable<zze> iterable, boolean z) {
        int i = 0;
        for (zze zzmv : iterable) {
            i = zzmv.zzmv() ? 1 : i;
        }
        return i != 0 ? 1 : 3;
    }

    private final void zzap(int i) {
        if (this.zzaDJ == null) {
            this.zzaDJ = Integer.valueOf(i);
        } else if (this.zzaDJ.intValue() != i) {
            String valueOf = String.valueOf(zzaq(i));
            String valueOf2 = String.valueOf(zzaq(this.zzaDJ.intValue()));
            throw new IllegalStateException(new StringBuilder((String.valueOf(valueOf).length() + 51) + String.valueOf(valueOf2).length()).append("Cannot use sign-in mode: ").append(valueOf).append(". Mode was already set to ").append(valueOf2).toString());
        }
        if (this.zzaDz == null) {
            Object obj = null;
            for (zze zzmv : this.zzaDF.values()) {
                obj = zzmv.zzmv() ? 1 : obj;
            }
            switch (this.zzaDJ.intValue()) {
                case 1:
                    if (obj == null) {
                        throw new IllegalStateException("SIGN_IN_MODE_REQUIRED cannot be used on a GoogleApiClient that does not contain any authenticated APIs. Use connect() instead.");
                    }
                    break;
                case 2:
                    if (obj != null) {
                        this.zzaDz = zzbbk.zza(this.mContext, this, this.zzaCv, this.zzrM, this.zzaBd, this.zzaDF, this.zzaCA, this.zzaCD, this.zzaBe, this.zzaDI);
                        return;
                    }
                    break;
            }
            this.zzaDz = new zzbcx(this.mContext, this, this.zzaCv, this.zzrM, this.zzaBd, this.zzaDF, this.zzaCA, this.zzaCD, this.zzaBe, this.zzaDI, this);
        }
    }

    private static String zzaq(int i) {
        switch (i) {
            case 1:
                return "SIGN_IN_MODE_REQUIRED";
            case 2:
                return "SIGN_IN_MODE_OPTIONAL";
            case 3:
                return "SIGN_IN_MODE_NONE";
            default:
                return "UNKNOWN";
        }
    }

    private final void zzqc() {
        this.zzaDy.zzrA();
        this.zzaDz.connect();
    }

    public final void connect() {
        boolean z = false;
        this.zzaCv.lock();
        try {
            if (this.zzaBb >= 0) {
                zzbo.zza(this.zzaDJ != null, "Sign-in mode should have been set explicitly by auto-manage.");
            } else if (this.zzaDJ == null) {
                this.zzaDJ = Integer.valueOf(zza(this.zzaDF.values(), false));
            } else if (this.zzaDJ.intValue() == 2) {
                throw new IllegalStateException("Cannot call connect() when SignInMode is set to SIGN_IN_MODE_OPTIONAL. Call connect(SIGN_IN_MODE_OPTIONAL) instead.");
            }
            int intValue = this.zzaDJ.intValue();
            this.zzaCv.lock();
            if (intValue == 3 || intValue == 1 || intValue == 2) {
                z = true;
            }
            zzbo.zzb(z, "Illegal sign-in mode: " + intValue);
            zzap(intValue);
            zzqc();
        } catch (Throwable th) {
        } finally {
            this.zzaCv.unlock();
        }
        this.zzaCv.unlock();
    }

    public final void disconnect() {
        this.zzaCv.lock();
        try {
            this.zzaDL.release();
            if (this.zzaDz != null) {
                this.zzaDz.disconnect();
            }
            this.zzaDH.release();
            for (zzbay zzbay : this.zzaCJ) {
                zzbay.zza(null);
                zzbay.cancel();
            }
            this.zzaCJ.clear();
            if (this.zzaDz != null) {
                zzqe();
                this.zzaDy.zzrz();
                this.zzaCv.unlock();
            }
        } finally {
            this.zzaCv.unlock();
        }
    }

    public final void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.append(str).append("mContext=").println(this.mContext);
        printWriter.append(str).append("mResuming=").print(this.zzaDA);
        printWriter.append(" mWorkQueue.size()=").print(this.zzaCJ.size());
        printWriter.append(" mUnconsumedApiCalls.size()=").println(this.zzaDL.zzaFl.size());
        if (this.zzaDz != null) {
            this.zzaDz.dump(str, fileDescriptor, printWriter, strArr);
        }
    }

    public final Looper getLooper() {
        return this.zzrM;
    }

    public final boolean isConnected() {
        return this.zzaDz != null && this.zzaDz.isConnected();
    }

    public final void registerConnectionFailedListener(OnConnectionFailedListener onConnectionFailedListener) {
        this.zzaDy.registerConnectionFailedListener(onConnectionFailedListener);
    }

    public final void unregisterConnectionFailedListener(OnConnectionFailedListener onConnectionFailedListener) {
        this.zzaDy.unregisterConnectionFailedListener(onConnectionFailedListener);
    }

    public final <C extends zze> C zza(zzc<C> zzc) {
        Object obj = (zze) this.zzaDF.get(zzc);
        zzbo.zzb(obj, (Object) "Appropriate Api was not requested.");
        return obj;
    }

    public final void zzc(ConnectionResult connectionResult) {
        if (!com.google.android.gms.common.zze.zze(this.mContext, connectionResult.getErrorCode())) {
            zzqe();
        }
        if (!this.zzaDA) {
            this.zzaDy.zzk(connectionResult);
            this.zzaDy.zzrz();
        }
    }

    public final <A extends zzb, T extends zzbay<? extends Result, A>> T zze(T t) {
        zzbo.zzb(t.zzpd() != null, (Object) "This task can not be executed (it's probably a Batch or malformed)");
        boolean containsKey = this.zzaDF.containsKey(t.zzpd());
        String name = t.zzpg() != null ? t.zzpg().getName() : "the API";
        zzbo.zzb(containsKey, new StringBuilder(String.valueOf(name).length() + 65).append("GoogleApiClient is not configured to use ").append(name).append(" required for this call.").toString());
        this.zzaCv.lock();
        try {
            if (this.zzaDz == null) {
                throw new IllegalStateException("GoogleApiClient is not connected yet.");
            }
            if (this.zzaDA) {
                this.zzaCJ.add(t);
                while (!this.zzaCJ.isEmpty()) {
                    zzbay zzbay = (zzbay) this.zzaCJ.remove();
                    this.zzaDL.zzb(zzbay);
                    zzbay.zzr(Status.zzaBo);
                }
            } else {
                t = this.zzaDz.zze(t);
                this.zzaCv.unlock();
            }
            return t;
        } finally {
            this.zzaCv.unlock();
        }
    }

    public final void zze(int i, boolean z) {
        if (!(i != 1 || z || this.zzaDA)) {
            this.zzaDA = true;
            if (this.zzaDE == null) {
                this.zzaDE = GoogleApiAvailability.zza(this.mContext.getApplicationContext(), new zzbcv(this));
            }
            this.zzaDD.sendMessageDelayed(this.zzaDD.obtainMessage(1), this.zzaDB);
            this.zzaDD.sendMessageDelayed(this.zzaDD.obtainMessage(2), this.zzaDC);
        }
        this.zzaDL.zzqM();
        this.zzaDy.zzaA(i);
        this.zzaDy.zzrz();
        if (i == 2) {
            zzqc();
        }
    }

    public final void zzm(Bundle bundle) {
        while (!this.zzaCJ.isEmpty()) {
            zze((zzbay) this.zzaCJ.remove());
        }
        this.zzaDy.zzn(bundle);
    }

    final boolean zzqe() {
        if (!this.zzaDA) {
            return false;
        }
        this.zzaDA = false;
        this.zzaDD.removeMessages(2);
        this.zzaDD.removeMessages(1);
        if (this.zzaDE != null) {
            this.zzaDE.unregister();
            this.zzaDE = null;
        }
        return true;
    }

    final boolean zzqf() {
        boolean z = false;
        this.zzaCv.lock();
        try {
            if (this.zzaDK != null) {
                if (!this.zzaDK.isEmpty()) {
                    z = true;
                }
                this.zzaCv.unlock();
            }
            return z;
        } finally {
            this.zzaCv.unlock();
        }
    }

    final String zzqg() {
        Writer stringWriter = new StringWriter();
        dump("", null, new PrintWriter(stringWriter), null);
        return stringWriter.toString();
    }

    static /* synthetic */ void zza(zzbcp zzbcp) {
        zzbcp.zzaCv.lock();
        try {
            if (zzbcp.zzaDA) {
                zzbcp.zzqc();
            }
            zzbcp.zzaCv.unlock();
        } catch (Throwable th) {
            zzbcp.zzaCv.unlock();
        }
    }

    static /* synthetic */ void zzb(zzbcp zzbcp) {
        zzbcp.zzaCv.lock();
        try {
            if (zzbcp.zzqe()) {
                zzbcp.zzqc();
            }
            zzbcp.zzaCv.unlock();
        } catch (Throwable th) {
            zzbcp.zzaCv.unlock();
        }
    }
}
