package com.google.android.gms.internal;

public abstract class zzamh extends zzamg {
    private boolean zzafK;

    protected zzamh(zzamj zzamj) {
        super(zzamj);
    }

    public final void initialize() {
        zzjD();
        this.zzafK = true;
    }

    public final boolean isInitialized() {
        return this.zzafK;
    }

    protected abstract void zzjD();

    protected final void zzkD() {
        if (!isInitialized()) {
            throw new IllegalStateException("Not initialized");
        }
    }
}
