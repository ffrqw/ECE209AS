package com.google.android.gms.common.api;

import android.content.Context;
import android.content.Intent;
import android.os.IInterface;
import android.os.Looper;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.internal.zzal;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.internal.zzj;
import com.google.android.gms.common.internal.zzq;
import java.io.PrintWriter;
import java.util.Set;

public final class Api<O> {
    private final String mName;
    private final zzi<?> zzaAA;
    private final zza<?, O> zzaAx;
    private final zzh<?, O> zzaAy = null;
    private final zzf<?> zzaAz;

    public static abstract class zzd<T extends zzb, O> {
    }

    public static abstract class zza<T extends zze, O> extends zzd<T, O> {
        public abstract T zza(Context context, Looper looper, zzq zzq, O o, ConnectionCallbacks connectionCallbacks, OnConnectionFailedListener onConnectionFailedListener);
    }

    public interface zzb {
    }

    public static class zzc<C extends zzb> {
    }

    public interface zze extends zzb {
        void disconnect();

        void dump$ec96877(String str, PrintWriter printWriter);

        boolean isConnected();

        boolean isConnecting();

        void zza(zzal zzal, Set<Scope> set);

        void zza(zzj zzj);

        Intent zzmH();

        boolean zzmv();
    }

    public static final class zzf<C extends zze> extends zzc<C> {
    }

    public interface zzg<T extends IInterface> extends zzb {
        T zzd$13514312();

        String zzdb();

        String zzdc();
    }

    public static abstract class zzh<T extends zzg, O> extends zzd<T, O> {
    }

    public static final class zzi<C extends zzg> extends zzc<C> {
    }

    public <C extends zze> Api(String str, zza<C, O> zza, zzf<C> zzf) {
        zzbo.zzb((Object) zza, (Object) "Cannot construct an Api with a null ClientBuilder");
        zzbo.zzb((Object) zzf, (Object) "Cannot construct an Api with a null ClientKey");
        this.mName = str;
        this.zzaAx = zza;
        this.zzaAz = zzf;
        this.zzaAA = null;
    }

    public final String getName() {
        return this.mName;
    }

    public final zza<?, O> zzpc() {
        zzbo.zza(this.zzaAx != null, "This API was constructed with a SimpleClientBuilder. Use getSimpleClientBuilder");
        return this.zzaAx;
    }

    public final zzc<?> zzpd() {
        if (this.zzaAz != null) {
            return this.zzaAz;
        }
        throw new IllegalStateException("This API was constructed with null client keys. This should not be possible.");
    }
}
