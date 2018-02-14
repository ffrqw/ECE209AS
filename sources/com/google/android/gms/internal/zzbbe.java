package com.google.android.gms.internal;

import android.os.Looper;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.PendingResult.zza;
import com.google.android.gms.common.api.Releasable;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzao;
import com.google.android.gms.common.internal.zzbo;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

public abstract class zzbbe<R extends Result> extends PendingResult<R> {
    static final ThreadLocal<Boolean> zzaBV = new zzbbf();
    private Status mStatus;
    private boolean zzJ;
    private final Object zzaBW = new Object();
    private zzbbg<R> zzaBX = new zzbbg(Looper.getMainLooper());
    private WeakReference<GoogleApiClient> zzaBY = new WeakReference(null);
    private final ArrayList<zza> zzaBZ = new ArrayList();
    private R zzaBj;
    private ResultCallback<? super R> zzaCa;
    private final AtomicReference<zzbex> zzaCb = new AtomicReference();
    private zzbbh zzaCc;
    private volatile boolean zzaCd;
    private boolean zzaCe;
    private zzao zzaCf;
    private boolean zzaCh = false;
    private final CountDownLatch zztJ = new CountDownLatch(1);

    @Deprecated
    zzbbe() {
    }

    private final R get() {
        R r;
        boolean z = true;
        synchronized (this.zzaBW) {
            if (this.zzaCd) {
                z = false;
            }
            zzbo.zza(z, "Result has already been consumed.");
            zzbo.zza(isReady(), "Result is not ready.");
            r = this.zzaBj;
            this.zzaBj = null;
            this.zzaCa = null;
            this.zzaCd = true;
        }
        zzbex zzbex = (zzbex) this.zzaCb.getAndSet(null);
        if (zzbex != null) {
            zzbex.zzc(this);
        }
        return r;
    }

    private boolean isCanceled() {
        boolean z;
        synchronized (this.zzaBW) {
            z = this.zzJ;
        }
        return z;
    }

    private boolean isReady() {
        return this.zztJ.getCount() == 0;
    }

    private final void zzb(R r) {
        this.zzaBj = r;
        this.zzaCf = null;
        this.zztJ.countDown();
        this.mStatus = this.zzaBj.getStatus();
        if (this.zzJ) {
            this.zzaCa = null;
        } else if (this.zzaCa != null) {
            this.zzaBX.removeMessages(2);
            this.zzaBX.zza(this.zzaCa, get());
        } else if (this.zzaBj instanceof Releasable) {
            this.zzaCc = new zzbbh();
        }
        ArrayList arrayList = this.zzaBZ;
        int size = arrayList.size();
        int i = 0;
        while (i < size) {
            Object obj = arrayList.get(i);
            i++;
            ((zza) obj).zzo$e184e5d();
        }
        this.zzaBZ.clear();
    }

    public static void zzc(Result result) {
        if (!(result instanceof Releasable)) {
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void cancel() {
        /*
        r2 = this;
        r1 = r2.zzaBW;
        monitor-enter(r1);
        r0 = r2.zzJ;	 Catch:{ all -> 0x0020 }
        if (r0 != 0) goto L_0x000b;
    L_0x0007:
        r0 = r2.zzaCd;	 Catch:{ all -> 0x0020 }
        if (r0 == 0) goto L_0x000d;
    L_0x000b:
        monitor-exit(r1);	 Catch:{ all -> 0x0020 }
    L_0x000c:
        return;
    L_0x000d:
        r0 = r2.zzaBj;	 Catch:{ all -> 0x0020 }
        zzc(r0);	 Catch:{ all -> 0x0020 }
        r0 = 1;
        r2.zzJ = r0;	 Catch:{ all -> 0x0020 }
        r0 = com.google.android.gms.common.api.Status.zzaBq;	 Catch:{ all -> 0x0020 }
        r0 = r2.zzb$34dcf236();	 Catch:{ all -> 0x0020 }
        r2.zzb(r0);	 Catch:{ all -> 0x0020 }
        monitor-exit(r1);	 Catch:{ all -> 0x0020 }
        goto L_0x000c;
    L_0x0020:
        r0 = move-exception;
        monitor-exit(r1);	 Catch:{ all -> 0x0020 }
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.zzbbe.cancel():void");
    }

    public final void setResult(R r) {
        boolean z = true;
        synchronized (this.zzaBW) {
            if (this.zzaCe || this.zzJ) {
                zzc(r);
                return;
            }
            if (isReady()) {
            }
            zzbo.zza(!isReady(), "Results have already been set");
            if (this.zzaCd) {
                z = false;
            }
            zzbo.zza(z, "Result has already been consumed");
            zzb(r);
        }
    }

    public final void zza(zzbex zzbex) {
        this.zzaCb.set(zzbex);
    }

    protected abstract R zzb$34dcf236();

    public final boolean zzpB() {
        boolean isCanceled;
        synchronized (this.zzaBW) {
            if (((GoogleApiClient) this.zzaBY.get()) == null || !this.zzaCh) {
                cancel();
            }
            isCanceled = isCanceled();
        }
        return isCanceled;
    }

    public final void zzpC() {
        boolean z = this.zzaCh || ((Boolean) zzaBV.get()).booleanValue();
        this.zzaCh = z;
    }

    public final void zzs(Status status) {
        synchronized (this.zzaBW) {
            if (!isReady()) {
                setResult(zzb$34dcf236());
                this.zzaCe = true;
            }
        }
    }
}
