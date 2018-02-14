package com.google.firebase;

import android.content.Context;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzbe;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.internal.zzby;
import com.google.android.gms.common.util.zzt;
import java.util.Arrays;

public final class FirebaseOptions {
    private final String zzaoM;
    private final String zzbVj;
    private final String zzbVk;
    private final String zzbVl;
    private final String zzbVm;
    private final String zzbVn;
    private final String zzbVo;

    private FirebaseOptions(String str, String str2, String str3, String str4, String str5, String str6, String str7) {
        zzbo.zza(!zzt.zzcL(str), "ApplicationId must be set.");
        this.zzaoM = str;
        this.zzbVj = str2;
        this.zzbVk = str3;
        this.zzbVl = str4;
        this.zzbVm = str5;
        this.zzbVn = str6;
        this.zzbVo = str7;
    }

    public static FirebaseOptions fromResource(Context context) {
        zzby zzby = new zzby(context);
        Object string = zzby.getString("google_app_id");
        return TextUtils.isEmpty(string) ? null : new FirebaseOptions(string, zzby.getString("google_api_key"), zzby.getString("firebase_database_url"), zzby.getString("ga_trackingId"), zzby.getString("gcm_defaultSenderId"), zzby.getString("google_storage_bucket"), zzby.getString("project_id"));
    }

    public final boolean equals(Object obj) {
        if (!(obj instanceof FirebaseOptions)) {
            return false;
        }
        FirebaseOptions firebaseOptions = (FirebaseOptions) obj;
        return zzbe.equal(this.zzaoM, firebaseOptions.zzaoM) && zzbe.equal(this.zzbVj, firebaseOptions.zzbVj) && zzbe.equal(this.zzbVk, firebaseOptions.zzbVk) && zzbe.equal(this.zzbVl, firebaseOptions.zzbVl) && zzbe.equal(this.zzbVm, firebaseOptions.zzbVm) && zzbe.equal(this.zzbVn, firebaseOptions.zzbVn) && zzbe.equal(this.zzbVo, firebaseOptions.zzbVo);
    }

    public final String getApplicationId() {
        return this.zzaoM;
    }

    public final String getGcmSenderId() {
        return this.zzbVm;
    }

    public final int hashCode() {
        return Arrays.hashCode(new Object[]{this.zzaoM, this.zzbVj, this.zzbVk, this.zzbVl, this.zzbVm, this.zzbVn, this.zzbVo});
    }

    public final String toString() {
        return zzbe.zzt(this).zzg("applicationId", this.zzaoM).zzg("apiKey", this.zzbVj).zzg("databaseUrl", this.zzbVk).zzg("gcmSenderId", this.zzbVm).zzg("storageBucket", this.zzbVn).zzg("projectId", this.zzbVo).toString();
    }
}
