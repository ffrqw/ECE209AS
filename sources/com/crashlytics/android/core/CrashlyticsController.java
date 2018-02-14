package com.crashlytics.android.core;

import android.app.Activity;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.core.LogFileManager.DirectoryProvider;
import com.crashlytics.android.core.internal.models.SessionEventData;
import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.Kit;
import io.fabric.sdk.android.services.common.CommonUtils;
import io.fabric.sdk.android.services.common.Crash.FatalException;
import io.fabric.sdk.android.services.common.Crash.LoggedException;
import io.fabric.sdk.android.services.common.DeliveryMechanism;
import io.fabric.sdk.android.services.common.IdManager;
import io.fabric.sdk.android.services.network.HttpRequestFactory;
import io.fabric.sdk.android.services.persistence.FileStore;
import io.fabric.sdk.android.services.settings.PromptSettingsData;
import io.fabric.sdk.android.services.settings.SessionSettingsData;
import io.fabric.sdk.android.services.settings.Settings;
import io.fabric.sdk.android.services.settings.SettingsData;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class CrashlyticsController {
    static final FilenameFilter ANY_SESSION_FILENAME_FILTER = new FilenameFilter() {
        public final boolean accept(File file, String filename) {
            return CrashlyticsController.SESSION_FILE_PATTERN.matcher(filename).matches();
        }
    };
    private static final String[] INITIAL_SESSION_PART_TAGS = new String[]{"SessionUser", "SessionApp", "SessionOS", "SessionDevice"};
    static final Comparator<File> LARGEST_FILE_NAME_FIRST = new Comparator<File>() {
        public final /* bridge */ /* synthetic */ int compare(Object obj, Object obj2) {
            return ((File) obj2).getName().compareTo(((File) obj).getName());
        }
    };
    private static final Map<String, String> SEND_AT_CRASHTIME_HEADER = Collections.singletonMap("X-CRASHLYTICS-SEND-FLAGS", "1");
    static final FilenameFilter SESSION_FILE_FILTER = new FilenameFilter() {
        public final boolean accept(File dir, String filename) {
            return filename.length() == 39 && filename.endsWith(".cls");
        }
    };
    private static final Pattern SESSION_FILE_PATTERN = Pattern.compile("([\\d|A-Z|a-z]{12}\\-[\\d|A-Z|a-z]{4}\\-[\\d|A-Z|a-z]{4}\\-[\\d|A-Z|a-z]{12}).+");
    static final Comparator<File> SMALLEST_FILE_NAME_FIRST = new Comparator<File>() {
        public final /* bridge */ /* synthetic */ int compare(Object obj, Object obj2) {
            return ((File) obj).getName().compareTo(((File) obj2).getName());
        }
    };
    private final AppData appData;
    private final CrashlyticsBackgroundWorker backgroundWorker;
    private CrashlyticsUncaughtExceptionHandler crashHandler;
    private final CrashlyticsCore crashlyticsCore;
    private final DevicePowerStateListener devicePowerStateListener;
    private final AtomicInteger eventCounter = new AtomicInteger(0);
    private final FileStore fileStore;
    private final HandlingExceptionCheck handlingExceptionCheck;
    private final HttpRequestFactory httpRequestFactory;
    private final IdManager idManager;
    private final LogFileDirectoryProvider logFileDirectoryProvider;
    private final LogFileManager logFileManager;
    private final PreferenceManager preferenceManager;
    private final ReportFilesProvider reportFilesProvider;
    private final StackTraceTrimmingStrategy stackTraceTrimmingStrategy;
    private final String unityVersion;

    private static class AnySessionPartFileFilter implements FilenameFilter {
        private AnySessionPartFileFilter() {
        }

        public final boolean accept(File file, String fileName) {
            return !CrashlyticsController.SESSION_FILE_FILTER.accept(file, fileName) && CrashlyticsController.SESSION_FILE_PATTERN.matcher(fileName).matches();
        }
    }

    static class FileNameContainsFilter implements FilenameFilter {
        private final String string;

        public FileNameContainsFilter(String s) {
            this.string = s;
        }

        public final boolean accept(File dir, String filename) {
            return filename.contains(this.string) && !filename.endsWith(".cls_temp");
        }
    }

    static class InvalidPartFileFilter implements FilenameFilter {
        InvalidPartFileFilter() {
        }

        public final boolean accept(File file, String fileName) {
            return ClsFileOutputStream.TEMP_FILENAME_FILTER.accept(file, fileName) || fileName.contains("SessionMissingBinaryImages");
        }
    }

    private static final class LogFileDirectoryProvider implements DirectoryProvider {
        private final FileStore rootFileStore;

        public LogFileDirectoryProvider(FileStore rootFileStore) {
            this.rootFileStore = rootFileStore;
        }

        public final File getLogFileDir() {
            File logFileDir = new File(this.rootFileStore.getFilesDir(), "log-files");
            if (!logFileDir.exists()) {
                logFileDir.mkdirs();
            }
            return logFileDir;
        }
    }

    private static final class PrivacyDialogCheck implements SendCheck {
        private final Kit kit;
        private final PreferenceManager preferenceManager;
        private final PromptSettingsData promptData;

        public PrivacyDialogCheck(Kit kit, PreferenceManager preferenceManager, PromptSettingsData promptData) {
            this.kit = kit;
            this.preferenceManager = preferenceManager;
            this.promptData = promptData;
        }

        public final boolean canSendReports() {
            Activity activity = this.kit.getFabric().getCurrentActivity();
            if (activity == null || activity.isFinishing()) {
                return true;
            }
            final CrashPromptDialog dialog = CrashPromptDialog.create(activity, this.promptData, new AlwaysSendCallback() {
                public final void sendUserReportsWithoutPrompting(boolean send) {
                    PrivacyDialogCheck.this.preferenceManager.setShouldAlwaysSendReports(true);
                }
            });
            activity.runOnUiThread(new Runnable() {
                public final void run() {
                    dialog.show();
                }
            });
            Fabric.getLogger().d("CrashlyticsCore", "Waiting for user opt-in.");
            dialog.await();
            return dialog.getOptIn();
        }
    }

    private final class ReportUploaderFilesProvider implements ReportFilesProvider {
        private ReportUploaderFilesProvider() {
        }

        public final File[] getCompleteSessionFiles() {
            return CrashlyticsController.this.listCompleteSessionFiles();
        }

        public final File[] getInvalidSessionFiles() {
            return CrashlyticsController.this.getInvalidFilesDir().listFiles();
        }
    }

    private final class ReportUploaderHandlingExceptionCheck implements HandlingExceptionCheck {
        private ReportUploaderHandlingExceptionCheck() {
        }

        public final boolean isHandlingException() {
            return CrashlyticsController.this.isHandlingException();
        }
    }

    private static final class SendReportRunnable implements Runnable {
        private final Context context;
        private final Report report;
        private final ReportUploader reportUploader;

        public SendReportRunnable(Context context, Report report, ReportUploader reportUploader) {
            this.context = context;
            this.report = report;
            this.reportUploader = reportUploader;
        }

        public final void run() {
            if (CommonUtils.canTryConnection(this.context)) {
                Fabric.getLogger().d("CrashlyticsCore", "Attempting to send crash report at time of crash...");
                this.reportUploader.forceUpload(this.report);
            }
        }
    }

    static class SessionPartFileFilter implements FilenameFilter {
        private final String sessionId;

        public SessionPartFileFilter(String sessionId) {
            this.sessionId = sessionId;
        }

        public final boolean accept(File file, String fileName) {
            if (fileName.equals(this.sessionId + ".cls") || !fileName.contains(this.sessionId) || fileName.endsWith(".cls_temp")) {
                return false;
            }
            return true;
        }
    }

    static /* synthetic */ void access$1400(CrashlyticsController x0, SessionEventData x1) throws IOException {
        Closeable clsFileOutputStream;
        Throwable e;
        Object obj = 1;
        Flushable flushable = null;
        try {
            File[] listSortedSessionBeginFiles = x0.listSortedSessionBeginFiles();
            String sessionIdFromSessionFile = listSortedSessionBeginFiles.length > 1 ? getSessionIdFromSessionFile(listSortedSessionBeginFiles[1]) : null;
            if (sessionIdFromSessionFile == null) {
                Fabric.getLogger().e("CrashlyticsCore", "Tried to write a native crash while no session was open.", null);
                CommonUtils.flushOrLog(null, "Failed to flush to session begin file.");
                CommonUtils.closeOrLog(null, "Failed to close fatal exception file output stream.");
                return;
            }
            recordFatalExceptionAnswersEvent(sessionIdFromSessionFile, String.format(Locale.US, "<native-crash [%s (%s)]>", new Object[]{x1.signal.code, x1.signal.name}));
            if (x1.binaryImages == null || x1.binaryImages.length <= 0) {
                obj = null;
            }
            clsFileOutputStream = new ClsFileOutputStream(x0.getFilesDir(), sessionIdFromSessionFile + (obj != null ? "SessionCrash" : "SessionMissingBinaryImages"));
            try {
                flushable = CodedOutputStream.newInstance(clsFileOutputStream);
                NativeCrashWriter.writeNativeCrash(x1, new LogFileManager(x0.crashlyticsCore.getContext(), x0.logFileDirectoryProvider, sessionIdFromSessionFile), new MetaDataStore(x0.getFilesDir()).readKeyData(sessionIdFromSessionFile), flushable);
                CommonUtils.flushOrLog(flushable, "Failed to flush to session begin file.");
                CommonUtils.closeOrLog(clsFileOutputStream, "Failed to close fatal exception file output stream.");
            } catch (Exception e2) {
                e = e2;
                try {
                    Fabric.getLogger().e("CrashlyticsCore", "An error occurred in the native crash logger", e);
                    CommonUtils.flushOrLog(flushable, "Failed to flush to session begin file.");
                    CommonUtils.closeOrLog(clsFileOutputStream, "Failed to close fatal exception file output stream.");
                } catch (Throwable th) {
                    e = th;
                    CommonUtils.flushOrLog(flushable, "Failed to flush to session begin file.");
                    CommonUtils.closeOrLog(clsFileOutputStream, "Failed to close fatal exception file output stream.");
                    throw e;
                }
            }
        } catch (Exception e3) {
            e = e3;
            clsFileOutputStream = null;
            Fabric.getLogger().e("CrashlyticsCore", "An error occurred in the native crash logger", e);
            CommonUtils.flushOrLog(flushable, "Failed to flush to session begin file.");
            CommonUtils.closeOrLog(clsFileOutputStream, "Failed to close fatal exception file output stream.");
        } catch (Throwable th2) {
            e = th2;
            clsFileOutputStream = null;
            CommonUtils.flushOrLog(flushable, "Failed to flush to session begin file.");
            CommonUtils.closeOrLog(clsFileOutputStream, "Failed to close fatal exception file output stream.");
            throw e;
        }
    }

    static /* synthetic */ void access$400(CrashlyticsController x0, Date x1, Thread x2, Throwable x3) {
        Throwable e;
        Closeable closeable;
        Flushable flushable = null;
        try {
            String currentSessionId = x0.getCurrentSessionId();
            if (currentSessionId == null) {
                Fabric.getLogger().e("CrashlyticsCore", "Tried to write a fatal exception while no session was open.", null);
                CommonUtils.flushOrLog(null, "Failed to flush to session begin file.");
                CommonUtils.closeOrLog(null, "Failed to close fatal exception file output stream.");
                return;
            }
            recordFatalExceptionAnswersEvent(currentSessionId, x3.getClass().getName());
            Closeable clsFileOutputStream = new ClsFileOutputStream(x0.getFilesDir(), currentSessionId + "SessionCrash");
            try {
                flushable = CodedOutputStream.newInstance(clsFileOutputStream);
                x0.writeSessionEvent(flushable, x1, x2, x3, "crash", true);
                CommonUtils.flushOrLog(flushable, "Failed to flush to session begin file.");
                CommonUtils.closeOrLog(clsFileOutputStream, "Failed to close fatal exception file output stream.");
            } catch (Exception e2) {
                e = e2;
                closeable = clsFileOutputStream;
                try {
                    Fabric.getLogger().e("CrashlyticsCore", "An error occurred in the fatal exception logger", e);
                    CommonUtils.flushOrLog(flushable, "Failed to flush to session begin file.");
                    CommonUtils.closeOrLog(closeable, "Failed to close fatal exception file output stream.");
                } catch (Throwable th) {
                    e = th;
                    CommonUtils.flushOrLog(flushable, "Failed to flush to session begin file.");
                    CommonUtils.closeOrLog(closeable, "Failed to close fatal exception file output stream.");
                    throw e;
                }
            } catch (Throwable th2) {
                e = th2;
                closeable = clsFileOutputStream;
                CommonUtils.flushOrLog(flushable, "Failed to flush to session begin file.");
                CommonUtils.closeOrLog(closeable, "Failed to close fatal exception file output stream.");
                throw e;
            }
        } catch (Exception e3) {
            e = e3;
            closeable = null;
            Fabric.getLogger().e("CrashlyticsCore", "An error occurred in the fatal exception logger", e);
            CommonUtils.flushOrLog(flushable, "Failed to flush to session begin file.");
            CommonUtils.closeOrLog(closeable, "Failed to close fatal exception file output stream.");
        } catch (Throwable th3) {
            e = th3;
            closeable = null;
            CommonUtils.flushOrLog(flushable, "Failed to flush to session begin file.");
            CommonUtils.closeOrLog(closeable, "Failed to close fatal exception file output stream.");
            throw e;
        }
    }

    static /* synthetic */ void access$900(CrashlyticsController x0, Date x1, Thread x2, Throwable x3) {
        Throwable e;
        Closeable closeable;
        Flushable flushable = null;
        String currentSessionId = x0.getCurrentSessionId();
        if (currentSessionId == null) {
            Fabric.getLogger().e("CrashlyticsCore", "Tried to write a non-fatal exception while no session was open.", null);
            return;
        }
        String name = x3.getClass().getName();
        if (((Answers) Fabric.getKit(Answers.class)) == null) {
            Fabric.getLogger().d("CrashlyticsCore", "Answers is not available");
        } else {
            LoggedException loggedException = new LoggedException(currentSessionId, name);
        }
        try {
            Fabric.getLogger().d("CrashlyticsCore", "Crashlytics is logging non-fatal exception \"" + x3 + "\" from thread " + x2.getName());
            Closeable clsFileOutputStream = new ClsFileOutputStream(x0.getFilesDir(), currentSessionId + "SessionEvent" + CommonUtils.padWithZerosToMaxIntWidth(x0.eventCounter.getAndIncrement()));
            try {
                flushable = CodedOutputStream.newInstance(clsFileOutputStream);
                x0.writeSessionEvent(flushable, x1, x2, x3, "error", false);
                CommonUtils.flushOrLog(flushable, "Failed to flush to non-fatal file.");
                CommonUtils.closeOrLog(clsFileOutputStream, "Failed to close non-fatal file output stream.");
            } catch (Exception e2) {
                e = e2;
                closeable = clsFileOutputStream;
                try {
                    Fabric.getLogger().e("CrashlyticsCore", "An error occurred in the non-fatal exception logger", e);
                    CommonUtils.flushOrLog(flushable, "Failed to flush to non-fatal file.");
                    CommonUtils.closeOrLog(closeable, "Failed to close non-fatal file output stream.");
                    x0.trimSessionEventFiles(currentSessionId, 64);
                } catch (Throwable th) {
                    e = th;
                    CommonUtils.flushOrLog(flushable, "Failed to flush to non-fatal file.");
                    CommonUtils.closeOrLog(closeable, "Failed to close non-fatal file output stream.");
                    throw e;
                }
            } catch (Throwable th2) {
                e = th2;
                closeable = clsFileOutputStream;
                CommonUtils.flushOrLog(flushable, "Failed to flush to non-fatal file.");
                CommonUtils.closeOrLog(closeable, "Failed to close non-fatal file output stream.");
                throw e;
            }
        } catch (Exception e3) {
            e = e3;
            closeable = null;
            Fabric.getLogger().e("CrashlyticsCore", "An error occurred in the non-fatal exception logger", e);
            CommonUtils.flushOrLog(flushable, "Failed to flush to non-fatal file.");
            CommonUtils.closeOrLog(closeable, "Failed to close non-fatal file output stream.");
            x0.trimSessionEventFiles(currentSessionId, 64);
        } catch (Throwable th3) {
            e = th3;
            closeable = null;
            CommonUtils.flushOrLog(flushable, "Failed to flush to non-fatal file.");
            CommonUtils.closeOrLog(closeable, "Failed to close non-fatal file output stream.");
            throw e;
        }
        try {
            x0.trimSessionEventFiles(currentSessionId, 64);
        } catch (Throwable e4) {
            Fabric.getLogger().e("CrashlyticsCore", "An error occurred when trimming non-fatal files.", e4);
        }
    }

    CrashlyticsController(CrashlyticsCore crashlyticsCore, CrashlyticsBackgroundWorker backgroundWorker, HttpRequestFactory httpRequestFactory, IdManager idManager, PreferenceManager preferenceManager, FileStore fileStore, AppData appData, UnityVersionProvider unityVersionProvider) {
        this.crashlyticsCore = crashlyticsCore;
        this.backgroundWorker = backgroundWorker;
        this.httpRequestFactory = httpRequestFactory;
        this.idManager = idManager;
        this.preferenceManager = preferenceManager;
        this.fileStore = fileStore;
        this.appData = appData;
        this.unityVersion = unityVersionProvider.getUnityVersion();
        Context context = crashlyticsCore.getContext();
        this.logFileDirectoryProvider = new LogFileDirectoryProvider(fileStore);
        this.logFileManager = new LogFileManager(context, this.logFileDirectoryProvider);
        this.reportFilesProvider = new ReportUploaderFilesProvider();
        this.handlingExceptionCheck = new ReportUploaderHandlingExceptionCheck();
        this.devicePowerStateListener = new DevicePowerStateListener(context);
        this.stackTraceTrimmingStrategy = new MiddleOutFallbackStrategy(1024, new RemoveRepeatsStrategy(10));
    }

    final synchronized void handleUncaughtException(final Thread thread, final Throwable ex) {
        Fabric.getLogger().d("CrashlyticsCore", "Crashlytics is handling uncaught exception \"" + ex + "\" from thread " + thread.getName());
        this.devicePowerStateListener.dispose();
        final Date time = new Date();
        this.backgroundWorker.submitAndWait(new Callable<Void>() {
            public final /* bridge */ /* synthetic */ Object call() throws Exception {
                CrashlyticsController.this.crashlyticsCore.createCrashMarker();
                CrashlyticsController.access$400(CrashlyticsController.this, time, thread, ex);
                SettingsData awaitSettingsData = Settings.getInstance().awaitSettingsData();
                SessionSettingsData sessionSettingsData = awaitSettingsData != null ? awaitSettingsData.sessionData : null;
                CrashlyticsController.this.doCloseSessions(sessionSettingsData);
                CrashlyticsController.access$500(CrashlyticsController.this);
                if (sessionSettingsData != null) {
                    CrashlyticsController.this.trimSessionFiles(sessionSettingsData.maxCompleteSessionsCount);
                }
                if (!CrashlyticsController.this.shouldPromptUserBeforeSendingCrashReports(awaitSettingsData)) {
                    CrashlyticsController.access$700(CrashlyticsController.this, awaitSettingsData);
                }
                return null;
            }
        });
    }

    final void submitAllReports(float delay, SettingsData settingsData) {
        if (settingsData == null) {
            Fabric.getLogger().w("CrashlyticsCore", "Could not send reports. Settings are not available.");
            return;
        }
        new ReportUploader(this.appData.apiKey, getCreateReportSpiCall(settingsData.appData.reportsUrl), this.reportFilesProvider, this.handlingExceptionCheck).uploadReports(delay, shouldPromptUserBeforeSendingCrashReports(settingsData) ? new PrivacyDialogCheck(this.crashlyticsCore, this.preferenceManager, settingsData.promptData) : new AlwaysSendCheck());
    }

    final void writeToLog(final long timestamp, final String msg) {
        this.backgroundWorker.submit(new Callable<Void>() {
            public final /* bridge */ /* synthetic */ Object call() throws Exception {
                if (!CrashlyticsController.this.isHandlingException()) {
                    CrashlyticsController.this.logFileManager.writeToLog(timestamp, msg);
                }
                return null;
            }
        });
    }

    final void writeNonFatalException(final Thread thread, final Throwable ex) {
        final Date now = new Date();
        this.backgroundWorker.submit(new Runnable() {
            public final void run() {
                if (!CrashlyticsController.this.isHandlingException()) {
                    CrashlyticsController.access$900(CrashlyticsController.this, now, thread, ex);
                }
            }
        });
    }

    final void cacheUserData(final String userId, final String userName, final String userEmail) {
        this.backgroundWorker.submit(new Callable<Void>() {
            public final /* bridge */ /* synthetic */ Object call() throws Exception {
                new MetaDataStore(CrashlyticsController.this.getFilesDir()).writeUserData(CrashlyticsController.this.getCurrentSessionId(), new UserMetaData(userId, userName, userEmail));
                return null;
            }
        });
    }

    final void cacheKeyData(final Map<String, String> keyData) {
        this.backgroundWorker.submit(new Callable<Void>() {
            public final /* bridge */ /* synthetic */ Object call() throws Exception {
                new MetaDataStore(CrashlyticsController.this.getFilesDir()).writeKeyData(CrashlyticsController.this.getCurrentSessionId(), keyData);
                return null;
            }
        });
    }

    private String getCurrentSessionId() {
        File[] sessionBeginFiles = listSortedSessionBeginFiles();
        return sessionBeginFiles.length > 0 ? getSessionIdFromSessionFile(sessionBeginFiles[0]) : null;
    }

    static String getSessionIdFromSessionFile(File sessionFile) {
        return sessionFile.getName().substring(0, 35);
    }

    final boolean finalizeSessions(final SessionSettingsData sessionSettingsData) {
        return ((Boolean) this.backgroundWorker.submitAndWait(new Callable<Boolean>() {
            public final /* bridge */ /* synthetic */ Object call() throws Exception {
                if (CrashlyticsController.this.isHandlingException()) {
                    Fabric.getLogger().d("CrashlyticsCore", "Skipping session finalization because a crash has already occurred.");
                    return Boolean.FALSE;
                }
                Fabric.getLogger().d("CrashlyticsCore", "Finalizing previously open sessions.");
                CrashlyticsController.this.doCloseSessions(sessionSettingsData, true);
                Fabric.getLogger().d("CrashlyticsCore", "Closed all previously open sessions");
                return Boolean.TRUE;
            }
        })).booleanValue();
    }

    final void doCloseSessions(SessionSettingsData sessionSettingsData) throws Exception {
        doCloseSessions(sessionSettingsData, false);
    }

    private void doCloseSessions(SessionSettingsData sessionSettingsData, boolean excludeCurrent) throws Exception {
        int offset;
        Throwable th;
        Flushable flushable;
        Closeable closeable = null;
        byte b = (byte) 1;
        if (excludeCurrent) {
            offset = 1;
        } else {
            offset = 0;
        }
        int i = offset + 8;
        Set hashSet = new HashSet();
        File[] listSortedSessionBeginFiles = listSortedSessionBeginFiles();
        int min = Math.min(i, listSortedSessionBeginFiles.length);
        for (i = 0; i < min; i++) {
            hashSet.add(getSessionIdFromSessionFile(listSortedSessionBeginFiles[i]));
        }
        this.logFileManager.discardOldLogFiles(hashSet);
        retainSessions(listFilesMatching(new AnySessionPartFileFilter()), hashSet);
        File[] sessionBeginFiles = listSortedSessionBeginFiles();
        if (sessionBeginFiles.length <= offset) {
            Fabric.getLogger().d("CrashlyticsCore", "No open sessions to be closed.");
            return;
        }
        String mostRecentSessionIdToClose = getSessionIdFromSessionFile(sessionBeginFiles[offset]);
        try {
            Closeable clsFileOutputStream = new ClsFileOutputStream(getFilesDir(), mostRecentSessionIdToClose + "SessionUser");
            try {
                Flushable newInstance = CodedOutputStream.newInstance(clsFileOutputStream);
                try {
                    UserMetaData userMetaData = isHandlingException() ? new UserMetaData(this.crashlyticsCore.getUserIdentifier(), this.crashlyticsCore.getUserName(), this.crashlyticsCore.getUserEmail()) : new MetaDataStore(getFilesDir()).readUserData(mostRecentSessionIdToClose);
                    if (!(userMetaData.id == null && userMetaData.name == null && userMetaData.email == null)) {
                        b = (byte) 0;
                    }
                    if (b != (byte) 0) {
                        CommonUtils.flushOrLog(newInstance, "Failed to flush session user file.");
                        CommonUtils.closeOrLog(clsFileOutputStream, "Failed to close session user file.");
                    } else {
                        SessionProtobufHelper.writeSessionUser(newInstance, userMetaData.id, userMetaData.name, userMetaData.email);
                        CommonUtils.flushOrLog(newInstance, "Failed to flush session user file.");
                        CommonUtils.closeOrLog(clsFileOutputStream, "Failed to close session user file.");
                    }
                    if (sessionSettingsData == null) {
                        Fabric.getLogger().d("CrashlyticsCore", "Unable to close session. Settings are not loaded.");
                    } else {
                        closeOpenSessions(sessionBeginFiles, offset, sessionSettingsData.maxCustomExceptionEvents);
                    }
                } catch (Throwable th2) {
                    th = th2;
                    flushable = newInstance;
                    closeable = clsFileOutputStream;
                    CommonUtils.flushOrLog(flushable, "Failed to flush session user file.");
                    CommonUtils.closeOrLog(closeable, "Failed to close session user file.");
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                Object obj = closeable;
                closeable = clsFileOutputStream;
                CommonUtils.flushOrLog(flushable, "Failed to flush session user file.");
                CommonUtils.closeOrLog(closeable, "Failed to close session user file.");
                throw th;
            }
        } catch (Throwable th4) {
            th = th4;
            flushable = closeable;
            CommonUtils.flushOrLog(flushable, "Failed to flush session user file.");
            CommonUtils.closeOrLog(closeable, "Failed to close session user file.");
            throw th;
        }
    }

    private void closeOpenSessions(File[] sessionBeginFiles, int beginIndex, int maxLoggedExceptionsCount) {
        Fabric.getLogger().d("CrashlyticsCore", "Closing open sessions.");
        for (int i = beginIndex; i < sessionBeginFiles.length; i++) {
            File sessionBeginFile = sessionBeginFiles[i];
            String sessionIdentifier = getSessionIdFromSessionFile(sessionBeginFile);
            Fabric.getLogger().d("CrashlyticsCore", "Closing session: " + sessionIdentifier);
            Fabric.getLogger().d("CrashlyticsCore", "Collecting session parts for ID " + sessionIdentifier);
            File[] listFilesMatching = listFilesMatching(new FileNameContainsFilter(sessionIdentifier + "SessionCrash"));
            boolean z = listFilesMatching != null && listFilesMatching.length > 0;
            Fabric.getLogger().d("CrashlyticsCore", String.format(Locale.US, "Session %s has fatal exception: %s", new Object[]{sessionIdentifier, Boolean.valueOf(z)}));
            File[] listFilesMatching2 = listFilesMatching(new FileNameContainsFilter(sessionIdentifier + "SessionEvent"));
            boolean z2 = listFilesMatching2 != null && listFilesMatching2.length > 0;
            Fabric.getLogger().d("CrashlyticsCore", String.format(Locale.US, "Session %s has non-fatal exceptions: %s", new Object[]{sessionIdentifier, Boolean.valueOf(z2)}));
            if (z || z2) {
                File[] listFilesMatching3;
                if (listFilesMatching2.length > maxLoggedExceptionsCount) {
                    Fabric.getLogger().d("CrashlyticsCore", String.format(Locale.US, "Trimming down to %d logged exceptions.", new Object[]{Integer.valueOf(maxLoggedExceptionsCount)}));
                    trimSessionEventFiles(sessionIdentifier, maxLoggedExceptionsCount);
                    listFilesMatching3 = listFilesMatching(new FileNameContainsFilter(sessionIdentifier + "SessionEvent"));
                } else {
                    listFilesMatching3 = listFilesMatching2;
                }
                synthesizeSessionFile(sessionBeginFile, sessionIdentifier, listFilesMatching3, z ? listFilesMatching[0] : null);
            } else {
                Fabric.getLogger().d("CrashlyticsCore", "No events present for session ID " + sessionIdentifier);
            }
            Fabric.getLogger().d("CrashlyticsCore", "Removing session part files for ID " + sessionIdentifier);
            deleteSessionPartFilesFor(sessionIdentifier);
        }
    }

    final File[] listCompleteSessionFiles() {
        List<File> completeSessionFiles = new LinkedList();
        Collections.addAll(completeSessionFiles, listFilesMatching(getFatalSessionFilesDir(), SESSION_FILE_FILTER));
        Collections.addAll(completeSessionFiles, listFilesMatching(getNonFatalSessionFilesDir(), SESSION_FILE_FILTER));
        Collections.addAll(completeSessionFiles, listFilesMatching(getFilesDir(), SESSION_FILE_FILTER));
        return (File[]) completeSessionFiles.toArray(new File[completeSessionFiles.size()]);
    }

    private File[] listFilesMatching(FilenameFilter filter) {
        return listFilesMatching(getFilesDir(), filter);
    }

    private File[] listFilesMatching(File directory, FilenameFilter filter) {
        return ensureFileArrayNotNull(directory.listFiles(filter));
    }

    private static File[] ensureFileArrayNotNull(File[] files) {
        return files == null ? new File[0] : files;
    }

    private void trimSessionEventFiles(String sessionId, int limit) {
        Utils.capFileCount(getFilesDir(), new FileNameContainsFilter(sessionId + "SessionEvent"), limit, SMALLEST_FILE_NAME_FIRST);
    }

    final void trimSessionFiles(int maxCompleteSessionsCount) {
        int remaining = maxCompleteSessionsCount - Utils.capFileCount(getFatalSessionFilesDir(), maxCompleteSessionsCount, SMALLEST_FILE_NAME_FIRST);
        Utils.capFileCount(getFilesDir(), SESSION_FILE_FILTER, remaining - Utils.capFileCount(getNonFatalSessionFilesDir(), remaining, SMALLEST_FILE_NAME_FIRST), SMALLEST_FILE_NAME_FIRST);
    }

    private static void retainSessions(File[] files, Set<String> sessionIdsToKeep) {
        int length = files.length;
        int i = 0;
        while (i < length) {
            File sessionPartFile = files[i];
            String fileName = sessionPartFile.getName();
            Matcher matcher = SESSION_FILE_PATTERN.matcher(fileName);
            if (matcher.matches()) {
                if (!sessionIdsToKeep.contains(matcher.group(1))) {
                    Fabric.getLogger().d("CrashlyticsCore", "Trimming session file: " + fileName);
                    sessionPartFile.delete();
                }
                i++;
            } else {
                Fabric.getLogger().d("CrashlyticsCore", "Deleting unknown file: " + fileName);
                sessionPartFile.delete();
                return;
            }
        }
    }

    final void cleanInvalidTempFiles() {
        this.backgroundWorker.submit(new Runnable() {
            public final void run() {
                CrashlyticsController.this.doCleanInvalidTempFiles(CrashlyticsController.this.listFilesMatching(new InvalidPartFileFilter()));
            }
        });
    }

    final void doCleanInvalidTempFiles(File[] invalidFiles) {
        int length;
        int i = 0;
        final Set<String> invalidSessionIds = new HashSet();
        for (File invalidFile : invalidFiles) {
            Fabric.getLogger().d("CrashlyticsCore", "Found invalid session part file: " + invalidFile);
            invalidSessionIds.add(getSessionIdFromSessionFile(invalidFile));
        }
        if (!invalidSessionIds.isEmpty()) {
            File invalidFilesDir = getInvalidFilesDir();
            if (!invalidFilesDir.exists()) {
                invalidFilesDir.mkdir();
            }
            File[] listFilesMatching = listFilesMatching(new FilenameFilter() {
                public final boolean accept(File dir, String filename) {
                    if (filename.length() < 35) {
                        return false;
                    }
                    return invalidSessionIds.contains(filename.substring(0, 35));
                }
            });
            length = listFilesMatching.length;
            while (i < length) {
                File sessionFile = listFilesMatching[i];
                Fabric.getLogger().d("CrashlyticsCore", "Moving session file: " + sessionFile);
                if (!sessionFile.renameTo(new File(invalidFilesDir, sessionFile.getName()))) {
                    Fabric.getLogger().d("CrashlyticsCore", "Could not move session file. Deleting " + sessionFile);
                    sessionFile.delete();
                }
                i++;
            }
            trimInvalidSessionFiles();
        }
    }

    private void trimInvalidSessionFiles() {
        File invalidFilesDir = getInvalidFilesDir();
        if (invalidFilesDir.exists()) {
            File[] oldInvalidFiles = listFilesMatching(invalidFilesDir, new InvalidPartFileFilter());
            Arrays.sort(oldInvalidFiles, Collections.reverseOrder());
            Set<String> sessionIdsToKeep = new HashSet();
            for (int i = 0; i < oldInvalidFiles.length && sessionIdsToKeep.size() < 4; i++) {
                sessionIdsToKeep.add(getSessionIdFromSessionFile(oldInvalidFiles[i]));
            }
            retainSessions(ensureFileArrayNotNull(invalidFilesDir.listFiles()), sessionIdsToKeep);
        }
    }

    final void writeExternalCrashEvent(final SessionEventData crashEventData) {
        this.backgroundWorker.submit(new Callable<Void>() {
            public final /* bridge */ /* synthetic */ Object call() throws Exception {
                if (!CrashlyticsController.this.isHandlingException()) {
                    CrashlyticsController.access$1400(CrashlyticsController.this, crashEventData);
                }
                return null;
            }
        });
    }

    private void writeSessionEvent(CodedOutputStream cos, Date time, Thread thread, Throwable ex, String eventType, boolean includeAllThreads) throws Exception {
        Thread[] threads;
        Map<String, String> attributes;
        TrimmedThrowableData trimmedEx = new TrimmedThrowableData(ex, this.stackTraceTrimmingStrategy);
        Context context = this.crashlyticsCore.getContext();
        long eventTime = time.getTime() / 1000;
        Float batteryLevel = CommonUtils.getBatteryLevel(context);
        int batteryVelocity = CommonUtils.getBatteryVelocity(context, this.devicePowerStateListener.isPowerConnected());
        boolean proximityEnabled = CommonUtils.getProximitySensorEnabled(context);
        int orientation = context.getResources().getConfiguration().orientation;
        long usedRamBytes = CommonUtils.getTotalRamInBytes() - CommonUtils.calculateFreeRamInBytes(context);
        long diskUsedBytes = CommonUtils.calculateUsedDiskSpaceInBytes(Environment.getDataDirectory().getPath());
        RunningAppProcessInfo runningAppProcessInfo = CommonUtils.getAppProcessInfo(context.getPackageName(), context);
        List<StackTraceElement[]> stacks = new LinkedList();
        StackTraceElement[] exceptionStack = trimmedEx.stacktrace;
        String buildId = this.appData.buildId;
        String appIdentifier = this.idManager.getAppIdentifier();
        if (includeAllThreads) {
            Map<Thread, StackTraceElement[]> allStackTraces = Thread.getAllStackTraces();
            threads = new Thread[allStackTraces.size()];
            int i = 0;
            for (Entry<Thread, StackTraceElement[]> entry : allStackTraces.entrySet()) {
                threads[i] = (Thread) entry.getKey();
                stacks.add(this.stackTraceTrimmingStrategy.getTrimmedStackTrace((StackTraceElement[]) entry.getValue()));
                i++;
            }
        } else {
            threads = new Thread[0];
        }
        if (CommonUtils.getBooleanResourceValue(context, "com.crashlytics.CollectCustomKeys", true)) {
            attributes = this.crashlyticsCore.getAttributes();
            if (attributes != null && attributes.size() > 1) {
                attributes = new TreeMap(attributes);
            }
        } else {
            attributes = new TreeMap();
        }
        SessionProtobufHelper.writeSessionEvent(cos, eventTime, eventType, trimmedEx, thread, exceptionStack, threads, stacks, attributes, this.logFileManager, runningAppProcessInfo, orientation, appIdentifier, buildId, batteryLevel, batteryVelocity, proximityEnabled, usedRamBytes, diskUsedBytes);
    }

    private void synthesizeSessionFile(File sessionBeginFile, String sessionId, File[] nonFatalFiles, File fatalFile) {
        Exception e;
        Throwable e2;
        boolean hasFatal = fatalFile != null;
        File outputDir = hasFatal ? getFatalSessionFilesDir() : getNonFatalSessionFilesDir();
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        ClsFileOutputStream fos = null;
        try {
            ClsFileOutputStream fos2 = new ClsFileOutputStream(outputDir, sessionId);
            try {
                CodedOutputStream cos = CodedOutputStream.newInstance(fos2);
                Fabric.getLogger().d("CrashlyticsCore", "Collecting SessionStart data for session ID " + sessionId);
                writeToCosFromFile(cos, sessionBeginFile);
                cos.writeUInt64(4, new Date().getTime() / 1000);
                cos.writeBool(5, hasFatal);
                cos.writeUInt32(11, 1);
                cos.writeEnum(12, 3);
                writeInitialPartsTo(cos, sessionId);
                writeNonFatalEventsTo(cos, nonFatalFiles, sessionId);
                if (hasFatal) {
                    writeToCosFromFile(cos, fatalFile);
                }
                CommonUtils.flushOrLog(cos, "Error flushing session file stream");
                CommonUtils.closeOrLog(fos2, "Failed to close CLS file");
                fos = fos2;
            } catch (Exception e3) {
                e = e3;
                fos = fos2;
                try {
                    Fabric.getLogger().e("CrashlyticsCore", "Failed to write session file for session ID: " + sessionId, e);
                    CommonUtils.flushOrLog(null, "Error flushing session file stream");
                    if (fos != null) {
                        try {
                            fos.closeInProgressStream();
                        } catch (Throwable e22) {
                            Fabric.getLogger().e("CrashlyticsCore", "Error closing session file stream in the presence of an exception", e22);
                        }
                    }
                } catch (Throwable th) {
                    e22 = th;
                    CommonUtils.flushOrLog(null, "Error flushing session file stream");
                    CommonUtils.closeOrLog(fos, "Failed to close CLS file");
                    throw e22;
                }
            } catch (Throwable th2) {
                e22 = th2;
                fos = fos2;
                CommonUtils.flushOrLog(null, "Error flushing session file stream");
                CommonUtils.closeOrLog(fos, "Failed to close CLS file");
                throw e22;
            }
        } catch (Exception e4) {
            e = e4;
            Fabric.getLogger().e("CrashlyticsCore", "Failed to write session file for session ID: " + sessionId, e);
            CommonUtils.flushOrLog(null, "Error flushing session file stream");
            if (fos != null) {
                fos.closeInProgressStream();
            }
        }
    }

    private static void writeNonFatalEventsTo(CodedOutputStream cos, File[] nonFatalFiles, String sessionId) {
        Arrays.sort(nonFatalFiles, CommonUtils.FILE_MODIFIED_COMPARATOR);
        for (File nonFatalFile : nonFatalFiles) {
            try {
                Fabric.getLogger().d("CrashlyticsCore", String.format(Locale.US, "Found Non Fatal for session ID %s in %s ", new Object[]{sessionId, nonFatalFile.getName()}));
                writeToCosFromFile(cos, nonFatalFile);
            } catch (Exception e) {
                Fabric.getLogger().e("CrashlyticsCore", "Error writting non-fatal to session.", e);
            }
        }
    }

    private void writeInitialPartsTo(CodedOutputStream cos, String sessionId) throws IOException {
        String[] strArr = INITIAL_SESSION_PART_TAGS;
        for (int i = 0; i < 4; i++) {
            String tag = strArr[i];
            File[] sessionPartFiles = listFilesMatching(new FileNameContainsFilter(sessionId + tag));
            if (sessionPartFiles.length == 0) {
                Fabric.getLogger().e("CrashlyticsCore", "Can't find " + tag + " data for session ID " + sessionId, null);
            } else {
                Fabric.getLogger().d("CrashlyticsCore", "Collecting " + tag + " data for session ID " + sessionId);
                writeToCosFromFile(cos, sessionPartFiles[0]);
            }
        }
    }

    private static void writeToCosFromFile(CodedOutputStream cos, File file) throws IOException {
        Throwable th;
        if (file.exists()) {
            FileInputStream fis = null;
            try {
                FileInputStream fis2 = new FileInputStream(file);
                try {
                    copyToCodedOutputStream(fis2, cos, (int) file.length());
                    CommonUtils.closeOrLog(fis2, "Failed to close file input stream.");
                    return;
                } catch (Throwable th2) {
                    th = th2;
                    fis = fis2;
                    CommonUtils.closeOrLog(fis, "Failed to close file input stream.");
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                CommonUtils.closeOrLog(fis, "Failed to close file input stream.");
                throw th;
            }
        }
        Fabric.getLogger().e("CrashlyticsCore", "Tried to include a file that doesn't exist: " + file.getName(), null);
    }

    private static void copyToCodedOutputStream(InputStream inStream, CodedOutputStream cos, int bufferLength) throws IOException {
        byte[] buffer = new byte[bufferLength];
        int offset = 0;
        while (offset < buffer.length) {
            int numRead = inStream.read(buffer, offset, buffer.length - offset);
            if (numRead < 0) {
                break;
            }
            offset += numRead;
        }
        cos.writeRawBytes(buffer);
    }

    final boolean isHandlingException() {
        return this.crashHandler != null && this.crashHandler.isHandlingException();
    }

    final File getFilesDir() {
        return this.fileStore.getFilesDir();
    }

    private File getFatalSessionFilesDir() {
        return new File(getFilesDir(), "fatal-sessions");
    }

    private File getNonFatalSessionFilesDir() {
        return new File(getFilesDir(), "nonfatal-sessions");
    }

    final File getInvalidFilesDir() {
        return new File(getFilesDir(), "invalidClsFiles");
    }

    private boolean shouldPromptUserBeforeSendingCrashReports(SettingsData settingsData) {
        if (settingsData == null || !settingsData.featuresData.promptEnabled || this.preferenceManager.shouldAlwaysSendReports()) {
            return false;
        }
        return true;
    }

    private CreateReportSpiCall getCreateReportSpiCall(String reportsUrl) {
        return new DefaultCreateReportSpiCall(this.crashlyticsCore, CommonUtils.getStringsFileValue(this.crashlyticsCore.getContext(), "com.crashlytics.ApiEndpoint"), reportsUrl, this.httpRequestFactory);
    }

    private static void recordFatalExceptionAnswersEvent(String sessionId, String exceptionName) {
        Answers answers = (Answers) Fabric.getKit(Answers.class);
        if (answers == null) {
            Fabric.getLogger().d("CrashlyticsCore", "Answers is not available");
        } else {
            answers.onException(new FatalException(sessionId, exceptionName));
        }
    }

    final void enableExceptionHandling(UncaughtExceptionHandler defaultHandler) {
        this.backgroundWorker.submit(new Callable<Void>() {
            public final /* bridge */ /* synthetic */ Object call() throws Exception {
                CrashlyticsController.access$500(CrashlyticsController.this);
                return null;
            }
        });
        this.crashHandler = new CrashlyticsUncaughtExceptionHandler(new CrashListener() {
            public final void onUncaughtException(Thread thread, Throwable ex) {
                CrashlyticsController.this.handleUncaughtException(thread, ex);
            }
        }, defaultHandler);
        Thread.setDefaultUncaughtExceptionHandler(this.crashHandler);
    }

    private void deleteSessionPartFilesFor(String sessionId) {
        for (File file : listFilesMatching(new SessionPartFileFilter(sessionId))) {
            file.delete();
        }
    }

    private File[] listSortedSessionBeginFiles() {
        File[] sessionBeginFiles = listFilesMatching(new FileNameContainsFilter("BeginSession"));
        Arrays.sort(sessionBeginFiles, LARGEST_FILE_NAME_FIRST);
        return sessionBeginFiles;
    }

    static /* synthetic */ void access$500(CrashlyticsController x0) throws Exception {
        Closeable clsFileOutputStream;
        Throwable th;
        Date date = new Date();
        String clsuuid = new CLSUUID(x0.idManager).toString();
        Fabric.getLogger().d("CrashlyticsCore", "Opening a new session with ID " + clsuuid);
        Flushable flushable = null;
        try {
            clsFileOutputStream = new ClsFileOutputStream(x0.getFilesDir(), clsuuid + "BeginSession");
            try {
                flushable = CodedOutputStream.newInstance(clsFileOutputStream);
                Object[] objArr = new Object[1];
                CrashlyticsCore crashlyticsCore = x0.crashlyticsCore;
                objArr[0] = "2.3.17.dev";
                SessionProtobufHelper.writeBeginSession(flushable, clsuuid, String.format(Locale.US, "Crashlytics Android SDK/%s", objArr), date.getTime() / 1000);
                CommonUtils.flushOrLog(flushable, "Failed to flush to session begin file.");
                CommonUtils.closeOrLog(clsFileOutputStream, "Failed to close begin session file.");
                clsFileOutputStream = null;
                flushable = null;
                try {
                    Closeable clsFileOutputStream2 = new ClsFileOutputStream(x0.getFilesDir(), clsuuid + "SessionApp");
                    try {
                        Flushable newInstance = CodedOutputStream.newInstance(clsFileOutputStream2);
                        try {
                            SessionProtobufHelper.writeSessionApp(newInstance, x0.idManager.getAppIdentifier(), x0.appData.apiKey, x0.appData.versionCode, x0.appData.versionName, x0.idManager.getAppInstallIdentifier(), DeliveryMechanism.determineFrom(x0.appData.installerPackageName).getId(), x0.unityVersion);
                            CommonUtils.flushOrLog(newInstance, "Failed to flush to session app file.");
                            CommonUtils.closeOrLog(clsFileOutputStream2, "Failed to close session app file.");
                            flushable = null;
                            try {
                                clsFileOutputStream = new ClsFileOutputStream(x0.getFilesDir(), clsuuid + "SessionOS");
                                try {
                                    flushable = CodedOutputStream.newInstance(clsFileOutputStream);
                                    SessionProtobufHelper.writeSessionOS(flushable, CommonUtils.isRooted(x0.crashlyticsCore.getContext()));
                                    CommonUtils.flushOrLog(flushable, "Failed to flush to session OS file.");
                                    CommonUtils.closeOrLog(clsFileOutputStream, "Failed to close session OS file.");
                                    clsFileOutputStream = null;
                                    flushable = null;
                                    try {
                                        OutputStream clsFileOutputStream3 = new ClsFileOutputStream(x0.getFilesDir(), clsuuid + "SessionDevice");
                                        try {
                                            flushable = CodedOutputStream.newInstance(clsFileOutputStream3);
                                            Context context = x0.crashlyticsCore.getContext();
                                            StatFs statFs = new StatFs(Environment.getDataDirectory().getPath());
                                            SessionProtobufHelper.writeSessionDevice(flushable, x0.idManager.getDeviceUUID(), CommonUtils.getCpuArchitectureInt(), Build.MODEL, Runtime.getRuntime().availableProcessors(), CommonUtils.getTotalRamInBytes(), ((long) statFs.getBlockCount()) * ((long) statFs.getBlockSize()), CommonUtils.isEmulator(context), x0.idManager.getDeviceIdentifiers(), CommonUtils.getDeviceState(context), Build.MANUFACTURER, Build.PRODUCT);
                                            CommonUtils.flushOrLog(flushable, "Failed to flush session device info.");
                                            CommonUtils.closeOrLog(clsFileOutputStream3, "Failed to close session device file.");
                                            x0.logFileManager.setCurrentSession(clsuuid);
                                        } catch (Throwable th2) {
                                            th = th2;
                                            Object obj = clsFileOutputStream3;
                                            CommonUtils.flushOrLog(flushable, "Failed to flush session device info.");
                                            CommonUtils.closeOrLog(clsFileOutputStream, "Failed to close session device file.");
                                            throw th;
                                        }
                                    } catch (Throwable th3) {
                                        th = th3;
                                        CommonUtils.flushOrLog(flushable, "Failed to flush session device info.");
                                        CommonUtils.closeOrLog(clsFileOutputStream, "Failed to close session device file.");
                                        throw th;
                                    }
                                } catch (Throwable th4) {
                                    th = th4;
                                    CommonUtils.flushOrLog(flushable, "Failed to flush to session OS file.");
                                    CommonUtils.closeOrLog(clsFileOutputStream, "Failed to close session OS file.");
                                    throw th;
                                }
                            } catch (Throwable th5) {
                                th = th5;
                                clsFileOutputStream = null;
                                CommonUtils.flushOrLog(flushable, "Failed to flush to session OS file.");
                                CommonUtils.closeOrLog(clsFileOutputStream, "Failed to close session OS file.");
                                throw th;
                            }
                        } catch (Throwable th6) {
                            clsFileOutputStream = clsFileOutputStream2;
                            Flushable flushable2 = newInstance;
                            th = th6;
                            flushable = flushable2;
                            CommonUtils.flushOrLog(flushable, "Failed to flush to session app file.");
                            CommonUtils.closeOrLog(clsFileOutputStream, "Failed to close session app file.");
                            throw th;
                        }
                    } catch (Throwable th7) {
                        th = th7;
                        clsFileOutputStream = clsFileOutputStream2;
                        CommonUtils.flushOrLog(flushable, "Failed to flush to session app file.");
                        CommonUtils.closeOrLog(clsFileOutputStream, "Failed to close session app file.");
                        throw th;
                    }
                } catch (Throwable th8) {
                    th = th8;
                    CommonUtils.flushOrLog(flushable, "Failed to flush to session app file.");
                    CommonUtils.closeOrLog(clsFileOutputStream, "Failed to close session app file.");
                    throw th;
                }
            } catch (Throwable th9) {
                th = th9;
                CommonUtils.flushOrLog(flushable, "Failed to flush to session begin file.");
                CommonUtils.closeOrLog(clsFileOutputStream, "Failed to close begin session file.");
                throw th;
            }
        } catch (Throwable th10) {
            th = th10;
            clsFileOutputStream = null;
            CommonUtils.flushOrLog(flushable, "Failed to flush to session begin file.");
            CommonUtils.closeOrLog(clsFileOutputStream, "Failed to close begin session file.");
            throw th;
        }
    }

    static /* synthetic */ void access$700(CrashlyticsController x0, SettingsData x1) {
        if (x1 == null) {
            Fabric.getLogger().w("CrashlyticsCore", "Cannot send reports. Settings are unavailable.");
            return;
        }
        Context context = x0.crashlyticsCore.getContext();
        ReportUploader reportUploader = new ReportUploader(x0.appData.apiKey, x0.getCreateReportSpiCall(x1.appData.reportsUrl), x0.reportFilesProvider, x0.handlingExceptionCheck);
        for (File sessionReport : x0.listCompleteSessionFiles()) {
            x0.backgroundWorker.submit(new SendReportRunnable(context, new SessionReport(sessionReport, SEND_AT_CRASHTIME_HEADER), reportUploader));
        }
    }
}
