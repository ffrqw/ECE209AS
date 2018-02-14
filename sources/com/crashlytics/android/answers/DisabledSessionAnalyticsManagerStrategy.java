package com.crashlytics.android.answers;

import io.fabric.sdk.android.services.settings.AnalyticsSettingsData;
import java.io.IOException;

final class DisabledSessionAnalyticsManagerStrategy implements SessionAnalyticsManagerStrategy {
    DisabledSessionAnalyticsManagerStrategy() {
    }

    public final void setAnalyticsSettingsData(AnalyticsSettingsData analyticsSettingsData, String protocolAndHostOverride) {
    }

    public final void processEvent(Builder builder) {
    }

    public final void sendEvents() {
    }

    public final void deleteAllEvents() {
    }

    public final boolean rollFileOver() throws IOException {
        return false;
    }

    public final void cancelTimeBasedFileRollOver() {
    }
}
