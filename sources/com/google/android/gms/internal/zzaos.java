package com.google.android.gms.internal;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ServiceInfo;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.util.zzm;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class zzaos {
    private static final char[] zzaiO = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static zzall zza(zzaoc zzaoc, String str) {
        zzbo.zzu(zzaoc);
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        HashMap hashMap = new HashMap();
        try {
            String str2 = "?";
            String valueOf = String.valueOf(str);
            Map zza = zzm.zza(new URI(valueOf.length() != 0 ? str2.concat(valueOf) : new String(str2)), "UTF-8");
            zzall zzall = new zzall();
            zzall.zzbd((String) zza.get("utm_content"));
            zzall.zzbb((String) zza.get("utm_medium"));
            zzall.setName((String) zza.get("utm_campaign"));
            zzall.zzba((String) zza.get("utm_source"));
            zzall.zzbc((String) zza.get("utm_term"));
            zzall.zzbe((String) zza.get("utm_id"));
            zzall.zzbf((String) zza.get("anid"));
            zzall.zzbg((String) zza.get("gclid"));
            zzall.zzbh((String) zza.get("dclid"));
            zzall.zzbi((String) zza.get("aclid"));
            return zzall;
        } catch (URISyntaxException e) {
            zzaoc.zzd("No valid campaign data found", e);
            return null;
        }
    }

    public static String zza(Locale locale) {
        if (locale == null) {
            return null;
        }
        Object language = locale.getLanguage();
        if (TextUtils.isEmpty(language)) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(language.toLowerCase());
        if (!TextUtils.isEmpty(locale.getCountry())) {
            stringBuilder.append("-").append(locale.getCountry().toLowerCase());
        }
        return stringBuilder.toString();
    }

    public static void zza(Map<String, String> map, String str, Map<String, String> map2) {
        zzb((Map) map, str, (String) map2.get(str));
    }

    public static boolean zza(double d, String str) {
        if (d <= 0.0d || d >= 100.0d) {
            return false;
        }
        int i;
        if (TextUtils.isEmpty(str)) {
            i = 1;
        } else {
            i = 0;
            for (int length = str.length() - 1; length >= 0; length--) {
                char charAt = str.charAt(length);
                i = (((i << 6) & 268435455) + charAt) + (charAt << 14);
                int i2 = 266338304 & i;
                if (i2 != 0) {
                    i ^= i2 >> 21;
                }
            }
        }
        return ((double) (i % 10000)) >= 100.0d * d;
    }

    public static boolean zza(Context context, String str, boolean z) {
        try {
            ActivityInfo receiverInfo = context.getPackageManager().getReceiverInfo(new ComponentName(context, str), 2);
            if (receiverInfo != null && receiverInfo.enabled && (!z || receiverInfo.exported)) {
                return true;
            }
        } catch (NameNotFoundException e) {
        }
        return false;
    }

    public static double zza$505d11f3(String str) {
        double d = 100.0d;
        if (str != null) {
            try {
                d = Double.parseDouble(str);
            } catch (NumberFormatException e) {
            }
        }
        return d;
    }

    public static void zzb(Map<String, String> map, String str, String str2) {
        if (str2 != null && !map.containsKey(str)) {
            map.put(str, str2);
        }
    }

    public static void zzb(Map<String, String> map, String str, boolean z) {
        if (!map.containsKey(str)) {
            map.put(str, z ? "1" : "0");
        }
    }

    public static long zzbC(String str) {
        long j = 0;
        if (str != null) {
            try {
                j = Long.parseLong(str);
            } catch (NumberFormatException e) {
            }
        }
        return j;
    }

    public static MessageDigest zzbE(String str) {
        int i = 0;
        while (i < 2) {
            try {
                MessageDigest instance = MessageDigest.getInstance(str);
                if (instance != null) {
                    return instance;
                }
                i++;
            } catch (NoSuchAlgorithmException e) {
            }
        }
        return null;
    }

    public static boolean zzbF(String str) {
        return TextUtils.isEmpty(str) || !str.startsWith("http:");
    }

    public static void zzc(Map<String, String> map, String str, String str2) {
        if (str2 != null && TextUtils.isEmpty((CharSequence) map.get(str))) {
            map.put(str, str2);
        }
    }

    public static boolean zzf$505cbf47(String str) {
        return (str == null || str.equalsIgnoreCase("true") || str.equalsIgnoreCase("yes") || str.equalsIgnoreCase("1")) ? true : (str.equalsIgnoreCase("false") || str.equalsIgnoreCase("no") || str.equalsIgnoreCase("0")) ? false : true;
    }

    public static boolean zzw(Context context, String str) {
        try {
            ServiceInfo serviceInfo = context.getPackageManager().getServiceInfo(new ComponentName(context, str), 4);
            if (serviceInfo != null && serviceInfo.enabled) {
                return true;
            }
        } catch (NameNotFoundException e) {
        }
        return false;
    }
}
