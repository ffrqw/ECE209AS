package com.crashlytics.android.core;

import android.content.Context;
import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.services.common.CommonUtils;
import java.io.File;
import java.util.Set;

final class LogFileManager {
    private static final NoopLogStore NOOP_LOG_STORE = new NoopLogStore();
    private final Context context;
    private FileLogStore currentLog;
    private final DirectoryProvider directoryProvider;

    public interface DirectoryProvider {
        File getLogFileDir();
    }

    private static final class NoopLogStore implements FileLogStore {
        private NoopLogStore() {
        }

        public final void writeToLog(long timestamp, String msg) {
        }

        public final ByteString getLogAsByteString() {
            return null;
        }

        public final void closeLogFile() {
        }

        public final void deleteLogFile() {
        }
    }

    LogFileManager(Context context, DirectoryProvider directoryProvider) {
        this(context, directoryProvider, null);
    }

    LogFileManager(Context context, DirectoryProvider directoryProvider, String currentSessionId) {
        this.context = context;
        this.directoryProvider = directoryProvider;
        this.currentLog = NOOP_LOG_STORE;
        setCurrentSession(currentSessionId);
    }

    final void setCurrentSession(String sessionId) {
        this.currentLog.closeLogFile();
        this.currentLog = NOOP_LOG_STORE;
        if (sessionId != null) {
            if (CommonUtils.getBooleanResourceValue(this.context, "com.crashlytics.CollectCustomLogs", true)) {
                this.currentLog = new QueueFileLogStore(new File(this.directoryProvider.getLogFileDir(), "crashlytics-userlog-" + sessionId + ".temp"), 65536);
                return;
            }
            Fabric.getLogger().d("CrashlyticsCore", "Preferences requested no custom logs. Aborting log file creation.");
        }
    }

    final void writeToLog(long timestamp, String msg) {
        this.currentLog.writeToLog(timestamp, msg);
    }

    final ByteString getByteStringForLog() {
        return this.currentLog.getLogAsByteString();
    }

    final void clearLog() {
        this.currentLog.deleteLogFile();
    }

    final void discardOldLogFiles(Set<String> sessionIdsToKeep) {
        File[] logFiles = this.directoryProvider.getLogFileDir().listFiles();
        if (logFiles != null) {
            for (File file : logFiles) {
                Object name = file.getName();
                int lastIndexOf = name.lastIndexOf(".temp");
                if (lastIndexOf != -1) {
                    name = name.substring(20, lastIndexOf);
                }
                if (!sessionIdsToKeep.contains(name)) {
                    file.delete();
                }
            }
        }
    }
}
