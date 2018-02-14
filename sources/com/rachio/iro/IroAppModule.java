package com.rachio.iro;

import android.app.Application;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.rachio.iro.cloud.RestClient;
import com.rachio.iro.model.Event;
import com.rachio.iro.model.db.Database;
import com.rachio.iro.utils.UiThreadExecutor;
import java.util.concurrent.Executor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public final class IroAppModule {
    private final IroApplication app;
    private final Database database;
    private final PrefsWrapper prefsWrapper = new PrefsWrapper(this.app);
    private final RestClient restClient = new RestClient();
    private final Tracker tracker;

    static class DefaultThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final String namePrefix;
        private final AtomicInteger threadNumber = new AtomicInteger(1);

        DefaultThreadFactory() {
            ThreadGroup threadGroup;
            SecurityManager s = System.getSecurityManager();
            if (s != null) {
                threadGroup = s.getThreadGroup();
            } else {
                threadGroup = Thread.currentThread().getThreadGroup();
            }
            this.group = threadGroup;
            this.namePrefix = "pool-" + poolNumber.getAndIncrement() + "-thread-";
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(this.group, r, this.namePrefix + this.threadNumber.getAndIncrement(), 0);
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            if (t.getPriority() != 10) {
                t.setPriority(10);
            }
            return t;
        }
    }

    public IroAppModule(IroApplication iroApplication) {
        this.app = iroApplication;
        if (this.prefsWrapper.isUserLoggedIn()) {
            this.restClient.setUserHeaders(this.prefsWrapper.getLoggedInUserCredentials().getSessionKeys());
        }
        this.database = new Database(this.app);
        Event.cleanEvents(this.database);
        this.tracker = GoogleAnalytics.getInstance(this.app).newTracker("UA-41010688-5");
    }

    final Application provideApplication() {
        return this.app;
    }

    final Database provideDatabase() {
        return this.database;
    }

    final PrefsWrapper providePrefsWrapper() {
        return this.prefsWrapper;
    }

    final RestClient provideRestClient() {
        return this.restClient;
    }

    final Tracker provideTracker() {
        return this.tracker;
    }

    static Executor providesBackgroundThreadPool() {
        return new ThreadPoolExecutor(0, 512, 60, TimeUnit.SECONDS, new SynchronousQueue(), new DefaultThreadFactory());
    }

    static UiThreadExecutor providesUiThreadExecutor() {
        return new UiThreadExecutor();
    }
}
