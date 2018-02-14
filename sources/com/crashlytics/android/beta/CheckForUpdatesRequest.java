package com.crashlytics.android.beta;

import android.support.v7.widget.helper.ItemTouchHelper.Callback;
import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.Kit;
import io.fabric.sdk.android.services.common.AbstractSpiCall;
import io.fabric.sdk.android.services.network.HttpMethod;
import io.fabric.sdk.android.services.network.HttpRequest;
import io.fabric.sdk.android.services.network.HttpRequestFactory;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

final class CheckForUpdatesRequest extends AbstractSpiCall {
    private final CheckForUpdatesResponseTransform responseTransform;

    public CheckForUpdatesRequest(Kit kit, String protocolAndHostOverride, String url, HttpRequestFactory requestFactory, CheckForUpdatesResponseTransform responseTransform) {
        super(kit, protocolAndHostOverride, url, requestFactory, HttpMethod.GET$6bc89afe);
        this.responseTransform = responseTransform;
    }

    public final CheckForUpdatesResponse invoke(String apiKey, String betaDeviceToken, BuildProperties buildProps) {
        HttpRequest httpRequest = null;
        try {
            Object obj;
            Map<String, String> queryParams = new HashMap();
            queryParams.put("build_version", buildProps.versionCode);
            queryParams.put("display_version", buildProps.versionName);
            queryParams.put("instance", buildProps.buildId);
            queryParams.put("source", "3");
            httpRequest = getHttpRequest(queryParams).header("Accept", "application/json").header("User-Agent", "Crashlytics Android SDK/" + this.kit.getVersion()).header("X-CRASHLYTICS-DEVELOPER-TOKEN", "470fa2b4ae81cd56ecbcda9735803434cec591fa").header("X-CRASHLYTICS-API-CLIENT-TYPE", "android").header("X-CRASHLYTICS-API-CLIENT-VERSION", this.kit.getVersion()).header("X-CRASHLYTICS-API-KEY", apiKey).header("X-CRASHLYTICS-BETA-TOKEN", "3:" + betaDeviceToken);
            Fabric.getLogger().d("Beta", "Checking for updates from " + getUrl());
            Fabric.getLogger().d("Beta", "Checking for updates query params are: " + queryParams);
            if (Callback.DEFAULT_DRAG_ANIMATION_DURATION == httpRequest.code()) {
                obj = 1;
            } else {
                obj = null;
            }
            if (obj != null) {
                Fabric.getLogger().d("Beta", "Checking for updates was successful");
                JSONObject responseJson = new JSONObject(httpRequest.body());
                CheckForUpdatesResponse checkForUpdatesResponse = new CheckForUpdatesResponse(responseJson.optString("url", null), responseJson.optString("version_string", null), responseJson.optString("display_version", null), responseJson.optString("build_version", null), responseJson.optString("identifier", null), responseJson.optString("instance_identifier", null));
                if (httpRequest == null) {
                    return checkForUpdatesResponse;
                }
                Fabric.getLogger().d("Fabric", "Checking for updates request ID: " + httpRequest.header("X-REQUEST-ID"));
                return checkForUpdatesResponse;
            }
            Fabric.getLogger().e("Beta", "Checking for updates failed. Response code: " + httpRequest.code());
            if (httpRequest != null) {
                Fabric.getLogger().d("Fabric", "Checking for updates request ID: " + httpRequest.header("X-REQUEST-ID"));
            }
            return null;
        } catch (Exception e) {
            Fabric.getLogger().e("Beta", "Error while checking for updates from " + getUrl(), e);
            if (httpRequest != null) {
                Fabric.getLogger().d("Fabric", "Checking for updates request ID: " + httpRequest.header("X-REQUEST-ID"));
            }
        } catch (Throwable th) {
            if (httpRequest != null) {
                Fabric.getLogger().d("Fabric", "Checking for updates request ID: " + httpRequest.header("X-REQUEST-ID"));
            }
        }
    }
}
