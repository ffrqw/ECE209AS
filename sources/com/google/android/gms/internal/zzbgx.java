package com.google.android.gms.internal;

import android.os.Process;

final class zzbgx implements Runnable {
    private final int mPriority = 0;
    private final Runnable zzv;

    public zzbgx(Runnable runnable, int i) {
        this.zzv = runnable;
    }

    public final void run() {
        Process.setThreadPriority(this.mPriority);
        this.zzv.run();
    }
}
