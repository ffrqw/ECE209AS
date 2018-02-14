package com.crashlytics.android.answers;

import android.app.Activity;
import com.crashlytics.android.answers.BackgroundManager.Listener;
import io.fabric.sdk.android.ActivityLifecycleManager;
import io.fabric.sdk.android.Fabric;
import java.util.Collections;
import java.util.Map;

final class SessionAnalyticsManager implements Listener {
    final BackgroundManager backgroundManager;
    final AnswersEventsHandler eventsHandler;
    private final long installedAt;
    final ActivityLifecycleManager lifecycleManager;
    final AnswersPreferenceManager preferenceManager;

    SessionAnalyticsManager(AnswersEventsHandler eventsHandler, ActivityLifecycleManager lifecycleManager, BackgroundManager backgroundManager, AnswersPreferenceManager preferenceManager, long installedAt) {
        this.eventsHandler = eventsHandler;
        this.lifecycleManager = lifecycleManager;
        this.backgroundManager = backgroundManager;
        this.preferenceManager = preferenceManager;
        this.installedAt = installedAt;
    }

    public final void enable() {
        Object obj;
        this.eventsHandler.enable();
        this.lifecycleManager.registerCallbacks(new AnswersLifecycleCallbacks(this, this.backgroundManager));
        this.backgroundManager.registerListener(this);
        if (this.preferenceManager.hasAnalyticsLaunched()) {
            obj = null;
        } else {
            obj = 1;
        }
        if (obj != null) {
            long j = this.installedAt;
            Fabric.getLogger().d("Answers", "Logged install");
            AnswersEventsHandler answersEventsHandler = this.eventsHandler;
            Builder builder = new Builder(Type.INSTALL);
            builder.details = Collections.singletonMap("installedAt", String.valueOf(j));
            answersEventsHandler.processEventAsyncAndFlush(builder);
            this.preferenceManager.setAnalyticsLaunched();
        }
    }

    public final void onLifecycle(Activity activity, Type type) {
        Fabric.getLogger().d("Answers", "Logged lifecycle event: " + type.name());
        AnswersEventsHandler answersEventsHandler = this.eventsHandler;
        Map singletonMap = Collections.singletonMap("activity", activity.getClass().getName());
        Builder builder = new Builder(type);
        builder.details = singletonMap;
        answersEventsHandler.processEventAsync(builder);
    }

    public final void onBackground() {
        Fabric.getLogger().d("Answers", "Flush events when app is backgrounded");
        this.eventsHandler.flushEvents();
    }
}
