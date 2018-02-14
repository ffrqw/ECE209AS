package com.squareup.okhttp;

import com.squareup.okhttp.internal.Internal;
import com.squareup.okhttp.internal.RouteDatabase;
import com.squareup.okhttp.internal.Util;
import com.squareup.okhttp.internal.framed.FramedConnection;
import com.squareup.okhttp.internal.http.StreamAllocation;
import com.squareup.okhttp.internal.io.RealConnection;
import java.lang.ref.Reference;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class ConnectionPool {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static final ConnectionPool systemDefault;
    private Runnable cleanupRunnable;
    private final Deque<RealConnection> connections;
    private final Executor executor;
    private final long keepAliveDurationNs;
    private final int maxIdleConnections;
    final RouteDatabase routeDatabase;

    static {
        boolean z;
        if (ConnectionPool.class.desiredAssertionStatus()) {
            z = false;
        } else {
            z = true;
        }
        $assertionsDisabled = z;
        String keepAlive = System.getProperty("http.keepAlive");
        String keepAliveDuration = System.getProperty("http.keepAliveDuration");
        String maxIdleConnections = System.getProperty("http.maxConnections");
        long keepAliveDurationMs = keepAliveDuration != null ? Long.parseLong(keepAliveDuration) : 300000;
        if (keepAlive != null && !Boolean.parseBoolean(keepAlive)) {
            systemDefault = new ConnectionPool(0, keepAliveDurationMs);
        } else if (maxIdleConnections != null) {
            systemDefault = new ConnectionPool(Integer.parseInt(maxIdleConnections), keepAliveDurationMs);
        } else {
            systemDefault = new ConnectionPool(5, keepAliveDurationMs);
        }
    }

    private ConnectionPool(int maxIdleConnections, long keepAliveDurationMs) {
        this(maxIdleConnections, keepAliveDurationMs, TimeUnit.MILLISECONDS);
    }

    private ConnectionPool(int maxIdleConnections, long keepAliveDuration, TimeUnit timeUnit) {
        this.executor = new ThreadPoolExecutor(0, 1, 60, TimeUnit.SECONDS, new LinkedBlockingQueue(), Util.threadFactory("OkHttp ConnectionPool", true));
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
        this.maxIdleConnections = maxIdleConnections;
        this.keepAliveDurationNs = timeUnit.toNanos(keepAliveDuration);
        if (keepAliveDuration <= 0) {
            throw new IllegalArgumentException("keepAliveDuration <= 0: " + keepAliveDuration);
        }
    }

    public static ConnectionPool getDefault() {
        return systemDefault;
    }

    final RealConnection get(Address address, StreamAllocation streamAllocation) {
        if ($assertionsDisabled || Thread.holdsLock(this)) {
            for (RealConnection connection : this.connections) {
                int size = connection.allocations.size();
                FramedConnection framedConnection = connection.framedConnection;
                if (size < (framedConnection != null ? framedConnection.maxConcurrentStreams() : 1) && address.equals(connection.getRoute().address) && !connection.noNewStreams) {
                    streamAllocation.acquire(connection);
                    return connection;
                }
            }
            return null;
        }
        throw new AssertionError();
    }

    final void put(RealConnection connection) {
        if ($assertionsDisabled || Thread.holdsLock(this)) {
            if (this.connections.isEmpty()) {
                this.executor.execute(this.cleanupRunnable);
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
                    if (((Reference) list.get(i2)).get() != null) {
                        i2++;
                    } else {
                        Internal.logger.warning("A connection to " + connection.getRoute().address.url + " was leaked. Did you forget to close a response body?");
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
                Util.closeQuietly(longestIdleConnection.socket);
                return 0;
            } else if (idleConnectionCount > 0) {
                r10 = this.keepAliveDurationNs - longestIdleDurationNs;
                return r10;
            } else if (inUseConnectionCount > 0) {
                r10 = this.keepAliveDurationNs;
                return r10;
            } else {
                return -1;
            }
        }
    }
}
