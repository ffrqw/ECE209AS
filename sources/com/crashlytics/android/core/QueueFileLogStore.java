package com.crashlytics.android.core;

import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.services.common.CommonUtils;
import io.fabric.sdk.android.services.common.QueueFile;
import io.fabric.sdk.android.services.common.QueueFile.ElementReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

final class QueueFileLogStore implements FileLogStore {
    private QueueFile logFile;
    private final int maxLogSize;
    private final File workingFile;

    public QueueFileLogStore(File workingFile, int maxLogSize) {
        this.workingFile = workingFile;
        this.maxLogSize = maxLogSize;
    }

    public final void writeToLog(long timestamp, String msg) {
        openLogFile();
        if (this.logFile != null) {
            String str;
            if (msg == null) {
                str = "null";
            } else {
                str = msg;
            }
            try {
                int i = this.maxLogSize / 4;
                if (str.length() > i) {
                    str = "..." + str.substring(str.length() - i);
                }
                str = str.replaceAll("\r", " ").replaceAll("\n", " ");
                this.logFile.add(String.format(Locale.US, "%d %s%n", new Object[]{Long.valueOf(timestamp), str}).getBytes("UTF-8"));
                while (!this.logFile.isEmpty() && this.logFile.usedBytes() > this.maxLogSize) {
                    this.logFile.remove();
                }
            } catch (Throwable e) {
                Fabric.getLogger().e("CrashlyticsCore", "There was a problem writing to the Crashlytics log.", e);
            }
        }
    }

    public final ByteString getLogAsByteString() {
        if (!this.workingFile.exists()) {
            return null;
        }
        openLogFile();
        if (this.logFile == null) {
            return null;
        }
        final int[] offsetHolder = new int[]{0};
        final byte[] logBytes = new byte[this.logFile.usedBytes()];
        try {
            this.logFile.forEach(new ElementReader() {
                public final void read(InputStream in, int length) throws IOException {
                    try {
                        in.read(logBytes, offsetHolder[0], length);
                        int[] iArr = offsetHolder;
                        iArr[0] = iArr[0] + length;
                    } finally {
                        in.close();
                    }
                }
            });
        } catch (IOException e) {
            Fabric.getLogger().e("CrashlyticsCore", "A problem occurred while reading the Crashlytics log file.", e);
        }
        return ByteString.copyFrom(logBytes, 0, offsetHolder[0]);
    }

    public final void closeLogFile() {
        CommonUtils.closeOrLog(this.logFile, "There was a problem closing the Crashlytics log file.");
        this.logFile = null;
    }

    public final void deleteLogFile() {
        closeLogFile();
        this.workingFile.delete();
    }

    private void openLogFile() {
        if (this.logFile == null) {
            try {
                this.logFile = new QueueFile(this.workingFile);
            } catch (IOException e) {
                Fabric.getLogger().e("CrashlyticsCore", "Could not open log file: " + this.workingFile, e);
            }
        }
    }
}
