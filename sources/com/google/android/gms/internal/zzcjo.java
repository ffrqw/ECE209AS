package com.google.android.gms.internal;

import com.shinobicontrols.charts.R;
import java.io.IOException;

public final class zzcjo extends adj<zzcjo> {
    private static volatile zzcjo[] zzbuR;
    public zzcjr zzbuS;
    public zzcjp zzbuT;
    public Boolean zzbuU;
    public String zzbuV;

    public zzcjo() {
        this.zzbuS = null;
        this.zzbuT = null;
        this.zzbuU = null;
        this.zzbuV = null;
        this.zzcsd = null;
        this.zzcsm = -1;
    }

    public static zzcjo[] zzzw() {
        if (zzbuR == null) {
            synchronized (adn.zzcsl) {
                if (zzbuR == null) {
                    zzbuR = new zzcjo[0];
                }
            }
        }
        return zzbuR;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzcjo)) {
            return false;
        }
        zzcjo zzcjo = (zzcjo) obj;
        if (this.zzbuS == null) {
            if (zzcjo.zzbuS != null) {
                return false;
            }
        } else if (!this.zzbuS.equals(zzcjo.zzbuS)) {
            return false;
        }
        if (this.zzbuT == null) {
            if (zzcjo.zzbuT != null) {
                return false;
            }
        } else if (!this.zzbuT.equals(zzcjo.zzbuT)) {
            return false;
        }
        if (this.zzbuU == null) {
            if (zzcjo.zzbuU != null) {
                return false;
            }
        } else if (!this.zzbuU.equals(zzcjo.zzbuU)) {
            return false;
        }
        if (this.zzbuV == null) {
            if (zzcjo.zzbuV != null) {
                return false;
            }
        } else if (!this.zzbuV.equals(zzcjo.zzbuV)) {
            return false;
        }
        return (this.zzcsd == null || this.zzcsd.isEmpty()) ? zzcjo.zzcsd == null || zzcjo.zzcsd.isEmpty() : this.zzcsd.equals(zzcjo.zzcsd);
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = ((this.zzbuV == null ? 0 : this.zzbuV.hashCode()) + (((this.zzbuU == null ? 0 : this.zzbuU.hashCode()) + (((this.zzbuT == null ? 0 : this.zzbuT.hashCode()) + (((this.zzbuS == null ? 0 : this.zzbuS.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31)) * 31)) * 31;
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
                    if (this.zzbuS == null) {
                        this.zzbuS = new zzcjr();
                    }
                    adg.zza(this.zzbuS);
                    continue;
                case 18:
                    if (this.zzbuT == null) {
                        this.zzbuT = new zzcjp();
                    }
                    adg.zza(this.zzbuT);
                    continue;
                case 24:
                    this.zzbuU = Boolean.valueOf(adg.zzLE());
                    continue;
                case R.styleable.ChartTheme_sc_seriesAreaColor1 /*34*/:
                    this.zzbuV = adg.readString();
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
        if (this.zzbuS != null) {
            adh.zza(1, this.zzbuS);
        }
        if (this.zzbuT != null) {
            adh.zza(2, this.zzbuT);
        }
        if (this.zzbuU != null) {
            adh.zzk(3, this.zzbuU.booleanValue());
        }
        if (this.zzbuV != null) {
            adh.zzl(4, this.zzbuV);
        }
        super.zza(adh);
    }

    protected final int zzn() {
        int zzn = super.zzn();
        if (this.zzbuS != null) {
            zzn += adh.zzb(1, this.zzbuS);
        }
        if (this.zzbuT != null) {
            zzn += adh.zzb(2, this.zzbuT);
        }
        if (this.zzbuU != null) {
            this.zzbuU.booleanValue();
            zzn += adh.zzcv(24) + 1;
        }
        return this.zzbuV != null ? zzn + adh.zzm(4, this.zzbuV) : zzn;
    }
}
