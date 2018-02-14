package com.google.android.gms.internal;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

public final class zzbea {
    private final Set<zzbdw<?>> zzauB = Collections.newSetFromMap(new WeakHashMap());

    public final void release() {
        for (zzbdw clear : this.zzauB) {
            clear.clear();
        }
        this.zzauB.clear();
    }
}
