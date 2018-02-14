package com.google.android.gms.internal;

import com.shinobicontrols.charts.R;
import java.io.IOException;

public final class zzcjr extends adj<zzcjr> {
    public Integer zzbve;
    public String zzbvf;
    public Boolean zzbvg;
    public String[] zzbvh;

    public zzcjr() {
        this.zzbve = null;
        this.zzbvf = null;
        this.zzbvg = null;
        this.zzbvh = ads.EMPTY_STRING_ARRAY;
        this.zzcsd = null;
        this.zzcsm = -1;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzcjr)) {
            return false;
        }
        zzcjr zzcjr = (zzcjr) obj;
        if (this.zzbve == null) {
            if (zzcjr.zzbve != null) {
                return false;
            }
        } else if (!this.zzbve.equals(zzcjr.zzbve)) {
            return false;
        }
        if (this.zzbvf == null) {
            if (zzcjr.zzbvf != null) {
                return false;
            }
        } else if (!this.zzbvf.equals(zzcjr.zzbvf)) {
            return false;
        }
        if (this.zzbvg == null) {
            if (zzcjr.zzbvg != null) {
                return false;
            }
        } else if (!this.zzbvg.equals(zzcjr.zzbvg)) {
            return false;
        }
        return !adn.equals(this.zzbvh, zzcjr.zzbvh) ? false : (this.zzcsd == null || this.zzcsd.isEmpty()) ? zzcjr.zzcsd == null || zzcjr.zzcsd.isEmpty() : this.zzcsd.equals(zzcjr.zzcsd);
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = ((((this.zzbvg == null ? 0 : this.zzbvg.hashCode()) + (((this.zzbvf == null ? 0 : this.zzbvf.hashCode()) + (((this.zzbve == null ? 0 : this.zzbve.intValue()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31)) * 31) + adn.hashCode(this.zzbvh)) * 31;
        if (!(this.zzcsd == null || this.zzcsd.isEmpty())) {
            i = this.zzcsd.hashCode();
        }
        return hashCode + i;
    }

    public final /* synthetic */ adp zza(adg adg) throws IOException {
        while (true) {
            int zzLB = adg.zzLB();
            int position;
            switch (zzLB) {
                case 0:
                    break;
                case 8:
                    position = adg.getPosition();
                    int zzLG = adg.zzLG();
                    switch (zzLG) {
                        case 0:
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                        case 6:
                            this.zzbve = Integer.valueOf(zzLG);
                            break;
                        default:
                            adg.zzcp(position);
                            zza(adg, zzLB);
                            continue;
                    }
                case 18:
                    this.zzbvf = adg.readString();
                    continue;
                case 24:
                    this.zzbvg = Boolean.valueOf(adg.zzLE());
                    continue;
                case R.styleable.ChartTheme_sc_seriesAreaColor1 /*34*/:
                    position = ads.zzb(adg, 34);
                    zzLB = this.zzbvh == null ? 0 : this.zzbvh.length;
                    Object obj = new String[(position + zzLB)];
                    if (zzLB != 0) {
                        System.arraycopy(this.zzbvh, 0, obj, 0, zzLB);
                    }
                    while (zzLB < obj.length - 1) {
                        obj[zzLB] = adg.readString();
                        adg.zzLB();
                        zzLB++;
                    }
                    obj[zzLB] = adg.readString();
                    this.zzbvh = obj;
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
        if (this.zzbve != null) {
            adh.zzr(1, this.zzbve.intValue());
        }
        if (this.zzbvf != null) {
            adh.zzl(2, this.zzbvf);
        }
        if (this.zzbvg != null) {
            adh.zzk(3, this.zzbvg.booleanValue());
        }
        if (this.zzbvh != null && this.zzbvh.length > 0) {
            for (String str : this.zzbvh) {
                if (str != null) {
                    adh.zzl(4, str);
                }
            }
        }
        super.zza(adh);
    }

    protected final int zzn() {
        int i = 0;
        int zzn = super.zzn();
        if (this.zzbve != null) {
            zzn += adh.zzs(1, this.zzbve.intValue());
        }
        if (this.zzbvf != null) {
            zzn += adh.zzm(2, this.zzbvf);
        }
        if (this.zzbvg != null) {
            this.zzbvg.booleanValue();
            zzn += adh.zzcv(24) + 1;
        }
        if (this.zzbvh == null || this.zzbvh.length <= 0) {
            return zzn;
        }
        int i2 = 0;
        int i3 = 0;
        while (i < this.zzbvh.length) {
            String str = this.zzbvh[i];
            if (str != null) {
                i3++;
                i2 += adh.zzhS(str);
            }
            i++;
        }
        return (zzn + i2) + (i3 * 1);
    }
}
