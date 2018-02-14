package okhttp3.internal.connection;

import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.net.Socket;
import okhttp3.Address;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.Route;
import okhttp3.internal.Internal;
import okhttp3.internal.Util;
import okhttp3.internal.http.HttpCodec;
import okhttp3.internal.http2.ConnectionShutdownException;
import okhttp3.internal.http2.ErrorCode;
import okhttp3.internal.http2.StreamResetException;

public final class StreamAllocation {
    static final /* synthetic */ boolean $assertionsDisabled = (!StreamAllocation.class.desiredAssertionStatus());
    public final Address address;
    private final Object callStackTrace;
    private HttpCodec codec;
    private RealConnection connection;
    private final ConnectionPool connectionPool;
    private int refusedStreamCount;
    private boolean released;
    private Route route;
    private final RouteSelector routeSelector;

    public static final class StreamAllocationReference extends WeakReference<StreamAllocation> {
        public final Object callStackTrace;

        StreamAllocationReference(StreamAllocation referent, Object callStackTrace) {
            super(referent);
            this.callStackTrace = callStackTrace;
        }
    }

    public StreamAllocation(ConnectionPool connectionPool, Address address, Object callStackTrace) {
        this.connectionPool = connectionPool;
        this.address = address;
        this.routeSelector = new RouteSelector(address, routeDatabase());
        this.callStackTrace = callStackTrace;
    }

    public final HttpCodec newStream(OkHttpClient client, boolean doExtensiveHealthChecks) {
        try {
            HttpCodec resultCodec = findHealthyConnection(client.connectTimeoutMillis(), client.readTimeoutMillis(), client.writeTimeoutMillis(), client.retryOnConnectionFailure(), doExtensiveHealthChecks).newCodec(client, this);
            synchronized (this.connectionPool) {
                this.codec = resultCodec;
            }
            return resultCodec;
        } catch (IOException e) {
            throw new RouteException(e);
        }
    }

