package com.google.android.gms.internal;

import com.google.android.gms.common.util.zze;

public final class zzaoa {
    private final String zzaeX;
    private final long zzaih;
    private final int zzaii;
    private double zzaij;
    private long zzaik;
    private final Object zzail;
    private final zze zzvw;

    private zzaoa(String str, zze zze) {
        this.zzail = new Object();
        this.zzaii = 60;
        this.zzaij = 60.0d;
        this.zzaih = 2000;
        this.zzaeX = str;
        this.zzvw = zze;
    }

    public zzaoa(String str, zze zze, byte b) {
        this(str, zze);
    }

    public final boolean zzlL() {
        boolean z;
        synchronized (this.zzail) {
            long currentTimeMillis = this.zzvw.currentTimeMillis();
            if (this.zzaij < 60.0d) {
                double d = ((double) (currentTimeMillis - this.zzaik)) / 2000.0d;
                if (d > 0.0d) {
                    this.zzaij = Math.min(60.0d, d + this.zzaij);
                }
            }
            this.zzaik = currentTimeMillis;
            if (this.zzaij >= 1.0d) {
                this.zzaij -= 1.0d;
                z = true;
            } else {
                String str = this.zzaeX;
                zzaob.zzaT(new StringBuilder(String.valueOf(str).length() + 34).append("Excessive ").append(str).append(" detected; call ignored.").toString());
                z = false;
            }
        }
        return z;
    }
}
