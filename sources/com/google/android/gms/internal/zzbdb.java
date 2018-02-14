package com.google.android.gms.internal;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.util.zza;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public final class zzbdb implements Callback {
    public static final Status zzaEc = new Status(4, "Sign-out occurred while this API call was in progress.");
    private static final Status zzaEd = new Status(4, "The user must be signed in to make this API call.");
    private static zzbdb zzaEf;
    private static final Object zzuF = new Object();
    private final Context mContext;
    private final Handler mHandler;
    private final GoogleApiAvailability zzaBd;
    private final Map<zzbat<?>, zzbdd<?>> zzaCB = new ConcurrentHashMap(5, 0.75f, 1);
    private long zzaDB = 120000;
    private long zzaDC = 5000;
    private long zzaEe = 10000;
    private int zzaEg = -1;
    private final AtomicInteger zzaEh = new AtomicInteger(1);
    private final AtomicInteger zzaEi = new AtomicInteger(0);
    private zzbbw zzaEj = null;
    private final Set<zzbat<?>> zzaEk = new zza();
    private final Set<zzbat<?>> zzaEl = new zza();

    private zzbdb(Context context, Looper looper, GoogleApiAvailability googleApiAvailability) {
        this.mContext = context;
        this.mHandler = new Handler(looper, this);
        this.zzaBd = googleApiAvailability;
        this.mHandler.sendMessage(this.mHandler.obtainMessage(6));
    }

    public static zzbdb zzay(Context context) {
        zzbdb zzbdb;
        synchronized (zzuF) {
            if (zzaEf == null) {
                HandlerThread handlerThread = new HandlerThread("GoogleApiHandler", 9);
                handlerThread.start();
                zzaEf = new zzbdb(context.getApplicationContext(), handlerThread.getLooper(), GoogleApiAvailability.getInstance());
            }
            zzbdb = zzaEf;
        }
        return zzbdb;
    }

    private final void zzc(GoogleApi<?> googleApi) {
        zzbat zzph = googleApi.zzph();
        zzbdd zzbdd = (zzbdd) this.zzaCB.get(zzph);
        if (zzbdd == null) {
            zzbdd = new zzbdd(this, googleApi);
            this.zzaCB.put(zzph, zzbdd);
        }
        if (zzbdd.zzmv()) {
            this.zzaEl.add(zzph);
        }
        zzbdd.connect();
    }

    private final void zzqn() {
        for (zzbat remove : this.zzaEl) {
            ((zzbdd) this.zzaCB.remove(remove)).signOut();
        }
        this.zzaEl.clear();
    }

    public final boolean handleMessage(Message message) {
        zzbdd zzbdd;
        switch (message.what) {
            case 1:
                this.zzaEe = ((Boolean) message.obj).booleanValue() ? 10000 : 300000;
                this.mHandler.removeMessages(12);
                for (zzbat obtainMessage : this.zzaCB.keySet()) {
                    this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(12, obtainMessage), this.zzaEe);
                }
                break;
            case 2:
                zzbav zzbav = (zzbav) message.obj;
                for (zzbat zzbat : zzbav.zzpt()) {
                    zzbdd zzbdd2 = (zzbdd) this.zzaCB.get(zzbat);
                    if (zzbdd2 == null) {
                        zzbav.zza(zzbat, new ConnectionResult(13));
                        break;
                    } else if (zzbdd2.isConnected()) {
                        zzbav.zza(zzbat, ConnectionResult.zzazX);
                    } else if (zzbdd2.zzqu() != null) {
                        zzbav.zza(zzbat, zzbdd2.zzqu());
                    } else {
                        zzbdd2.zza(zzbav);
                    }
                }
                break;
            case 3:
                for (zzbdd zzbdd3 : this.zzaCB.values()) {
                    zzbdd3.zzqt();
                    zzbdd3.connect();
                }
                break;
            case 4:
            case 8:
            case 13:
                zzbed zzbed = (zzbed) message.obj;
                zzbdd = (zzbdd) this.zzaCB.get(zzbed.zzaET.zzph());
                if (zzbdd == null) {
                    zzc(zzbed.zzaET);
                    zzbdd = (zzbdd) this.zzaCB.get(zzbed.zzaET.zzph());
                }
                if (zzbdd.zzmv() && this.zzaEi.get() != zzbed.zzaES) {
                    zzbed.zzaER.zzp(zzaEc);
                    zzbdd.signOut();
                    break;
                }
                zzbdd.zza(zzbed.zzaER);
                break;
                break;
            case 5:
                String valueOf;
                String valueOf2;
                int i = message.arg1;
                ConnectionResult connectionResult = (ConnectionResult) message.obj;
                for (zzbdd zzbdd4 : this.zzaCB.values()) {
                    if (zzbdd4.getInstanceId() == i) {
                        if (zzbdd4 != null) {
                            Log.wtf("GoogleApiManager", "Could not find API instance " + i + " while trying to fail enqueued calls.", new Exception());
                            break;
                        }
                        valueOf = String.valueOf(this.zzaBd.getErrorString(connectionResult.getErrorCode()));
                        valueOf2 = String.valueOf(connectionResult.getErrorMessage());
                        zzbdd4.zzt(new Status(17, new StringBuilder((String.valueOf(valueOf).length() + 69) + String.valueOf(valueOf2).length()).append("Error resolution was canceled by the user, original error message: ").append(valueOf).append(": ").append(valueOf2).toString()));
                        break;
                    }
                }
                zzbdd4 = null;
                if (zzbdd4 != null) {
                    Log.wtf("GoogleApiManager", "Could not find API instance " + i + " while trying to fail enqueued calls.", new Exception());
                } else {
                    valueOf = String.valueOf(this.zzaBd.getErrorString(connectionResult.getErrorCode()));
                    valueOf2 = String.valueOf(connectionResult.getErrorMessage());
                    zzbdd4.zzt(new Status(17, new StringBuilder((String.valueOf(valueOf).length() + 69) + String.valueOf(valueOf2).length()).append("Error resolution was canceled by the user, original error message: ").append(valueOf).append(": ").append(valueOf2).toString()));
                }
            case 6:
                if (this.mContext.getApplicationContext() instanceof Application) {
                    zzbaw.zza((Application) this.mContext.getApplicationContext());
                    zzbaw.zzpv().zza(new zzbdc(this));
                    if (!zzbaw.zzpv().zzab$138603()) {
                        this.zzaEe = 300000;
                        break;
                    }
                }
                break;
            case 7:
                zzc((GoogleApi) message.obj);
                break;
            case 9:
                if (this.zzaCB.containsKey(message.obj)) {
                    ((zzbdd) this.zzaCB.get(message.obj)).resume();
                    break;
                }
                break;
            case 10:
                zzqn();
                break;
            case 11:
                if (this.zzaCB.containsKey(message.obj)) {
                    ((zzbdd) this.zzaCB.get(message.obj)).zzqd();
                    break;
                }
                break;
            case 12:
                if (this.zzaCB.containsKey(message.obj)) {
                    ((zzbdd) this.zzaCB.get(message.obj)).zzqx();
                    break;
                }
                break;
            default:
                Log.w("GoogleApiManager", "Unknown message id: " + message.what);
                return false;
        }
        return true;
    }

    public final void zza(ConnectionResult connectionResult, int i) {
        if (!zzc(connectionResult, i)) {
            this.mHandler.sendMessage(this.mHandler.obtainMessage(5, i, 0, connectionResult));
        }
    }

    public final void zza(zzbbw zzbbw) {
        synchronized (zzuF) {
            if (this.zzaEj != zzbbw) {
                this.zzaEj = zzbbw;
                this.zzaEk.clear();
                this.zzaEk.addAll(zzbbw.zzpR());
            }
        }
    }

    final void zzb(zzbbw zzbbw) {
        synchronized (zzuF) {
            if (this.zzaEj == zzbbw) {
                this.zzaEj = null;
                this.zzaEk.clear();
            }
        }
    }

    final boolean zzc(ConnectionResult connectionResult, int i) {
        return this.zzaBd.zza(this.mContext, connectionResult, i);
    }

    public final void zzps() {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(3));
    }
}
