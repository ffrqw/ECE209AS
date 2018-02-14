package io.fabric.sdk.android;

import android.content.Context;
import io.fabric.sdk.android.services.common.IdManager;
import io.fabric.sdk.android.services.concurrency.DependsOn;
import io.fabric.sdk.android.services.concurrency.Task;
import java.io.File;
import java.util.Collection;

public abstract class Kit<Result> implements Comparable<Kit> {
    Context context;
    final DependsOn dependsOnAnnotation = ((DependsOn) getClass().getAnnotation(DependsOn.class));
    Fabric fabric;
    IdManager idManager;
    InitializationCallback<Result> initializationCallback;
    InitializationTask<Result> initializationTask = new InitializationTask(this);

    protected abstract Result doInBackground();

    public abstract String getIdentifier();

    public abstract String getVersion();

    public /* bridge */ /* synthetic */ int compareTo(Object obj) {
        Kit kit = (Kit) obj;
        if (containsAnnotatedDependency(kit)) {
            return 1;
        }
        if (kit.containsAnnotatedDependency(this)) {
            return -1;
        }
        if (hasAnnotatedDependency() && !kit.hasAnnotatedDependency()) {
            return 1;
        }
        if (hasAnnotatedDependency() || !kit.hasAnnotatedDependency()) {
            return 0;
        }
        return -1;
    }

    final void injectParameters(Context context, Fabric fabric, InitializationCallback<Result> callback, IdManager idManager) {
        this.fabric = fabric;
        this.context = new FabricContext(context, getIdentifier(), getPath());
        this.initializationCallback = callback;
        this.idManager = idManager;
    }

    final void initialize() {
        this.initializationTask.executeOnExecutor(this.fabric.getExecutorService(), null);
    }

    protected boolean onPreExecute() {
        return true;
    }

    protected final IdManager getIdManager() {
        return this.idManager;
    }

    public final Context getContext() {
        return this.context;
    }

    public final Fabric getFabric() {
        return this.fabric;
    }

    public final String getPath() {
        return ".Fabric" + File.separator + getIdentifier();
    }

    private boolean containsAnnotatedDependency(Kit target) {
        if (!hasAnnotatedDependency()) {
            return false;
        }
        for (Class<?> dep : this.dependsOnAnnotation.value()) {
            if (dep.isAssignableFrom(target.getClass())) {
                return true;
            }
        }
        return false;
    }

    private boolean hasAnnotatedDependency() {
        return this.dependsOnAnnotation != null;
    }

    protected final Collection<Task> getDependencies() {
        return this.initializationTask.getDependencies();
    }
}
