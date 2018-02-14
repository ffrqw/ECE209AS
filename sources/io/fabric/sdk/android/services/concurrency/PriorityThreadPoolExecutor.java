package io.fabric.sdk.android.services.concurrency;

import android.annotation.TargetApi;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class PriorityThreadPoolExecutor extends ThreadPoolExecutor {
    private static final int CORE_POOL_SIZE;
    private static final int CPU_COUNT;
    private static final int MAXIMUM_POOL_SIZE = ((CPU_COUNT << 1) + 1);

    protected static final class PriorityThreadFactory implements ThreadFactory {
        private final int threadPriority = 10;

        public PriorityThreadFactory(int threadPriority) {
        }

        public final Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setPriority(this.threadPriority);
            thread.setName("Queue");
            return thread;
        }
    }

    static {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        CPU_COUNT = availableProcessors;
        CORE_POOL_SIZE = availableProcessors + 1;
    }

    private <T extends Runnable & Dependency & Task & PriorityProvider> PriorityThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, DependencyPriorityBlockingQueue<T> workQueue, ThreadFactory factory) {
        super(corePoolSize, maximumPoolSize, 1, unit, workQueue, factory);
        prestartAllCoreThreads();
    }

    public static PriorityThreadPoolExecutor create() {
        return new PriorityThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, 1, TimeUnit.SECONDS, new DependencyPriorityBlockingQueue(), new PriorityThreadFactory(10));
    }

    protected final <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
        return new PriorityFutureTask(runnable, value);
    }

    protected final <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        return new PriorityFutureTask(callable);
    }

    @TargetApi(9)
    public final void execute(Runnable command) {
        if (PriorityTask.isProperDelegate(command)) {
            super.execute(command);
        } else {
            super.execute(newTaskFor(command, null));
        }
    }

    protected final void afterExecute(Runnable runnable, Throwable throwable) {
        Task task = (Task) runnable;
        task.setFinished(true);
        task.setError(throwable);
        ((DependencyPriorityBlockingQueue) super.getQueue()).recycleBlockedQueue();
        super.afterExecute(runnable, throwable);
    }

    public final /* bridge */ /* synthetic */ BlockingQueue getQueue() {
        return (DependencyPriorityBlockingQueue) super.getQueue();
    }
}
