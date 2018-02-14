package com.google.android.gms.common.api;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.google.android.gms.common.api.Api.zze;
import com.google.android.gms.common.api.GoogleApiClient.Builder;
import com.google.android.gms.internal.zzbat;
import com.google.android.gms.internal.zzbdd;
import com.google.android.gms.internal.zzbej;

public class GoogleApi<O> {
    private final Context mContext;
    private final int mId;
    private final zzbat<O> zzaAK;
    private final Api<O> zzayW;

    public final int getInstanceId() {
        return this.mId;
    }

    public zze zza(Looper looper, zzbdd<O> zzbdd) {
        return this.zzayW.zzpc().zza(this.mContext, looper, new Builder(this.mContext).zze(null).zzpn(), null, zzbdd, zzbdd);
    }

    public zzbej zza(Context context, Handler handler) {
        return new zzbej(context, handler);
    }

    public final zzbat<O> zzph() {
        return this.zzaAK;
    }
}
