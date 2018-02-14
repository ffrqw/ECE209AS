package com.crashlytics.android.core;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.util.Log;
import com.crashlytics.android.core.internal.CrashEventDataProvider;
import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.Kit;
import io.fabric.sdk.android.services.common.ApiKey;
import io.fabric.sdk.android.services.common.CommonUtils;
import io.fabric.sdk.android.services.common.ExecutorUtils;
import io.fabric.sdk.android.services.common.IdManager;
import io.fabric.sdk.android.services.concurrency.DependsOn;
import io.fabric.sdk.android.services.concurrency.Priority;
import io.fabric.sdk.android.services.concurrency.PriorityCallable;
import io.fabric.sdk.android.services.concurrency.Task;
import io.fabric.sdk.android.services.concurrency.UnmetDependencyException;
import io.fabric.sdk.android.services.network.DefaultHttpRequestFactory;
import io.fabric.sdk.android.services.network.HttpRequestFactory;
import io.fabric.sdk.android.services.persistence.FileStore;
import io.fabric.sdk.android.services.persistence.FileStoreImpl;
import io.fabric.sdk.android.services.persistence.PreferenceStore;
import io.fabric.sdk.android.services.persistence.PreferenceStoreImpl;
import io.fabric.sdk.android.services.settings.Settings;
import io.fabric.sdk.android.services.settings.SettingsData;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@DependsOn({CrashEventDataProvider.class})
public class CrashlyticsCore extends Kit<Void> {
    private final ConcurrentHashMap<String, String> attributes;
    private CrashlyticsBackgroundWorker backgroundWorker;
    private CrashlyticsController controller;
    private CrashlyticsFileMarker crashMarker;
    private float delay;
    private boolean disabled;
    private HttpRequestFactory httpRequestFactory;
    private CrashlyticsFileMarker initializationMarker;
    private CrashlyticsListener listener;
    private final PinningInfoProvider pinningInfo;
    private final long startTime;
    private String userEmail;
    private String userId;
    private String userName;

    private static final class CrashMarkerCheck implements Callable<Boolean> {
        private final CrashlyticsFileMarker crashMarker;

        public CrashMarkerCheck(CrashlyticsFileMarker crashMarker) {
            this.crashMarker = crashMarker;
        }

        public final /* bridge */ /* synthetic */ Object call() throws Exception {
            if (!this.crashMarker.isPresent()) {
                return Boolean.FALSE;
            }
            Fabric.getLogger().d("CrashlyticsCore", "Found previous crash marker.");
            this.crashMarker.remove();
            return Boolean.TRUE;
        }
    }

    private static final class NoOpListener implements CrashlyticsListener {
        private NoOpListener() {
        }
    }

    public CrashlyticsCore() {
        this(1.0f, null, null, false);
    }

    private CrashlyticsCore(float delay, CrashlyticsListener listener, PinningInfoProvider pinningInfo, boolean disabled) {
        this(1.0f, null, null, false, ExecutorUtils.buildSingleThreadExecutorService("Crashlytics Exception Handler"));
    }

    private CrashlyticsCore(float delay, CrashlyticsListener listener, PinningInfoProvider pinningInfo, boolean disabled, ExecutorService crashHandlerExecutor) {
        this.userId = null;
        this.userEmail = null;
        this.userName = null;
        this.delay = delay;
        if (listener == null) {
            listener = new NoOpListener();
        }
        this.listener = listener;
        this.pinningInfo = pinningInfo;
        this.disabled = disabled;
        this.backgroundWorker = new CrashlyticsBackgroundWorker(crashHandlerExecutor);
        this.attributes = new ConcurrentHashMap();
        this.startTime = System.currentTimeMillis();
    }

    protected final boolean onPreExecute() {
        return onPreExecute(super.getContext());
    }

