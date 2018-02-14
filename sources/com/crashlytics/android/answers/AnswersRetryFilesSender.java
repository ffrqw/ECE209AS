package com.crashlytics.android.answers;

import io.fabric.sdk.android.services.events.FilesSender;
import java.io.File;
import java.util.List;

final class AnswersRetryFilesSender implements FilesSender {
    private final SessionAnalyticsFilesSender filesSender;
    private final RetryManager retryManager;

    AnswersRetryFilesSender(SessionAnalyticsFilesSender filesSender, RetryManager retryManager) {
        this.filesSender = filesSender;
        this.retryManager = retryManager;
    }

    public final boolean send(List<File> files) {
        long currentNanoTime = System.nanoTime();
        if (!this.retryManager.canRetry(currentNanoTime)) {
            return false;
        }
        if (this.filesSender.send(files)) {
            this.retryManager.reset();
            return true;
        }
        this.retryManager.recordRetry(currentNanoTime);
        return false;
    }
}
