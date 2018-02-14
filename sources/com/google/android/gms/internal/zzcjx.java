package com.google.android.gms.internal;

import com.shinobicontrols.charts.R;
import java.io.IOException;

public final class zzcjx extends adj<zzcjx> {
    private static volatile zzcjx[] zzbvz;
    public String name;
    public String zzaIF;
    private Float zzbuA;
    public Double zzbuB;
    public Long zzbvA;

    public zzcjx() {
        this.name = null;
        this.zzaIF = null;
        this.zzbvA = null;
        this.zzbuA = null;
        this.zzbuB = null;
        this.zzcsd = null;
        this.zzcsm = -1;
    }

    public static zzcjx[] zzzC() {
        if (zzbvz == null) {
            synchronized (adn.zzcsl) {
                if (zzbvz == null) {
                    zzbvz = new zzcjx[0];
                }
            }
        }
        return zzbvz;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzcjx)) {
            return false;
        }
        zzcjx zzcjx = (zzcjx) obj;
        if (this.name == null) {
            if (zzcjx.name != null) {
                return false;
            }
        } else if (!this.name.equals(zzcjx.name)) {
            return false;
        }
        if (this.zzaIF == null) {
            if (zzcjx.zzaIF != null) {
                return false;
            }
        } else if (!this.zzaIF.equals(zzcjx.zzaIF)) {
            return false;
        }
        if (this.zzbvA == null) {
            if (zzcjx.zzbvA != null) {
                return false;
            }
        } else if (!this.zzbvA.equals(zzcjx.zzbvA)) {
            return false;
        }
        if (this.zzbuA == null) {
            if (zzcjx.zzbuA != null) {
                return false;
            }
        } else if (!this.zzbuA.equals(zzcjx.zzbuA)) {
            return false;
        }
        if (this.zzbuB == null) {
            if (zzcjx.zzbuB != null) {
                return false;
            }
        } else if (!this.zzbuB.equals(zzcjx.zzbuB)) {
            return false;
        }
        return (this.zzcsd == null || this.zzcsd.isEmpty()) ? zzcjx.zzcsd == null || zzcjx.zzcsd.isEmpty() : this.zzcsd.equals(zzcjx.zzcsd);
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = ((this.zzbuB == null ? 0 : this.zzbuB.hashCode()) + (((this.zzbuA == null ? 0 : this.zzbuA.hashCode()) + (((this.zzbvA == null ? 0 : this.zzbvA.hashCode()) + (((this.zzaIF == null ? 0 : this.zzaIF.hashCode()) + (((this.name == null ? 0 : this.name.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31)) * 31)) * 31)) * 31;
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
                    this.name = adg.readString();
                    continue;
                case 18:
                    this.zzaIF = adg.readString();
                    continue;
                case 24:
                    this.zzbvA = Long.valueOf(adg.zzLH());
                    continue;
                case R.styleable.ChartTheme_sc_seriesLineColor2 /*37*/:
                    this.zzbuA = Float.valueOf(Float.intBitsToFloat(adg.zzLI()));
                    continue;
                case R.styleable.ChartTheme_sc_seriesLineColor3 /*41*/:
                    this.zzbuB = Double.valueOf(Double.longBitsToDouble(adg.zzLJ()));
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
        if (this.name != null) {
            adh.zzl(1, this.name);
        }
        if (this.zzaIF != null) {
            adh.zzl(2, this.zzaIF);
        }
        if (this.zzbvA != null) {
            adh.zzb(3, this.zzbvA.longValue());
        }
        if (this.zzbuA != null) {
            adh.zzc(4, this.zzbuA.floatValue());
        }
        if (this.zzbuB != null) {
            adh.zza(5, this.zzbuB.doubleValue());
        }
        super.zza(adh);
    }

    protected final int zzn() {
        int zzn = super.zzn();
        if (this.name != null) {
            zzn += adh.zzm(1, this.name);
        }
        if (this.zzaIF != null) {
            zzn += adh.zzm(2, this.zzaIF);
        }
        if (this.zzbvA != null) {
            zzn += adh.zze(3, this.zzbvA.longValue());
        }
        if (this.zzbuA != null) {
            this.zzbuA.floatValue();
            zzn += adh.zzcv(32) + 4;
        }
        if (this.zzbuB == null) {
            return zzn;
        }
        this.zzbuB.doubleValue();
        return zzn + (adh.zzcv(40) + 8);
    }
}
