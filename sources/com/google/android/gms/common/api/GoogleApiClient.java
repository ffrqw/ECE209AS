package com.google.android.gms.common.api;

import android.accounts.Account;
import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.util.ArrayMap;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Api.zza;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.Api.zzc;
import com.google.android.gms.common.api.Api.zze;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.internal.zzq;
import com.google.android.gms.common.internal.zzr;
import com.google.android.gms.internal.zzbau;
import com.google.android.gms.internal.zzbay;
import com.google.android.gms.internal.zzbbi;
import com.google.android.gms.internal.zzbcp;
import com.google.android.gms.internal.zzctg;
import com.google.android.gms.internal.zzctk;
import com.google.android.gms.internal.zzctl;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.locks.ReentrantLock;

public abstract class GoogleApiClient {
    private static final Set<GoogleApiClient> zzaAS = Collections.newSetFromMap(new WeakHashMap());

    public static final class Builder {
        private final Context mContext;
        private final Set<Scope> zzaAT = new HashSet();
        private final Set<Scope> zzaAU = new HashSet();
        private String zzaAX;
        private final Map<Api<?>, zzr> zzaAY = new ArrayMap();
        private final Map<Api<?>, Object> zzaAZ = new ArrayMap();
        private int zzaBb = -1;
        private GoogleApiAvailability zzaBd = GoogleApiAvailability.getInstance();
        private zza<? extends zzctk, zzctl> zzaBe = zzctg.zzajS;
        private final ArrayList<ConnectionCallbacks> zzaBf = new ArrayList();
        private final ArrayList<OnConnectionFailedListener> zzaBg = new ArrayList();
        private boolean zzaBh = false;
        private Account zzajb;
        private String zzake;
        private Looper zzrM;

        public Builder(Context context) {
            this.mContext = context;
            this.zzrM = context.getMainLooper();
            this.zzake = context.getPackageName();
            this.zzaAX = context.getClass().getName();
        }

        public final Builder addApi(Api<? extends Object> api) {
            zzbo.zzb((Object) api, (Object) "Api must not be null");
            this.zzaAZ.put(api, null);
            Collection emptyList = Collections.emptyList();
            this.zzaAU.addAll(emptyList);
            this.zzaAT.addAll(emptyList);
            return this;
        }

        public final Builder addConnectionCallbacks(ConnectionCallbacks connectionCallbacks) {
            zzbo.zzb((Object) connectionCallbacks, (Object) "Listener must not be null");
            this.zzaBf.add(connectionCallbacks);
            return this;
        }

        public final Builder addOnConnectionFailedListener(OnConnectionFailedListener onConnectionFailedListener) {
            zzbo.zzb((Object) onConnectionFailedListener, (Object) "Listener must not be null");
            this.zzaBg.add(onConnectionFailedListener);
            return this;
        }

        public final GoogleApiClient build() {
            zzbo.zzb(!this.zzaAZ.isEmpty(), (Object) "must call addApi() to add at least one API");
            zzq zzpn = zzpn();
            Map zzrp = zzpn.zzrp();
            Map arrayMap = new ArrayMap();
            Map arrayMap2 = new ArrayMap();
            ArrayList arrayList = new ArrayList();
            for (Api api : this.zzaAZ.keySet()) {
                Object obj = this.zzaAZ.get(api);
                boolean z = zzrp.get(api) != null;
                arrayMap.put(api, Boolean.valueOf(z));
                ConnectionCallbacks zzbbi = new zzbbi(api, z);
                arrayList.add(zzbbi);
                arrayMap2.put(api.zzpd(), api.zzpc().zza(this.mContext, this.zzrM, zzpn, obj, zzbbi, zzbbi));
            }
            GoogleApiClient zzbcp = new zzbcp(this.mContext, new ReentrantLock(), this.zzrM, zzpn, this.zzaBd, this.zzaBe, arrayMap, this.zzaBf, this.zzaBg, arrayMap2, this.zzaBb, zzbcp.zza(arrayMap2.values(), true), arrayList);
            synchronized (GoogleApiClient.zzaAS) {
                GoogleApiClient.zzaAS.add(zzbcp);
            }
            if (this.zzaBb >= 0) {
                zzbau.zza(null).zza(this.zzaBb, zzbcp, null);
            }
            return zzbcp;
        }

        public final Builder zze(Account account) {
            this.zzajb = account;
            return this;
        }

        public final zzq zzpn() {
            zzctl zzctl = zzctl.zzbCM;
            if (this.zzaAZ.containsKey(zzctg.API)) {
                zzctl = (zzctl) this.zzaAZ.get(zzctg.API);
            }
            return new zzq(this.zzajb, this.zzaAT, this.zzaAY, 0, null, this.zzake, this.zzaAX, zzctl);
        }
    }

    public interface ConnectionCallbacks {
        void onConnected(Bundle bundle);

        void onConnectionSuspended(int i);
    }

    public interface OnConnectionFailedListener {
        void onConnectionFailed(ConnectionResult connectionResult);
    }

    public abstract void connect();

    public abstract void disconnect();

    public abstract void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr);

    public Looper getLooper() {
        throw new UnsupportedOperationException();
    }

    public abstract boolean isConnected();

    public abstract void registerConnectionFailedListener(OnConnectionFailedListener onConnectionFailedListener);

    public abstract void unregisterConnectionFailedListener(OnConnectionFailedListener onConnectionFailedListener);

    public <C extends zze> C zza(zzc<C> zzc) {
        throw new UnsupportedOperationException();
    }

    public <A extends zzb, T extends zzbay<? extends Result, A>> T zze(T t) {
        throw new UnsupportedOperationException();
    }
}
