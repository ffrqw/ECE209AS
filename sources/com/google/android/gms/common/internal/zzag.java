package com.google.android.gms.common.internal;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import com.google.android.gms.common.stats.zza;
import java.util.HashMap;

final class zzag extends zzae implements Callback {
    private final Context mApplicationContext;
    private final Handler mHandler;
    private final HashMap<zzaf, zzah> zzaHP = new HashMap();
    private final zza zzaHQ;
    private final long zzaHR;
    private final long zzaHS;

    zzag(Context context) {
        this.mApplicationContext = context.getApplicationContext();
        this.mHandler = new Handler(context.getMainLooper(), this);
        this.zzaHQ = zza.zzrU();
        this.zzaHR = 5000;
        this.zzaHS = 300000;
    }

    public final boolean handleMessage(Message message) {
        zzaf zzaf;
        zzah zzah;
        switch (message.what) {
            case 0:
                synchronized (this.zzaHP) {
                    zzaf = (zzaf) message.obj;
                    zzah = (zzah) this.zzaHP.get(zzaf);
                    if (zzah != null && zzah.zzrC()) {
                        if (zzah.isBound()) {
                            zzah.zzcC$552c4e01();
                        }
                        this.zzaHP.remove(zzaf);
                    }
                }
                return true;
            case 1:
                synchronized (this.zzaHP) {
                    zzaf = (zzaf) message.obj;
                    zzah = (zzah) this.zzaHP.get(zzaf);
                    if (zzah != null && zzah.getState() == 3) {
                        String valueOf = String.valueOf(zzaf);
                        Log.wtf("GmsClientSupervisor", new StringBuilder(String.valueOf(valueOf).length() + 47).append("Timeout waiting for ServiceConnection callback ").append(valueOf).toString(), new Exception());
                        ComponentName componentName = zzah.getComponentName();
                        if (componentName == null) {
                            componentName = null;
                        }
                        zzah.onServiceDisconnected(componentName == null ? new ComponentName(zzaf.getPackage(), "unknown") : componentName);
                    }
                }
                return true;
            default:
                return false;
        }
    }

    protected final boolean zza(zzaf zzaf, ServiceConnection serviceConnection, String str) {
        boolean isBound;
        zzbo.zzb((Object) serviceConnection, (Object) "ServiceConnection must not be null");
        synchronized (this.zzaHP) {
            zzah zzah = (zzah) this.zzaHP.get(zzaf);
            if (zzah != null) {
                this.mHandler.removeMessages(0, zzaf);
                if (!zzah.zza(serviceConnection)) {
                    zzah.zza$2d8eac7(serviceConnection);
                    switch (zzah.getState()) {
                        case 1:
                            serviceConnection.onServiceConnected(zzah.getComponentName(), zzah.getBinder());
                            break;
                        case 2:
                            zzah.zzcB(str);
                            break;
                        default:
                            break;
                    }
                }
                String valueOf = String.valueOf(zzaf);
                throw new IllegalStateException(new StringBuilder(String.valueOf(valueOf).length() + 81).append("Trying to bind a GmsServiceConnection that was already connected before.  config=").append(valueOf).toString());
            }
            zzah = new zzah(this, zzaf);
            zzah.zza$2d8eac7(serviceConnection);
            zzah.zzcB(str);
            this.zzaHP.put(zzaf, zzah);
            isBound = zzah.isBound();
        }
        return isBound;
    }

    protected final void zzb(zzaf zzaf, ServiceConnection serviceConnection, String str) {
        zzbo.zzb((Object) serviceConnection, (Object) "ServiceConnection must not be null");
        synchronized (this.zzaHP) {
            zzah zzah = (zzah) this.zzaHP.get(zzaf);
            String valueOf;
            if (zzah == null) {
                valueOf = String.valueOf(zzaf);
                throw new IllegalStateException(new StringBuilder(String.valueOf(valueOf).length() + 50).append("Nonexistent connection status for service config: ").append(valueOf).toString());
            } else if (zzah.zza(serviceConnection)) {
                zzah.zzb$2d8eac7(serviceConnection);
                if (zzah.zzrC()) {
                    this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(0, zzaf), 5000);
                }
            } else {
                valueOf = String.valueOf(zzaf);
                throw new IllegalStateException(new StringBuilder(String.valueOf(valueOf).length() + 76).append("Trying to unbind a GmsServiceConnection  that was not bound before.  config=").append(valueOf).toString());
            }
        }
    }
}
