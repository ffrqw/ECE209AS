package okhttp3;

import java.lang.ref.Reference;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import okhttp3.internal.Util;
import okhttp3.internal.connection.RealConnection;
import okhttp3.internal.connection.RouteDatabase;
import okhttp3.internal.connection.StreamAllocation;
import okhttp3.internal.connection.StreamAllocation.StreamAllocationReference;
import okhttp3.internal.platform.Platform;

public final class ConnectionPool {
    static final /* synthetic */ boolean $assertionsDisabled = (!ConnectionPool.class.desiredAssertionStatus());
    private static final Executor executor = new ThreadPoolExecutor(0, ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED, 60, TimeUnit.SECONDS, new SynchronousQueue(), Util.threadFactory("OkHttp ConnectionPool", true));
    private final Runnable cleanupRunnable;
    boolean cleanupRunning;
    private final Deque<RealConnection> connections;
    private final long keepAliveDurationNs;
    private final int maxIdleConnections;
    final RouteDatabase routeDatabase;

    public ConnectionPool() {
        this(5, 5, TimeUnit.MINUTES);
    }

    private ConnectionPool(int maxIdleConnections, long keepAliveDuration, TimeUnit timeUnit) {
        this.cleanupRunnable = new Runnable() {
            public final void run() {
                while (true) {
                    long waitNanos = ConnectionPool.this.cleanup(System.nanoTime());
                    if (waitNanos != -1) {
                        if (waitNanos > 0) {
                            long waitMillis = waitNanos / 1000000;
                            waitNanos -= waitMillis * 1000000;
                            synchronized (ConnectionPool.this) {
                                try {
                                    ConnectionPool.this.wait(waitMillis, (int) waitNanos);
                                } catch (InterruptedException e) {
                                }
                            }
                        }
                    } else {
                        return;
                    }
                }
            }
        };
        this.connections = new ArrayDeque();
        this.routeDatabase = new RouteDatabase();
        this.maxIdleConnections = 5;
        this.keepAliveDurationNs = timeUnit.toNanos(5);
        if (5 <= 0) {
            throw new IllegalArgumentException("keepAliveDuration <= 0: " + 5);
        }
    }

    final RealConnection get(Address address, StreamAllocation streamAllocation) {
        if ($assertionsDisabled || Thread.holdsLock(this)) {
            for (RealConnection connection : this.connections) {
                if (connection.isEligible(address)) {
                    streamAllocation.acquire(connection);
                    return connection;
                }
            }
            return null;
        }
        throw new AssertionError();
    }

    final Socket deduplicate(Address address, StreamAllocation streamAllocation) {
        if ($assertionsDisabled || Thread.holdsLock(this)) {
            for (RealConnection connection : this.connections) {
                if (connection.isEligible(address) && connection.isMultiplexed() && connection != streamAllocation.connection()) {
                    return streamAllocation.releaseAndAcquire(connection);
                }
            }
            return null;
        }
        throw new AssertionError();
    }

    final void put(RealConnection connection) {
        if ($assertionsDisabled || Thread.holdsLock(this)) {
            if (!this.cleanupRunning) {
                this.cleanupRunning = true;
                executor.execute(this.cleanupRunnable);
            }
            this.connections.add(connection);
            return;
        }
        throw new AssertionError();
    }

    final boolean connectionBecameIdle(RealConnection connection) {
        if (!$assertionsDisabled && !Thread.holdsLock(this)) {
            throw new AssertionError();
        } else if (connection.noNewStreams || this.maxIdleConnections == 0) {
            this.connections.remove(connection);
            return true;
        } else {
            notifyAll();
            return false;
        }
    }

    final long cleanup(long now) {
        int inUseConnectionCount = 0;
        int idleConnectionCount = 0;
        RealConnection longestIdleConnection = null;
        long longestIdleDurationNs = Long.MIN_VALUE;
        synchronized (this) {
            for (RealConnection connection : this.connections) {
                int i;
                List list = connection.allocations;
                int i2 = 0;
                while (i2 < list.size()) {
                    Reference reference = (Reference) list.get(i2);
                    if (reference.get() != null) {
                        i2++;
                    } else {
                        StreamAllocationReference streamAllocationReference = (StreamAllocationReference) reference;
                        Platform.get().logCloseableLeak("A connection to " + connection.route().address.url + " was leaked. Did you forget to close a response body?", streamAllocationReference.callStackTrace);
                        list.remove(i2);
                        connection.noNewStreams = true;
                        if (list.isEmpty()) {
                            connection.idleAtNanos = now - this.keepAliveDurationNs;
                            i = 0;
                            break;
                        }
                    }
                }
                i = list.size();
                if (i > 0) {
                    inUseConnectionCount++;
                } else {
                    idleConnectionCount++;
                    long idleDurationNs = now - connection.idleAtNanos;
                    if (idleDurationNs > longestIdleDurationNs) {
                        longestIdleDurationNs = idleDurationNs;
                        longestIdleConnection = connection;
                    }
                }
            }
            if (longestIdleDurationNs >= this.keepAliveDurationNs || idleConnectionCount > this.maxIdleConnections) {
                this.connections.remove(longestIdleConnection);
                Util.closeQuietly(longestIdleConnection.socket());
                return 0;
            } else if (idleConnectionCount > 0) {
                r10 = this.keepAliveDurationNs - longestIdleDurationNs;
                return r10;
            } else if (inUseConnectionCount > 0) {
                r10 = this.keepAliveDurationNs;
                return r10;
            } else {
                this.cleanupRunning = false;
                return -1;
            }
        }
    }
}