    private boolean onPreExecute(Context context) {
        if (this.disabled) {
            return false;
        }
        String apiKey = new ApiKey().getValue(context);
        if (apiKey == null) {
            return false;
        }
        Object obj;
        String buildId = CommonUtils.resolveBuildId(context);
        if (!CommonUtils.getBooleanResourceValue(context, "com.crashlytics.RequireBuildId", true)) {
            Fabric.getLogger().d("CrashlyticsCore", "Configured not to require a build ID.");
            obj = 1;
        } else if (CommonUtils.isNullOrEmpty(buildId)) {
            Log.e("CrashlyticsCore", ".");
            Log.e("CrashlyticsCore", ".     |  | ");
            Log.e("CrashlyticsCore", ".     |  |");
            Log.e("CrashlyticsCore", ".     |  |");
            Log.e("CrashlyticsCore", ".   \\ |  | /");
            Log.e("CrashlyticsCore", ".    \\    /");
            Log.e("CrashlyticsCore", ".     \\  /");
            Log.e("CrashlyticsCore", ".      \\/");
            Log.e("CrashlyticsCore", ".");
            Log.e("CrashlyticsCore", "This app relies on Crashlytics. Please sign up for access at https://fabric.io/sign_up,\ninstall an Android build tool and ask a team member to invite you to this app's organization.");
            Log.e("CrashlyticsCore", ".");
            Log.e("CrashlyticsCore", ".      /\\");
            Log.e("CrashlyticsCore", ".     /  \\");
            Log.e("CrashlyticsCore", ".    /    \\");
            Log.e("CrashlyticsCore", ".   / |  | \\");
            Log.e("CrashlyticsCore", ".     |  |");
            Log.e("CrashlyticsCore", ".     |  |");
            Log.e("CrashlyticsCore", ".     |  |");
            Log.e("CrashlyticsCore", ".");
            obj = null;
        } else {
            obj = 1;
        }
        if (obj == null) {
            throw new UnmetDependencyException("This app relies on Crashlytics. Please sign up for access at https://fabric.io/sign_up,\ninstall an Android build tool and ask a team member to invite you to this app's organization.");
        }
        try {
            Fabric.getLogger().i("CrashlyticsCore", "Initializing Crashlytics " + "2.3.17.dev");
            FileStore fileStore = new FileStoreImpl(this);
            this.crashMarker = new CrashlyticsFileMarker("crash_marker", fileStore);
            this.initializationMarker = new CrashlyticsFileMarker("initialization_marker", fileStore);
            PreferenceStore preferenceStoreImpl = new PreferenceStoreImpl(getContext(), "com.crashlytics.android.core.CrashlyticsCore");
            if (!preferenceStoreImpl.get().getBoolean("preferences_migration_complete", false)) {
                PreferenceStore preferenceStoreImpl2 = new PreferenceStoreImpl(this);
                obj = (preferenceStoreImpl.get().contains("always_send_reports_opt_in") || !preferenceStoreImpl2.get().contains("always_send_reports_opt_in")) ? null : 1;
                if (obj != null) {
                    preferenceStoreImpl.save(preferenceStoreImpl.edit().putBoolean("always_send_reports_opt_in", preferenceStoreImpl2.get().getBoolean("always_send_reports_opt_in", false)));
                }
                preferenceStoreImpl.save(preferenceStoreImpl.edit().putBoolean("preferences_migration_complete", true));
            }
            PreferenceManager preferenceManager = new PreferenceManager(preferenceStoreImpl);
            CrashlyticsPinningInfoProvider infoProvider = this.pinningInfo != null ? new CrashlyticsPinningInfoProvider(this.pinningInfo) : null;
            this.httpRequestFactory = new DefaultHttpRequestFactory(Fabric.getLogger());
            this.httpRequestFactory.setPinningInfoProvider(infoProvider);
            IdManager idManager = getIdManager();
            String packageName = context.getPackageName();
            String installerPackageName = idManager.getInstallerPackageName();
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
            AppData appData = new AppData(apiKey, buildId, installerPackageName, packageName, Integer.toString(packageInfo.versionCode), packageInfo.versionName == null ? "0.0" : packageInfo.versionName);
            UnityVersionProvider unityVersionProvider = new ManifestUnityVersionProvider(context, appData.packageName);
            Fabric.getLogger().d("CrashlyticsCore", "Installer package name is: " + appData.installerPackageName);
            this.controller = new CrashlyticsController(this, this.backgroundWorker, this.httpRequestFactory, idManager, preferenceManager, fileStore, appData, unityVersionProvider);
            boolean initializeSynchronously = ((Boolean) this.backgroundWorker.submitAndWait(new Callable<Boolean>() {
                public final /* bridge */ /* synthetic */ Object call() throws Exception {
                    return Boolean.valueOf(CrashlyticsCore.this.initializationMarker.isPresent());
                }
            })).booleanValue();
            if (Boolean.TRUE.equals((Boolean) this.backgroundWorker.submitAndWait(new CrashMarkerCheck(this.crashMarker)))) {
                this.controller.enableExceptionHandling(Thread.getDefaultUncaughtExceptionHandler());
            } else {
                this.controller.enableExceptionHandling(Thread.getDefaultUncaughtExceptionHandler());
            }
            if (initializeSynchronously && CommonUtils.canTryConnection(context)) {
                Fabric.getLogger().d("CrashlyticsCore", "Crashlytics did not finish previous background initialization. Initializing synchronously.");
                finishInitSynchronously();
                return false;
            }
            Fabric.getLogger().d("CrashlyticsCore", "Exception handling initialization successful");
            return true;
        } catch (Exception e) {
            Fabric.getLogger().e("CrashlyticsCore", "Crashlytics was not started due to an exception during initialization", e);
            this.controller = null;
            return false;
        }
    }

