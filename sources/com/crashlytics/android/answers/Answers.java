package com.crashlytics.android.answers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build.VERSION;
import android.os.Looper;
import io.fabric.sdk.android.ActivityLifecycleManager;
import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.Kit;
import io.fabric.sdk.android.services.common.CommonUtils;
import io.fabric.sdk.android.services.common.Crash.FatalException;
import io.fabric.sdk.android.services.common.ExecutorUtils;
import io.fabric.sdk.android.services.network.DefaultHttpRequestFactory;
import io.fabric.sdk.android.services.network.HttpRequestFactory;
import io.fabric.sdk.android.services.persistence.FileStoreImpl;
import io.fabric.sdk.android.services.persistence.PreferenceStoreImpl;
import io.fabric.sdk.android.services.settings.AnalyticsSettingsData;
import io.fabric.sdk.android.services.settings.Settings;
import io.fabric.sdk.android.services.settings.SettingsData;
import java.io.File;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

public class Answers extends Kit<Boolean> {
    SessionAnalyticsManager analyticsManager;

    public final void onException(FatalException exception) {
        if (this.analyticsManager != null) {
            SessionAnalyticsManager sessionAnalyticsManager = this.analyticsManager;
            String sessionId = exception.getSessionId();
            String exceptionName = exception.getExceptionName();
            if (Looper.myLooper() == Looper.getMainLooper()) {
                throw new IllegalStateException("onCrash called from main thread!!!");
            }
            Fabric.getLogger().d("Answers", "Logged crash");
            AnswersEventsHandler answersEventsHandler = sessionAnalyticsManager.eventsHandler;
            Map singletonMap = Collections.singletonMap("sessionId", sessionId);
            Builder builder = new Builder(Type.CRASH);
            builder.details = singletonMap;
            builder.customAttributes = Collections.singletonMap("exceptionName", exceptionName);
            answersEventsHandler.processEvent(builder, true, false);
        }
    }

    @SuppressLint({"NewApi"})
    protected final boolean onPreExecute() {
        try {
            long installedAt;
            Context context = getContext();
            PackageManager packageManager = context.getPackageManager();
            String packageName = context.getPackageName();
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            String versionCode = Integer.toString(packageInfo.versionCode);
            String versionName = packageInfo.versionName == null ? "0.0" : packageInfo.versionName;
            if (VERSION.SDK_INT >= 9) {
                installedAt = packageInfo.firstInstallTime;
            } else {
                installedAt = new File(packageManager.getApplicationInfo(packageName, 0).sourceDir).lastModified();
            }
            SessionMetadataCollector sessionMetadataCollector = new SessionMetadataCollector(context, getIdManager(), versionCode, versionName);
            AnswersFilesManagerProvider answersFilesManagerProvider = new AnswersFilesManagerProvider(context, new FileStoreImpl(this));
            HttpRequestFactory defaultHttpRequestFactory = new DefaultHttpRequestFactory(Fabric.getLogger());
            ActivityLifecycleManager activityLifecycleManager = new ActivityLifecycleManager(context);
            ScheduledExecutorService buildSingleThreadScheduledExecutorService = ExecutorUtils.buildSingleThreadScheduledExecutorService("Answers Events Handler");
            this.analyticsManager = new SessionAnalyticsManager(new AnswersEventsHandler(this, context, answersFilesManagerProvider, sessionMetadataCollector, defaultHttpRequestFactory, buildSingleThreadScheduledExecutorService), activityLifecycleManager, new BackgroundManager(buildSingleThreadScheduledExecutorService), new AnswersPreferenceManager(new PreferenceStoreImpl(context, "settings")), installedAt);
            this.analyticsManager.enable();
            return true;
        } catch (Exception e) {
            Fabric.getLogger().e("Answers", "Error retrieving app properties", e);
            return false;
        }
    }

    private Boolean doInBackground() {
        try {
            SettingsData settingsData = Settings.getInstance().awaitSettingsData();
            if (settingsData == null) {
                Fabric.getLogger().e("Answers", "Failed to retrieve settings");
                return Boolean.valueOf(false);
            } else if (settingsData.featuresData.collectAnalytics) {
                Fabric.getLogger().d("Answers", "Analytics collection enabled");
                r2 = this.analyticsManager;
                AnalyticsSettingsData analyticsSettingsData = settingsData.analyticsSettingsData;
                String stringsFileValue = CommonUtils.getStringsFileValue(getContext(), "com.crashlytics.ApiEndpoint");
                r2.backgroundManager.setFlushOnBackground(analyticsSettingsData.flushOnBackground);
                r2.eventsHandler.setAnalyticsSettingsData(analyticsSettingsData, stringsFileValue);
                return Boolean.valueOf(true);
            } else {
                Fabric.getLogger().d("Answers", "Analytics collection disabled");
                r2 = this.analyticsManager;
                r2.lifecycleManager.resetCallbacks();
                r2.eventsHandler.disable();
                return Boolean.valueOf(false);
            }
        } catch (Exception e) {
            Fabric.getLogger().e("Answers", "Error dealing with settings", e);
            return Boolean.valueOf(false);
        }
    }

    public final String getIdentifier() {
        return "com.crashlytics.sdk.android:answers";
    }

    public final String getVersion() {
        return "1.3.13.dev";
    }
}
