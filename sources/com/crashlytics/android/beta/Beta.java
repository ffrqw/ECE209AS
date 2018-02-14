package com.crashlytics.android.beta;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build.VERSION;
import android.text.TextUtils;
import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.Kit;
import io.fabric.sdk.android.services.cache.MemoryValueCache;
import io.fabric.sdk.android.services.common.DeviceIdentifierProvider;
import io.fabric.sdk.android.services.common.IdManager;
import io.fabric.sdk.android.services.common.IdManager.DeviceIdentifierType;
import io.fabric.sdk.android.services.common.SystemCurrentTimeProvider;
import io.fabric.sdk.android.services.network.DefaultHttpRequestFactory;
import io.fabric.sdk.android.services.persistence.PreferenceStoreImpl;
import io.fabric.sdk.android.services.settings.BetaSettingsData;
import io.fabric.sdk.android.services.settings.Settings;
import io.fabric.sdk.android.services.settings.SettingsData;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public final class Beta extends Kit<Boolean> implements DeviceIdentifierProvider {
    private final MemoryValueCache<String> deviceTokenCache = new MemoryValueCache();
    private final DeviceTokenLoader deviceTokenLoader = new DeviceTokenLoader();
    private UpdatesController updatesController;

    protected final /* bridge */ /* synthetic */ Object doInBackground() {
        boolean z = false;
        Fabric.getLogger().d("Beta", "Beta kit initializing...");
        Context context = getContext();
        IdManager idManager = getIdManager();
        idManager.getInstallerPackageName();
        if (TextUtils.isEmpty(getBetaDeviceToken$5b1592bd(context))) {
            Fabric.getLogger().d("Beta", "A Beta device token was not found for this app");
            return Boolean.valueOf(false);
        }
        BetaSettingsData betaSettingsData;
        Fabric.getLogger().d("Beta", "Beta device token is present, checking for app updates.");
        SettingsData awaitSettingsData = Settings.getInstance().awaitSettingsData();
        if (awaitSettingsData != null) {
            betaSettingsData = awaitSettingsData.betaSettingsData;
        } else {
            betaSettingsData = null;
        }
        BuildProperties loadBuildProperties = loadBuildProperties(context);
        if (!(betaSettingsData == null || TextUtils.isEmpty(betaSettingsData.updateUrl) || loadBuildProperties == null)) {
            z = true;
        }
        if (z) {
            this.updatesController.initialize(context, this, idManager, betaSettingsData, loadBuildProperties, new PreferenceStoreImpl(this), new SystemCurrentTimeProvider(), new DefaultHttpRequestFactory(Fabric.getLogger()));
        }
        return Boolean.valueOf(true);
    }

    @TargetApi(14)
    protected final boolean onPreExecute() {
        UpdatesController activityLifecycleCheckForUpdatesController;
        getContext().getApplicationContext();
        if (VERSION.SDK_INT >= 14) {
            activityLifecycleCheckForUpdatesController = new ActivityLifecycleCheckForUpdatesController(getFabric().getActivityLifecycleManager(), getFabric().getExecutorService());
        } else {
            activityLifecycleCheckForUpdatesController = new ImmediateCheckForUpdatesController();
        }
        this.updatesController = activityLifecycleCheckForUpdatesController;
        return true;
    }

    public final Map<DeviceIdentifierType, String> getDeviceIdentifiers() {
        getIdManager().getInstallerPackageName();
        String betaDeviceToken = getBetaDeviceToken$5b1592bd(getContext());
        Map<DeviceIdentifierType, String> ids = new HashMap();
        if (!TextUtils.isEmpty(betaDeviceToken)) {
            ids.put(DeviceIdentifierType.FONT_TOKEN, betaDeviceToken);
        }
        return ids;
    }

    public final String getIdentifier() {
        return "com.crashlytics.sdk.android:beta";
    }

    public final String getVersion() {
        return "1.2.5.dev";
    }

    private String getBetaDeviceToken$5b1592bd(Context context) {
        String token = null;
        try {
            String cachedToken = (String) this.deviceTokenCache.get(context, this.deviceTokenLoader);
            if ("".equals(cachedToken)) {
                token = null;
            } else {
                token = cachedToken;
            }
        } catch (Exception e) {
            Fabric.getLogger().e("Beta", "Failed to load the Beta device token", e);
        }
        Fabric.getLogger().d("Beta", "Beta device token present: " + (!TextUtils.isEmpty(token)));
        return token;
    }

    private static BuildProperties loadBuildProperties(Context context) {
        Exception e;
        Throwable th;
        InputStream buildPropsStream = null;
        BuildProperties buildProperties = null;
        try {
            buildPropsStream = context.getAssets().open("crashlytics-build.properties");
            if (buildPropsStream != null) {
                Properties properties = new Properties();
                properties.load(buildPropsStream);
                BuildProperties buildProps = new BuildProperties(properties.getProperty("version_code"), properties.getProperty("version_name"), properties.getProperty("build_id"), properties.getProperty("package_name"));
                try {
                    Fabric.getLogger().d("Beta", buildProps.packageName + " build properties: " + buildProps.versionName + " (" + buildProps.versionCode + ") - " + buildProps.buildId);
                    buildProperties = buildProps;
                } catch (Exception e2) {
                    e = e2;
                    buildProperties = buildProps;
                    try {
                        Fabric.getLogger().e("Beta", "Error reading Beta build properties", e);
                        if (buildPropsStream != null) {
                            try {
                                buildPropsStream.close();
                            } catch (IOException e3) {
                                Fabric.getLogger().e("Beta", "Error closing Beta build properties asset", e3);
                            }
                        }
                        return buildProperties;
                    } catch (Throwable th2) {
                        th = th2;
                        if (buildPropsStream != null) {
                            try {
                                buildPropsStream.close();
                            } catch (IOException e32) {
                                Fabric.getLogger().e("Beta", "Error closing Beta build properties asset", e32);
                            }
                        }
                        throw th;
                    }
                } catch (Throwable th3) {
                    th = th3;
                    buildProperties = buildProps;
                    if (buildPropsStream != null) {
                        buildPropsStream.close();
                    }
                    throw th;
                }
            }
            if (buildPropsStream != null) {
                try {
                    buildPropsStream.close();
                } catch (IOException e322) {
                    Fabric.getLogger().e("Beta", "Error closing Beta build properties asset", e322);
                }
            }
        } catch (Exception e4) {
            e = e4;
            Fabric.getLogger().e("Beta", "Error reading Beta build properties", e);
            if (buildPropsStream != null) {
                buildPropsStream.close();
            }
            return buildProperties;
        }
        return buildProperties;
    }
}
