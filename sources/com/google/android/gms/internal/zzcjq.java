package com.google.android.gms.internal;

import java.io.IOException;

public final class zzcjq extends adj<zzcjq> {
    private static volatile zzcjq[] zzbvb;
    public Integer zzbuM;
    public String zzbvc;
    public zzcjo zzbvd;

    public zzcjq() {
        this.zzbuM = null;
        this.zzbvc = null;
        this.zzbvd = null;
        this.zzcsd = null;
        this.zzcsm = -1;
    }

    public static zzcjq[] zzzx() {
        if (zzbvb == null) {
            synchronized (adn.zzcsl) {
                if (zzbvb == null) {
                    zzbvb = new zzcjq[0];
                }
            }
        }
        return zzbvb;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzcjq)) {
            return false;
        }
        zzcjq zzcjq = (zzcjq) obj;
        if (this.zzbuM == null) {
            if (zzcjq.zzbuM != null) {
                return false;
            }
        } else if (!this.zzbuM.equals(zzcjq.zzbuM)) {
            return false;
        }
        if (this.zzbvc == null) {
            if (zzcjq.zzbvc != null) {
                return false;
            }
        } else if (!this.zzbvc.equals(zzcjq.zzbvc)) {
            return false;
        }
        if (this.zzbvd == null) {
            if (zzcjq.zzbvd != null) {
                return false;
            }
        } else if (!this.zzbvd.equals(zzcjq.zzbvd)) {
            return false;
        }
        return (this.zzcsd == null || this.zzcsd.isEmpty()) ? zzcjq.zzcsd == null || zzcjq.zzcsd.isEmpty() : this.zzcsd.equals(zzcjq.zzcsd);
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = ((this.zzbvd == null ? 0 : this.zzbvd.hashCode()) + (((this.zzbvc == null ? 0 : this.zzbvc.hashCode()) + (((this.zzbuM == null ? 0 : this.zzbuM.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31)) * 31;
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
                    this.zzbvc = adg.readString();
                    continue;
                case 26:
                    if (this.zzbvd == null) {
                        this.zzbvd = new zzcjo();
                    }
                    adg.zza(this.zzbvd);
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
        if (this.zzbvc != null) {
            adh.zzl(2, this.zzbvc);
        }
        if (this.zzbvd != null) {
            adh.zza(3, this.zzbvd);
        }
        super.zza(adh);
    }

    protected final int zzn() {
        int zzn = super.zzn();
        if (this.zzbuM != null) {
            zzn += adh.zzs(1, this.zzbuM.intValue());
        }
        if (this.zzbvc != null) {
            zzn += adh.zzm(2, this.zzbvc);
        }
        return this.zzbvd != null ? zzn + adh.zzb(3, this.zzbvd) : zzn;
    }
}
