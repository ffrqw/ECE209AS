package io.fabric.sdk.android.services.concurrency;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AsyncTask<Params, Progress, Result> {
    private static final int CORE_POOL_SIZE;
    private static final int CPU_COUNT;
    private static final int MAXIMUM_POOL_SIZE = ((CPU_COUNT << 1) + 1);
    public static final Executor SERIAL_EXECUTOR = new SerialExecutor();
    public static final Executor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, 1, TimeUnit.SECONDS, poolWorkQueue, threadFactory);
    private static volatile Executor defaultExecutor = SERIAL_EXECUTOR;
    private static final InternalHandler handler = new InternalHandler();
    private static final BlockingQueue<Runnable> poolWorkQueue = new LinkedBlockingQueue(128);
    private static final ThreadFactory threadFactory = new ThreadFactory() {
        private final AtomicInteger count = new AtomicInteger(1);

        public final Thread newThread(Runnable r) {
            return new Thread(r, "AsyncTask #" + this.count.getAndIncrement());
        }
    };
    private final AtomicBoolean cancelled = new AtomicBoolean();
    private final FutureTask<Result> future = new FutureTask<Result>(this.worker) {
        protected final void done() {
            try {
                AsyncTask.access$400(AsyncTask.this, get());
            } catch (InterruptedException e) {
                Log.w("AsyncTask", e);
            } catch (ExecutionException e2) {
                throw new RuntimeException("An error occured while executing doInBackground()", e2.getCause());
            } catch (CancellationException e3) {
                AsyncTask.access$400(AsyncTask.this, null);
            }
        }
    };
    private volatile int status$13d31845 = Status.PENDING$13d31845;
    private final AtomicBoolean taskInvoked = new AtomicBoolean();
    private final WorkerRunnable<Params, Result> worker = new WorkerRunnable<Params, Result>() {
        public final Result call() throws Exception {
            AsyncTask.this.taskInvoked.set(true);
            Process.setThreadPriority(10);
            return AsyncTask.this.postResult(AsyncTask.this.doInBackground(this.params));
        }
    };

    private static abstract class WorkerRunnable<Params, Result> implements Callable<Result> {
        Params[] params;

        private WorkerRunnable() {
        }
    }

    /* renamed from: io.fabric.sdk.android.services.concurrency.AsyncTask$4 */
    static /* synthetic */ class AnonymousClass4 {
        static final /* synthetic */ int[] $SwitchMap$io$fabric$sdk$android$services$concurrency$AsyncTask$Status = new int[3];

        static {
            Status.values$5f835e81();
            try {
                int[] iArr = $SwitchMap$io$fabric$sdk$android$services$concurrency$AsyncTask$Status;
                int i = Status.RUNNING$13d31845;
                iArr[1] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr = $SwitchMap$io$fabric$sdk$android$services$concurrency$AsyncTask$Status;
                i = Status.FINISHED$13d31845;
                iArr[2] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    private static class AsyncTaskResult<Data> {
        final Data[] data;
        final AsyncTask task;

        AsyncTaskResult(AsyncTask task, Data... data) {
            this.task = task;
            this.data = data;
        }
    }

    private static class InternalHandler extends Handler {
        public InternalHandler() {
            super(Looper.getMainLooper());
        }

        public final void handleMessage(Message msg) {
            AsyncTaskResult result = msg.obj;
            switch (msg.what) {
                case 1:
                    AsyncTask.access$500(result.task, result.data[0]);
                    return;
                case 2:
                    AsyncTask.onProgressUpdate$1b4f7664();
                    return;
                default:
                    return;
            }
        }
    }

    private static class SerialExecutor implements Executor {
        Runnable active;
        final LinkedList<Runnable> tasks;

        private SerialExecutor() {
            this.tasks = new LinkedList();
        }

        public final synchronized void execute(final Runnable r) {
            this.tasks.offer(new Runnable() {
                public final void run() {
                    try {
                        r.run();
                    } finally {
                        SerialExecutor.this.scheduleNext();
                    }
                }
            });
            if (this.active == null) {
                scheduleNext();
            }
        }

        protected final synchronized void scheduleNext() {
            Runnable runnable = (Runnable) this.tasks.poll();
            this.active = runnable;
            if (runnable != null) {
                AsyncTask.THREAD_POOL_EXECUTOR.execute(this.active);
            }
        }
    }

    public enum Status {
        ;

        public static int[] values$5f835e81() {
            return (int[]) $VALUES$61a16ac0.clone();
        }

        static {
            PENDING$13d31845 = 1;
            RUNNING$13d31845 = 2;
            FINISHED$13d31845 = 3;
            $VALUES$61a16ac0 = new int[]{1, 2, 3};
        }
    }

    protected abstract Result doInBackground(Params... paramsArr);

    static {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        CPU_COUNT = availableProcessors;
        CORE_POOL_SIZE = availableProcessors + 1;
    }

    private Result postResult(Result result) {
        handler.obtainMessage(1, new AsyncTaskResult(this, result)).sendToTarget();
        return result;
    }

    public final int getStatus$47668da6() {
        return this.status$13d31845;
    }

    protected void onPreExecute() {
    }

    protected void onPostExecute(Result result) {
    }

    protected static void onProgressUpdate$1b4f7664() {
    }

    protected void onCancelled(Result result) {
    }

    public final boolean isCancelled() {
        return this.cancelled.get();
    }

    public final boolean cancel(boolean mayInterruptIfRunning) {
        this.cancelled.set(true);
        return this.future.cancel(true);
    }

    public final AsyncTask<Params, Progress, Result> executeOnExecutor(Executor exec, Params... params) {
        if (this.status$13d31845 != Status.PENDING$13d31845) {
            switch (AnonymousClass4.$SwitchMap$io$fabric$sdk$android$services$concurrency$AsyncTask$Status[this.status$13d31845 - 1]) {
                case 1:
                    throw new IllegalStateException("Cannot execute task: the task is already running.");
                case 2:
                    throw new IllegalStateException("Cannot execute task: the task has already been executed (a task can be executed only once)");
            }
        }
        this.status$13d31845 = Status.RUNNING$13d31845;
        onPreExecute();
        this.worker.params = params;
        exec.execute(this.future);
        return this;
    }

    static /* synthetic */ void access$400(AsyncTask x0, Object x1) {
        if (!x0.taskInvoked.get()) {
            x0.postResult(x1);
        }
    }

    static /* synthetic */ void access$500(AsyncTask x0, Object x1) {
        if (x0.cancelled.get()) {
            x0.onCancelled(x1);
        } else {
            x0.onPostExecute(x1);
        }
        x0.status$13d31845 = Status.FINISHED$13d31845;
    }
}
