package com.crashlytics.android.core;

import android.content.Context;
import io.fabric.sdk.android.services.common.CommonUtils;
import io.fabric.sdk.android.services.settings.PromptSettingsData;

final class DialogStringResolver {
    private final Context context;
    private final PromptSettingsData promptData;

    public DialogStringResolver(Context context, PromptSettingsData promptData) {
        this.context = context;
        this.promptData = promptData;
    }

    public final String getTitle() {
        return resourceOrFallbackValue("com.crashlytics.CrashSubmissionPromptTitle", this.promptData.title);
    }

    public final String getMessage() {
        return resourceOrFallbackValue("com.crashlytics.CrashSubmissionPromptMessage", this.promptData.message);
    }

    public final String getSendButtonTitle() {
        return resourceOrFallbackValue("com.crashlytics.CrashSubmissionSendTitle", this.promptData.sendButtonTitle);
    }

    public final String getAlwaysSendButtonTitle() {
        return resourceOrFallbackValue("com.crashlytics.CrashSubmissionAlwaysSendTitle", this.promptData.alwaysSendButtonTitle);
    }

    public final String getCancelButtonTitle() {
        return resourceOrFallbackValue("com.crashlytics.CrashSubmissionCancelTitle", this.promptData.cancelButtonTitle);
    }

    private String resourceOrFallbackValue(String resourceName, String settingsValue) {
        Object obj;
        String stringsFileValue = CommonUtils.getStringsFileValue(this.context, resourceName);
        if (stringsFileValue == null || stringsFileValue.length() == 0) {
            obj = 1;
        } else {
            obj = null;
        }
        if (obj != null) {
            return settingsValue;
        }
        return stringsFileValue;
    }
}
