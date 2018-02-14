package com.squareup.picasso;

import android.net.NetworkInfo;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

final class PicassoExecutorService extends ThreadPoolExecutor {

    private static final class PicassoFutureTask extends FutureTask<BitmapHunter> implements Comparable<PicassoFutureTask> {
        private final BitmapHunter hunter;

        public final /* bridge */ /* synthetic */ int compareTo(Object obj) {
            PicassoFutureTask picassoFutureTask = (PicassoFutureTask) obj;
            int i = this.hunter.priority$159b5429;
            int i2 = picassoFutureTask.hunter.priority$159b5429;
            return i == i2 ? this.hunter.sequence - picassoFutureTask.hunter.sequence : (i2 - 1) - (i - 1);
        }

        public PicassoFutureTask(BitmapHunter hunter) {
            super(hunter, null);
            this.hunter = hunter;
        }
    }

    PicassoExecutorService() {
        super(3, 3, 0, TimeUnit.MILLISECONDS, new PriorityBlockingQueue(), new PicassoThreadFactory());
    }

    final void adjustThreadCount(NetworkInfo info) {
        if (info == null || !info.isConnectedOrConnecting()) {
            setThreadCount(3);
            return;
        }
        switch (info.getType()) {
            case 0:
                switch (info.getSubtype()) {
                    case 1:
                    case 2:
                        setThreadCount(1);
                        return;
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 12:
                        setThreadCount(2);
                        return;
                    case 13:
                    case 14:
                    case 15:
                        setThreadCount(3);
                        return;
                    default:
                        setThreadCount(3);
                        return;
                }
            case 1:
            case 6:
            case 9:
                setThreadCount(4);
                return;
            default:
                setThreadCount(3);
                return;
        }
    }

    private void setThreadCount(int threadCount) {
        setCorePoolSize(threadCount);
        setMaximumPoolSize(threadCount);
    }

    public final Future<?> submit(Runnable task) {
        PicassoFutureTask ftask = new PicassoFutureTask((BitmapHunter) task);
        execute(ftask);
        return ftask;
    }
}
