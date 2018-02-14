package com.google.android.gms.internal;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.internal.zzy;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api.zza;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.zzbr;
import com.google.android.gms.common.internal.zzq;
import java.util.HashSet;
import java.util.Set;

public final class zzbej extends zzctp implements ConnectionCallbacks, OnConnectionFailedListener {
    private static zza<? extends zzctk, zzctl> zzaEV = zzctg.zzajS;
    private final Context mContext;
    private final Handler mHandler;
    private final zza<? extends zzctk, zzctl> zzaAx = zzaEV;
    private zzq zzaCA;
    private zzctk zzaDh;
    private final boolean zzaEW = true;
    private zzbel zzaEX;
    private Set<Scope> zzame;

    public zzbej(Context context, Handler handler) {
        this.mContext = context;
        this.mHandler = handler;
    }

    public final void onConnected(Bundle bundle) {
        this.zzaDh.zza(this);
    }

    public final void onConnectionFailed(ConnectionResult connectionResult) {
        this.zzaEX.zzh(connectionResult);
    }

    public final void onConnectionSuspended(int i) {
        this.zzaDh.disconnect();
    }

    public final void zza(zzbel zzbel) {
        if (this.zzaDh != null) {
            this.zzaDh.disconnect();
        }
        if (this.zzaEW) {
            GoogleSignInOptions zzmO = zzy.zzaj(this.mContext).zzmO();
            this.zzame = zzmO == null ? new HashSet() : new HashSet(zzmO.zzmA());
            this.zzaCA = new zzq(null, this.zzame, null, 0, null, null, null, zzctl.zzbCM);
        }
        this.zzaCA.zzc(Integer.valueOf(System.identityHashCode(this)));
        this.zzaDh = (zzctk) this.zzaAx.zza(this.mContext, this.mHandler.getLooper(), this.zzaCA, this.zzaCA.zzrt(), this, this);
        this.zzaEX = zzbel;
        this.zzaDh.connect();
    }

    public final void zzb(zzctx zzctx) {
        this.mHandler.post(new zzbek(this, zzctx));
    }

    public final void zzqI() {
        if (this.zzaDh != null) {
            this.zzaDh.disconnect();
        }
    }

    static /* synthetic */ void zza(zzbej zzbej, zzctx zzctx) {
        ConnectionResult zzpz = zzctx.zzpz();
        if (zzpz.isSuccess()) {
            zzbr zzAx = zzctx.zzAx();
            ConnectionResult zzpz2 = zzAx.zzpz();
            if (zzpz2.isSuccess()) {
                zzbej.zzaEX.zzb(zzAx.zzrH(), zzbej.zzame);
            } else {
                String valueOf = String.valueOf(zzpz2);
                Log.wtf("SignInCoordinator", new StringBuilder(String.valueOf(valueOf).length() + 48).append("Sign-in succeeded with resolve account failure: ").append(valueOf).toString(), new Exception());
                zzbej.zzaEX.zzh(zzpz2);
            }
        } else {
            zzbej.zzaEX.zzh(zzpz);
        }
        zzbej.zzaDh.disconnect();
    }
}
