package com.crashlytics.android.answers;

import android.annotation.TargetApi;
import android.os.Build.VERSION;
import io.fabric.sdk.android.services.events.EventTransform;
import java.io.IOException;
import org.json.JSONException;
import org.json.JSONObject;

final class SessionEventTransform implements EventTransform<SessionEvent> {
    SessionEventTransform() {
    }

    public final /* bridge */ /* synthetic */ byte[] toBytes(Object obj) throws IOException {
        return buildJsonForEvent((SessionEvent) obj).toString().getBytes("UTF-8");
    }

    @TargetApi(9)
    private static JSONObject buildJsonForEvent(SessionEvent event) throws IOException {
        try {
            JSONObject jsonObject = new JSONObject();
            SessionEventMetadata eventMetadata = event.sessionEventMetadata;
            jsonObject.put("appBundleId", eventMetadata.appBundleId);
            jsonObject.put("executionId", eventMetadata.executionId);
            jsonObject.put("installationId", eventMetadata.installationId);
            jsonObject.put("androidId", eventMetadata.androidId);
            jsonObject.put("advertisingId", eventMetadata.advertisingId);
            jsonObject.put("limitAdTrackingEnabled", eventMetadata.limitAdTrackingEnabled);
            jsonObject.put("betaDeviceToken", eventMetadata.betaDeviceToken);
            jsonObject.put("buildId", eventMetadata.buildId);
            jsonObject.put("osVersion", eventMetadata.osVersion);
            jsonObject.put("deviceModel", eventMetadata.deviceModel);
            jsonObject.put("appVersionCode", eventMetadata.appVersionCode);
            jsonObject.put("appVersionName", eventMetadata.appVersionName);
            jsonObject.put("timestamp", event.timestamp);
            jsonObject.put("type", event.type.toString());
            if (event.details != null) {
                jsonObject.put("details", new JSONObject(event.details));
            }
            jsonObject.put("customType", event.customType);
            if (event.customAttributes != null) {
                jsonObject.put("customAttributes", new JSONObject(event.customAttributes));
            }
            jsonObject.put("predefinedType", event.predefinedType);
            if (event.predefinedAttributes != null) {
                jsonObject.put("predefinedAttributes", new JSONObject(event.predefinedAttributes));
            }
            return jsonObject;
        } catch (JSONException e) {
            if (VERSION.SDK_INT >= 9) {
                throw new IOException(e.getMessage(), e);
            }
            throw new IOException(e.getMessage());
        }
    }
}