    private RealConnection findHealthyConnection(int connectTimeout, int readTimeout, int writeTimeout, boolean connectionRetryEnabled, boolean doExtensiveHealthChecks) throws IOException {
        RealConnection candidate;
        while (true) {
            candidate = findConnection(connectTimeout, readTimeout, writeTimeout, connectionRetryEnabled);
            synchronized (this.connectionPool) {
                if (candidate.successCount != 0) {
                    if (candidate.isHealthy(doExtensiveHealthChecks)) {
                        break;
                    }
                    noNewStreams();
                } else {
                    break;
                }
            }
        }
        return candidate;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private okhttp3.internal.connection.RealConnection findConnection(int r9, int r10, int r11, boolean r12) throws java.io.IOException {
        /*
        r8 = this;
        r5 = r8.connectionPool;
        monitor-enter(r5);
        r4 = r8.released;	 Catch:{ all -> 0x000f }
        if (r4 == 0) goto L_0x0012;
    L_0x0007:
        r4 = new java.lang.IllegalStateException;	 Catch:{ all -> 0x000f }
        r6 = "released";
        r4.<init>(r6);	 Catch:{ all -> 0x000f }
        throw r4;	 Catch:{ all -> 0x000f }
    L_0x000f:
        r4 = move-exception;
        monitor-exit(r5);	 Catch:{ all -> 0x000f }
        throw r4;
    L_0x0012:
        r4 = r8.codec;	 Catch:{ all -> 0x000f }
        if (r4 == 0) goto L_0x001e;
    L_0x0016:
        r4 = new java.lang.IllegalStateException;	 Catch:{ all -> 0x000f }
        r6 = "codec != null";
        r4.<init>(r6);	 Catch:{ all -> 0x000f }
        throw r4;	 Catch:{ all -> 0x000f }
    L_0x001e:
        r0 = r8.connection;	 Catch:{ all -> 0x000f }
        if (r0 == 0) goto L_0x0028;
    L_0x0022:
        r4 = r0.noNewStreams;	 Catch:{ all -> 0x000f }
        if (r4 != 0) goto L_0x0028;
    L_0x0026:
        monitor-exit(r5);	 Catch:{ all -> 0x000f }
    L_0x0027:
        return r0;
    L_0x0028:
        r4 = okhttp3.internal.Internal.instance;	 Catch:{ all -> 0x000f }
        r6 = r8.connectionPool;	 Catch:{ all -> 0x000f }
        r7 = r8.address;	 Catch:{ all -> 0x000f }
        r4.get(r6, r7, r8);	 Catch:{ all -> 0x000f }
        r4 = r8.connection;	 Catch:{ all -> 0x000f }
        if (r4 == 0) goto L_0x0039;
    L_0x0035:
        r0 = r8.connection;	 Catch:{ all -> 0x000f }
        monitor-exit(r5);	 Catch:{ all -> 0x000f }
        goto L_0x0027;
    L_0x0039:
        r2 = r8.route;	 Catch:{ all -> 0x000f }
        monitor-exit(r5);	 Catch:{ all -> 0x000f }
        if (r2 != 0) goto L_0x0044;
    L_0x003e:
        r4 = r8.routeSelector;
        r2 = r4.next();
    L_0x0044:
        r5 = r8.connectionPool;
        monitor-enter(r5);
        r8.route = r2;	 Catch:{ all -> 0x0088 }
        r4 = 0;
        r8.refusedStreamCount = r4;	 Catch:{ all -> 0x0088 }
        r1 = new okhttp3.internal.connection.RealConnection;	 Catch:{ all -> 0x0088 }
        r4 = r8.connectionPool;	 Catch:{ all -> 0x0088 }
        r1.<init>(r4, r2);	 Catch:{ all -> 0x0088 }
        r8.acquire(r1);	 Catch:{ all -> 0x0088 }
        monitor-exit(r5);	 Catch:{ all -> 0x0088 }
        r1.connect(r9, r10, r11, r12);
        r4 = r8.routeDatabase();
        r5 = r1.route();
        r4.connected(r5);
        r3 = 0;
        r5 = r8.connectionPool;
        monitor-enter(r5);
        r4 = okhttp3.internal.Internal.instance;	 Catch:{ all -> 0x008b }
        r6 = r8.connectionPool;	 Catch:{ all -> 0x008b }
        r4.put(r6, r1);	 Catch:{ all -> 0x008b }
        r4 = r1.isMultiplexed();	 Catch:{ all -> 0x008b }
        if (r4 == 0) goto L_0x0082;
    L_0x0076:
        r4 = okhttp3.internal.Internal.instance;	 Catch:{ all -> 0x008b }
        r6 = r8.connectionPool;	 Catch:{ all -> 0x008b }
        r7 = r8.address;	 Catch:{ all -> 0x008b }
        r3 = r4.deduplicate(r6, r7, r8);	 Catch:{ all -> 0x008b }
        r1 = r8.connection;	 Catch:{ all -> 0x008b }
    L_0x0082:
        monitor-exit(r5);	 Catch:{ all -> 0x008b }
        okhttp3.internal.Util.closeQuietly(r3);
        r0 = r1;
        goto L_0x0027;
    L_0x0088:
        r4 = move-exception;
        monitor-exit(r5);	 Catch:{ all -> 0x0088 }
        throw r4;
    L_0x008b:
        r4 = move-exception;
        monitor-exit(r5);	 Catch:{ all -> 0x008b }
        throw r4;
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.connection.StreamAllocation.findConnection(int, int, int, boolean):okhttp3.internal.connection.RealConnection");
    }

    public final void streamFinished(boolean noNewStreams, HttpCodec codec) {
        Socket socket;
        synchronized (this.connectionPool) {
            if (codec != null) {
                if (codec == this.codec) {
                    if (!noNewStreams) {
                        RealConnection realConnection = this.connection;
                        realConnection.successCount++;
                    }
                    socket = deallocate(noNewStreams, false, true);
                }
            }
            throw new IllegalStateException("expected " + this.codec + " but was " + codec);
        }
        Util.closeQuietly(socket);
    }

    public final HttpCodec codec() {
        HttpCodec httpCodec;
        synchronized (this.connectionPool) {
            httpCodec = this.codec;
        }
        return httpCodec;
    }

    private RouteDatabase routeDatabase() {
        return Internal.instance.routeDatabase(this.connectionPool);
    }

    public final synchronized RealConnection connection() {
        return this.connection;
    }

    public final void release() {
        Socket socket;
        synchronized (this.connectionPool) {
            socket = deallocate(false, true, false);
        }
        Util.closeQuietly(socket);
    }

    public final void noNewStreams() {
        Socket socket;
        synchronized (this.connectionPool) {
            socket = deallocate(true, false, false);
        }
        Util.closeQuietly(socket);
    }

    private Socket deallocate(boolean noNewStreams, boolean released, boolean streamFinished) {
        if ($assertionsDisabled || Thread.holdsLock(this.connectionPool)) {
            if (streamFinished) {
                this.codec = null;
            }
            if (released) {
                this.released = true;
            }
            Socket socket = null;
            if (this.connection != null) {
                if (noNewStreams) {
                    this.connection.noNewStreams = true;
                }
                if (this.codec == null && (this.released || this.connection.noNewStreams)) {
                    release(this.connection);
                    if (this.connection.allocations.isEmpty()) {
                        this.connection.idleAtNanos = System.nanoTime();
                        if (Internal.instance.connectionBecameIdle(this.connectionPool, this.connection)) {
                            socket = this.connection.socket();
                        }
                    }
                    this.connection = null;
                }
            }
            return socket;
        }
        throw new AssertionError();
    }

    public final void streamFailed(IOException e) {
        Socket socket;
        boolean noNewStreams = false;
        synchronized (this.connectionPool) {
            if (e instanceof StreamResetException) {
                StreamResetException streamResetException = (StreamResetException) e;
                if (streamResetException.errorCode == ErrorCode.REFUSED_STREAM) {
                    this.refusedStreamCount++;
                }
                if (streamResetException.errorCode != ErrorCode.REFUSED_STREAM || this.refusedStreamCount > 1) {
                    noNewStreams = true;
                    this.route = null;
                }
            } else if (this.connection != null && (!this.connection.isMultiplexed() || (e instanceof ConnectionShutdownException))) {
                noNewStreams = true;
                if (this.connection.successCount == 0) {
                    if (!(this.route == null || e == null)) {
                        this.routeSelector.connectFailed(this.route, e);
                    }
                    this.route = null;
                }
            }
            socket = deallocate(noNewStreams, false, true);
        }
        Util.closeQuietly(socket);
    }

    public final void acquire(RealConnection connection) {
        if (!$assertionsDisabled && !Thread.holdsLock(this.connectionPool)) {
            throw new AssertionError();
        } else if (this.connection != null) {
            throw new IllegalStateException();
        } else {
            this.connection = connection;
            connection.allocations.add(new StreamAllocationReference(this, this.callStackTrace));
        }
    }

    private void release(RealConnection connection) {
        int size = connection.allocations.size();
        for (int i = 0; i < size; i++) {
            if (((Reference) connection.allocations.get(i)).get() == this) {
                connection.allocations.remove(i);
                return;
            }
        }
        throw new IllegalStateException();
    }

    public final Socket releaseAndAcquire(RealConnection newConnection) {
        if (!$assertionsDisabled && !Thread.holdsLock(this.connectionPool)) {
            throw new AssertionError();
        } else if (this.codec == null && this.connection.allocations.size() == 1) {
            Reference<StreamAllocation> onlyAllocation = (Reference) this.connection.allocations.get(0);
            Socket socket = deallocate(true, false, false);
            this.connection = newConnection;
            newConnection.allocations.add(onlyAllocation);
            return socket;
        } else {
            throw new IllegalStateException();
        }
    }

    public final boolean hasMoreRoutes() {
        return this.route != null || this.routeSelector.hasNext();
    }

    public final String toString() {
        RealConnection connection = connection();
        return connection != null ? connection.toString() : this.address.toString();
    }
}
