package com.google.android.gms.analytics;

import android.net.Uri;
import android.net.Uri.Builder;
import android.text.TextUtils;
import com.google.android.gms.analytics.ecommerce.Product;
import com.google.android.gms.analytics.ecommerce.Promotion;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.internal.zzalk;
import com.google.android.gms.internal.zzall;
import com.google.android.gms.internal.zzalm;
import com.google.android.gms.internal.zzaln;
import com.google.android.gms.internal.zzalo;
import com.google.android.gms.internal.zzalp;
import com.google.android.gms.internal.zzalq;
import com.google.android.gms.internal.zzalr;
import com.google.android.gms.internal.zzals;
import com.google.android.gms.internal.zzalt;
import com.google.android.gms.internal.zzalu;
import com.google.android.gms.internal.zzalv;
import com.google.android.gms.internal.zzalw;
import com.google.android.gms.internal.zzamg;
import com.google.android.gms.internal.zzami;
import com.google.android.gms.internal.zzamj;
import com.google.android.gms.internal.zzamm;
import com.google.android.gms.internal.zzanx;
import com.google.android.gms.internal.zzaos;
import com.j256.ormlite.stmt.query.SimpleComparison;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public final class zzb extends zzamg implements zzo {
    private static DecimalFormat zzadn;
    private final zzamj zzadj;
    private final String zzado;
    private final Uri zzadp;
    private final boolean zzadq;
    private final boolean zzadr;

    public zzb(zzamj zzamj, String str) {
        this(zzamj, str, (byte) 0);
    }

    private zzb(zzamj zzamj, String str, byte b) {
        super(zzamj);
        zzbo.zzcF(str);
        this.zzadj = zzamj;
        this.zzado = str;
        this.zzadq = true;
        this.zzadr = false;
        this.zzadp = zzaZ(this.zzado);
    }

    private static void zza(Map<String, String> map, String str, double d) {
        if (d != 0.0d) {
            map.put(str, zzb(d));
        }
    }

    private static void zza(Map<String, String> map, String str, int i, int i2) {
        if (i > 0 && i2 > 0) {
            map.put(str, i + "x" + i2);
        }
    }

    private static void zza(Map<String, String> map, String str, String str2) {
        if (!TextUtils.isEmpty(str2)) {
            map.put(str, str2);
        }
    }

    private static void zza(Map<String, String> map, String str, boolean z) {
        if (z) {
            map.put(str, "1");
        }
    }

    static Uri zzaZ(String str) {
        zzbo.zzcF(str);
        Builder builder = new Builder();
        builder.scheme("uri");
        builder.authority("google-analytics.com");
        builder.path(str);
        return builder.build();
    }

    private static String zzb(double d) {
        if (zzadn == null) {
            zzadn = new DecimalFormat("0.######");
        }
        return zzadn.format(d);
    }

    private static Map<String, String> zzc(zzi zzi) {
        String str;
        CharSequence zzD;
        Map hashMap = new HashMap();
        zzalo zzalo = (zzalo) zzi.zza(zzalo.class);
        if (zzalo != null) {
            for (Entry entry : zzalo.zzjR().entrySet()) {
                Object obj;
                Boolean value = entry.getValue();
                if (value == null) {
                    obj = null;
                } else if (value instanceof String) {
                    str = (String) value;
                    if (TextUtils.isEmpty(str)) {
                        obj = null;
                    }
                } else if (value instanceof Double) {
                    Double d = (Double) value;
                    obj = d.doubleValue() != 0.0d ? zzb(d.doubleValue()) : null;
                } else {
                    obj = value instanceof Boolean ? value != Boolean.FALSE ? "1" : null : String.valueOf(value);
                }
                if (obj != null) {
                    hashMap.put((String) entry.getKey(), obj);
                }
            }
        }
        zzalt zzalt = (zzalt) zzi.zza(zzalt.class);
        if (zzalt != null) {
            zza(hashMap, "t", zzalt.zzjW());
            zza(hashMap, "cid", zzalt.zzjX());
            zza(hashMap, "uid", zzalt.getUserId());
            zza(hashMap, "sc", zzalt.zzka());
            zza(hashMap, "sf", zzalt.zzkc());
            zza(hashMap, "ni", zzalt.zzkb());
            zza(hashMap, "adid", zzalt.zzjY());
            zza(hashMap, "ate", zzalt.zzjZ());
        }
        zzalu zzalu = (zzalu) zzi.zza(zzalu.class);
        if (zzalu != null) {
            zza(hashMap, "cd", zzalu.zzkd());
            zza(hashMap, "a", (double) zzalu.zzke());
            zza(hashMap, "dr", zzalu.zzkf());
        }
        zzalr zzalr = (zzalr) zzi.zza(zzalr.class);
        if (zzalr != null) {
            zza(hashMap, "ec", zzalr.getCategory());
            zza(hashMap, "ea", zzalr.getAction());
            zza(hashMap, "el", zzalr.getLabel());
            zza(hashMap, "ev", (double) zzalr.getValue());
        }
        zzall zzall = (zzall) zzi.zza(zzall.class);
        if (zzall != null) {
            zza(hashMap, "cn", zzall.getName());
            zza(hashMap, "cs", zzall.getSource());
            zza(hashMap, "cm", zzall.zzjJ());
            zza(hashMap, "ck", zzall.zzjK());
            zza(hashMap, "cc", zzall.getContent());
            zza(hashMap, "ci", zzall.getId());
            zza(hashMap, "anid", zzall.zzjL());
            zza(hashMap, "gclid", zzall.zzjM());
            zza(hashMap, "dclid", zzall.zzjN());
            zza(hashMap, "aclid", zzall.zzjO());
        }
        zzals zzals = (zzals) zzi.zza(zzals.class);
        if (zzals != null) {
            zza(hashMap, "exd", zzals.zzafa);
            zza(hashMap, "exf", zzals.zzafb);
        }
        zzalv zzalv = (zzalv) zzi.zza(zzalv.class);
        if (zzalv != null) {
            zza(hashMap, "sn", zzalv.zzafq);
            zza(hashMap, "sa", zzalv.zzaeX);
            zza(hashMap, "st", zzalv.zzafr);
        }
        zzalw zzalw = (zzalw) zzi.zza(zzalw.class);
        if (zzalw != null) {
            zza(hashMap, "utv", zzalw.zzafs);
            zza(hashMap, "utt", (double) zzalw.zzaft);
            zza(hashMap, "utc", zzalw.mCategory);
            zza(hashMap, "utl", zzalw.zzaeY);
        }
        zzalm zzalm = (zzalm) zzi.zza(zzalm.class);
        if (zzalm != null) {
            for (Entry entry2 : zzalm.zzjP().entrySet()) {
                zzD = zzf.zzD(((Integer) entry2.getKey()).intValue());
                if (!TextUtils.isEmpty(zzD)) {
                    hashMap.put(zzD, (String) entry2.getValue());
                }
            }
        }
        zzaln zzaln = (zzaln) zzi.zza(zzaln.class);
        if (zzaln != null) {
            for (Entry entry22 : zzaln.zzjQ().entrySet()) {
                zzD = zzf.zzF(((Integer) entry22.getKey()).intValue());
                if (!TextUtils.isEmpty(zzD)) {
                    hashMap.put(zzD, zzb(((Double) entry22.getValue()).doubleValue()));
                }
            }
        }
        zzalq zzalq = (zzalq) zzi.zza(zzalq.class);
        if (zzalq != null) {
            if (zzalq.zzjS() != null) {
                for (Entry entry3 : new HashMap(null).entrySet()) {
                    if (((String) entry3.getKey()).startsWith("&")) {
                        hashMap.put(((String) entry3.getKey()).substring(1), (String) entry3.getValue());
                    } else {
                        hashMap.put((String) entry3.getKey(), (String) entry3.getValue());
                    }
                }
            }
            int i = 1;
            for (Promotion zzbl : zzalq.zzjV()) {
                hashMap.putAll(zzbl.zzbl(zzf.zzJ(i)));
                i++;
            }
            i = 1;
            for (Product zzbl2 : zzalq.zzjT()) {
                hashMap.putAll(zzbl2.zzbl(zzf.zzH(i)));
                i++;
            }
            i = 1;
            for (Entry entry222 : zzalq.zzjU().entrySet()) {
                List<Product> list = (List) entry222.getValue();
                String zzM = zzf.zzM(i);
                int i2 = 1;
                for (Product zzbl22 : list) {
                    String valueOf = String.valueOf(zzM);
                    String valueOf2 = String.valueOf(zzf.zzK(i2));
                    hashMap.putAll(zzbl22.zzbl(valueOf2.length() != 0 ? valueOf.concat(valueOf2) : new String(valueOf)));
                    i2++;
                }
                if (!TextUtils.isEmpty((CharSequence) entry222.getKey())) {
                    String valueOf3 = String.valueOf(zzM);
                    str = String.valueOf("nm");
                    hashMap.put(str.length() != 0 ? valueOf3.concat(str) : new String(valueOf3), (String) entry222.getKey());
                }
                i++;
            }
        }
        zzalp zzalp = (zzalp) zzi.zza(zzalp.class);
        if (zzalp != null) {
            zza(hashMap, "ul", zzalp.getLanguage());
            zza(hashMap, "sd", (double) zzalp.zzaeU);
            zza(hashMap, "sr", zzalp.zzNY, zzalp.zzNZ);
            zza(hashMap, "vp", zzalp.zzaeV, zzalp.zzaeW);
        }
        zzalk zzalk = (zzalk) zzi.zza(zzalk.class);
        if (zzalk != null) {
            zza(hashMap, "an", zzalk.zzjG());
            zza(hashMap, "aid", zzalk.zzhl());
            zza(hashMap, "aiid", zzalk.zzjI());
            zza(hashMap, "av", zzalk.zzjH());
        }
        return hashMap;
    }

    public final void zzb(zzi zzi) {
        zzbo.zzu(zzi);
        zzbo.zzb(zzi.zzju(), (Object) "Can't deliver not submitted measurement");
        zzbo.zzcG("deliver should be called on worker thread");
        zzi zzjp = zzi.zzjp();
        zzalt zzalt = (zzalt) zzjp.zzb(zzalt.class);
        if (TextUtils.isEmpty(zzalt.zzjW())) {
            zzkr().zze(zzc(zzjp), "Ignoring measurement without type");
        } else if (TextUtils.isEmpty(zzalt.zzjX())) {
            zzkr().zze(zzc(zzjp), "Ignoring measurement without client id");
        } else if (!this.zzadj.zzkG().getAppOptOut()) {
            double zzkc = zzalt.zzkc();
            if (zzaos.zza(zzkc, zzalt.zzjX())) {
                zzb("Sampling enabled. Hit sampled out. sampling rate", Double.valueOf(zzkc));
                return;
            }
            Map zzc = zzc(zzjp);
            zzc.put("v", "1");
            zzc.put("_v", zzami.zzafL);
            zzc.put("tid", this.zzado);
            if (this.zzadj.zzkG().isDryRunEnabled()) {
                StringBuilder stringBuilder = new StringBuilder();
                for (Entry entry : zzc.entrySet()) {
                    if (stringBuilder.length() != 0) {
                        stringBuilder.append(", ");
                    }
                    stringBuilder.append((String) entry.getKey());
                    stringBuilder.append(SimpleComparison.EQUAL_TO_OPERATION);
                    stringBuilder.append((String) entry.getValue());
                }
                zzc("Dry run is enabled. GoogleAnalytics would have sent", stringBuilder.toString());
                return;
            }
            Map hashMap = new HashMap();
            zzaos.zzb(hashMap, "uid", zzalt.getUserId());
            zzalk zzalk = (zzalk) zzi.zza(zzalk.class);
            if (zzalk != null) {
                zzaos.zzb(hashMap, "an", zzalk.zzjG());
                zzaos.zzb(hashMap, "aid", zzalk.zzhl());
                zzaos.zzb(hashMap, "av", zzalk.zzjH());
                zzaos.zzb(hashMap, "aiid", zzalk.zzjI());
            }
            zzc.put("_s", String.valueOf(zzkv().zza(new zzamm(zzalt.zzjX(), this.zzado, !TextUtils.isEmpty(zzalt.zzjY()), 0, hashMap))));
            zzkv().zza(new zzanx(zzkr(), zzc, zzi.zzjs(), true));
        }
    }

    public final Uri zzjl() {
        return this.zzadp;
    }
}
