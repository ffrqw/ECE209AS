package okhttp3;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import okhttp3.internal.Util;

public final class Dispatcher {
    private ExecutorService executorService;
    private int maxRequests = 64;
    private int maxRequestsPerHost = 5;
    private final Deque<AsyncCall> readyAsyncCalls = new ArrayDeque();
    private final Deque<AsyncCall> runningAsyncCalls = new ArrayDeque();
    private final Deque<RealCall> runningSyncCalls = new ArrayDeque();

    private synchronized ExecutorService executorService() {
        if (this.executorService == null) {
            this.executorService = new ThreadPoolExecutor(0, ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED, 60, TimeUnit.SECONDS, new SynchronousQueue(), Util.threadFactory("OkHttp Dispatcher", false));
        }
        return this.executorService;
    }

    private void promoteCalls() {
        if (this.runningAsyncCalls.size() < this.maxRequests && !this.readyAsyncCalls.isEmpty()) {
            Iterator<AsyncCall> i = this.readyAsyncCalls.iterator();
            while (i.hasNext()) {
                AsyncCall call = (AsyncCall) i.next();
                int i2 = 0;
                for (AsyncCall host : this.runningAsyncCalls) {
                    int i3;
                    if (host.host().equals(call.host())) {
                        i3 = i2 + 1;
                    } else {
                        i3 = i2;
                    }
                    i2 = i3;
                }
                if (i2 < this.maxRequestsPerHost) {
                    i.remove();
                    this.runningAsyncCalls.add(call);
                    executorService().execute(call);
                }
                if (this.runningAsyncCalls.size() >= this.maxRequests) {
                    return;
                }
            }
        }
    }

    final synchronized void executed(RealCall call) {
        this.runningSyncCalls.add(call);
    }

    final void finished(AsyncCall call) {
        finished(this.runningAsyncCalls, call, true);
    }

    final void finished(RealCall call) {
        finished(this.runningSyncCalls, call, false);
    }

    private <T> void finished(Deque<T> calls, T call, boolean promoteCalls) {
        synchronized (this) {
            if (calls.remove(call)) {
                if (promoteCalls) {
                    promoteCalls();
                }
                runningCallsCount();
            } else {
                throw new AssertionError("Call wasn't in-flight!");
            }
        }
    }

    private synchronized int runningCallsCount() {
        return this.runningAsyncCalls.size() + this.runningSyncCalls.size();
    }
}