    protected final Void doInBackground() {
        this.backgroundWorker.submitAndWait(new Callable<Void>() {
            public final /* bridge */ /* synthetic */ Object call() throws Exception {
                CrashlyticsCore.this.initializationMarker.create();
                Fabric.getLogger().d("CrashlyticsCore", "Initialization marker file created.");
                return null;
            }
        });
        if (null != null) {
            this.controller.writeExternalCrashEvent(null);
        }
        this.controller.cleanInvalidTempFiles();
        try {
            SettingsData settingsData = Settings.getInstance().awaitSettingsData();
            if (settingsData == null) {
                Fabric.getLogger().w("CrashlyticsCore", "Received null settings, skipping report submission!");
            } else if (settingsData.featuresData.collectReports) {
                if (!this.controller.finalizeSessions(settingsData.sessionData)) {
                    Fabric.getLogger().d("CrashlyticsCore", "Could not finalize previous sessions.");
                }
                this.controller.submitAllReports(this.delay, settingsData);
                markInitializationComplete();
            } else {
                Fabric.getLogger().d("CrashlyticsCore", "Collection of crash reports disabled in Crashlytics settings.");
                markInitializationComplete();
            }
        } catch (Exception e) {
            Fabric.getLogger().e("CrashlyticsCore", "Crashlytics encountered a problem during asynchronous initialization.", e);
        } finally {
            markInitializationComplete();
        }
        return null;
    }

    public final String getIdentifier() {
        return "com.crashlytics.sdk.android.crashlytics-core";
    }

    public final String getVersion() {
        return "2.3.17.dev";
    }

    public final void logException(Throwable throwable) {
        if (this.disabled || !ensureFabricWithCalled("prior to logging exceptions.")) {
            return;
        }
        if (throwable == null) {
            Fabric.getLogger().log(5, "CrashlyticsCore", "Crashlytics is ignoring a request to log a null exception.");
        } else {
            this.controller.writeNonFatalException(Thread.currentThread(), throwable);
        }
    }

    public final void setUserIdentifier(String identifier) {
        if (!this.disabled && ensureFabricWithCalled("prior to setting user data.")) {
            this.userId = sanitizeAttribute(identifier);
            this.controller.cacheUserData(this.userId, this.userName, null);
        }
    }

    public final void setUserName(String name) {
        if (!this.disabled && ensureFabricWithCalled("prior to setting user data.")) {
            this.userName = sanitizeAttribute(name);
            this.controller.cacheUserData(this.userId, this.userName, null);
        }
    }

