package com.google.android.gms.internal;

import android.content.Context;
import java.util.concurrent.atomic.AtomicReference;

public final class aac {
    private static final AtomicReference<aac> zzbVi = new AtomicReference();

    private aac() {
    }

    public static aac zzbL(Context context) {
        zzbVi.compareAndSet(null, new aac());
        return (aac) zzbVi.get();
    }

    public static void zze$4c05e04e() {
    }
}
