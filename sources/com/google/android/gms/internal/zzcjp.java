package com.google.android.gms.internal;

import com.shinobicontrols.charts.R;
import java.io.IOException;

public final class zzcjp extends adj<zzcjp> {
    public Integer zzbuW;
    public Boolean zzbuX;
    public String zzbuY;
    public String zzbuZ;
    public String zzbva;

    public zzcjp() {
        this.zzbuW = null;
        this.zzbuX = null;
        this.zzbuY = null;
        this.zzbuZ = null;
        this.zzbva = null;
        this.zzcsd = null;
        this.zzcsm = -1;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzcjp)) {
            return false;
        }
        zzcjp zzcjp = (zzcjp) obj;
        if (this.zzbuW == null) {
            if (zzcjp.zzbuW != null) {
                return false;
            }
        } else if (!this.zzbuW.equals(zzcjp.zzbuW)) {
            return false;
        }
        if (this.zzbuX == null) {
            if (zzcjp.zzbuX != null) {
                return false;
            }
        } else if (!this.zzbuX.equals(zzcjp.zzbuX)) {
            return false;
        }
        if (this.zzbuY == null) {
            if (zzcjp.zzbuY != null) {
                return false;
            }
        } else if (!this.zzbuY.equals(zzcjp.zzbuY)) {
            return false;
        }
        if (this.zzbuZ == null) {
            if (zzcjp.zzbuZ != null) {
                return false;
            }
        } else if (!this.zzbuZ.equals(zzcjp.zzbuZ)) {
            return false;
        }
        if (this.zzbva == null) {
            if (zzcjp.zzbva != null) {
                return false;
            }
        } else if (!this.zzbva.equals(zzcjp.zzbva)) {
            return false;
        }
        return (this.zzcsd == null || this.zzcsd.isEmpty()) ? zzcjp.zzcsd == null || zzcjp.zzcsd.isEmpty() : this.zzcsd.equals(zzcjp.zzcsd);
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = ((this.zzbva == null ? 0 : this.zzbva.hashCode()) + (((this.zzbuZ == null ? 0 : this.zzbuZ.hashCode()) + (((this.zzbuY == null ? 0 : this.zzbuY.hashCode()) + (((this.zzbuX == null ? 0 : this.zzbuX.hashCode()) + (((this.zzbuW == null ? 0 : this.zzbuW.intValue()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31)) * 31)) * 31)) * 31;
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
                    int position = adg.getPosition();
                    int zzLG = adg.zzLG();
                    switch (zzLG) {
                        case 0:
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                            this.zzbuW = Integer.valueOf(zzLG);
                            break;
                        default:
                            adg.zzcp(position);
                            zza(adg, zzLB);
                            continue;
                    }
                case 16:
                    this.zzbuX = Boolean.valueOf(adg.zzLE());
                    continue;
                case 26:
                    this.zzbuY = adg.readString();
                    continue;
                case R.styleable.ChartTheme_sc_seriesAreaColor1 /*34*/:
                    this.zzbuZ = adg.readString();
                    continue;
                case R.styleable.ChartTheme_sc_seriesAreaColor3 /*42*/:
                    this.zzbva = adg.readString();
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
        if (this.zzbuW != null) {
            adh.zzr(1, this.zzbuW.intValue());
        }
        if (this.zzbuX != null) {
            adh.zzk(2, this.zzbuX.booleanValue());
        }
        if (this.zzbuY != null) {
            adh.zzl(3, this.zzbuY);
        }
        if (this.zzbuZ != null) {
            adh.zzl(4, this.zzbuZ);
        }
        if (this.zzbva != null) {
            adh.zzl(5, this.zzbva);
        }
        super.zza(adh);
    }

    protected final int zzn() {
        int zzn = super.zzn();
        if (this.zzbuW != null) {
            zzn += adh.zzs(1, this.zzbuW.intValue());
        }
        if (this.zzbuX != null) {
            this.zzbuX.booleanValue();
            zzn += adh.zzcv(16) + 1;
        }
        if (this.zzbuY != null) {
            zzn += adh.zzm(3, this.zzbuY);
        }
        if (this.zzbuZ != null) {
            zzn += adh.zzm(4, this.zzbuZ);
        }
        return this.zzbva != null ? zzn + adh.zzm(5, this.zzbva) : zzn;
    }
}