    public final void setString(String key, String value) {
        if (this.disabled || !ensureFabricWithCalled("prior to setting keys.")) {
            return;
        }
        if (key == null) {
            Context context = getContext();
            if (context == null || !CommonUtils.isAppDebuggable(context)) {
                Fabric.getLogger().e("CrashlyticsCore", "Attempting to set custom attribute with null key, ignoring.", null);
                return;
            }
            throw new IllegalArgumentException("Custom attribute key must not be null.");
        }
        key = sanitizeAttribute(key);
        if (this.attributes.size() < 64 || this.attributes.containsKey(key)) {
            this.attributes.put(key, value == null ? "" : sanitizeAttribute(value));
            this.controller.cacheKeyData(this.attributes);
            return;
        }
        Fabric.getLogger().d("CrashlyticsCore", "Exceeded maximum number of custom attributes (64)");
    }

    final Map<String, String> getAttributes() {
        return Collections.unmodifiableMap(this.attributes);
    }

    final String getUserIdentifier() {
        return getIdManager().canCollectUserIds() ? this.userId : null;
    }

    final String getUserEmail() {
        getIdManager().canCollectUserIds();
        return null;
    }

    final String getUserName() {
        return getIdManager().canCollectUserIds() ? this.userName : null;
    }

    private void finishInitSynchronously() {
        PriorityCallable<Void> callable = new PriorityCallable<Void>() {
            public final int getPriority$16699175() {
                return Priority.IMMEDIATE$4601d4ec;
            }

            public final /* bridge */ /* synthetic */ Object call() throws Exception {
                return CrashlyticsCore.this.doInBackground();
            }
        };
        for (Task task : getDependencies()) {
            callable.addDependency(task);
        }
        Future<Void> future = getFabric().getExecutorService().submit(callable);
        Fabric.getLogger().d("CrashlyticsCore", "Crashlytics detected incomplete initialization on previous app launch. Will initialize synchronously.");
        try {
            future.get(4, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Fabric.getLogger().e("CrashlyticsCore", "Crashlytics was interrupted during initialization.", e);
        } catch (ExecutionException e2) {
            Fabric.getLogger().e("CrashlyticsCore", "Problem encountered during Crashlytics initialization.", e2);
        } catch (TimeoutException e3) {
            Fabric.getLogger().e("CrashlyticsCore", "Crashlytics timed out during initialization.", e3);
        }
    }

    private void markInitializationComplete() {
        this.backgroundWorker.submit(new Callable<Boolean>() {
            private Boolean call() throws Exception {
                try {
                    boolean removed = CrashlyticsCore.this.initializationMarker.remove();
                    Fabric.getLogger().d("CrashlyticsCore", "Initialization marker file removed: " + removed);
                    return Boolean.valueOf(removed);
                } catch (Exception e) {
                    Fabric.getLogger().e("CrashlyticsCore", "Problem encountered deleting Crashlytics initialization marker.", e);
                    return Boolean.valueOf(false);
                }
            }
        });
    }

    final void createCrashMarker() {
        this.crashMarker.create();
    }

    private static String sanitizeAttribute(String input) {
        if (input == null) {
            return input;
        }
        input = input.trim();
        if (input.length() > 1024) {
            return input.substring(0, 1024);
        }
        return input;
    }

    public final void log(int priority, String tag, String msg) {
        if (!this.disabled && ensureFabricWithCalled("prior to logging messages.")) {
            this.controller.writeToLog(System.currentTimeMillis() - this.startTime, CommonUtils.logPriorityToString(priority) + "/" + tag + " " + msg);
        }
        Fabric.getLogger().log(priority, tag, msg, true);
    }

    private static boolean ensureFabricWithCalled(String msg) {
        CrashlyticsCore instance = (CrashlyticsCore) Fabric.getKit(CrashlyticsCore.class);
        if (instance != null && instance.controller != null) {
            return true;
        }
        Fabric.getLogger().e("CrashlyticsCore", "Crashlytics must be initialized by calling Fabric.with(Context) " + msg, null);
        return false;
    }
}
