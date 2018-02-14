package io.fabric.sdk.android;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import io.fabric.sdk.android.services.common.ApiKey;
import io.fabric.sdk.android.services.common.CommonUtils;
import io.fabric.sdk.android.services.common.DeliveryMechanism;
import io.fabric.sdk.android.services.network.DefaultHttpRequestFactory;
import io.fabric.sdk.android.services.network.HttpRequestFactory;
import io.fabric.sdk.android.services.settings.AppRequestData;
import io.fabric.sdk.android.services.settings.AppSettingsData;
import io.fabric.sdk.android.services.settings.CreateAppSpiCall;
import io.fabric.sdk.android.services.settings.IconRequest;
import io.fabric.sdk.android.services.settings.Settings;
import io.fabric.sdk.android.services.settings.SettingsData;
import io.fabric.sdk.android.services.settings.UpdateAppSpiCall;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

final class Onboarding extends Kit<Boolean> {
    private String applicationLabel;
    private String installerPackageName;
    private final Future<Map<String, KitInfo>> kitsFinder;
    private PackageInfo packageInfo;
    private PackageManager packageManager;
    private String packageName;
    private final Collection<Kit> providedKits;
    private final HttpRequestFactory requestFactory = new DefaultHttpRequestFactory();
    private String targetAndroidSdkVersion;
    private String versionCode;
    private String versionName;

    public Onboarding(Future<Map<String, KitInfo>> kitsFinder, Collection<Kit> providedKits) {
        this.kitsFinder = kitsFinder;
        this.providedKits = providedKits;
    }

    public final String getVersion() {
        return "1.3.17.dev";
    }

    protected final boolean onPreExecute() {
        try {
            this.installerPackageName = this.idManager.getInstallerPackageName();
            this.packageManager = this.context.getPackageManager();
            this.packageName = this.context.getPackageName();
            this.packageInfo = this.packageManager.getPackageInfo(this.packageName, 0);
            this.versionCode = Integer.toString(this.packageInfo.versionCode);
            this.versionName = this.packageInfo.versionName == null ? "0.0" : this.packageInfo.versionName;
            this.applicationLabel = this.packageManager.getApplicationLabel(this.context.getApplicationInfo()).toString();
            this.targetAndroidSdkVersion = Integer.toString(this.context.getApplicationInfo().targetSdkVersion);
            return true;
        } catch (NameNotFoundException e) {
            Fabric.getLogger().e("Fabric", "Failed init", e);
            return false;
        }
    }

    private SettingsData retrieveSettingsData() {
        try {
            Settings.getInstance().initialize(this, this.idManager, this.requestFactory, this.versionCode, this.versionName, getOverridenSpiEndpoint()).loadSettingsData();
            return Settings.getInstance().awaitSettingsData();
        } catch (Exception e) {
            Fabric.getLogger().e("Fabric", "Error dealing with settings", e);
            return null;
        }
    }

    private static Map<String, KitInfo> mergeKits(Map<String, KitInfo> scannedKits, Collection<Kit> providedKits) {
        for (Kit kit : providedKits) {
            if (!scannedKits.containsKey(kit.getIdentifier())) {
                scannedKits.put(kit.getIdentifier(), new KitInfo(kit.getIdentifier(), kit.getVersion(), "binary"));
            }
        }
        return scannedKits;
    }

    public final String getIdentifier() {
        return "io.fabric.sdk.android:fabric";
    }

    private Boolean doInBackground() {
        String iconHash = CommonUtils.getAppIconHashOrNull(this.context);
        boolean appConfigured = false;
        SettingsData settingsData = retrieveSettingsData();
        if (settingsData != null) {
            try {
                Map<String, KitInfo> scannedKits;
                if (this.kitsFinder != null) {
                    scannedKits = (Map) this.kitsFinder.get();
                } else {
                    scannedKits = new HashMap();
                }
                Map<String, KitInfo> mergedKits = mergeKits(scannedKits, this.providedKits);
                AppSettingsData appSettingsData = settingsData.appData;
                Collection values = mergedKits.values();
                if ("new".equals(appSettingsData.status)) {
                    if (new CreateAppSpiCall(this, getOverridenSpiEndpoint(), appSettingsData.url, this.requestFactory).invoke(buildAppRequest(IconRequest.build(this.context, iconHash), values))) {
                        appConfigured = Settings.getInstance().loadSettingsSkippingCache();
                    } else {
                        Fabric.getLogger().e("Fabric", "Failed to create app with Crashlytics service.", null);
                        appConfigured = false;
                    }
                } else if ("configured".equals(appSettingsData.status)) {
                    appConfigured = Settings.getInstance().loadSettingsSkippingCache();
                } else {
                    if (appSettingsData.updateRequired) {
                        Fabric.getLogger().d("Fabric", "Server says an update is required - forcing a full App update.");
                        new UpdateAppSpiCall(this, getOverridenSpiEndpoint(), appSettingsData.url, this.requestFactory).invoke(buildAppRequest(IconRequest.build(this.context, iconHash), values));
                    }
                    appConfigured = true;
                }
            } catch (Exception e) {
                Fabric.getLogger().e("Fabric", "Error performing auto configuration.", e);
            }
        }
        return Boolean.valueOf(appConfigured);
    }

    private AppRequestData buildAppRequest(IconRequest iconRequest, Collection<KitInfo> sdkKits) {
        return new AppRequestData(new ApiKey().getValue(this.context), this.idManager.getAppIdentifier(), this.versionName, this.versionCode, CommonUtils.createInstanceIdFrom(CommonUtils.resolveBuildId(context)), this.applicationLabel, DeliveryMechanism.determineFrom(this.installerPackageName).getId(), this.targetAndroidSdkVersion, "0", iconRequest, sdkKits);
    }

    private String getOverridenSpiEndpoint() {
        return CommonUtils.getStringsFileValue(this.context, "com.crashlytics.ApiEndpoint");
    }
}
