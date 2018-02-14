package android.support.v4.content;

import android.os.Binder;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;
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

abstract class ModernAsyncTask<Params, Progress, Result> {
    public static final Executor THREAD_POOL_EXECUTOR;
    private static volatile Executor sDefaultExecutor;
    private static InternalHandler sHandler;
    private static final BlockingQueue<Runnable> sPoolWorkQueue = new LinkedBlockingQueue(10);
    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public final Thread newThread(Runnable r) {
            return new Thread(r, "ModernAsyncTask #" + this.mCount.getAndIncrement());
        }
    };
    private final AtomicBoolean mCancelled = new AtomicBoolean();
    private final FutureTask<Result> mFuture = new FutureTask<Result>(this.mWorker) {
        protected final void done() {
            try {
                ModernAsyncTask.this.postResultIfNotInvoked(get());
            } catch (InterruptedException e) {
                Log.w("AsyncTask", e);
            } catch (ExecutionException e2) {
                throw new RuntimeException("An error occurred while executing doInBackground()", e2.getCause());
            } catch (CancellationException e3) {
                ModernAsyncTask.this.postResultIfNotInvoked(null);
            } catch (Throwable t) {
                RuntimeException runtimeException = new RuntimeException("An error occurred while executing doInBackground()", t);
            }
        }
    };
    private volatile int mStatus$20e42a94 = Status.PENDING$20e42a94;
    private final AtomicBoolean mTaskInvoked = new AtomicBoolean();
    private final WorkerRunnable<Params, Result> mWorker = new WorkerRunnable<Params, Result>() {
        public final Result call() throws Exception {
            ModernAsyncTask.this.mTaskInvoked.set(true);
            Object result = null;
            try {
                Process.setThreadPriority(10);
                result = ModernAsyncTask.this.doInBackground(this.mParams);
                Binder.flushPendingCommands();
                ModernAsyncTask.this.postResult(result);
                return result;
            } catch (Throwable th) {
                ModernAsyncTask.this.postResult(result);
            }
        }
    };

    private static abstract class WorkerRunnable<Params, Result> implements Callable<Result> {
        Params[] mParams;

        WorkerRunnable() {
        }
    }

    /* renamed from: android.support.v4.content.ModernAsyncTask$4 */
    static /* synthetic */ class AnonymousClass4 {
        static final /* synthetic */ int[] $SwitchMap$android$support$v4$content$ModernAsyncTask$Status = new int[3];

        static {
            Status.values$2233144e();
            try {
                int[] iArr = $SwitchMap$android$support$v4$content$ModernAsyncTask$Status;
                int i = Status.RUNNING$20e42a94;
                iArr[1] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr = $SwitchMap$android$support$v4$content$ModernAsyncTask$Status;
                i = Status.FINISHED$20e42a94;
                iArr[2] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    private static class AsyncTaskResult<Data> {
        final Data[] mData;
        final ModernAsyncTask mTask;

        AsyncTaskResult(ModernAsyncTask task, Data... data) {
            this.mTask = task;
            this.mData = data;
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
                    result.mTask.finish(result.mData[0]);
                    return;
                case 2:
                    ModernAsyncTask.onProgressUpdate$1b4f7664();
                    return;
                default:
                    return;
            }
        }
    }

    public enum Status {
        ;

        public static int[] values$2233144e() {
            return (int[]) $VALUES$2b1ab531.clone();
        }

        static {
            PENDING$20e42a94 = 1;
            RUNNING$20e42a94 = 2;
            FINISHED$20e42a94 = 3;
            $VALUES$2b1ab531 = new int[]{1, 2, 3};
        }
    }

    protected abstract Result doInBackground(Params... paramsArr);

    static {
        Executor threadPoolExecutor = new ThreadPoolExecutor(5, 128, 1, TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory);
        THREAD_POOL_EXECUTOR = threadPoolExecutor;
        sDefaultExecutor = threadPoolExecutor;
    }

    private static Handler getHandler() {
        Handler handler;
        synchronized (ModernAsyncTask.class) {
            if (sHandler == null) {
                sHandler = new InternalHandler();
            }
            handler = sHandler;
        }
        return handler;
    }

    final void postResultIfNotInvoked(Result result) {
        if (!this.mTaskInvoked.get()) {
            postResult(result);
        }
    }

    final Result postResult(Result result) {
        getHandler().obtainMessage(1, new AsyncTaskResult(this, result)).sendToTarget();
        return result;
    }

    protected void onPostExecute(Result result) {
    }

    protected static void onProgressUpdate$1b4f7664() {
    }

    protected void onCancelled(Result result) {
    }

    public final boolean isCancelled() {
        return this.mCancelled.get();
    }

    public final boolean cancel(boolean mayInterruptIfRunning) {
        this.mCancelled.set(true);
        return this.mFuture.cancel(false);
    }

    public final ModernAsyncTask<Params, Progress, Result> executeOnExecutor(Executor exec, Params... paramsArr) {
        if (this.mStatus$20e42a94 != Status.PENDING$20e42a94) {
            switch (AnonymousClass4.$SwitchMap$android$support$v4$content$ModernAsyncTask$Status[this.mStatus$20e42a94 - 1]) {
                case 1:
                    throw new IllegalStateException("Cannot execute task: the task is already running.");
                case 2:
                    throw new IllegalStateException("Cannot execute task: the task has already been executed (a task can be executed only once)");
            }
        }
        this.mStatus$20e42a94 = Status.RUNNING$20e42a94;
        this.mWorker.mParams = null;
        exec.execute(this.mFuture);
        return this;
    }

    final void finish(Result result) {
        if (this.mCancelled.get()) {
            onCancelled(result);
        } else {
            onPostExecute(result);
        }
        this.mStatus$20e42a94 = Status.FINISHED$20e42a94;
    }
}
