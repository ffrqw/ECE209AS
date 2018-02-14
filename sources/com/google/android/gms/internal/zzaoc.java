package com.google.android.gms.internal;

import com.google.android.gms.common.internal.zzbo;
import java.util.Map;
import java.util.Map.Entry;

public class zzaoc extends zzamh {
    private static String zzain = "3";
    private static String zzaio = "01VDIWEA?";
    private static zzaoc zzaip;

    public zzaoc(zzamj zzamj) {
        super(zzamj);
    }

    private static String zzk(Object obj) {
        if (obj == null) {
            return null;
        }
        Object l = obj instanceof Integer ? new Long((long) ((Integer) obj).intValue()) : obj;
        if (!(l instanceof Long)) {
            return l instanceof Boolean ? String.valueOf(l) : l instanceof Throwable ? l.getClass().getCanonicalName() : "-";
        } else {
            if (Math.abs(((Long) l).longValue()) < 100) {
                return String.valueOf(l);
            }
            String str = String.valueOf(l).charAt(0) == '-' ? "-" : "";
            String valueOf = String.valueOf(Math.abs(((Long) l).longValue()));
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(str);
            stringBuilder.append(Math.round(Math.pow(10.0d, (double) (valueOf.length() - 1))));
            stringBuilder.append("...");
            stringBuilder.append(str);
            stringBuilder.append(Math.round(Math.pow(10.0d, (double) valueOf.length()) - 1.0d));
            return stringBuilder.toString();
        }
    }

    public static zzaoc zzlM() {
        return zzaip;
    }

    public final void zza(zzanx zzanx, String str) {
        Object zzanx2 = zzanx != null ? zzanx.toString() : "no hit data";
        String str2 = "Discarding hit. ";
        String valueOf = String.valueOf(str);
        zzd(valueOf.length() != 0 ? str2.concat(valueOf) : new String(str2), zzanx2);
    }

    public final synchronized void zzb(int i, String str, Object obj, Object obj2, Object obj3) {
        int i2 = 0;
        synchronized (this) {
            zzbo.zzu(str);
            if (i >= 0) {
                i2 = i;
            }
            int length = i2 >= zzaio.length() ? zzaio.length() - 1 : i2;
            char c = zzks().zzln() ? 'C' : 'c';
            String str2 = zzain;
            char charAt = zzaio.charAt(length);
            String str3 = zzami.VERSION;
            String valueOf = String.valueOf(zzamg.zzc(str, zzk(obj), zzk(obj2), zzk(obj3)));
            String stringBuilder = new StringBuilder(((String.valueOf(str2).length() + 3) + String.valueOf(str3).length()) + String.valueOf(valueOf).length()).append(str2).append(charAt).append(c).append(str3).append(":").append(valueOf).toString();
            if (stringBuilder.length() > 1024) {
                stringBuilder = stringBuilder.substring(0, 1024);
            }
            zzaog zzkH = zzkp().zzkH();
            if (zzkH != null) {
                zzkH.zzlZ().zzbA(stringBuilder);
            }
        }
    }

    public final void zze(Map<String, String> map, String str) {
        Object stringBuilder;
        if (map != null) {
            StringBuilder stringBuilder2 = new StringBuilder();
            for (Entry entry : map.entrySet()) {
                if (stringBuilder2.length() > 0) {
                    stringBuilder2.append(',');
                }
                stringBuilder2.append((String) entry.getKey());
                stringBuilder2.append('=');
                stringBuilder2.append((String) entry.getValue());
            }
            stringBuilder = stringBuilder2.toString();
        } else {
            stringBuilder = "no hit data";
        }
        String str2 = "Discarding hit. ";
        String valueOf = String.valueOf(str);
        zzd(valueOf.length() != 0 ? str2.concat(valueOf) : new String(str2), stringBuilder);
    }

    protected final void zzjD() {
        synchronized (zzaoc.class) {
            zzaip = this;
        }
    }
}
