package com.crashlytics.android.beta;

import android.annotation.SuppressLint;
import android.content.Context;
import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.services.common.ApiKey;
import io.fabric.sdk.android.services.common.CommonUtils;
import io.fabric.sdk.android.services.common.CurrentTimeProvider;
import io.fabric.sdk.android.services.common.IdManager;
import io.fabric.sdk.android.services.common.IdManager.DeviceIdentifierType;
import io.fabric.sdk.android.services.network.HttpRequestFactory;
import io.fabric.sdk.android.services.persistence.PreferenceStore;
import io.fabric.sdk.android.services.settings.BetaSettingsData;
import java.util.concurrent.atomic.AtomicBoolean;

abstract class AbstractCheckForUpdatesController implements UpdatesController {
    private Beta beta;
    private BetaSettingsData betaSettings;
    private BuildProperties buildProps;
    private Context context;
    private CurrentTimeProvider currentTimeProvider;
    private final AtomicBoolean externallyReady;
    private HttpRequestFactory httpRequestFactory;
    private IdManager idManager;
    private final AtomicBoolean initialized;
    private long lastCheckTimeMillis;
    private PreferenceStore preferenceStore;

    public AbstractCheckForUpdatesController() {
        this(false);
    }

    public AbstractCheckForUpdatesController(boolean externallyReady) {
        this.initialized = new AtomicBoolean();
        this.lastCheckTimeMillis = 0;
        this.externallyReady = new AtomicBoolean(externallyReady);
    }

    public final void initialize(Context context, Beta beta, IdManager idManager, BetaSettingsData betaSettings, BuildProperties buildProps, PreferenceStore preferenceStore, CurrentTimeProvider currentTimeProvider, HttpRequestFactory httpRequestFactory) {
        this.context = context;
        this.beta = beta;
        this.idManager = idManager;
        this.betaSettings = betaSettings;
        this.buildProps = buildProps;
        this.preferenceStore = preferenceStore;
        this.currentTimeProvider = currentTimeProvider;
        this.httpRequestFactory = httpRequestFactory;
        this.initialized.set(true);
        if (this.externallyReady.get()) {
            checkForUpdates();
        }
    }

    protected final boolean signalExternallyReady() {
        this.externallyReady.set(true);
        return this.initialized.get();
    }

    @SuppressLint({"CommitPrefEdits"})
    protected final void checkForUpdates() {
        synchronized (this.preferenceStore) {
            if (this.preferenceStore.get().contains("last_update_check")) {
                this.preferenceStore.save(this.preferenceStore.edit().remove("last_update_check"));
            }
        }
        long currentTimeMillis = this.currentTimeProvider.getCurrentTimeMillis();
        long updateCheckDelayMillis = ((long) this.betaSettings.updateSuspendDurationSeconds) * 1000;
        Fabric.getLogger().d("Beta", "Check for updates delay: " + updateCheckDelayMillis);
        Fabric.getLogger().d("Beta", "Check for updates last check time: " + this.lastCheckTimeMillis);
        long nextCheckTimeMillis = this.lastCheckTimeMillis + updateCheckDelayMillis;
        Fabric.getLogger().d("Beta", "Check for updates current time: " + currentTimeMillis + ", next check time: " + nextCheckTimeMillis);
        if (currentTimeMillis >= nextCheckTimeMillis) {
            try {
                Fabric.getLogger().d("Beta", "Performing update check");
                new CheckForUpdatesRequest(this.beta, CommonUtils.getStringsFileValue(this.beta.getContext(), "com.crashlytics.ApiEndpoint"), this.betaSettings.updateUrl, this.httpRequestFactory, new CheckForUpdatesResponseTransform()).invoke(new ApiKey().getValue(this.context), (String) this.idManager.getDeviceIdentifiers().get(DeviceIdentifierType.FONT_TOKEN), this.buildProps);
            } finally {
                this.lastCheckTimeMillis = currentTimeMillis;
            }
        } else {
            Fabric.getLogger().d("Beta", "Check for updates next check time was not passed");
        }
    }
}
