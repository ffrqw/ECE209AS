package com.google.android.gms.internal;

import android.text.TextUtils;
import com.google.android.gms.common.internal.zzbo;
import com.j256.ormlite.stmt.query.SimpleComparison;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public final class zzanx {
    private final Map<String, String> zzHa;
    private final List<zzane> zzaib;
    private final long zzaic;
    private final long zzaid;
    private final int zzaie;
    private final boolean zzaif;
    private final String zzaig;

    public zzanx(zzamg zzamg, Map<String, String> map, long j, boolean z) {
        this(zzamg, map, j, z, 0, 0, null);
    }

    public zzanx(zzamg zzamg, Map<String, String> map, long j, boolean z, long j2, int i) {
        this(zzamg, map, j, z, j2, i, null);
    }

    public zzanx(zzamg zzamg, Map<String, String> map, long j, boolean z, long j2, int i, List<zzane> list) {
        zzbo.zzu(zzamg);
        zzbo.zzu(map);
        this.zzaid = j;
        this.zzaif = z;
        this.zzaic = j2;
        this.zzaie = i;
        this.zzaib = list != null ? list : Collections.emptyList();
        this.zzaig = zzt(list);
        Map hashMap = new HashMap();
        for (Entry entry : map.entrySet()) {
            String zza;
            if (zzj(entry.getKey())) {
                zza = zza(zzamg, entry.getKey());
                if (zza != null) {
                    hashMap.put(zza, zzb(zzamg, entry.getValue()));
                }
            }
        }
        for (Entry entry2 : map.entrySet()) {
            if (!zzj(entry2.getKey())) {
                zza = zza(zzamg, entry2.getKey());
                if (zza != null) {
                    hashMap.put(zza, zzb(zzamg, entry2.getValue()));
                }
            }
        }
        if (!TextUtils.isEmpty(this.zzaig)) {
            zzaos.zzb(hashMap, "_v", this.zzaig);
            if (this.zzaig.equals("ma4.0.0") || this.zzaig.equals("ma4.0.1")) {
                hashMap.remove("adid");
            }
        }
        this.zzHa = Collections.unmodifiableMap(hashMap);
    }

    private static String zza(zzamg zzamg, Object obj) {
        if (obj == null) {
            return null;
        }
        Object obj2 = obj.toString();
        if (obj2.startsWith("&")) {
            obj2 = obj2.substring(1);
        }
        int length = obj2.length();
        if (length > 256) {
            obj2 = obj2.substring(0, 256);
            zzamg.zzc("Hit param name is too long and will be trimmed", Integer.valueOf(length), obj2);
        }
        return TextUtils.isEmpty(obj2) ? null : obj2;
    }

    private static String zzb(zzamg zzamg, Object obj) {
        String obj2 = obj == null ? "" : obj.toString();
        int length = obj2.length();
        if (length <= 8192) {
            return obj2;
        }
        obj2 = obj2.substring(0, 8192);
        zzamg.zzc("Hit param value is too long and will be trimmed", Integer.valueOf(length), obj2);
        return obj2;
    }

    private static boolean zzj(Object obj) {
        return obj == null ? false : obj.toString().startsWith("&");
    }

    private final String zzn(String str, String str2) {
        zzbo.zzcF(str);
        zzbo.zzb(!str.startsWith("&"), (Object) "Short param name required");
        String str3 = (String) this.zzHa.get(str);
        return str3 != null ? str3 : str2;
    }

    private static String zzt(List<zzane> list) {
        CharSequence value;
        if (list != null) {
            for (zzane zzane : list) {
                if ("appendVersion".equals(zzane.getId())) {
                    value = zzane.getValue();
                    break;
                }
            }
        }
        value = null;
        return TextUtils.isEmpty(value) ? null : value;
    }

    public final String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("ht=").append(this.zzaid);
        if (this.zzaic != 0) {
            stringBuffer.append(", dbId=").append(this.zzaic);
        }
        if (this.zzaie != 0) {
            stringBuffer.append(", appUID=").append(this.zzaie);
        }
        List arrayList = new ArrayList(this.zzHa.keySet());
        Collections.sort(arrayList);
        ArrayList arrayList2 = (ArrayList) arrayList;
        int size = arrayList2.size();
        int i = 0;
        while (i < size) {
            Object obj = arrayList2.get(i);
            i++;
            String str = (String) obj;
            stringBuffer.append(", ");
            stringBuffer.append(str);
            stringBuffer.append(SimpleComparison.EQUAL_TO_OPERATION);
            stringBuffer.append((String) this.zzHa.get(str));
        }
        return stringBuffer.toString();
    }

    public final Map<String, String> zzdV() {
        return this.zzHa;
    }

    public final int zzlE() {
        return this.zzaie;
    }

    public final long zzlF() {
        return this.zzaic;
    }

    public final long zzlG() {
        return this.zzaid;
    }

    public final List<zzane> zzlH() {
        return this.zzaib;
    }

    public final boolean zzlI() {
        return this.zzaif;
    }

    public final long zzlJ() {
        return zzaos.zzbC(zzn("_s", "0"));
    }

    public final String zzlK() {
        return zzn("_m", "");
    }
}
