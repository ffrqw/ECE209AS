package rx.plugins;

import java.util.concurrent.atomic.AtomicReference;

public final class RxJavaPlugins {
    static final RxJavaErrorHandler DEFAULT_ERROR_HANDLER = new RxJavaErrorHandler() {
    };
    private static final RxJavaPlugins INSTANCE = new RxJavaPlugins();
    private final AtomicReference<RxJavaErrorHandler> errorHandler = new AtomicReference();
    private final AtomicReference<RxJavaObservableExecutionHook> observableExecutionHook = new AtomicReference();
    private final AtomicReference<RxJavaSchedulersHook> schedulersHook = new AtomicReference();

    public static RxJavaPlugins getInstance() {
        return INSTANCE;
    }

    RxJavaPlugins() {
    }

    public final RxJavaErrorHandler getErrorHandler() {
        if (this.errorHandler.get() == null) {
            Object impl = getPluginImplementationViaProperty(RxJavaErrorHandler.class);
            if (impl == null) {
                this.errorHandler.compareAndSet(null, DEFAULT_ERROR_HANDLER);
            } else {
                this.errorHandler.compareAndSet(null, (RxJavaErrorHandler) impl);
            }
        }
        return (RxJavaErrorHandler) this.errorHandler.get();
    }

    public final RxJavaObservableExecutionHook getObservableExecutionHook() {
        if (this.observableExecutionHook.get() == null) {
            Object impl = getPluginImplementationViaProperty(RxJavaObservableExecutionHook.class);
            if (impl == null) {
                this.observableExecutionHook.compareAndSet(null, RxJavaObservableExecutionHookDefault.getInstance());
            } else {
                this.observableExecutionHook.compareAndSet(null, (RxJavaObservableExecutionHook) impl);
            }
        }
        return (RxJavaObservableExecutionHook) this.observableExecutionHook.get();
    }

    private static Object getPluginImplementationViaProperty(Class<?> pluginClass) {
        String classSimpleName = pluginClass.getSimpleName();
        String implementingClass = System.getProperty("rxjava.plugin." + classSimpleName + ".implementation");
        if (implementingClass == null) {
            return null;
        }
        try {
            return Class.forName(implementingClass).asSubclass(pluginClass).newInstance();
        } catch (ClassCastException e) {
            throw new RuntimeException(classSimpleName + " implementation is not an instance of " + classSimpleName + ": " + implementingClass);
        } catch (ClassNotFoundException e2) {
            throw new RuntimeException(classSimpleName + " implementation class not found: " + implementingClass, e2);
        } catch (InstantiationException e3) {
            throw new RuntimeException(classSimpleName + " implementation not able to be instantiated: " + implementingClass, e3);
        } catch (IllegalAccessException e4) {
            throw new RuntimeException(classSimpleName + " implementation not able to be accessed: " + implementingClass, e4);
        }
    }

    public final RxJavaSchedulersHook getSchedulersHook() {
        if (this.schedulersHook.get() == null) {
            Object impl = getPluginImplementationViaProperty(RxJavaSchedulersHook.class);
            if (impl == null) {
                this.schedulersHook.compareAndSet(null, RxJavaSchedulersHook.getDefaultInstance());
            } else {
                this.schedulersHook.compareAndSet(null, (RxJavaSchedulersHook) impl);
            }
        }
        return (RxJavaSchedulersHook) this.schedulersHook.get();
    }
}
