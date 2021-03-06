package com.google.android.gms.internal;

public final class adl implements Cloneable {
    private static final adm zzcsf = new adm();
    private int mSize;
    private boolean zzcsg;
    private int[] zzcsh;
    private adm[] zzcsi;

    adl() {
        this(10);
    }

    private adl(int i) {
        this.zzcsg = false;
        int idealIntArraySize = idealIntArraySize(i);
        this.zzcsh = new int[idealIntArraySize];
        this.zzcsi = new adm[idealIntArraySize];
        this.mSize = 0;
    }

    private static int idealIntArraySize(int i) {
        int i2 = i << 2;
        for (int i3 = 4; i3 < 32; i3++) {
            if (i2 <= (1 << i3) - 12) {
                i2 = (1 << i3) - 12;
                break;
            }
        }
        return i2 / 4;
    }

    private final int zzcz(int i) {
        int i2 = 0;
        int i3 = this.mSize - 1;
        while (i2 <= i3) {
            int i4 = (i2 + i3) >>> 1;
            int i5 = this.zzcsh[i4];
            if (i5 < i) {
                i2 = i4 + 1;
            } else if (i5 <= i) {
                return i4;
            } else {
                i3 = i4 - 1;
            }
        }
        return i2 ^ -1;
    }

    public final /* synthetic */ Object clone() throws CloneNotSupportedException {
        int i = this.mSize;
        adl adl = new adl(i);
        System.arraycopy(this.zzcsh, 0, adl.zzcsh, 0, i);
        for (int i2 = 0; i2 < i; i2++) {
            if (this.zzcsi[i2] != null) {
                adl.zzcsi[i2] = (adm) this.zzcsi[i2].clone();
            }
        }
        adl.mSize = i;
        return adl;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof adl)) {
            return false;
        }
        adl adl = (adl) obj;
        if (this.mSize != adl.mSize) {
            return false;
        }
        int i;
        boolean z;
        int[] iArr = this.zzcsh;
        int[] iArr2 = adl.zzcsh;
        int i2 = this.mSize;
        for (i = 0; i < i2; i++) {
            if (iArr[i] != iArr2[i]) {
                z = false;
                break;
            }
        }
        z = true;
        if (z) {
            adm[] admArr = this.zzcsi;
            adm[] admArr2 = adl.zzcsi;
            i2 = this.mSize;
            for (i = 0; i < i2; i++) {
                if (!admArr[i].equals(admArr2[i])) {
                    z = false;
                    break;
                }
            }
            z = true;
            if (z) {
                return true;
            }
        }
        return false;
    }

    public final int hashCode() {
        int i = 17;
        for (int i2 = 0; i2 < this.mSize; i2++) {
            i = (((i * 31) + this.zzcsh[i2]) * 31) + this.zzcsi[i2].hashCode();
        }
        return i;
    }

    public final boolean isEmpty() {
        return this.mSize == 0;
    }

    final int size() {
        return this.mSize;
    }

    final void zza(int i, adm adm) {
        int zzcz = zzcz(i);
        if (zzcz >= 0) {
            this.zzcsi[zzcz] = adm;
            return;
        }
        zzcz ^= -1;
        if (zzcz >= this.mSize || this.zzcsi[zzcz] != zzcsf) {
            if (this.mSize >= this.zzcsh.length) {
                int idealIntArraySize = idealIntArraySize(this.mSize + 1);
                Object obj = new int[idealIntArraySize];
                Object obj2 = new adm[idealIntArraySize];
                System.arraycopy(this.zzcsh, 0, obj, 0, this.zzcsh.length);
                System.arraycopy(this.zzcsi, 0, obj2, 0, this.zzcsi.length);
                this.zzcsh = obj;
                this.zzcsi = obj2;
            }
            if (this.mSize - zzcz != 0) {
                System.arraycopy(this.zzcsh, zzcz, this.zzcsh, zzcz + 1, this.mSize - zzcz);
                System.arraycopy(this.zzcsi, zzcz, this.zzcsi, zzcz + 1, this.mSize - zzcz);
            }
            this.zzcsh[zzcz] = i;
            this.zzcsi[zzcz] = adm;
            this.mSize++;
            return;
        }
        this.zzcsh[zzcz] = i;
        this.zzcsi[zzcz] = adm;
    }

    final adm zzcx(int i) {
        int zzcz = zzcz(i);
        return (zzcz < 0 || this.zzcsi[zzcz] == zzcsf) ? null : this.zzcsi[zzcz];
    }

    final adm zzcy(int i) {
        return this.zzcsi[i];
    }
}
