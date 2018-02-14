package com.crashlytics.android.answers;

import android.content.Context;
import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.Kit;
import io.fabric.sdk.android.services.common.ApiKey;
import io.fabric.sdk.android.services.common.CommonUtils;
import io.fabric.sdk.android.services.concurrency.internal.DefaultRetryPolicy;
import io.fabric.sdk.android.services.concurrency.internal.ExponentialBackoff;
import io.fabric.sdk.android.services.concurrency.internal.RetryState;
import io.fabric.sdk.android.services.events.FilesSender;
import io.fabric.sdk.android.services.events.TimeBasedFileRollOverRunnable;
import io.fabric.sdk.android.services.network.HttpRequestFactory;
import io.fabric.sdk.android.services.settings.AnalyticsSettingsData;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

final class EnabledSessionAnalyticsManagerStrategy implements SessionAnalyticsManagerStrategy {
    ApiKey apiKey = new ApiKey();
    private final Context context;
    boolean customEventsEnabled = true;
    EventFilter eventFilter = new KeepAllEventFilter();
    private final ScheduledExecutorService executorService;
    private final SessionAnalyticsFilesManager filesManager;
    FilesSender filesSender;
    private final HttpRequestFactory httpRequestFactory;
    private final Kit kit;
    final SessionEventMetadata metadata;
    boolean predefinedEventsEnabled = true;
    private final AtomicReference<ScheduledFuture<?>> rolloverFutureRef = new AtomicReference();
    volatile int rolloverIntervalSeconds = -1;

    public EnabledSessionAnalyticsManagerStrategy(Kit kit, Context context, ScheduledExecutorService executor, SessionAnalyticsFilesManager filesManager, HttpRequestFactory httpRequestFactory, SessionEventMetadata metadata) {
        this.kit = kit;
        this.context = context;
        this.executorService = executor;
        this.filesManager = filesManager;
        this.httpRequestFactory = httpRequestFactory;
        this.metadata = metadata;
    }

    public final void setAnalyticsSettingsData(AnalyticsSettingsData analyticsSettingsData, String protocolAndHostOverride) {
        this.filesSender = new AnswersRetryFilesSender(new SessionAnalyticsFilesSender(this.kit, protocolAndHostOverride, analyticsSettingsData.analyticsURL, this.httpRequestFactory, this.apiKey.getValue(this.context)), new RetryManager(new RetryState(new RandomBackoff(new ExponentialBackoff(1000, 8), 0.1d), new DefaultRetryPolicy(5))));
        this.filesManager.setAnalyticsSettingsData(analyticsSettingsData);
        this.customEventsEnabled = analyticsSettingsData.trackCustomEvents;
        Fabric.getLogger().d("Answers", "Custom event tracking " + (this.customEventsEnabled ? "enabled" : "disabled"));
        this.predefinedEventsEnabled = analyticsSettingsData.trackPredefinedEvents;
        Fabric.getLogger().d("Answers", "Predefined event tracking " + (this.predefinedEventsEnabled ? "enabled" : "disabled"));
        if (analyticsSettingsData.samplingRate > 1) {
            Fabric.getLogger().d("Answers", "Event sampling enabled");
            this.eventFilter = new SamplingEventFilter(analyticsSettingsData.samplingRate);
        }
        this.rolloverIntervalSeconds = analyticsSettingsData.flushIntervalSeconds;
        scheduleTimeBasedFileRollOver(0, (long) this.rolloverIntervalSeconds);
    }

    public final void processEvent(Builder builder) {
        byte b = (byte) 0;
        SessionEvent event = new SessionEvent(this.metadata, builder.timestamp, builder.type, builder.details, null, builder.customAttributes, null, null);
        if (!this.customEventsEnabled && Type.CUSTOM.equals(event.type)) {
            Fabric.getLogger().d("Answers", "Custom events tracking disabled - skipping event: " + event);
        } else if (!this.predefinedEventsEnabled && Type.PREDEFINED.equals(event.type)) {
            Fabric.getLogger().d("Answers", "Predefined events tracking disabled - skipping event: " + event);
        } else if (this.eventFilter.skipEvent(event)) {
            Fabric.getLogger().d("Answers", "Skipping filtered event: " + event);
        } else {
            try {
                this.filesManager.writeEvent(event);
            } catch (IOException e) {
                Fabric.getLogger().e("Answers", "Failed to write event: " + event, e);
            }
            if (this.rolloverIntervalSeconds != -1) {
                b = (byte) 1;
            }
            if (b != (byte) 0) {
                scheduleTimeBasedFileRollOver((long) this.rolloverIntervalSeconds, (long) this.rolloverIntervalSeconds);
            }
        }
    }

    public final void sendEvents() {
        if (this.filesSender == null) {
            CommonUtils.logControlled(this.context, "skipping files send because we don't yet know the target endpoint");
            return;
        }
        CommonUtils.logControlled(this.context, "Sending all files");
        int filesSent = 0;
        List<File> batch = this.filesManager.getBatchOfFilesToSend();
        while (batch.size() > 0) {
            try {
                CommonUtils.logControlled(this.context, String.format(Locale.US, "attempt to send batch of %d files", new Object[]{Integer.valueOf(batch.size())}));
                boolean cleanup = this.filesSender.send(batch);
                if (cleanup) {
                    filesSent += batch.size();
                    this.filesManager.deleteSentFiles(batch);
                }
                if (!cleanup) {
                    break;
                }
                batch = this.filesManager.getBatchOfFilesToSend();
            } catch (Exception e) {
                CommonUtils.logControlledError$43da9ce8(this.context, "Failed to send batch of analytics files to server: " + e.getMessage());
            }
        }
        if (filesSent == 0) {
            this.filesManager.deleteOldestInRollOverIfOverMax();
        }
    }

    public final void cancelTimeBasedFileRollOver() {
        if (this.rolloverFutureRef.get() != null) {
            CommonUtils.logControlled(this.context, "Cancelling time-based rollover because no events are currently being generated.");
            ((ScheduledFuture) this.rolloverFutureRef.get()).cancel(false);
            this.rolloverFutureRef.set(null);
        }
    }

    public final void deleteAllEvents() {
        this.filesManager.deleteAllEventsFiles();
    }

    public final boolean rollFileOver() {
        try {
            return this.filesManager.rollFileOver();
        } catch (IOException e) {
            CommonUtils.logControlledError$43da9ce8(this.context, "Failed to roll file over.");
            return false;
        }
    }

    private void scheduleTimeBasedFileRollOver(long initialDelaySecs, long frequencySecs) {
        if (this.rolloverFutureRef.get() == null) {
            Runnable rollOverRunnable = new TimeBasedFileRollOverRunnable(this.context, this);
            CommonUtils.logControlled(this.context, "Scheduling time based file roll over every " + frequencySecs + " seconds");
            try {
                this.rolloverFutureRef.set(this.executorService.scheduleAtFixedRate(rollOverRunnable, initialDelaySecs, frequencySecs, TimeUnit.SECONDS));
            } catch (RejectedExecutionException e) {
                CommonUtils.logControlledError$43da9ce8(this.context, "Failed to schedule time based file roll over");
            }
        }
    }
}
