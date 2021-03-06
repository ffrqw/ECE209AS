package io.fabric.sdk.android.services.concurrency;

import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class PriorityFutureTask<V> extends FutureTask<V> implements Dependency<Task>, PriorityProvider, Task {
    final Object delegate;

    public final /* bridge */ /* synthetic */ void addDependency(Object obj) {
        ((Dependency) ((PriorityProvider) getDelegate())).addDependency((Task) obj);
    }

    public PriorityFutureTask(Callable<V> callable) {
        super(callable);
        this.delegate = checkAndInitDelegate(callable);
    }

    public PriorityFutureTask(Runnable runnable, V result) {
        super(runnable, result);
        this.delegate = checkAndInitDelegate(runnable);
    }

    public int compareTo(Object another) {
        return ((PriorityProvider) getDelegate()).compareTo(another);
    }

    public final Collection<Task> getDependencies() {
        return ((Dependency) ((PriorityProvider) getDelegate())).getDependencies();
    }

    public final boolean areDependenciesMet() {
        return ((Dependency) ((PriorityProvider) getDelegate())).areDependenciesMet();
    }

    public final int getPriority$16699175() {
        return ((PriorityProvider) getDelegate()).getPriority$16699175();
    }

    public final void setFinished(boolean finished) {
        ((Task) ((PriorityProvider) getDelegate())).setFinished(finished);
    }

    public final boolean isFinished() {
        return ((Task) ((PriorityProvider) getDelegate())).isFinished();
    }

    public final void setError(Throwable throwable) {
        ((Task) ((PriorityProvider) getDelegate())).setError(throwable);
    }

    public <T extends Dependency<Task> & PriorityProvider & Task> T getDelegate() {
        return (Dependency) this.delegate;
    }

    private static <T extends Dependency<Task> & PriorityProvider & Task> T checkAndInitDelegate(Object object) {
        if (PriorityTask.isProperDelegate(object)) {
            return (Dependency) object;
        }
        return new PriorityTask();
    }
}
