package io.fabric.sdk.android.services.settings;

import android.content.res.Resources.NotFoundException;
import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.Kit;
import io.fabric.sdk.android.KitInfo;
import io.fabric.sdk.android.services.common.AbstractSpiCall;
import io.fabric.sdk.android.services.common.CommonUtils;
import io.fabric.sdk.android.services.common.ResponseParser;
import io.fabric.sdk.android.services.network.HttpRequest;
import io.fabric.sdk.android.services.network.HttpRequestFactory;
import java.io.InputStream;
import java.util.Locale;

abstract class AbstractAppSpiCall extends AbstractSpiCall {
    public AbstractAppSpiCall(Kit kit, String protocolAndHostOverride, String url, HttpRequestFactory requestFactory, int method) {
        super(kit, protocolAndHostOverride, url, requestFactory, method);
    }

    public boolean invoke(AppRequestData requestData) {
        HttpRequest httpRequest = applyMultipartDataTo(getHttpRequest().header("X-CRASHLYTICS-API-KEY", requestData.apiKey).header("X-CRASHLYTICS-API-CLIENT-TYPE", "android").header("X-CRASHLYTICS-API-CLIENT-VERSION", this.kit.getVersion()), requestData);
        Fabric.getLogger().d("Fabric", "Sending app info to " + getUrl());
        if (requestData.icon != null) {
            Fabric.getLogger().d("Fabric", "App icon hash is " + requestData.icon.hash);
            Fabric.getLogger().d("Fabric", "App icon size is " + requestData.icon.width + "x" + requestData.icon.height);
        }
        int statusCode = httpRequest.code();
        Fabric.getLogger().d("Fabric", ("POST".equals(httpRequest.method()) ? "Create" : "Update") + " app request ID: " + httpRequest.header("X-REQUEST-ID"));
        Fabric.getLogger().d("Fabric", "Result was " + statusCode);
        if (ResponseParser.parse(statusCode) == 0) {
            return true;
        }
        return false;
    }

    private HttpRequest applyMultipartDataTo(HttpRequest request, AppRequestData requestData) {
        request = request.part("app[identifier]", null, requestData.appId).part("app[name]", null, requestData.name).part("app[display_version]", null, requestData.displayVersion).part("app[build_version]", null, requestData.buildVersion).part("app[source]", Integer.valueOf(requestData.source)).part("app[minimum_sdk_version]", null, requestData.minSdkVersion).part("app[built_sdk_version]", null, requestData.builtSdkVersion);
        if (!CommonUtils.isNullOrEmpty(requestData.instanceIdentifier)) {
            request.part("app[instance_identifier]", null, requestData.instanceIdentifier);
        }
        if (requestData.icon != null) {
            InputStream inputStream = null;
            try {
                inputStream = this.kit.getContext().getResources().openRawResource(requestData.icon.iconResourceId);
                request.part("app[icon][hash]", null, requestData.icon.hash).part("app[icon][data]", "icon.png", "application/octet-stream", inputStream).part("app[icon][width]", Integer.valueOf(requestData.icon.width)).part("app[icon][height]", Integer.valueOf(requestData.icon.height));
            } catch (NotFoundException e) {
                Fabric.getLogger().e("Fabric", "Failed to find app icon with resource ID: " + requestData.icon.iconResourceId, e);
            } finally {
                CommonUtils.closeOrLog(inputStream, "Failed to close app icon InputStream.");
            }
        }
        if (requestData.sdkKits != null) {
            for (KitInfo kit : requestData.sdkKits) {
                request.part(String.format(Locale.US, "app[build][libraries][%s][version]", new Object[]{kit.getIdentifier()}), null, kit.getVersion());
                request.part(String.format(Locale.US, "app[build][libraries][%s][type]", new Object[]{kit.getIdentifier()}), null, kit.getBuildType());
            }
        }
        return request;
    }
}
