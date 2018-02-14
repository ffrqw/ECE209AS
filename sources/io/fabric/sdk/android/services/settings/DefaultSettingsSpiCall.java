package io.fabric.sdk.android.services.settings;

import android.support.v7.widget.helper.ItemTouchHelper.Callback;
import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.Kit;
import io.fabric.sdk.android.services.common.AbstractSpiCall;
import io.fabric.sdk.android.services.common.CommonUtils;
import io.fabric.sdk.android.services.network.HttpMethod;
import io.fabric.sdk.android.services.network.HttpRequest;
import io.fabric.sdk.android.services.network.HttpRequest.HttpRequestException;
import io.fabric.sdk.android.services.network.HttpRequestFactory;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

final class DefaultSettingsSpiCall extends AbstractSpiCall implements SettingsSpiCall {
    public DefaultSettingsSpiCall(Kit kit, String protocolAndHostOverride, String url, HttpRequestFactory requestFactory) {
        this(kit, protocolAndHostOverride, url, requestFactory, HttpMethod.GET$6bc89afe);
    }

    private DefaultSettingsSpiCall(Kit kit, String protocolAndHostOverride, String url, HttpRequestFactory requestFactory, int method) {
        super(kit, protocolAndHostOverride, url, requestFactory, method);
    }

    public final JSONObject invoke(SettingsRequest requestData) {
        JSONObject toReturn;
        HttpRequest httpRequest = null;
        try {
            Object obj;
            Map<String, String> queryParams = new HashMap();
            queryParams.put("build_version", requestData.buildVersion);
            queryParams.put("display_version", requestData.displayVersion);
            queryParams.put("source", Integer.toString(requestData.source));
            if (requestData.iconHash != null) {
                queryParams.put("icon_hash", requestData.iconHash);
            }
            String str = requestData.instanceId;
            if (!CommonUtils.isNullOrEmpty(str)) {
                queryParams.put("instance", str);
            }
            httpRequest = getHttpRequest(queryParams);
            applyNonNullHeader(httpRequest, "X-CRASHLYTICS-API-KEY", requestData.apiKey);
            applyNonNullHeader(httpRequest, "X-CRASHLYTICS-API-CLIENT-TYPE", "android");
            applyNonNullHeader(httpRequest, "X-CRASHLYTICS-API-CLIENT-VERSION", this.kit.getVersion());
            applyNonNullHeader(httpRequest, "Accept", "application/json");
            applyNonNullHeader(httpRequest, "X-CRASHLYTICS-DEVICE-MODEL", requestData.deviceModel);
            applyNonNullHeader(httpRequest, "X-CRASHLYTICS-OS-BUILD-VERSION", requestData.osBuildVersion);
            applyNonNullHeader(httpRequest, "X-CRASHLYTICS-OS-DISPLAY-VERSION", requestData.osDisplayVersion);
            applyNonNullHeader(httpRequest, "X-CRASHLYTICS-ADVERTISING-TOKEN", requestData.advertisingId);
            applyNonNullHeader(httpRequest, "X-CRASHLYTICS-INSTALLATION-ID", requestData.installationId);
            applyNonNullHeader(httpRequest, "X-CRASHLYTICS-ANDROID-ID", requestData.androidId);
            Fabric.getLogger().d("Fabric", "Requesting settings from " + getUrl());
            Fabric.getLogger().d("Fabric", "Settings query params were: " + queryParams);
            int code = httpRequest.code();
            Fabric.getLogger().d("Fabric", "Settings result was: " + code);
            if (code == Callback.DEFAULT_DRAG_ANIMATION_DURATION || code == 201 || code == 202 || code == 203) {
                obj = 1;
            } else {
                obj = null;
            }
            if (obj != null) {
                toReturn = getJsonObjectFrom(httpRequest.body());
            } else {
                Fabric.getLogger().e("Fabric", "Failed to retrieve settings from " + getUrl());
                toReturn = null;
            }
            if (httpRequest != null) {
                Fabric.getLogger().d("Fabric", "Settings request ID: " + httpRequest.header("X-REQUEST-ID"));
            }
        } catch (HttpRequestException e) {
            Fabric.getLogger().e("Fabric", "Settings request failed.", e);
            toReturn = null;
            if (httpRequest != null) {
                Fabric.getLogger().d("Fabric", "Settings request ID: " + httpRequest.header("X-REQUEST-ID"));
            }
        } catch (Throwable th) {
            if (httpRequest != null) {
                Fabric.getLogger().d("Fabric", "Settings request ID: " + httpRequest.header("X-REQUEST-ID"));
            }
        }
        return toReturn;
    }

    private JSONObject getJsonObjectFrom(String httpRequestBody) {
        try {
            return new JSONObject(httpRequestBody);
        } catch (Exception e) {
            Fabric.getLogger().d("Fabric", "Failed to parse settings JSON from " + getUrl(), e);
            Fabric.getLogger().d("Fabric", "Settings response " + httpRequestBody);
            return null;
        }
    }

    private static void applyNonNullHeader(HttpRequest request, String key, String value) {
        if (value != null) {
            request.header(key, value);
        }
    }
}
