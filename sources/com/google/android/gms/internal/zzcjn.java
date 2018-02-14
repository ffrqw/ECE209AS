package com.google.android.gms.internal;

import com.shinobicontrols.charts.R;
import java.io.IOException;

public final class zzcjn extends adj<zzcjn> {
    private static volatile zzcjn[] zzbuL;
    public Integer zzbuM;
    public String zzbuN;
    public zzcjo[] zzbuO;
    private Boolean zzbuP;
    public zzcjp zzbuQ;

    public zzcjn() {
        this.zzbuM = null;
        this.zzbuN = null;
        this.zzbuO = zzcjo.zzzw();
        this.zzbuP = null;
        this.zzbuQ = null;
        this.zzcsd = null;
        this.zzcsm = -1;
    }

    public static zzcjn[] zzzv() {
        if (zzbuL == null) {
            synchronized (adn.zzcsl) {
                if (zzbuL == null) {
                    zzbuL = new zzcjn[0];
                }
            }
        }
        return zzbuL;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzcjn)) {
            return false;
        }
        zzcjn zzcjn = (zzcjn) obj;
        if (this.zzbuM == null) {
            if (zzcjn.zzbuM != null) {
                return false;
            }
        } else if (!this.zzbuM.equals(zzcjn.zzbuM)) {
            return false;
        }
        if (this.zzbuN == null) {
            if (zzcjn.zzbuN != null) {
                return false;
            }
        } else if (!this.zzbuN.equals(zzcjn.zzbuN)) {
            return false;
        }
        if (!adn.equals(this.zzbuO, zzcjn.zzbuO)) {
            return false;
        }
        if (this.zzbuP == null) {
            if (zzcjn.zzbuP != null) {
                return false;
            }
        } else if (!this.zzbuP.equals(zzcjn.zzbuP)) {
            return false;
        }
        if (this.zzbuQ == null) {
            if (zzcjn.zzbuQ != null) {
                return false;
            }
        } else if (!this.zzbuQ.equals(zzcjn.zzbuQ)) {
            return false;
        }
        return (this.zzcsd == null || this.zzcsd.isEmpty()) ? zzcjn.zzcsd == null || zzcjn.zzcsd.isEmpty() : this.zzcsd.equals(zzcjn.zzcsd);
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = ((this.zzbuQ == null ? 0 : this.zzbuQ.hashCode()) + (((this.zzbuP == null ? 0 : this.zzbuP.hashCode()) + (((((this.zzbuN == null ? 0 : this.zzbuN.hashCode()) + (((this.zzbuM == null ? 0 : this.zzbuM.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31) + adn.hashCode(this.zzbuO)) * 31)) * 31)) * 31;
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
                case 8:
                    this.zzbuM = Integer.valueOf(adg.zzLG());
                    continue;
                case 18:
                    this.zzbuN = adg.readString();
                    continue;
                case 26:
                    int zzb = ads.zzb(adg, 26);
                    zzLB = this.zzbuO == null ? 0 : this.zzbuO.length;
                    Object obj = new zzcjo[(zzb + zzLB)];
                    if (zzLB != 0) {
                        System.arraycopy(this.zzbuO, 0, obj, 0, zzLB);
                    }
                    while (zzLB < obj.length - 1) {
                        obj[zzLB] = new zzcjo();
                        adg.zza(obj[zzLB]);
                        adg.zzLB();
                        zzLB++;
                    }
                    obj[zzLB] = new zzcjo();
                    adg.zza(obj[zzLB]);
                    this.zzbuO = obj;
                    continue;
                case 32:
                    this.zzbuP = Boolean.valueOf(adg.zzLE());
                    continue;
                case R.styleable.ChartTheme_sc_seriesAreaColor3 /*42*/:
                    if (this.zzbuQ == null) {
                        this.zzbuQ = new zzcjp();
                    }
                    adg.zza(this.zzbuQ);
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
        if (this.zzbuM != null) {
            adh.zzr(1, this.zzbuM.intValue());
        }
        if (this.zzbuN != null) {
            adh.zzl(2, this.zzbuN);
        }
        if (this.zzbuO != null && this.zzbuO.length > 0) {
            for (adp adp : this.zzbuO) {
                if (adp != null) {
                    adh.zza(3, adp);
                }
            }
        }
        if (this.zzbuP != null) {
            adh.zzk(4, this.zzbuP.booleanValue());
        }
        if (this.zzbuQ != null) {
            adh.zza(5, this.zzbuQ);
        }
        super.zza(adh);
    }

    protected final int zzn() {
        int zzn = super.zzn();
        if (this.zzbuM != null) {
            zzn += adh.zzs(1, this.zzbuM.intValue());
        }
        if (this.zzbuN != null) {
            zzn += adh.zzm(2, this.zzbuN);
        }
        if (this.zzbuO != null && this.zzbuO.length > 0) {
            int i = zzn;
            for (adp adp : this.zzbuO) {
                if (adp != null) {
                    i += adh.zzb(3, adp);
                }
            }
            zzn = i;
        }
        if (this.zzbuP != null) {
            this.zzbuP.booleanValue();
            zzn += adh.zzcv(32) + 1;
        }
        return this.zzbuQ != null ? zzn + adh.zzb(5, this.zzbuQ) : zzn;
    }
}
