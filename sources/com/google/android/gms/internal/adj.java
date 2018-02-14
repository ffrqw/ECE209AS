package com.google.android.gms.internal;

import java.io.IOException;

public abstract class adj<M extends adj<M>> extends adp {
    protected adl zzcsd;

    public final /* synthetic */ adp zzLP() throws CloneNotSupportedException {
        return (adj) clone();
    }

    public void zza(adh adh) throws IOException {
        if (this.zzcsd != null) {
            for (int i = 0; i < this.zzcsd.size(); i++) {
                this.zzcsd.zzcy(i).zza(adh);
            }
        }
    }

    protected final boolean zza(adg adg, int i) throws IOException {
        int position = adg.getPosition();
        if (!adg.zzcm(i)) {
            return false;
        }
        int i2 = i >>> 3;
        adr adr = new adr(i, adg.zzp(position, adg.getPosition() - position));
        adm adm = null;
        if (this.zzcsd == null) {
            this.zzcsd = new adl();
        } else {
            adm = this.zzcsd.zzcx(i2);
        }
        if (adm == null) {
            adm = new adm();
            this.zzcsd.zza(i2, adm);
        }
        adm.zza(adr);
        return true;
    }

    protected int zzn() {
        int i = 0;
        if (this.zzcsd == null) {
            return 0;
        }
        int i2 = 0;
        while (i < this.zzcsd.size()) {
            i2 += this.zzcsd.zzcy(i).zzn();
            i++;
        }
        return i2;
    }

    public /* synthetic */ Object clone() throws CloneNotSupportedException {
        adj adj = (adj) super.zzLP();
        adn.zza(this, adj);
        return adj;
    }
}
