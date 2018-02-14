package io.fabric.sdk.android;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import io.fabric.sdk.android.ActivityLifecycleManager.Callbacks;
import io.fabric.sdk.android.services.common.IdManager;
import io.fabric.sdk.android.services.concurrency.DependsOn;
import io.fabric.sdk.android.services.concurrency.PriorityThreadPoolExecutor;
import io.fabric.sdk.android.services.concurrency.UnmetDependencyException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

public class Fabric {
    static final Logger DEFAULT_LOGGER = new DefaultLogger();
    static volatile Fabric singleton;
    private WeakReference<Activity> activity;
    private ActivityLifecycleManager activityLifecycleManager;
    private final Context context;
    final boolean debuggable;
    private final ExecutorService executorService;
    private final IdManager idManager;
    private final InitializationCallback<Fabric> initializationCallback;
    private AtomicBoolean initialized = new AtomicBoolean(false);
    private final InitializationCallback<?> kitInitializationCallback;
    private final Map<Class<? extends Kit>, Kit> kits;
    final Logger logger;
    private final Handler mainHandler;

    public static class Builder {
        private String appIdentifier;
        private final Context context;
        private Handler handler;
        private InitializationCallback<Fabric> initializationCallback;
        private Kit[] kits;
        private Logger logger;
        private PriorityThreadPoolExecutor threadPoolExecutor;

        public Builder(Context context) {
            if (context == null) {
                throw new IllegalArgumentException("Context must not be null.");
            }
            this.context = context;
        }

        public final Builder kits(Kit... kits) {
            if (this.kits != null) {
                throw new IllegalStateException("Kits already set.");
            }
            this.kits = kits;
            return this;
        }

        public final Fabric build() {
            Map<Class<? extends Kit>, Kit> kitMap;
            if (this.threadPoolExecutor == null) {
                this.threadPoolExecutor = PriorityThreadPoolExecutor.create();
            }
            if (this.handler == null) {
                this.handler = new Handler(Looper.getMainLooper());
            }
            if (this.logger == null) {
                this.logger = new DefaultLogger();
            }
            if (this.appIdentifier == null) {
                this.appIdentifier = this.context.getPackageName();
            }
            if (this.initializationCallback == null) {
                this.initializationCallback = InitializationCallback.EMPTY;
            }
            if (this.kits == null) {
                kitMap = new HashMap();
            } else {
                kitMap = Fabric.access$000(Arrays.asList(this.kits));
            }
            Context appContext = this.context.getApplicationContext();
            return new Fabric(appContext, kitMap, this.threadPoolExecutor, this.handler, this.logger, false, this.initializationCallback, new IdManager(appContext, this.appIdentifier, null, kitMap.values()), Fabric.access$100(this.context));
        }
    }

    Fabric(Context context, Map<Class<? extends Kit>, Kit> kits, PriorityThreadPoolExecutor threadPoolExecutor, Handler mainHandler, Logger logger, boolean debuggable, InitializationCallback callback, IdManager idManager, Activity rootActivity) {
        this.context = context;
        this.kits = kits;
        this.executorService = threadPoolExecutor;
        this.mainHandler = mainHandler;
        this.logger = logger;
        this.debuggable = debuggable;
        this.initializationCallback = callback;
        final int size = kits.size();
        this.kitInitializationCallback = new InitializationCallback() {
            final CountDownLatch kitInitializedLatch = new CountDownLatch(size);

            public final void success$5d527811() {
                this.kitInitializedLatch.countDown();
                if (this.kitInitializedLatch.getCount() == 0) {
                    Fabric.this.initialized.set(true);
                    Fabric.this.initializationCallback.success$5d527811();
                }
            }

            public final void failure(Exception exception) {
                Fabric.this.initializationCallback.failure(exception);
            }
        };
        this.idManager = idManager;
        setCurrentActivity(rootActivity);
    }

