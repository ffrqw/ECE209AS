package com.google.android.gms.internal;

import com.shinobicontrols.charts.R;
import java.io.IOException;

public final class aen extends adp {
    public long zzaLt;
    public String zzcuc;
    public String zzcud;
    public long zzcue;
    public String zzcuf;
    public long zzcug;
    public String zzcuh;
    public String zzcui;
    public String zzcuj;
    public String zzcuk;
    public String zzcul;
    public int zzcum;
    public aem[] zzcun;

    public aen() {
        this.zzcuc = "";
        this.zzcud = "";
        this.zzcue = 0;
        this.zzcuf = "";
        this.zzcug = 0;
        this.zzaLt = 0;
        this.zzcuh = "";
        this.zzcui = "";
        this.zzcuj = "";
        this.zzcuk = "";
        this.zzcul = "";
        this.zzcum = 0;
        this.zzcun = aem.zzMi();
        this.zzcsm = -1;
    }

    public final /* synthetic */ adp zza(adg adg) throws IOException {
        while (true) {
            int zzLB = adg.zzLB();
            switch (zzLB) {
                case 0:
                    break;
                case 10:
                    this.zzcuc = adg.readString();
                    continue;
                case 18:
                    this.zzcud = adg.readString();
                    continue;
                case 24:
                    this.zzcue = adg.zzLC();
                    continue;
                case R.styleable.ChartTheme_sc_seriesAreaColor1 /*34*/:
                    this.zzcuf = adg.readString();
                    continue;
                case R.styleable.ChartTheme_sc_seriesAreaGradientTransparentColor2 /*40*/:
                    this.zzcug = adg.zzLC();
                    continue;
                case R.styleable.ChartTheme_sc_seriesAreaGradientTransparentColor4 /*48*/:
                    this.zzaLt = adg.zzLC();
                    continue;
                case R.styleable.ChartTheme_sc_pieDonutFlavorColor2 /*58*/:
                    this.zzcuh = adg.readString();
                    continue;
                case R.styleable.ChartTheme_sc_financialRisingColor /*66*/:
                    this.zzcui = adg.readString();
                    continue;
                case R.styleable.ChartTheme_sc_annotationTextSize /*74*/:
                    this.zzcuj = adg.readString();
                    continue;
                case 82:
                    this.zzcuk = adg.readString();
                    continue;
                case 90:
                    this.zzcul = adg.readString();
                    continue;
                case 96:
                    this.zzcum = adg.zzLD();
                    continue;
                case 106:
                    int zzb = ads.zzb(adg, 106);
                    zzLB = this.zzcun == null ? 0 : this.zzcun.length;
                    Object obj = new aem[(zzb + zzLB)];
                    if (zzLB != 0) {
                        System.arraycopy(this.zzcun, 0, obj, 0, zzLB);
                    }
                    while (zzLB < obj.length - 1) {
                        obj[zzLB] = new aem();
                        adg.zza(obj[zzLB]);
                        adg.zzLB();
                        zzLB++;
                    }
                    obj[zzLB] = new aem();
                    adg.zza(obj[zzLB]);
                    this.zzcun = obj;
                    continue;
                default:
                    if (!adg.zzcm(zzLB)) {
                        break;
                    }
                    continue;
            }
            return this;
        }
    }

    public final void zza(adh adh) throws IOException {
        if (!(this.zzcuc == null || this.zzcuc.equals(""))) {
            adh.zzl(1, this.zzcuc);
        }
        if (!(this.zzcud == null || this.zzcud.equals(""))) {
            adh.zzl(2, this.zzcud);
        }
        if (this.zzcue != 0) {
            adh.zzb(3, this.zzcue);
        }
        if (!(this.zzcuf == null || this.zzcuf.equals(""))) {
            adh.zzl(4, this.zzcuf);
        }
        if (this.zzcug != 0) {
            adh.zzb(5, this.zzcug);
        }
        if (this.zzaLt != 0) {
            adh.zzb(6, this.zzaLt);
        }
        if (!(this.zzcuh == null || this.zzcuh.equals(""))) {
            adh.zzl(7, this.zzcuh);
        }
        if (!(this.zzcui == null || this.zzcui.equals(""))) {
            adh.zzl(8, this.zzcui);
        }
        if (!(this.zzcuj == null || this.zzcuj.equals(""))) {
            adh.zzl(9, this.zzcuj);
        }
        if (!(this.zzcuk == null || this.zzcuk.equals(""))) {
            adh.zzl(10, this.zzcuk);
        }
        if (!(this.zzcul == null || this.zzcul.equals(""))) {
            adh.zzl(11, this.zzcul);
        }
        if (this.zzcum != 0) {
            adh.zzr(12, this.zzcum);
        }
        if (this.zzcun != null && this.zzcun.length > 0) {
            for (adp adp : this.zzcun) {
                if (adp != null) {
                    adh.zza(13, adp);
                }
            }
        }
        super.zza(adh);
    }

    protected final int zzn() {
        int zzn = super.zzn();
        if (!(this.zzcuc == null || this.zzcuc.equals(""))) {
            zzn += adh.zzm(1, this.zzcuc);
        }
        if (!(this.zzcud == null || this.zzcud.equals(""))) {
            zzn += adh.zzm(2, this.zzcud);
        }
        if (this.zzcue != 0) {
            zzn += adh.zze(3, this.zzcue);
        }
        if (!(this.zzcuf == null || this.zzcuf.equals(""))) {
            zzn += adh.zzm(4, this.zzcuf);
        }
        if (this.zzcug != 0) {
            zzn += adh.zze(5, this.zzcug);
        }
        if (this.zzaLt != 0) {
            zzn += adh.zze(6, this.zzaLt);
        }
        if (!(this.zzcuh == null || this.zzcuh.equals(""))) {
            zzn += adh.zzm(7, this.zzcuh);
        }
        if (!(this.zzcui == null || this.zzcui.equals(""))) {
            zzn += adh.zzm(8, this.zzcui);
        }
        if (!(this.zzcuj == null || this.zzcuj.equals(""))) {
            zzn += adh.zzm(9, this.zzcuj);
        }
        if (!(this.zzcuk == null || this.zzcuk.equals(""))) {
            zzn += adh.zzm(10, this.zzcuk);
        }
        if (!(this.zzcul == null || this.zzcul.equals(""))) {
            zzn += adh.zzm(11, this.zzcul);
        }
        if (this.zzcum != 0) {
            zzn += adh.zzs(12, this.zzcum);
        }
        if (this.zzcun == null || this.zzcun.length <= 0) {
            return zzn;
        }
        int i = zzn;
        for (adp adp : this.zzcun) {
            if (adp != null) {
                i += adh.zzb(13, adp);
            }
        }
        return i;
    }
}
