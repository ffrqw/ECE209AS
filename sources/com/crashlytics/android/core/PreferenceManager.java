package com.crashlytics.android.core;

import android.annotation.SuppressLint;
import io.fabric.sdk.android.services.persistence.PreferenceStore;

@SuppressLint({"CommitPrefEdits"})
final class PreferenceManager {
    private final PreferenceStore preferenceStore;

    public PreferenceManager(PreferenceStore preferenceStore) {
        this.preferenceStore = preferenceStore;
    }

    final void setShouldAlwaysSendReports(boolean send) {
        this.preferenceStore.save(this.preferenceStore.edit().putBoolean("always_send_reports_opt_in", send));
    }

    final boolean shouldAlwaysSendReports() {
        return this.preferenceStore.get().getBoolean("always_send_reports_opt_in", false);
    }
}
