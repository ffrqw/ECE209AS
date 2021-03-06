package io.fabric.sdk.android.services.events;

import android.content.Context;
import io.fabric.sdk.android.services.common.CommonUtils;

public final class TimeBasedFileRollOverRunnable implements Runnable {
    private final Context context;
    private final FileRollOverManager fileRollOverManager;

    public TimeBasedFileRollOverRunnable(Context context, FileRollOverManager fileRollOverManager) {
        this.context = context;
        this.fileRollOverManager = fileRollOverManager;
    }

    public final void run() {
        try {
            CommonUtils.logControlled(this.context, "Performing time based file roll over.");
            if (!this.fileRollOverManager.rollFileOver()) {
                this.fileRollOverManager.cancelTimeBasedFileRollOver();
            }
        } catch (Exception e) {
            CommonUtils.logControlledError$43da9ce8(this.context, "Failed to roll over file");
        }
    }
}
