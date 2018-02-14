package com.google.android.gms.internal;

import android.content.ContentValues;
import android.database.sqlite.SQLiteException;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzbo;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

final class zzcej extends zzchj {
    zzcej(zzcgl zzcgl) {
        super(zzcgl);
    }

    private static Boolean zza(double d, zzcjp zzcjp) {
        try {
            return zza(new BigDecimal(d), zzcjp, Math.ulp(d));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Boolean zza(long j, zzcjp zzcjp) {
        try {
            return zza(new BigDecimal(j), zzcjp, 0.0d);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private final Boolean zza(zzcjn zzcjn, zzcjw zzcjw, long j) {
        Boolean zza;
        if (zzcjn.zzbuQ != null) {
            zza = zza(j, zzcjn.zzbuQ);
            if (zza == null) {
                return null;
            }
            if (!zza.booleanValue()) {
                return Boolean.valueOf(false);
            }
        }
        Set hashSet = new HashSet();
        for (zzcjo zzcjo : zzcjn.zzbuO) {
            if (TextUtils.isEmpty(zzcjo.zzbuV)) {
                zzwF().zzyz().zzj("null or empty param name in filter. event", zzwA().zzdW(zzcjw.name));
                return null;
            }
            hashSet.add(zzcjo.zzbuV);
        }
        Map arrayMap = new ArrayMap();
        for (zzcjx zzcjx : zzcjw.zzbvw) {
            if (hashSet.contains(zzcjx.name)) {
                if (zzcjx.zzbvA != null) {
                    arrayMap.put(zzcjx.name, zzcjx.zzbvA);
                } else if (zzcjx.zzbuB != null) {
                    arrayMap.put(zzcjx.name, zzcjx.zzbuB);
                } else if (zzcjx.zzaIF != null) {
                    arrayMap.put(zzcjx.name, zzcjx.zzaIF);
                } else {
                    zzwF().zzyz().zze("Unknown value for param. event, param", zzwA().zzdW(zzcjw.name), zzwA().zzdX(zzcjx.name));
                    return null;
                }
            }
        }
        for (zzcjo zzcjo2 : zzcjn.zzbuO) {
            boolean equals = Boolean.TRUE.equals(zzcjo2.zzbuU);
            String str = zzcjo2.zzbuV;
            if (TextUtils.isEmpty(str)) {
                zzwF().zzyz().zzj("Event has empty param name. event", zzwA().zzdW(zzcjw.name));
                return null;
            }
            Object obj = arrayMap.get(str);
            if (obj instanceof Long) {
                if (zzcjo2.zzbuT == null) {
                    zzwF().zzyz().zze("No number filter for long param. event, param", zzwA().zzdW(zzcjw.name), zzwA().zzdX(str));
                    return null;
                }
                zza = zza(((Long) obj).longValue(), zzcjo2.zzbuT);
                if (zza == null) {
                    return null;
                }
                if (((!zza.booleanValue() ? 1 : 0) ^ equals) != 0) {
                    return Boolean.valueOf(false);
                }
            } else if (obj instanceof Double) {
                if (zzcjo2.zzbuT == null) {
                    zzwF().zzyz().zze("No number filter for double param. event, param", zzwA().zzdW(zzcjw.name), zzwA().zzdX(str));
                    return null;
                }
                zza = zza(((Double) obj).doubleValue(), zzcjo2.zzbuT);
                if (zza == null) {
                    return null;
                }
                if (((!zza.booleanValue() ? 1 : 0) ^ equals) != 0) {
                    return Boolean.valueOf(false);
                }
            } else if (obj instanceof String) {
                if (zzcjo2.zzbuS != null) {
                    zza = zza((String) obj, zzcjo2.zzbuS);
                } else if (zzcjo2.zzbuT == null) {
                    zzwF().zzyz().zze("No filter for String param. event, param", zzwA().zzdW(zzcjw.name), zzwA().zzdX(str));
                    return null;
                } else if (zzcjl.zzez((String) obj)) {
                    zza = zza((String) obj, zzcjo2.zzbuT);
                } else {
                    zzwF().zzyz().zze("Invalid param value for number filter. event, param", zzwA().zzdW(zzcjw.name), zzwA().zzdX(str));
                    return null;
                }
                if (zza == null) {
                    return null;
                }
                if (((!zza.booleanValue() ? 1 : 0) ^ equals) != 0) {
                    return Boolean.valueOf(false);
                }
            } else if (obj == null) {
                zzwF().zzyD().zze("Missing param for filter. event, param", zzwA().zzdW(zzcjw.name), zzwA().zzdX(str));
                return Boolean.valueOf(false);
            } else {
                zzwF().zzyz().zze("Unknown param type. event, param", zzwA().zzdW(zzcjw.name), zzwA().zzdX(str));
                return null;
            }
        }
        return Boolean.valueOf(true);
    }

    private static Boolean zza(Boolean bool, boolean z) {
        return bool == null ? null : Boolean.valueOf(bool.booleanValue() ^ z);
    }

    private final Boolean zza(String str, int i, boolean z, String str2, List<String> list, String str3) {
        if (str == null) {
            return null;
        }
        if (i == 6) {
            if (list == null || list.size() == 0) {
                return null;
            }
        } else if (str2 == null) {
            return null;
        }
        if (!(z || i == 1)) {
            str = str.toUpperCase(Locale.ENGLISH);
        }
        switch (i) {
            case 1:
                try {
                    return Boolean.valueOf(Pattern.compile(str3, z ? 0 : 66).matcher(str).matches());
                } catch (PatternSyntaxException e) {
                    zzwF().zzyz().zzj("Invalid regular expression in REGEXP audience filter. expression", str3);
                    return null;
                }
            case 2:
                return Boolean.valueOf(str.startsWith(str2));
            case 3:
                return Boolean.valueOf(str.endsWith(str2));
            case 4:
                return Boolean.valueOf(str.contains(str2));
            case 5:
                return Boolean.valueOf(str.equals(str2));
            case 6:
                return Boolean.valueOf(list.contains(str));
            default:
                return null;
        }
    }

    private static Boolean zza(String str, zzcjp zzcjp) {
        Boolean bool = null;
        if (zzcjl.zzez(str)) {
            try {
                bool = zza(new BigDecimal(str), zzcjp, 0.0d);
            } catch (NumberFormatException e) {
            }
        }
        return bool;
    }

    private final Boolean zza(String str, zzcjr zzcjr) {
        int i = 0;
        String str2 = null;
        zzbo.zzu(zzcjr);
        if (str == null || zzcjr.zzbve == null || zzcjr.zzbve.intValue() == 0) {
            return null;
        }
        List list;
        if (zzcjr.zzbve.intValue() == 6) {
            if (zzcjr.zzbvh == null || zzcjr.zzbvh.length == 0) {
                return null;
            }
        } else if (zzcjr.zzbvf == null) {
            return null;
        }
        int intValue = zzcjr.zzbve.intValue();
        boolean z = zzcjr.zzbvg != null && zzcjr.zzbvg.booleanValue();
        String toUpperCase = (z || intValue == 1 || intValue == 6) ? zzcjr.zzbvf : zzcjr.zzbvf.toUpperCase(Locale.ENGLISH);
        if (zzcjr.zzbvh == null) {
            list = null;
        } else {
            String[] strArr = zzcjr.zzbvh;
            if (z) {
                list = Arrays.asList(strArr);
            } else {
                list = new ArrayList();
                int length = strArr.length;
                while (i < length) {
                    list.add(strArr[i].toUpperCase(Locale.ENGLISH));
                    i++;
                }
            }
        }
        if (intValue == 1) {
            str2 = toUpperCase;
        }
        return zza(str, intValue, z, toUpperCase, list, str2);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static java.lang.Boolean zza(java.math.BigDecimal r10, com.google.android.gms.internal.zzcjp r11, double r12) {
        /*
        r8 = 4;
        r7 = -1;
        r1 = 0;
        r0 = 1;
        r2 = 0;
        com.google.android.gms.common.internal.zzbo.zzu(r11);
        r3 = r11.zzbuW;
        if (r3 == 0) goto L_0x0014;
    L_0x000c:
        r3 = r11.zzbuW;
        r3 = r3.intValue();
        if (r3 != 0) goto L_0x0016;
    L_0x0014:
        r0 = r2;
    L_0x0015:
        return r0;
    L_0x0016:
        r3 = r11.zzbuW;
        r3 = r3.intValue();
        if (r3 != r8) goto L_0x0028;
    L_0x001e:
        r3 = r11.zzbuZ;
        if (r3 == 0) goto L_0x0026;
    L_0x0022:
        r3 = r11.zzbva;
        if (r3 != 0) goto L_0x002e;
    L_0x0026:
        r0 = r2;
        goto L_0x0015;
    L_0x0028:
        r3 = r11.zzbuY;
        if (r3 != 0) goto L_0x002e;
    L_0x002c:
        r0 = r2;
        goto L_0x0015;
    L_0x002e:
        r3 = r11.zzbuW;
        r6 = r3.intValue();
        r3 = r11.zzbuW;
        r3 = r3.intValue();
        if (r3 != r8) goto L_0x0066;
    L_0x003c:
        r3 = r11.zzbuZ;
        r3 = com.google.android.gms.internal.zzcjl.zzez(r3);
        if (r3 == 0) goto L_0x004c;
    L_0x0044:
        r3 = r11.zzbva;
        r3 = com.google.android.gms.internal.zzcjl.zzez(r3);
        if (r3 != 0) goto L_0x004e;
    L_0x004c:
        r0 = r2;
        goto L_0x0015;
    L_0x004e:
        r4 = new java.math.BigDecimal;	 Catch:{ NumberFormatException -> 0x0063 }
        r3 = r11.zzbuZ;	 Catch:{ NumberFormatException -> 0x0063 }
        r4.<init>(r3);	 Catch:{ NumberFormatException -> 0x0063 }
        r3 = new java.math.BigDecimal;	 Catch:{ NumberFormatException -> 0x0063 }
        r5 = r11.zzbva;	 Catch:{ NumberFormatException -> 0x0063 }
        r3.<init>(r5);	 Catch:{ NumberFormatException -> 0x0063 }
        r5 = r2;
    L_0x005d:
        if (r6 != r8) goto L_0x007e;
    L_0x005f:
        if (r4 != 0) goto L_0x0080;
    L_0x0061:
        r0 = r2;
        goto L_0x0015;
    L_0x0063:
        r0 = move-exception;
        r0 = r2;
        goto L_0x0015;
    L_0x0066:
        r3 = r11.zzbuY;
        r3 = com.google.android.gms.internal.zzcjl.zzez(r3);
        if (r3 != 0) goto L_0x0070;
    L_0x006e:
        r0 = r2;
        goto L_0x0015;
    L_0x0070:
        r3 = new java.math.BigDecimal;	 Catch:{ NumberFormatException -> 0x007b }
        r4 = r11.zzbuY;	 Catch:{ NumberFormatException -> 0x007b }
        r3.<init>(r4);	 Catch:{ NumberFormatException -> 0x007b }
        r4 = r2;
        r5 = r3;
        r3 = r2;
        goto L_0x005d;
    L_0x007b:
        r0 = move-exception;
        r0 = r2;
        goto L_0x0015;
    L_0x007e:
        if (r5 == 0) goto L_0x0083;
    L_0x0080:
        switch(r6) {
            case 1: goto L_0x0085;
            case 2: goto L_0x0092;
            case 3: goto L_0x00a0;
            case 4: goto L_0x00ee;
            default: goto L_0x0083;
        };
    L_0x0083:
        r0 = r2;
        goto L_0x0015;
    L_0x0085:
        r2 = r10.compareTo(r5);
        if (r2 != r7) goto L_0x0090;
    L_0x008b:
        r0 = java.lang.Boolean.valueOf(r0);
        goto L_0x0015;
    L_0x0090:
        r0 = r1;
        goto L_0x008b;
    L_0x0092:
        r2 = r10.compareTo(r5);
        if (r2 != r0) goto L_0x009e;
    L_0x0098:
        r0 = java.lang.Boolean.valueOf(r0);
        goto L_0x0015;
    L_0x009e:
        r0 = r1;
        goto L_0x0098;
    L_0x00a0:
        r2 = 0;
        r2 = (r12 > r2 ? 1 : (r12 == r2 ? 0 : -1));
        if (r2 == 0) goto L_0x00e0;
    L_0x00a6:
        r2 = new java.math.BigDecimal;
        r2.<init>(r12);
        r3 = new java.math.BigDecimal;
        r4 = 2;
        r3.<init>(r4);
        r2 = r2.multiply(r3);
        r2 = r5.subtract(r2);
        r2 = r10.compareTo(r2);
        if (r2 != r0) goto L_0x00de;
    L_0x00bf:
        r2 = new java.math.BigDecimal;
        r2.<init>(r12);
        r3 = new java.math.BigDecimal;
        r4 = 2;
        r3.<init>(r4);
        r2 = r2.multiply(r3);
        r2 = r5.add(r2);
        r2 = r10.compareTo(r2);
        if (r2 != r7) goto L_0x00de;
    L_0x00d8:
        r0 = java.lang.Boolean.valueOf(r0);
        goto L_0x0015;
    L_0x00de:
        r0 = r1;
        goto L_0x00d8;
    L_0x00e0:
        r2 = r10.compareTo(r5);
        if (r2 != 0) goto L_0x00ec;
    L_0x00e6:
        r0 = java.lang.Boolean.valueOf(r0);
        goto L_0x0015;
    L_0x00ec:
        r0 = r1;
        goto L_0x00e6;
    L_0x00ee:
        r2 = r10.compareTo(r4);
        if (r2 == r7) goto L_0x0100;
    L_0x00f4:
        r2 = r10.compareTo(r3);
        if (r2 == r0) goto L_0x0100;
    L_0x00fa:
        r0 = java.lang.Boolean.valueOf(r0);
        goto L_0x0015;
    L_0x0100:
        r0 = r1;
        goto L_0x00fa;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.zzcej.zza(java.math.BigDecimal, com.google.android.gms.internal.zzcjp, double):java.lang.Boolean");
    }

    final zzcjv[] zza(String str, zzcjw[] zzcjwArr, zzckb[] zzckbArr) {
        int intValue;
        zzcka zzcka;
        BitSet bitSet;
        BitSet bitSet2;
        Map map;
        Map map2;
        zzbo.zzcF(str);
        Set hashSet = new HashSet();
        ArrayMap arrayMap = new ArrayMap();
        Map arrayMap2 = new ArrayMap();
        ArrayMap arrayMap3 = new ArrayMap();
        Map zzdT = zzwz().zzdT(str);
        if (zzdT != null) {
            for (Integer intValue2 : zzdT.keySet()) {
                intValue = intValue2.intValue();
                zzcka = (zzcka) zzdT.get(Integer.valueOf(intValue));
                bitSet = (BitSet) arrayMap2.get(Integer.valueOf(intValue));
                bitSet2 = (BitSet) arrayMap3.get(Integer.valueOf(intValue));
                if (bitSet == null) {
                    bitSet = new BitSet();
                    arrayMap2.put(Integer.valueOf(intValue), bitSet);
                    bitSet2 = new BitSet();
                    arrayMap3.put(Integer.valueOf(intValue), bitSet2);
                }
                for (int i = 0; i < (zzcka.zzbwe.length << 6); i++) {
                    if (zzcjl.zza(zzcka.zzbwe, i)) {
                        zzwF().zzyD().zze("Filter already evaluated. audience ID, filter ID", Integer.valueOf(intValue), Integer.valueOf(i));
                        bitSet2.set(i);
                        if (zzcjl.zza(zzcka.zzbwf, i)) {
                            bitSet.set(i);
                        }
                    }
                }
                zzcjv zzcjv = new zzcjv();
                arrayMap.put(Integer.valueOf(intValue), zzcjv);
                zzcjv.zzbvu = Boolean.valueOf(false);
                zzcjv.zzbvt = zzcka;
                zzcjv.zzbvs = new zzcka();
                zzcjv.zzbvs.zzbwf = zzcjl.zza(bitSet);
                zzcjv.zzbvs.zzbwe = zzcjl.zza(bitSet2);
            }
        }
        if (zzcjwArr != null) {
            ArrayMap arrayMap4 = new ArrayMap();
            for (zzcjw zzcjw : zzcjwArr) {
                zzcev zzcev;
                zzcev zzE = zzwz().zzE(str, zzcjw.name);
                if (zzE == null) {
                    zzwF().zzyz().zze("Event aggregate wasn't created during raw event logging. appId, event", zzcfl.zzdZ(str), zzwA().zzdW(zzcjw.name));
                    zzcev = new zzcev(str, zzcjw.name, 1, 1, zzcjw.zzbvx.longValue());
                } else {
                    zzcev = zzE.zzys();
                }
                zzwz().zza(zzcev);
                long j = zzcev.zzbpG;
                map = (Map) arrayMap4.get(zzcjw.name);
                if (map == null) {
                    map = zzwz().zzJ(str, zzcjw.name);
                    if (map == null) {
                        map = new ArrayMap();
                    }
                    arrayMap4.put(zzcjw.name, map);
                    map2 = map;
                } else {
                    map2 = map;
                }
                for (Integer intValue22 : r7.keySet()) {
                    int intValue3 = intValue22.intValue();
                    if (hashSet.contains(Integer.valueOf(intValue3))) {
                        zzwF().zzyD().zzj("Skipping failed audience ID", Integer.valueOf(intValue3));
                    } else {
                        bitSet = (BitSet) arrayMap2.get(Integer.valueOf(intValue3));
                        bitSet2 = (BitSet) arrayMap3.get(Integer.valueOf(intValue3));
                        if (((zzcjv) arrayMap.get(Integer.valueOf(intValue3))) == null) {
                            zzcjv zzcjv2 = new zzcjv();
                            arrayMap.put(Integer.valueOf(intValue3), zzcjv2);
                            zzcjv2.zzbvu = Boolean.valueOf(true);
                            bitSet = new BitSet();
                            arrayMap2.put(Integer.valueOf(intValue3), bitSet);
                            bitSet2 = new BitSet();
                            arrayMap3.put(Integer.valueOf(intValue3), bitSet2);
                        }
                        for (zzcjn zzcjn : (List) r7.get(Integer.valueOf(intValue3))) {
                            if (zzwF().zzz(2)) {
                                zzwF().zzyD().zzd("Evaluating filter. audience, filter, event", Integer.valueOf(intValue3), zzcjn.zzbuM, zzwA().zzdW(zzcjn.zzbuN));
                                zzwF().zzyD().zzj("Filter definition", zzwA().zza(zzcjn));
                            }
                            if (zzcjn.zzbuM == null || zzcjn.zzbuM.intValue() > 256) {
                                zzwF().zzyz().zze("Invalid event filter ID. appId, id", zzcfl.zzdZ(str), String.valueOf(zzcjn.zzbuM));
                            } else if (bitSet.get(zzcjn.zzbuM.intValue())) {
                                zzwF().zzyD().zze("Event filter already evaluated true. audience ID, filter ID", Integer.valueOf(intValue3), zzcjn.zzbuM);
                            } else {
                                Object obj;
                                Boolean zza = zza(zzcjn, zzcjw, j);
                                zzcfn zzyD = zzwF().zzyD();
                                String str2 = "Event filter result";
                                if (zza == null) {
                                    obj = "null";
                                } else {
                                    Boolean bool = zza;
                                }
                                zzyD.zzj(str2, obj);
                                if (zza == null) {
                                    hashSet.add(Integer.valueOf(intValue3));
                                } else {
                                    bitSet2.set(zzcjn.zzbuM.intValue());
                                    if (zza.booleanValue()) {
                                        bitSet.set(zzcjn.zzbuM.intValue());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (zzckbArr != null) {
            Map arrayMap5 = new ArrayMap();
            for (zzckb zzckb : zzckbArr) {
                map = (Map) arrayMap5.get(zzckb.name);
                if (map == null) {
                    map = zzwz().zzK(str, zzckb.name);
                    if (map == null) {
                        map = new ArrayMap();
                    }
                    arrayMap5.put(zzckb.name, map);
                    map2 = map;
                } else {
                    map2 = map;
                }
                for (Integer intValue222 : r7.keySet()) {
                    int intValue4 = intValue222.intValue();
                    if (hashSet.contains(Integer.valueOf(intValue4))) {
                        zzwF().zzyD().zzj("Skipping failed audience ID", Integer.valueOf(intValue4));
                    } else {
                        bitSet = (BitSet) arrayMap2.get(Integer.valueOf(intValue4));
                        bitSet2 = (BitSet) arrayMap3.get(Integer.valueOf(intValue4));
                        if (((zzcjv) arrayMap.get(Integer.valueOf(intValue4))) == null) {
                            zzcjv2 = new zzcjv();
                            arrayMap.put(Integer.valueOf(intValue4), zzcjv2);
                            zzcjv2.zzbvu = Boolean.valueOf(true);
                            bitSet = new BitSet();
                            arrayMap2.put(Integer.valueOf(intValue4), bitSet);
                            bitSet2 = new BitSet();
                            arrayMap3.put(Integer.valueOf(intValue4), bitSet2);
                        }
                        for (zzcjq zzcjq : (List) r7.get(Integer.valueOf(intValue4))) {
                            if (zzwF().zzz(2)) {
                                zzwF().zzyD().zzd("Evaluating filter. audience, filter, property", Integer.valueOf(intValue4), zzcjq.zzbuM, zzwA().zzdY(zzcjq.zzbvc));
                                zzwF().zzyD().zzj("Filter definition", zzwA().zza(zzcjq));
                            }
                            if (zzcjq.zzbuM == null || zzcjq.zzbuM.intValue() > 256) {
                                zzwF().zzyz().zze("Invalid property filter ID. appId, id", zzcfl.zzdZ(str), String.valueOf(zzcjq.zzbuM));
                                hashSet.add(Integer.valueOf(intValue4));
                                break;
                            } else if (bitSet.get(zzcjq.zzbuM.intValue())) {
                                zzwF().zzyD().zze("Property filter already evaluated true. audience ID, filter ID", Integer.valueOf(intValue4), zzcjq.zzbuM);
                            } else {
                                Object obj2;
                                zzcjo zzcjo = zzcjq.zzbvd;
                                if (zzcjo == null) {
                                    zzwF().zzyz().zzj("Missing property filter. property", zzwA().zzdY(zzckb.name));
                                    bool = null;
                                } else {
                                    boolean equals = Boolean.TRUE.equals(zzcjo.zzbuU);
                                    if (zzckb.zzbvA != null) {
                                        if (zzcjo.zzbuT == null) {
                                            zzwF().zzyz().zzj("No number filter for long property. property", zzwA().zzdY(zzckb.name));
                                            bool = null;
                                        } else {
                                            bool = zza(zza(zzckb.zzbvA.longValue(), zzcjo.zzbuT), equals);
                                        }
                                    } else if (zzckb.zzbuB != null) {
                                        if (zzcjo.zzbuT == null) {
                                            zzwF().zzyz().zzj("No number filter for double property. property", zzwA().zzdY(zzckb.name));
                                            bool = null;
                                        } else {
                                            bool = zza(zza(zzckb.zzbuB.doubleValue(), zzcjo.zzbuT), equals);
                                        }
                                    } else if (zzckb.zzaIF == null) {
                                        zzwF().zzyz().zzj("User property has no value, property", zzwA().zzdY(zzckb.name));
                                        bool = null;
                                    } else if (zzcjo.zzbuS == null) {
                                        if (zzcjo.zzbuT == null) {
                                            zzwF().zzyz().zzj("No string or number filter defined. property", zzwA().zzdY(zzckb.name));
                                        } else if (zzcjl.zzez(zzckb.zzaIF)) {
                                            bool = zza(zza(zzckb.zzaIF, zzcjo.zzbuT), equals);
                                        } else {
                                            zzwF().zzyz().zze("Invalid user property value for Numeric number filter. property, value", zzwA().zzdY(zzckb.name), zzckb.zzaIF);
                                        }
                                        bool = null;
                                    } else {
                                        bool = zza(zza(zzckb.zzaIF, zzcjo.zzbuS), equals);
                                    }
                                }
                                zzcfn zzyD2 = zzwF().zzyD();
                                String str3 = "Property filter result";
                                if (bool == null) {
                                    obj2 = "null";
                                } else {
                                    zza = bool;
                                }
                                zzyD2.zzj(str3, obj2);
                                if (bool == null) {
                                    hashSet.add(Integer.valueOf(intValue4));
                                } else {
                                    bitSet2.set(zzcjq.zzbuM.intValue());
                                    if (bool.booleanValue()) {
                                        bitSet.set(zzcjq.zzbuM.intValue());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        zzcjv[] zzcjvArr = new zzcjv[arrayMap2.size()];
        int i2 = 0;
        for (Integer intValue2222 : arrayMap2.keySet()) {
            intValue = intValue2222.intValue();
            if (!hashSet.contains(Integer.valueOf(intValue))) {
                zzcjv2 = (zzcjv) arrayMap.get(Integer.valueOf(intValue));
                zzcjv = zzcjv2 == null ? new zzcjv() : zzcjv2;
                int i3 = i2 + 1;
                zzcjvArr[i2] = zzcjv;
                zzcjv.zzbuI = Integer.valueOf(intValue);
                zzcjv.zzbvs = new zzcka();
                zzcjv.zzbvs.zzbwf = zzcjl.zza((BitSet) arrayMap2.get(Integer.valueOf(intValue)));
                zzcjv.zzbvs.zzbwe = zzcjl.zza((BitSet) arrayMap3.get(Integer.valueOf(intValue)));
                zzcen zzwz = zzwz();
                zzcka = zzcjv.zzbvs;
                zzwz.zzkD();
                zzwz.zzjC();
                zzbo.zzcF(str);
                zzbo.zzu(zzcka);
                try {
                    byte[] bArr = new byte[zzcka.zzLW()];
                    adh zzc$2f392430 = adh.zzc$2f392430(bArr, bArr.length);
                    zzcka.zza(zzc$2f392430);
                    zzc$2f392430.zzLN();
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("app_id", str);
                    contentValues.put("audience_id", Integer.valueOf(intValue));
                    contentValues.put("current_results", bArr);
                    try {
                        if (zzwz.getWritableDatabase().insertWithOnConflict("audience_filter_values", null, contentValues, 5) == -1) {
                            zzwz.zzwF().zzyx().zzj("Failed to insert filter results (got -1). appId", zzcfl.zzdZ(str));
                        }
                        i2 = i3;
                    } catch (SQLiteException e) {
                        zzwz.zzwF().zzyx().zze("Error storing filter results. appId", zzcfl.zzdZ(str), e);
                        i2 = i3;
                    }
                } catch (IOException e2) {
                    zzwz.zzwF().zzyx().zze("Configuration loss. Failed to serialize filter results. appId", zzcfl.zzdZ(str), e2);
                    i2 = i3;
                }
            }
        }
        return (zzcjv[]) Arrays.copyOf(zzcjvArr, i2);
    }

    protected final void zzjD() {
    }
}
