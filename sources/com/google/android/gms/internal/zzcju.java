package com.google.android.gms.internal;

import java.io.IOException;

public final class zzcju extends adj<zzcju> {
    private static volatile zzcju[] zzbvq;
    public String key;
    public String value;

    public zzcju() {
        this.key = null;
        this.value = null;
        this.zzcsd = null;
        this.zzcsm = -1;
    }

    public static zzcju[] zzzz() {
        if (zzbvq == null) {
            synchronized (adn.zzcsl) {
                if (zzbvq == null) {
                    zzbvq = new zzcju[0];
                }
            }
        }
        return zzbvq;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzcju)) {
            return false;
        }
        zzcju zzcju = (zzcju) obj;
        if (this.key == null) {
            if (zzcju.key != null) {
                return false;
            }
        } else if (!this.key.equals(zzcju.key)) {
            return false;
        }
        if (this.value == null) {
            if (zzcju.value != null) {
                return false;
            }
        } else if (!this.value.equals(zzcju.value)) {
            return false;
        }
        return (this.zzcsd == null || this.zzcsd.isEmpty()) ? zzcju.zzcsd == null || zzcju.zzcsd.isEmpty() : this.zzcsd.equals(zzcju.zzcsd);
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = ((this.value == null ? 0 : this.value.hashCode()) + (((this.key == null ? 0 : this.key.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31;
        if (!(this.zzcsd == null || this.zzcsd.isEmpty())) {
            i = this.zzcsd.hashCode();
        }
        return hashCode + i;
    }

    public final /* synthetic */ adp zza(adg adg) throws IOException {
        while (true) {
            int zzLB = adg.zzLB();
            switch (zzLB) {
                case 0:
                    break;
                case 10:
                    this.key = adg.readString();
                    continue;
                case 18:
                    this.value = adg.readString();
                    continue;
                default:
                    if (!super.zza(adg, zzLB)) {
                        break;
                    }
                    continue;
            }
            return this;
        }
    }

    public final void zza(adh adh) throws IOException {
        if (this.key != null) {
            adh.zzl(1, this.key);
        }
        if (this.value != null) {
            adh.zzl(2, this.value);
        }
        super.zza(adh);
    }

    protected final int zzn() {
        int zzn = super.zzn();
        if (this.key != null) {
            zzn += adh.zzm(1, this.key);
        }
        return this.value != null ? zzn + adh.zzm(2, this.value) : zzn;
    }
}
