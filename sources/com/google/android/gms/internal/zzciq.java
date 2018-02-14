package com.google.android.gms.internal;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import android.os.RemoteException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.internal.zzf;
import com.google.android.gms.common.internal.zzg;
import com.google.android.gms.common.stats.zza;

public final class zzciq implements ServiceConnection, zzf, zzg {
    final /* synthetic */ zzcid zzbua;
    private volatile boolean zzbuh;
    private volatile zzcfk zzbui;

    protected zzciq(zzcid zzcid) {
        this.zzbua = zzcid;
    }

    public final void onConnected(Bundle bundle) {
        zzbo.zzcz("MeasurementServiceConnection.onConnected");
        synchronized (this) {
            try {
                zzcfd zzcfd = (zzcfd) this.zzbui.zzrf();
                this.zzbui = null;
                this.zzbua.zzwE().zzj(new zzcit(this, zzcfd));
            } catch (DeadObjectException e) {
                this.zzbui = null;
                this.zzbuh = false;
            } catch (IllegalStateException e2) {
                this.zzbui = null;
                this.zzbuh = false;
            }
        }
    }

    public final void onConnectionFailed(ConnectionResult connectionResult) {
        zzbo.zzcz("MeasurementServiceConnection.onConnectionFailed");
        zzcfl zzyQ = this.zzbua.zzboe.zzyQ();
        if (zzyQ != null) {
            zzyQ.zzyz().zzj("Service connection failed", connectionResult);
        }
        synchronized (this) {
            this.zzbuh = false;
            this.zzbui = null;
        }
        this.zzbua.zzwE().zzj(new zzciv(this));
    }

    public final void onConnectionSuspended(int i) {
        zzbo.zzcz("MeasurementServiceConnection.onConnectionSuspended");
        this.zzbua.zzwF().zzyC().log("Service connection suspended");
        this.zzbua.zzwE().zzj(new zzciu(this));
    }

    public final void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        zzcfd zzcfd;
        zzbo.zzcz("MeasurementServiceConnection.onServiceConnected");
        synchronized (this) {
            if (iBinder == null) {
                this.zzbuh = false;
                this.zzbua.zzwF().zzyx().log("Service connected with null binder");
                return;
            }
            try {
                String interfaceDescriptor = iBinder.getInterfaceDescriptor();
                if ("com.google.android.gms.measurement.internal.IMeasurementService".equals(interfaceDescriptor)) {
                    if (iBinder == null) {
                        zzcfd = null;
                    } else {
                        IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.measurement.internal.IMeasurementService");
                        zzcfd = queryLocalInterface instanceof zzcfd ? (zzcfd) queryLocalInterface : new zzcff(iBinder);
                    }
                    try {
                        this.zzbua.zzwF().zzyD().log("Bound to IMeasurementService interface");
                    } catch (RemoteException e) {
                        this.zzbua.zzwF().zzyx().log("Service connect failed to get IMeasurementService");
                        if (zzcfd != null) {
                            this.zzbuh = false;
                            try {
                                zza.zzrU();
                                this.zzbua.getContext().unbindService(this.zzbua.zzbtT);
                            } catch (IllegalArgumentException e2) {
                            }
                        } else {
                            this.zzbua.zzwE().zzj(new zzcir(this, zzcfd));
                        }
                    }
                    if (zzcfd != null) {
                        this.zzbuh = false;
                        zza.zzrU();
                        this.zzbua.getContext().unbindService(this.zzbua.zzbtT);
                    } else {
                        this.zzbua.zzwE().zzj(new zzcir(this, zzcfd));
                    }
                }
                this.zzbua.zzwF().zzyx().zzj("Got binder with a wrong descriptor", interfaceDescriptor);
                zzcfd = null;
                if (zzcfd != null) {
                    this.zzbua.zzwE().zzj(new zzcir(this, zzcfd));
                } else {
                    this.zzbuh = false;
                    zza.zzrU();
                    this.zzbua.getContext().unbindService(this.zzbua.zzbtT);
                }
            } catch (RemoteException e3) {
                zzcfd = null;
                this.zzbua.zzwF().zzyx().log("Service connect failed to get IMeasurementService");
                if (zzcfd != null) {
                    this.zzbua.zzwE().zzj(new zzcir(this, zzcfd));
                } else {
                    this.zzbuh = false;
                    zza.zzrU();
                    this.zzbua.getContext().unbindService(this.zzbua.zzbtT);
                }
            }
        }
    }

    public final void onServiceDisconnected(ComponentName componentName) {
        zzbo.zzcz("MeasurementServiceConnection.onServiceDisconnected");
        this.zzbua.zzwF().zzyC().log("Service disconnected");
        this.zzbua.zzwE().zzj(new zzcis(this, componentName));
    }

    public final void zzk(Intent intent) {
        this.zzbua.zzjC();
        Context context = this.zzbua.getContext();
        zza.zzrU();
        synchronized (this) {
            if (this.zzbuh) {
                this.zzbua.zzwF().zzyD().log("Connection attempt already in progress");
                return;
            }
            this.zzbuh = true;
            zza.zza(context, intent, this.zzbua.zzbtT, 129);
        }
    }

    public final void zzzm() {
        this.zzbua.zzjC();
        Context context = this.zzbua.getContext();
        synchronized (this) {
            if (this.zzbuh) {
                this.zzbua.zzwF().zzyD().log("Connection attempt already in progress");
            } else if (this.zzbui != null) {
                this.zzbua.zzwF().zzyD().log("Already awaiting connection attempt");
            } else {
                this.zzbui = new zzcfk(context, Looper.getMainLooper(), this, this);
                this.zzbua.zzwF().zzyD().log("Connecting to remote service");
                this.zzbuh = true;
                this.zzbui.zzrb();
            }
        }
    }
}
