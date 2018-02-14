package com.google.android.gms.internal;

import java.io.IOException;

public final class zzcka extends adj<zzcka> {
    public long[] zzbwe;
    public long[] zzbwf;

    public zzcka() {
        this.zzbwe = ads.zzcss;
        this.zzbwf = ads.zzcss;
        this.zzcsd = null;
        this.zzcsm = -1;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzcka)) {
            return false;
        }
        zzcka zzcka = (zzcka) obj;
        return !adn.equals(this.zzbwe, zzcka.zzbwe) ? false : !adn.equals(this.zzbwf, zzcka.zzbwf) ? false : (this.zzcsd == null || this.zzcsd.isEmpty()) ? zzcka.zzcsd == null || zzcka.zzcsd.isEmpty() : this.zzcsd.equals(zzcka.zzcsd);
    }

    public final int hashCode() {
        int hashCode = (((((getClass().getName().hashCode() + 527) * 31) + adn.hashCode(this.zzbwe)) * 31) + adn.hashCode(this.zzbwf)) * 31;
        int hashCode2 = (this.zzcsd == null || this.zzcsd.isEmpty()) ? 0 : this.zzcsd.hashCode();
        return hashCode2 + hashCode;
    }

    public final /* synthetic */ adp zza(adg adg) throws IOException {
        while (true) {
            int zzLB = adg.zzLB();
            int zzb;
            Object obj;
            int zzcn;
            Object obj2;
            switch (zzLB) {
                case 0:
                    break;
                case 8:
                    zzb = ads.zzb(adg, 8);
                    zzLB = this.zzbwe == null ? 0 : this.zzbwe.length;
                    obj = new long[(zzb + zzLB)];
                    if (zzLB != 0) {
                        System.arraycopy(this.zzbwe, 0, obj, 0, zzLB);
                    }
                    while (zzLB < obj.length - 1) {
                        obj[zzLB] = adg.zzLH();
                        adg.zzLB();
                        zzLB++;
                    }
                    obj[zzLB] = adg.zzLH();
                    this.zzbwe = obj;
                    continue;
                case 10:
                    zzcn = adg.zzcn(adg.zzLG());
                    zzb = adg.getPosition();
                    zzLB = 0;
                    while (adg.zzLL() > 0) {
                        adg.zzLH();
                        zzLB++;
                    }
                    adg.zzcp(zzb);
                    zzb = this.zzbwe == null ? 0 : this.zzbwe.length;
                    obj2 = new long[(zzLB + zzb)];
                    if (zzb != 0) {
                        System.arraycopy(this.zzbwe, 0, obj2, 0, zzb);
                    }
                    while (zzb < obj2.length) {
                        obj2[zzb] = adg.zzLH();
                        zzb++;
                    }
                    this.zzbwe = obj2;
                    adg.zzco(zzcn);
                    continue;
                case 16:
                    zzb = ads.zzb(adg, 16);
                    zzLB = this.zzbwf == null ? 0 : this.zzbwf.length;
                    obj = new long[(zzb + zzLB)];
                    if (zzLB != 0) {
                        System.arraycopy(this.zzbwf, 0, obj, 0, zzLB);
                    }
                    while (zzLB < obj.length - 1) {
                        obj[zzLB] = adg.zzLH();
                        adg.zzLB();
                        zzLB++;
                    }
                    obj[zzLB] = adg.zzLH();
                    this.zzbwf = obj;
                    continue;
                case 18:
                    zzcn = adg.zzcn(adg.zzLG());
                    zzb = adg.getPosition();
                    zzLB = 0;
                    while (adg.zzLL() > 0) {
                        adg.zzLH();
                        zzLB++;
                    }
                    adg.zzcp(zzb);
                    zzb = this.zzbwf == null ? 0 : this.zzbwf.length;
                    obj2 = new long[(zzLB + zzb)];
                    if (zzb != 0) {
                        System.arraycopy(this.zzbwf, 0, obj2, 0, zzb);
                    }
                    while (zzb < obj2.length) {
                        obj2[zzb] = adg.zzLH();
                        zzb++;
                    }
                    this.zzbwf = obj2;
                    adg.zzco(zzcn);
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
        if (this.zzbwe != null && this.zzbwe.length > 0) {
            for (long zza : this.zzbwe) {
                adh.zza(1, zza);
            }
        }
        if (this.zzbwf != null && this.zzbwf.length > 0) {
            while (i < this.zzbwf.length) {
                adh.zza(2, this.zzbwf[i]);
                i++;
            }
        }
        super.zza(adh);
    }

    protected final int zzn() {
        int i;
        int i2;
        int i3 = 0;
        int zzn = super.zzn();
        if (this.zzbwe == null || this.zzbwe.length <= 0) {
            i = zzn;
        } else {
            i2 = 0;
            for (long zzaP : this.zzbwe) {
                i2 += adh.zzaP(zzaP);
            }
            i = (zzn + i2) + (this.zzbwe.length * 1);
        }
        if (this.zzbwf == null || this.zzbwf.length <= 0) {
            return i;
        }
        i2 = 0;
        while (i3 < this.zzbwf.length) {
            i2 += adh.zzaP(this.zzbwf[i3]);
            i3++;
        }
        return (i + i2) + (this.zzbwf.length * 1);
    }
}
