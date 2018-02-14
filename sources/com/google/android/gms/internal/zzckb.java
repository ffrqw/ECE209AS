package com.google.android.gms.internal;

import com.shinobicontrols.charts.R;
import java.io.IOException;

public final class zzckb extends adj<zzckb> {
    private static volatile zzckb[] zzbwg;
    public String name;
    public String zzaIF;
    private Float zzbuA;
    public Double zzbuB;
    public Long zzbvA;
    public Long zzbwh;

    public zzckb() {
        this.zzbwh = null;
        this.name = null;
        this.zzaIF = null;
        this.zzbvA = null;
        this.zzbuA = null;
        this.zzbuB = null;
        this.zzcsd = null;
        this.zzcsm = -1;
    }

    public static zzckb[] zzzE() {
        if (zzbwg == null) {
            synchronized (adn.zzcsl) {
                if (zzbwg == null) {
                    zzbwg = new zzckb[0];
                }
            }
        }
        return zzbwg;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzckb)) {
            return false;
        }
        zzckb zzckb = (zzckb) obj;
        if (this.zzbwh == null) {
            if (zzckb.zzbwh != null) {
                return false;
            }
        } else if (!this.zzbwh.equals(zzckb.zzbwh)) {
            return false;
        }
        if (this.name == null) {
            if (zzckb.name != null) {
                return false;
            }
        } else if (!this.name.equals(zzckb.name)) {
            return false;
        }
        if (this.zzaIF == null) {
            if (zzckb.zzaIF != null) {
                return false;
            }
        } else if (!this.zzaIF.equals(zzckb.zzaIF)) {
            return false;
        }
        if (this.zzbvA == null) {
            if (zzckb.zzbvA != null) {
                return false;
            }
        } else if (!this.zzbvA.equals(zzckb.zzbvA)) {
            return false;
        }
        if (this.zzbuA == null) {
            if (zzckb.zzbuA != null) {
                return false;
            }
        } else if (!this.zzbuA.equals(zzckb.zzbuA)) {
            return false;
        }
        if (this.zzbuB == null) {
            if (zzckb.zzbuB != null) {
                return false;
            }
        } else if (!this.zzbuB.equals(zzckb.zzbuB)) {
            return false;
        }
        return (this.zzcsd == null || this.zzcsd.isEmpty()) ? zzckb.zzcsd == null || zzckb.zzcsd.isEmpty() : this.zzcsd.equals(zzckb.zzcsd);
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = ((this.zzbuB == null ? 0 : this.zzbuB.hashCode()) + (((this.zzbuA == null ? 0 : this.zzbuA.hashCode()) + (((this.zzbvA == null ? 0 : this.zzbvA.hashCode()) + (((this.zzaIF == null ? 0 : this.zzaIF.hashCode()) + (((this.name == null ? 0 : this.name.hashCode()) + (((this.zzbwh == null ? 0 : this.zzbwh.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31;
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
                    this.zzbwh = Long.valueOf(adg.zzLH());
                    continue;
                case 18:
                    this.name = adg.readString();
                    continue;
                case 26:
                    this.zzaIF = adg.readString();
                    continue;
                case 32:
                    this.zzbvA = Long.valueOf(adg.zzLH());
                    continue;
                case R.styleable.ChartTheme_sc_seriesLineColor4 /*45*/:
                    this.zzbuA = Float.valueOf(Float.intBitsToFloat(adg.zzLI()));
                    continue;
                case R.styleable.ChartTheme_sc_seriesLineColor5 /*49*/:
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
        if (this.zzbwh != null) {
            adh.zzb(1, this.zzbwh.longValue());
        }
        if (this.name != null) {
            adh.zzl(2, this.name);
        }
        if (this.zzaIF != null) {
            adh.zzl(3, this.zzaIF);
        }
        if (this.zzbvA != null) {
            adh.zzb(4, this.zzbvA.longValue());
        }
        if (this.zzbuA != null) {
            adh.zzc(5, this.zzbuA.floatValue());
        }
        if (this.zzbuB != null) {
            adh.zza(6, this.zzbuB.doubleValue());
        }
        super.zza(adh);
    }

    protected final int zzn() {
        int zzn = super.zzn();
        if (this.zzbwh != null) {
            zzn += adh.zze(1, this.zzbwh.longValue());
        }
        if (this.name != null) {
            zzn += adh.zzm(2, this.name);
        }
        if (this.zzaIF != null) {
            zzn += adh.zzm(3, this.zzaIF);
        }
        if (this.zzbvA != null) {
            zzn += adh.zze(4, this.zzbvA.longValue());
        }
        if (this.zzbuA != null) {
            this.zzbuA.floatValue();
            zzn += adh.zzcv(40) + 4;
        }
        if (this.zzbuB == null) {
            return zzn;
        }
        this.zzbuB.doubleValue();
        return zzn + (adh.zzcv(48) + 8);
    }
}
