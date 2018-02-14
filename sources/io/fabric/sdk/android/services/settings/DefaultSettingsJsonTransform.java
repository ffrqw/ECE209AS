package io.fabric.sdk.android.services.settings;

import io.fabric.sdk.android.services.common.CurrentTimeProvider;
import org.json.JSONException;
import org.json.JSONObject;

final class DefaultSettingsJsonTransform implements SettingsJsonTransform {
    DefaultSettingsJsonTransform() {
    }

    public final SettingsData buildFromJson(CurrentTimeProvider currentTimeProvider, JSONObject json) throws JSONException {
        long expiresAtMillis;
        int settingsVersion = json.optInt("settings_version", 0);
        int cacheDuration = json.optInt("cache_duration", 3600);
        JSONObject jSONObject = json.getJSONObject("app");
        String string = jSONObject.getString("identifier");
        String string2 = jSONObject.getString("status");
        String string3 = jSONObject.getString("url");
        String string4 = jSONObject.getString("reports_url");
        boolean optBoolean = jSONObject.optBoolean("update_required", false);
        AppIconSettingsData appIconSettingsData = null;
        if (jSONObject.has("icon") && jSONObject.getJSONObject("icon").has("hash")) {
            jSONObject = jSONObject.getJSONObject("icon");
            appIconSettingsData = new AppIconSettingsData(jSONObject.getString("hash"), jSONObject.getInt("width"), jSONObject.getInt("height"));
        }
        AppSettingsData appData = new AppSettingsData(string, string2, string3, string4, optBoolean, appIconSettingsData);
        jSONObject = json.getJSONObject("session");
        SessionSettingsData settingsData = new SessionSettingsData(jSONObject.optInt("log_buffer_size", 64000), jSONObject.optInt("max_chained_exception_depth", 8), jSONObject.optInt("max_custom_exception_events", 64), jSONObject.optInt("max_custom_key_value_pairs", 64), jSONObject.optInt("identifier_mask", 255), jSONObject.optBoolean("send_session_without_crash", false), jSONObject.optInt("max_complete_sessions_count", 4));
        JSONObject jSONObject2 = json.getJSONObject("prompt");
        PromptSettingsData promptData = new PromptSettingsData(jSONObject2.optString("title", "Send Crash Report?"), jSONObject2.optString("message", "Looks like we crashed! Please help us fix the problem by sending a crash report."), jSONObject2.optString("send_button_title", "Send"), jSONObject2.optBoolean("show_cancel_button", true), jSONObject2.optString("cancel_button_title", "Don't Send"), jSONObject2.optBoolean("show_always_send_button", true), jSONObject2.optString("always_send_button_title", "Always Send"));
        JSONObject jSONObject3 = json.getJSONObject("features");
        FeaturesSettingsData featuresSettingsData = new FeaturesSettingsData(jSONObject3.optBoolean("prompt_enabled", false), jSONObject3.optBoolean("collect_logged_exceptions", true), jSONObject3.optBoolean("collect_reports", true), jSONObject3.optBoolean("collect_analytics", false));
        JSONObject jSONObject4 = json.getJSONObject("analytics");
        AnalyticsSettingsData analyticsData = new AnalyticsSettingsData(jSONObject4.optString("url", "https://e.crashlytics.com/spi/v2/events"), jSONObject4.optInt("flush_interval_secs", 600), jSONObject4.optInt("max_byte_size_per_file", 8000), jSONObject4.optInt("max_file_count_per_send", 1), jSONObject4.optInt("max_pending_send_file_count", 100), jSONObject4.optBoolean("track_custom_events", true), jSONObject4.optBoolean("track_predefined_events", true), jSONObject4.optInt("sampling_rate", 1), jSONObject4.optBoolean("flush_on_background", true));
        jSONObject3 = json.getJSONObject("beta");
        BetaSettingsData betaData = new BetaSettingsData(jSONObject3.optString("update_endpoint", null), jSONObject3.optInt("update_suspend_duration", 3600));
        long j = (long) cacheDuration;
        if (json.has("expires_at")) {
            expiresAtMillis = json.getLong("expires_at");
        } else {
            expiresAtMillis = currentTimeProvider.getCurrentTimeMillis() + (j * 1000);
        }
        return new SettingsData(expiresAtMillis, appData, settingsData, promptData, featuresSettingsData, analyticsData, betaData, settingsVersion, cacheDuration);
    }
}
