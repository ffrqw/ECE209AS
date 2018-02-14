package com.crashlytics.android.answers;

import android.content.Context;
import android.os.Looper;
import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.Kit;
import io.fabric.sdk.android.services.common.SystemCurrentTimeProvider;
import io.fabric.sdk.android.services.events.EventsStorageListener;
import io.fabric.sdk.android.services.events.GZIPQueueFileEventStorage;
import io.fabric.sdk.android.services.network.HttpRequestFactory;
import io.fabric.sdk.android.services.settings.AnalyticsSettingsData;
import java.util.concurrent.ScheduledExecutorService;

final class AnswersEventsHandler implements EventsStorageListener {
    private final Context context;
    final ScheduledExecutorService executor;
    private final AnswersFilesManagerProvider filesManagerProvider;
    private final Kit kit;
    private final SessionMetadataCollector metadataCollector;
    private final HttpRequestFactory requestFactory;
    SessionAnalyticsManagerStrategy strategy = new DisabledSessionAnalyticsManagerStrategy();

    public AnswersEventsHandler(Kit kit, Context context, AnswersFilesManagerProvider filesManagerProvider, SessionMetadataCollector metadataCollector, HttpRequestFactory requestFactory, ScheduledExecutorService executor) {
        this.kit = kit;
        this.context = context;
        this.filesManagerProvider = filesManagerProvider;
        this.metadataCollector = metadataCollector;
        this.requestFactory = requestFactory;
        this.executor = executor;
    }

    public final void processEventAsync(Builder eventBuilder) {
        processEvent(eventBuilder, false, false);
    }

    public final void processEventAsyncAndFlush(Builder eventBuilder) {
        processEvent(eventBuilder, false, true);
    }

    public final void setAnalyticsSettingsData(final AnalyticsSettingsData analyticsSettingsData, final String protocolAndHostOverride) {
        executeAsync(new Runnable() {
            public final void run() {
                try {
                    AnswersEventsHandler.this.strategy.setAnalyticsSettingsData(analyticsSettingsData, protocolAndHostOverride);
                } catch (Exception e) {
                    Fabric.getLogger().e("Answers", "Failed to set analytics settings data", e);
                }
            }
        });
    }

    public final void disable() {
        executeAsync(new Runnable() {
            public final void run() {
                try {
                    SessionAnalyticsManagerStrategy prevStrategy = AnswersEventsHandler.this.strategy;
                    AnswersEventsHandler.this.strategy = new DisabledSessionAnalyticsManagerStrategy();
                    prevStrategy.deleteAllEvents();
                } catch (Exception e) {
                    Fabric.getLogger().e("Answers", "Failed to disable events", e);
                }
            }
        });
    }

    public final void onRollOver$552c4e01() {
        executeAsync(new Runnable() {
            public final void run() {
                try {
                    AnswersEventsHandler.this.strategy.sendEvents();
                } catch (Exception e) {
                    Fabric.getLogger().e("Answers", "Failed to send events files", e);
                }
            }
        });
    }

    public final void enable() {
        executeAsync(new Runnable() {
            public final void run() {
                try {
                    SessionEventMetadata metadata = AnswersEventsHandler.this.metadataCollector.getMetadata();
                    AnswersFilesManagerProvider access$100 = AnswersEventsHandler.this.filesManagerProvider;
                    if (Looper.myLooper() == Looper.getMainLooper()) {
                        throw new IllegalStateException("AnswersFilesManagerProvider cannot be called on the main thread");
                    }
                    SessionAnalyticsFilesManager filesManager = new SessionAnalyticsFilesManager(access$100.context, new SessionEventTransform(), new SystemCurrentTimeProvider(), new GZIPQueueFileEventStorage(access$100.context, access$100.fileStore.getFilesDir(), "session_analytics.tap", "session_analytics_to_send"));
                    filesManager.registerRollOverListener(AnswersEventsHandler.this);
                    AnswersEventsHandler.this.strategy = new EnabledSessionAnalyticsManagerStrategy(AnswersEventsHandler.this.kit, AnswersEventsHandler.this.context, AnswersEventsHandler.this.executor, filesManager, AnswersEventsHandler.this.requestFactory, metadata);
                } catch (Exception e) {
                    Fabric.getLogger().e("Answers", "Failed to enable events", e);
                }
            }
        });
    }

    public final void flushEvents() {
        executeAsync(new Runnable() {
            public final void run() {
                try {
                    AnswersEventsHandler.this.strategy.rollFileOver();
                } catch (Exception e) {
                    Fabric.getLogger().e("Answers", "Failed to flush events", e);
                }
            }
        });
    }

    final void processEvent(final Builder eventBuilder, boolean sync, final boolean flush) {
        Runnable runnable = new Runnable() {
            public final void run() {
                try {
                    AnswersEventsHandler.this.strategy.processEvent(eventBuilder);
                    if (flush) {
                        AnswersEventsHandler.this.strategy.rollFileOver();
                    }
                } catch (Exception e) {
                    Fabric.getLogger().e("Answers", "Failed to process event", e);
                }
            }
        };
        if (sync) {
            try {
                this.executor.submit(runnable).get();
                return;
            } catch (Throwable e) {
                Fabric.getLogger().e("Answers", "Failed to run events task", e);
                return;
            }
        }
        executeAsync(runnable);
    }

    private void executeAsync(Runnable runnable) {
        try {
            this.executor.submit(runnable);
        } catch (Exception e) {
            Fabric.getLogger().e("Answers", "Failed to submit events task", e);
        }
    }
}
