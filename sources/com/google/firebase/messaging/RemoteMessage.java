package com.google.firebase.messaging;

import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.v4.util.ArrayMap;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import java.util.Map;

public final class RemoteMessage extends zza {
    public static final Creator<RemoteMessage> CREATOR = new zzf();
    Bundle mBundle;
    private Map<String, String> zzadY;
    private Notification zzckY;

    public static class Notification {
        private final String mTag;
        private final String zzHD;
        private final String zzaoy;
        private final String zzckZ;
        private final String[] zzcla;
        private final String zzclb;
        private final String[] zzclc;
        private final String zzcld;
        private final String zzcle;
        private final String zzclf;
        private final String zzclg;
        private final Uri zzclh;

        private Notification(Bundle bundle) {
            this.zzaoy = zza.zze(bundle, "gcm.n.title");
            this.zzckZ = zza.zzh(bundle, "gcm.n.title");
            this.zzcla = zzk(bundle, "gcm.n.title");
            this.zzHD = zza.zze(bundle, "gcm.n.body");
            this.zzclb = zza.zzh(bundle, "gcm.n.body");
            this.zzclc = zzk(bundle, "gcm.n.body");
            this.zzcld = zza.zze(bundle, "gcm.n.icon");
            this.zzcle = zza.zzI(bundle);
            this.mTag = zza.zze(bundle, "gcm.n.tag");
            this.zzclf = zza.zze(bundle, "gcm.n.color");
            this.zzclg = zza.zze(bundle, "gcm.n.click_action");
            this.zzclh = zza.zzH(bundle);
        }

        private static String[] zzk(Bundle bundle, String str) {
            Object[] zzi = zza.zzi(bundle, str);
            if (zzi == null) {
                return null;
            }
            String[] strArr = new String[zzi.length];
            for (int i = 0; i < zzi.length; i++) {
                strArr[i] = String.valueOf(zzi[i]);
            }
            return strArr;
        }
    }

    RemoteMessage(Bundle bundle) {
        this.mBundle = bundle;
    }

    public final Map<String, String> getData() {
        if (this.zzadY == null) {
            this.zzadY = new ArrayMap();
            for (String str : this.mBundle.keySet()) {
                Object obj = this.mBundle.get(str);
                if (obj instanceof String) {
                    String str2 = (String) obj;
                    if (!(str.startsWith("google.") || str.startsWith("gcm.") || str.equals("from") || str.equals("message_type") || str.equals("collapse_key"))) {
                        this.zzadY.put(str, str2);
                    }
                }
            }
        }
        return this.zzadY;
    }

    public final Notification getNotification() {
        if (this.zzckY == null && zza.zzG(this.mBundle)) {
            this.zzckY = new Notification(this.mBundle);
        }
        return this.zzckY;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zza$f7bef55(parcel, 2, this.mBundle);
        zzd.zzI(parcel, zze);
    }
}
