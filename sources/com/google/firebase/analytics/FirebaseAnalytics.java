package com.google.firebase.analytics;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Keep;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.internal.zzcgl;

@Keep
public final class FirebaseAnalytics {
    private final zzcgl zzboe;

    public static class Event {
    }

    public static class Param {
    }

    public static class UserProperty {
    }

    public FirebaseAnalytics(zzcgl zzcgl) {
        zzbo.zzu(zzcgl);
        this.zzboe = zzcgl;
    }

    @Keep
    public static FirebaseAnalytics getInstance(Context context) {
        return zzcgl.zzbj(context).zzyT();
    }

    @Keep
    public final void setCurrentScreen(Activity activity, String str, String str2) {
        this.zzboe.zzwx().setCurrentScreen(activity, str, str2);
    }
}
