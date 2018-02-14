package com.google.android.gms.internal;

import java.io.IOException;

public abstract class adp {
    protected volatile int zzcsm = -1;

    public static final <T extends adp> T zza(T t, byte[] bArr) throws ado {
        return zza$86a0f7a(t, bArr, bArr.length);
    }

    private static <T extends adp> T zza$86a0f7a(T t, byte[] bArr, int i) throws ado {
        try {
            adg zzb$2f392411 = adg.zzb$2f392411(bArr, i);
            t.zza(zzb$2f392411);
            zzb$2f392411.zzcl(0);
            return t;
        } catch (ado e) {
            throw e;
        } catch (IOException e2) {
            throw new RuntimeException("Reading from a byte array threw an IOException (should never happen).");
        }
    }

    public /* synthetic */ Object clone() throws CloneNotSupportedException {
        return zzLP();
    }

    public String toString() {
        return adq.zzd(this);
    }

    public adp zzLP() throws CloneNotSupportedException {
        return (adp) super.clone();
    }

    public final int zzLV() {
        if (this.zzcsm < 0) {
            zzLW();
        }
        return this.zzcsm;
    }

    public final int zzLW() {
        int zzn = zzn();
        this.zzcsm = zzn;
        return zzn;
    }

    public abstract adp zza(adg adg) throws IOException;

    public void zza(adh adh) throws IOException {
    }

    protected int zzn() {
        return 0;
    }
}
