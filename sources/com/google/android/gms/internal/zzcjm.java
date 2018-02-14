package com.google.android.gms.internal;

import java.io.IOException;

public final class zzcjm extends adj<zzcjm> {
    private static volatile zzcjm[] zzbuH;
    public Integer zzbuI;
    public zzcjq[] zzbuJ;
    public zzcjn[] zzbuK;

    public zzcjm() {
        this.zzbuI = null;
        this.zzbuJ = zzcjq.zzzx();
        this.zzbuK = zzcjn.zzzv();
        this.zzcsd = null;
        this.zzcsm = -1;
    }

    public static zzcjm[] zzzu() {
        if (zzbuH == null) {
            synchronized (adn.zzcsl) {
                if (zzbuH == null) {
                    zzbuH = new zzcjm[0];
                }
            }
        }
        return zzbuH;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzcjm)) {
            return false;
        }
        zzcjm zzcjm = (zzcjm) obj;
        if (this.zzbuI == null) {
            if (zzcjm.zzbuI != null) {
                return false;
            }
        } else if (!this.zzbuI.equals(zzcjm.zzbuI)) {
            return false;
        }
        return !adn.equals(this.zzbuJ, zzcjm.zzbuJ) ? false : !adn.equals(this.zzbuK, zzcjm.zzbuK) ? false : (this.zzcsd == null || this.zzcsd.isEmpty()) ? zzcjm.zzcsd == null || zzcjm.zzcsd.isEmpty() : this.zzcsd.equals(zzcjm.zzcsd);
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = ((((((this.zzbuI == null ? 0 : this.zzbuI.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31) + adn.hashCode(this.zzbuJ)) * 31) + adn.hashCode(this.zzbuK)) * 31;
        if (!(this.zzcsd == null || this.zzcsd.isEmpty())) {
            i = this.zzcsd.hashCode();
        }
        return hashCode + i;
    }

    public final /* synthetic */ adp zza(adg adg) throws IOException {
        while (true) {
            int zzLB = adg.zzLB();
            int zzb;
            Object obj;
            switch (zzLB) {
                case 0:
                    break;
                case 8:
                    this.zzbuI = Integer.valueOf(adg.zzLG());
                    continue;
                case 18:
                    zzb = ads.zzb(adg, 18);
                    zzLB = this.zzbuJ == null ? 0 : this.zzbuJ.length;
                    obj = new zzcjq[(zzb + zzLB)];
                    if (zzLB != 0) {
                        System.arraycopy(this.zzbuJ, 0, obj, 0, zzLB);
                    }
                    while (zzLB < obj.length - 1) {
                        obj[zzLB] = new zzcjq();
                        adg.zza(obj[zzLB]);
                        adg.zzLB();
                        zzLB++;
                    }
                    obj[zzLB] = new zzcjq();
                    adg.zza(obj[zzLB]);
                    this.zzbuJ = obj;
                    continue;
                case 26:
                    zzb = ads.zzb(adg, 26);
                    zzLB = this.zzbuK == null ? 0 : this.zzbuK.length;
                    obj = new zzcjn[(zzb + zzLB)];
                    if (zzLB != 0) {
                        System.arraycopy(this.zzbuK, 0, obj, 0, zzLB);
                    }
                    while (zzLB < obj.length - 1) {
                        obj[zzLB] = new zzcjn();
                        adg.zza(obj[zzLB]);
                        adg.zzLB();
                        zzLB++;
                    }
                    obj[zzLB] = new zzcjn();
                    adg.zza(obj[zzLB]);
                    this.zzbuK = obj;
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
        int i = 0;
        if (this.zzbuI != null) {
            adh.zzr(1, this.zzbuI.intValue());
        }
        if (this.zzbuJ != null && this.zzbuJ.length > 0) {
            for (adp adp : this.zzbuJ) {
                if (adp != null) {
                    adh.zza(2, adp);
                }
            }
        }
        if (this.zzbuK != null && this.zzbuK.length > 0) {
            while (i < this.zzbuK.length) {
                adp adp2 = this.zzbuK[i];
                if (adp2 != null) {
                    adh.zza(3, adp2);
                }
                i++;
            }
        }
        super.zza(adh);
    }

    protected final int zzn() {
        int i = 0;
        int zzn = super.zzn();
        if (this.zzbuI != null) {
            zzn += adh.zzs(1, this.zzbuI.intValue());
        }
        if (this.zzbuJ != null && this.zzbuJ.length > 0) {
            int i2 = zzn;
            for (adp adp : this.zzbuJ) {
                if (adp != null) {
                    i2 += adh.zzb(2, adp);
                }
            }
            zzn = i2;
        }
        if (this.zzbuK != null && this.zzbuK.length > 0) {
            while (i < this.zzbuK.length) {
                adp adp2 = this.zzbuK[i];
                if (adp2 != null) {
                    zzn += adh.zzb(3, adp2);
                }
                i++;
            }
        }
        return zzn;
    }
}
