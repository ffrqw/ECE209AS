package com.crashlytics.android.answers;

import android.content.Context;
import io.fabric.sdk.android.services.common.CommonUtils;
import io.fabric.sdk.android.services.common.IdManager;
import io.fabric.sdk.android.services.common.IdManager.DeviceIdentifierType;
import java.util.Map;
import java.util.UUID;

final class SessionMetadataCollector {
    private final Context context;
    private final IdManager idManager;
    private final String versionCode;
    private final String versionName;

    public SessionMetadataCollector(Context context, IdManager idManager, String versionCode, String versionName) {
        this.context = context;
        this.idManager = idManager;
        this.versionCode = versionCode;
        this.versionName = versionName;
    }

    public final SessionEventMetadata getMetadata() {
        Map<DeviceIdentifierType, String> deviceIdentifiers = this.idManager.getDeviceIdentifiers();
        String appBundleId = this.idManager.getAppIdentifier();
        String installationId = this.idManager.getAppInstallIdentifier();
        String androidId = (String) deviceIdentifiers.get(DeviceIdentifierType.ANDROID_ID);
        String advertisingId = (String) deviceIdentifiers.get(DeviceIdentifierType.ANDROID_ADVERTISING_ID);
        Boolean limitAdTrackingEnabled = this.idManager.isLimitAdTrackingEnabled();
        String betaDeviceToken = (String) deviceIdentifiers.get(DeviceIdentifierType.FONT_TOKEN);
        String buildId = CommonUtils.resolveBuildId(this.context);
        IdManager idManager = this.idManager;
        return new SessionEventMetadata(appBundleId, UUID.randomUUID().toString(), installationId, androidId, advertisingId, limitAdTrackingEnabled, betaDeviceToken, buildId, idManager.getOsDisplayVersionString() + "/" + idManager.getOsBuildVersionString(), this.idManager.getModelName(), this.versionCode, this.versionName);
    }
}
