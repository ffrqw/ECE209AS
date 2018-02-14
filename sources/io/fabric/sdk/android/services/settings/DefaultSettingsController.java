package io.fabric.sdk.android.services.settings;

import android.content.SharedPreferences.Editor;
import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.Kit;
import io.fabric.sdk.android.services.common.CommonUtils;
import io.fabric.sdk.android.services.common.CurrentTimeProvider;
import io.fabric.sdk.android.services.persistence.PreferenceStore;
import io.fabric.sdk.android.services.persistence.PreferenceStoreImpl;
import org.json.JSONException;
import org.json.JSONObject;

final class DefaultSettingsController implements SettingsController {
    private final CachedSettingsIo cachedSettingsIo;
    private final CurrentTimeProvider currentTimeProvider;
    private final Kit kit;
    private final PreferenceStore preferenceStore = new PreferenceStoreImpl(this.kit);
    private final SettingsJsonTransform settingsJsonTransform;
    private final SettingsRequest settingsRequest;
    private final SettingsSpiCall settingsSpiCall;

    public DefaultSettingsController(Kit kit, SettingsRequest settingsRequest, CurrentTimeProvider currentTimeProvider, SettingsJsonTransform settingsJsonTransform, CachedSettingsIo cachedSettingsIo, SettingsSpiCall settingsSpiCall) {
        this.kit = kit;
        this.settingsRequest = settingsRequest;
        this.currentTimeProvider = currentTimeProvider;
        this.settingsJsonTransform = settingsJsonTransform;
        this.cachedSettingsIo = cachedSettingsIo;
        this.settingsSpiCall = settingsSpiCall;
    }

    public final SettingsData loadSettingsData() {
        return loadSettingsData(SettingsCacheBehavior.USE_CACHE);
    }

    public final SettingsData loadSettingsData(SettingsCacheBehavior cacheBehavior) {
        SettingsData toReturn = null;
        try {
            if (!Fabric.isDebuggable()) {
                Object obj;
                if (this.preferenceStore.get().getString("existing_instance_identifier", "").equals(getBuildInstanceIdentifierFromContext())) {
                    obj = null;
                } else {
                    obj = 1;
                }
                if (obj == null) {
                    toReturn = getCachedSettingsData(cacheBehavior);
                }
            }
            if (toReturn == null) {
                JSONObject settingsJson = this.settingsSpiCall.invoke(this.settingsRequest);
                if (settingsJson != null) {
                    toReturn = this.settingsJsonTransform.buildFromJson(this.currentTimeProvider, settingsJson);
                    this.cachedSettingsIo.writeCachedSettings(toReturn.expiresAtMillis, settingsJson);
                    logSettings(settingsJson, "Loaded settings: ");
                    String buildInstanceIdentifierFromContext = getBuildInstanceIdentifierFromContext();
                    Editor edit = this.preferenceStore.edit();
                    edit.putString("existing_instance_identifier", buildInstanceIdentifierFromContext);
                    this.preferenceStore.save(edit);
                }
            }
            if (toReturn == null) {
                toReturn = getCachedSettingsData(SettingsCacheBehavior.IGNORE_CACHE_EXPIRATION);
            }
        } catch (Exception e) {
            Fabric.getLogger().e("Fabric", "Unknown error while loading Crashlytics settings. Crashes will be cached until settings can be retrieved.", e);
        }
        return toReturn;
    }

    private SettingsData getCachedSettingsData(SettingsCacheBehavior cacheBehavior) {
        try {
            if (SettingsCacheBehavior.SKIP_CACHE_LOOKUP.equals(cacheBehavior)) {
                return null;
            }
            JSONObject settingsJson = this.cachedSettingsIo.readCachedSettings();
            if (settingsJson != null) {
                SettingsData settingsData = this.settingsJsonTransform.buildFromJson(this.currentTimeProvider, settingsJson);
                logSettings(settingsJson, "Loaded cached settings: ");
                long currentTimeMillis = this.currentTimeProvider.getCurrentTimeMillis();
                if (!SettingsCacheBehavior.IGNORE_CACHE_EXPIRATION.equals(cacheBehavior)) {
                    Object obj;
                    if (settingsData.expiresAtMillis < currentTimeMillis) {
                        obj = 1;
                    } else {
                        obj = null;
                    }
                    if (obj != null) {
                        Fabric.getLogger().d("Fabric", "Cached settings have expired.");
                        return null;
                    }
                }
                SettingsData toReturn = settingsData;
                Fabric.getLogger().d("Fabric", "Returning cached settings.");
                return toReturn;
            }
            Fabric.getLogger().d("Fabric", "No cached settings data found.");
            return null;
        } catch (Exception e) {
            Fabric.getLogger().e("Fabric", "Failed to get cached settings", e);
            return null;
        }
    }

    private static void logSettings(JSONObject json, String message) throws JSONException {
        Fabric.getLogger().d("Fabric", message + json.toString());
    }

    private String getBuildInstanceIdentifierFromContext() {
        return CommonUtils.createInstanceIdFrom(CommonUtils.resolveBuildId(this.kit.getContext()));
    }
}
