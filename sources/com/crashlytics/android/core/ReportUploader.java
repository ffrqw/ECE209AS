package com.crashlytics.android.core;

import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.services.common.BackgroundPriorityRunnable;
import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

final class ReportUploader {
    static final Map<String, String> HEADER_INVALID_CLS_FILE = Collections.singletonMap("X-CRASHLYTICS-INVALID-SESSION", "1");
    private static final short[] RETRY_INTERVALS = new short[]{(short) 10, (short) 20, (short) 30, (short) 60, (short) 120, (short) 300};
    private final String apiKey;
    private final CreateReportSpiCall createReportCall;
    private final Object fileAccessLock = new Object();
    private final HandlingExceptionCheck handlingExceptionCheck;
    private final ReportFilesProvider reportFilesProvider;
    private Thread uploadThread;

    interface SendCheck {
        boolean canSendReports();
    }

    interface ReportFilesProvider {
        File[] getCompleteSessionFiles();

        File[] getInvalidSessionFiles();
    }

    interface HandlingExceptionCheck {
        boolean isHandlingException();
    }

    static final class AlwaysSendCheck implements SendCheck {
        AlwaysSendCheck() {
        }

        public final boolean canSendReports() {
            return true;
        }
    }

    private class Worker extends BackgroundPriorityRunnable {
        private final float delay;
        private final SendCheck sendCheck;

        Worker(float delay, SendCheck sendCheck) {
            this.delay = delay;
            this.sendCheck = sendCheck;
        }

        public final void onRun() {
            try {
                Fabric.getLogger().d("CrashlyticsCore", "Starting report processing in " + this.delay + " second(s)...");
                if (this.delay > 0.0f) {
                    try {
                        Thread.sleep((long) (this.delay * 1000.0f));
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                List<Report> findReports = ReportUploader.this.findReports();
                if (!ReportUploader.this.handlingExceptionCheck.isHandlingException()) {
                    if (findReports.isEmpty() || this.sendCheck.canSendReports()) {
                        List list = findReports;
                        int i = 0;
                        while (!r1.isEmpty() && !ReportUploader.this.handlingExceptionCheck.isHandlingException()) {
                            Fabric.getLogger().d("CrashlyticsCore", "Attempting to send " + r1.size() + " report(s)");
                            for (Report forceUpload : r1) {
                                ReportUploader.this.forceUpload(forceUpload);
                            }
                            List findReports2 = ReportUploader.this.findReports();
                            if (findReports2.isEmpty()) {
                                list = findReports2;
                            } else {
                                int i2 = i + 1;
                                long j = (long) ReportUploader.RETRY_INTERVALS[Math.min(i, ReportUploader.RETRY_INTERVALS.length - 1)];
                                Fabric.getLogger().d("CrashlyticsCore", "Report submisson: scheduling delayed retry in " + j + " seconds");
                                try {
                                    Thread.sleep(j * 1000);
                                    i = i2;
                                    list = findReports2;
                                } catch (InterruptedException e2) {
                                    Thread.currentThread().interrupt();
                                }
                            }
                        }
                    } else {
                        Fabric.getLogger().d("CrashlyticsCore", "User declined to send. Removing " + findReports.size() + " Report(s).");
                        for (Report forceUpload2 : findReports) {
                            forceUpload2.remove();
                        }
                    }
                }
            } catch (Exception e3) {
                Fabric.getLogger().e("CrashlyticsCore", "An unexpected error occurred while attempting to upload crash reports.", e3);
            }
            ReportUploader.this.uploadThread = null;
        }
    }

    public ReportUploader(String apiKey, CreateReportSpiCall createReportCall, ReportFilesProvider reportFilesProvider, HandlingExceptionCheck handlingExceptionCheck) {
        if (createReportCall == null) {
            throw new IllegalArgumentException("createReportCall must not be null.");
        }
        this.createReportCall = createReportCall;
        this.apiKey = apiKey;
        this.reportFilesProvider = reportFilesProvider;
        this.handlingExceptionCheck = handlingExceptionCheck;
    }

    public final synchronized void uploadReports(float delay, SendCheck sendCheck) {
        if (this.uploadThread != null) {
            Fabric.getLogger().d("CrashlyticsCore", "Report upload has already been started.");
        } else {
            this.uploadThread = new Thread(new Worker(delay, sendCheck), "Crashlytics Report Uploader");
            this.uploadThread.start();
        }
    }

    final boolean forceUpload(Report report) {
        boolean removed = false;
        synchronized (this.fileAccessLock) {
            try {
                boolean sent = this.createReportCall.invoke(new CreateReportRequest(this.apiKey, report));
                Fabric.getLogger().i("CrashlyticsCore", "Crashlytics report upload " + (sent ? "complete: " : "FAILED: ") + report.getIdentifier());
                if (sent) {
                    report.remove();
                    removed = true;
                }
            } catch (Exception e) {
                Fabric.getLogger().e("CrashlyticsCore", "Error occurred sending report " + report, e);
            }
        }
        return removed;
    }

    final List<Report> findReports() {
        Fabric.getLogger().d("CrashlyticsCore", "Checking for crash reports...");
        synchronized (this.fileAccessLock) {
            File[] clsFiles = this.reportFilesProvider.getCompleteSessionFiles();
            File[] invalidClsFiles = this.reportFilesProvider.getInvalidSessionFiles();
        }
        List<Report> reports = new LinkedList();
        if (clsFiles != null) {
            for (File file : clsFiles) {
                Fabric.getLogger().d("CrashlyticsCore", "Found crash report " + file.getPath());
                reports.add(new SessionReport(file));
            }
        }
        Map<String, List<File>> invalidSessionFiles = new HashMap();
        if (invalidClsFiles != null) {
            for (File invalidFile : invalidClsFiles) {
                String sessionId = CrashlyticsController.getSessionIdFromSessionFile(invalidFile);
                if (!invalidSessionFiles.containsKey(sessionId)) {
                    invalidSessionFiles.put(sessionId, new LinkedList());
                }
                ((List) invalidSessionFiles.get(sessionId)).add(invalidFile);
            }
        }
        for (String key : invalidSessionFiles.keySet()) {
            Fabric.getLogger().d("CrashlyticsCore", "Found invalid session: " + key);
            List<File> invalidFiles = (List) invalidSessionFiles.get(key);
            reports.add(new InvalidSessionReport(key, (File[]) invalidFiles.toArray(new File[invalidFiles.size()])));
        }
        if (reports.isEmpty()) {
            Fabric.getLogger().d("CrashlyticsCore", "No reports found.");
        }
        return reports;
    }
}
