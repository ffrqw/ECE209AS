package io.fabric.sdk.android.services.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.services.persistence.PreferenceStore;
import io.fabric.sdk.android.services.persistence.PreferenceStoreImpl;

final class AdvertisingInfoProvider {
    private final Context context;
    private final PreferenceStore preferenceStore;

    public AdvertisingInfoProvider(Context context) {
        this.context = context.getApplicationContext();
        this.preferenceStore = new PreferenceStoreImpl(context, "TwitterAdvertisingInfoPreferences");
    }

    @SuppressLint({"CommitPrefEdits"})
    private void storeInfoToPreferences(AdvertisingInfo infoToReturn) {
        if (isInfoValid(infoToReturn)) {
            this.preferenceStore.save(this.preferenceStore.edit().putString("advertising_id", infoToReturn.advertisingId).putBoolean("limit_ad_tracking_enabled", infoToReturn.limitAdTrackingEnabled));
        } else {
            this.preferenceStore.save(this.preferenceStore.edit().remove("advertising_id").remove("limit_ad_tracking_enabled"));
        }
    }

    private static boolean isInfoValid(AdvertisingInfo advertisingInfo) {
        return (advertisingInfo == null || TextUtils.isEmpty(advertisingInfo.advertisingId)) ? false : true;
    }

    public final AdvertisingInfo getAdvertisingInfo() {
        final AdvertisingInfo infoToReturn = new AdvertisingInfo(this.preferenceStore.get().getString("advertising_id", ""), this.preferenceStore.get().getBoolean("limit_ad_tracking_enabled", false));
        if (isInfoValid(infoToReturn)) {
            Fabric.getLogger().d("Fabric", "Using AdvertisingInfo from Preference Store");
            new Thread(new BackgroundPriorityRunnable() {
                public final void onRun() {
                    AdvertisingInfo infoToStore = AdvertisingInfoProvider.this.getAdvertisingInfoFromStrategies();
                    if (!infoToReturn.equals(infoToStore)) {
                        Fabric.getLogger().d("Fabric", "Asychronously getting Advertising Info and storing it to preferences");
                        AdvertisingInfoProvider.this.storeInfoToPreferences(infoToStore);
                    }
                }
            }).start();
            return infoToReturn;
        }
        infoToReturn = getAdvertisingInfoFromStrategies();
        storeInfoToPreferences(infoToReturn);
        return infoToReturn;
    }

    private AdvertisingInfo getAdvertisingInfoFromStrategies() {
        AdvertisingInfo infoToReturn = new AdvertisingInfoReflectionStrategy(this.context).getAdvertisingInfo();
        if (isInfoValid(infoToReturn)) {
            Fabric.getLogger().d("Fabric", "Using AdvertisingInfo from Reflection Provider");
        } else {
            infoToReturn = new AdvertisingInfoServiceStrategy(this.context).getAdvertisingInfo();
            if (isInfoValid(infoToReturn)) {
                Fabric.getLogger().d("Fabric", "Using AdvertisingInfo from Service Provider");
            } else {
                Fabric.getLogger().d("Fabric", "AdvertisingInfo not present");
            }
        }
        return infoToReturn;
    }
}