    public static Fabric with(Context context, Kit... kits) {
        if (singleton == null) {
            synchronized (Fabric.class) {
                if (singleton == null) {
                    StringBuilder append;
                    Fabric build = new Builder(context).kits(kits).build();
                    singleton = build;
                    build.activityLifecycleManager = new ActivityLifecycleManager(build.context);
                    build.activityLifecycleManager.registerCallbacks(new Callbacks() {
                        public final void onActivityCreated$9bb446d(Activity activity) {
                            Fabric.this.setCurrentActivity(activity);
                        }

                        public final void onActivityStarted(Activity activity) {
                            Fabric.this.setCurrentActivity(activity);
                        }

                        public final void onActivityResumed(Activity activity) {
                            Fabric.this.setCurrentActivity(activity);
                        }
                    });
                    Context context2 = build.context;
                    Future submit = build.executorService.submit(new FabricKitsFinder(context2.getPackageCodePath()));
                    Collection values = build.kits.values();
                    Onboarding onboarding = new Onboarding(submit, values);
                    List<Kit> arrayList = new ArrayList(values);
                    Collections.sort(arrayList);
                    onboarding.injectParameters(context2, build, InitializationCallback.EMPTY, build.idManager);
                    for (Kit injectParameters : arrayList) {
                        injectParameters.injectParameters(context2, build, build.kitInitializationCallback, build.idManager);
                    }
                    onboarding.initialize();
                    if (getLogger().isLoggable$505cff18(3)) {
                        append = new StringBuilder("Initializing io.fabric.sdk.android:fabric").append(" [Version: 1.3.17.dev").append("], with the following kits:\n");
                    } else {
                        append = null;
                    }
                    for (Kit injectParameters2 : arrayList) {
                        injectParameters2.initializationTask.addDependency(onboarding.initializationTask);
                        addAnnotatedDependencies(build.kits, injectParameters2);
                        injectParameters2.initialize();
                        if (append != null) {
                            append.append(injectParameters2.getIdentifier()).append(" [Version: ").append(injectParameters2.getVersion()).append("]\n");
                        }
                    }
                    if (append != null) {
                        getLogger().d("Fabric", append.toString());
                    }
                }
            }
        }
        return singleton;
    }

    public final Fabric setCurrentActivity(Activity activity) {
        this.activity = new WeakReference(activity);
        return this;
    }

    public final Activity getCurrentActivity() {
        if (this.activity != null) {
            return (Activity) this.activity.get();
        }
        return null;
    }

    private static void addAnnotatedDependencies(Map<Class<? extends Kit>, Kit> kits, Kit dependentKit) {
        DependsOn dependsOn = dependentKit.dependsOnAnnotation;
        if (dependsOn != null) {
            for (Class<?> dependency : dependsOn.value()) {
                if (dependency.isInterface()) {
                    for (Kit kit : kits.values()) {
                        if (dependency.isAssignableFrom(kit.getClass())) {
                            dependentKit.initializationTask.addDependency(kit.initializationTask);
                        }
                    }
                } else if (((Kit) kits.get(dependency)) == null) {
                    throw new UnmetDependencyException("Referenced Kit was null, does the kit exist?");
                } else {
                    dependentKit.initializationTask.addDependency(((Kit) kits.get(dependency)).initializationTask);
                }
            }
        }
    }

    public final ActivityLifecycleManager getActivityLifecycleManager() {
        return this.activityLifecycleManager;
    }

    public final ExecutorService getExecutorService() {
        return this.executorService;
    }

    public static Logger getLogger() {
        if (singleton == null) {
            return DEFAULT_LOGGER;
        }
        return singleton.logger;
    }

    public static boolean isDebuggable() {
        if (singleton == null) {
            return false;
        }
        return singleton.debuggable;
    }

    private static void addToKitMap(Map<Class<? extends Kit>, Kit> map, Collection<? extends Kit> kits) {
        for (Kit kit : kits) {
            map.put(kit.getClass(), kit);
            if (kit instanceof KitGroup) {
                addToKitMap(map, ((KitGroup) kit).getKits());
            }
        }
    }

    public static <T extends Kit> T getKit(Class<T> cls) {
        if (singleton != null) {
            return (Kit) singleton.kits.get(cls);
        }
        throw new IllegalStateException("Must Initialize Fabric before using singleton()");
    }

    static /* synthetic */ Map access$000(Collection x0) {
        Map hashMap = new HashMap(x0.size());
        addToKitMap(hashMap, x0);
        return hashMap;
    }

    static /* synthetic */ Activity access$100(Context x0) {
        if (x0 instanceof Activity) {
            return (Activity) x0;
        }
        return null;
    }
}
