package io.fabric.sdk.android.services.concurrency;

import io.fabric.sdk.android.services.concurrency.AsyncTask.Status;
import java.util.Collection;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

public abstract class PriorityAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> implements Dependency<Task>, PriorityProvider, Task {
    private final PriorityTask priorityTask = new PriorityTask();

    private static class ProxyExecutor<Result> implements Executor {
        private final Executor executor;
        private final PriorityAsyncTask task;

        /* renamed from: io.fabric.sdk.android.services.concurrency.PriorityAsyncTask$ProxyExecutor$1 */
        class AnonymousClass1 extends PriorityFutureTask<Result> {
            AnonymousClass1(Runnable runnable, Object obj) {
                super(runnable, null);
            }

            public final <T extends Dependency<Task> & PriorityProvider & Task> T getDelegate() {
                return ProxyExecutor.this.task;
            }
        }

        public ProxyExecutor(Executor ex, PriorityAsyncTask task) {
            this.executor = ex;
            this.task = task;
        }

        public final void execute(Runnable command) {
            this.executor.execute(new AnonymousClass1(command, null));
        }
    }

    public final void executeOnExecutor(ExecutorService exec, Params... params) {
        super.executeOnExecutor(new ProxyExecutor(exec, this), params);
    }

    public int compareTo(Object another) {
        return Priority.compareTo(this, another);
    }

    public final void addDependency(Task task) {
        if (getStatus$47668da6() != Status.PENDING$13d31845) {
            throw new IllegalStateException("Must not add Dependency after task is running");
        }
        this.priorityTask.addDependency(task);
    }

    public final Collection<Task> getDependencies() {
        return this.priorityTask.getDependencies();
    }

    public final boolean areDependenciesMet() {
        return this.priorityTask.areDependenciesMet();
    }

    public int getPriority$16699175() {
        return this.priorityTask.getPriority$16699175();
    }

    public final void setFinished(boolean finished) {
        this.priorityTask.setFinished(finished);
    }

    public final boolean isFinished() {
        return this.priorityTask.isFinished();
    }

    public final void setError(Throwable throwable) {
        this.priorityTask.setError(throwable);
    }
}
