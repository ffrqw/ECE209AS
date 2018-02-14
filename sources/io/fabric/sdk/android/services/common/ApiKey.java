package io.fabric.sdk.android.services.common;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import io.fabric.sdk.android.Fabric;

public final class ApiKey {
    public final String getValue(Context context) {
        String apiKey = getApiKeyFromManifest(context);
        if (TextUtils.isEmpty(apiKey)) {
            apiKey = null;
            int resourcesIdentifier = CommonUtils.getResourcesIdentifier(context, "io.fabric.ApiKey", "string");
            if (resourcesIdentifier == 0) {
                Fabric.getLogger().d("Fabric", "Falling back to Crashlytics key lookup from Strings");
                resourcesIdentifier = CommonUtils.getResourcesIdentifier(context, "com.crashlytics.ApiKey", "string");
            }
            if (resourcesIdentifier != 0) {
                apiKey = context.getResources().getString(resourcesIdentifier);
            }
        }
        if (TextUtils.isEmpty(apiKey)) {
            if (Fabric.isDebuggable() || CommonUtils.isAppDebuggable(context)) {
                throw new IllegalArgumentException("Fabric could not be initialized, API key missing from AndroidManifest.xml. Add the following tag to your Application element \n\t<meta-data android:name=\"io.fabric.ApiKey\" android:value=\"YOUR_API_KEY\"/>");
            }
            Fabric.getLogger().e("Fabric", "Fabric could not be initialized, API key missing from AndroidManifest.xml. Add the following tag to your Application element \n\t<meta-data android:name=\"io.fabric.ApiKey\" android:value=\"YOUR_API_KEY\"/>");
        }
        return apiKey;
    }

    private static String getApiKeyFromManifest(Context context) {
        String apiKey = null;
        try {
            Bundle bundle = context.getPackageManager().getApplicationInfo(context.getPackageName(), 128).metaData;
            if (bundle != null) {
                apiKey = bundle.getString("io.fabric.ApiKey");
                if (apiKey == null) {
                    Fabric.getLogger().d("Fabric", "Falling back to Crashlytics key lookup from Manifest");
                    apiKey = bundle.getString("com.crashlytics.ApiKey");
                }
            }
        } catch (Exception e) {
            Fabric.getLogger().d("Fabric", "Caught non-fatal exception while retrieving apiKey: " + e);
        }
        return apiKey;
    }
}
