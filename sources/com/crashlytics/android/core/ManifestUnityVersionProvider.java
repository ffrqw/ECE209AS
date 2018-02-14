package com.crashlytics.android.core;

import android.content.Context;
import android.os.Bundle;

final class ManifestUnityVersionProvider implements UnityVersionProvider {
    private final Context context;
    private final String packageName;

    public ManifestUnityVersionProvider(Context context, String packageName) {
        this.context = context;
        this.packageName = packageName;
    }

    public final String getUnityVersion() {
        String unityVersion = null;
        try {
            Bundle metaData = this.context.getPackageManager().getApplicationInfo(this.packageName, 128).metaData;
            if (metaData != null) {
                unityVersion = metaData.getString("io.fabric.unity.crashlytics.version");
            }
        } catch (Exception e) {
        }
        return unityVersion;
    }
}
