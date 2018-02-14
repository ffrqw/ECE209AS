package com.squareup.okhttp.internal.http;

import com.squareup.okhttp.Address;
import com.squareup.okhttp.ConnectionPool;
import com.squareup.okhttp.internal.Internal;
import com.squareup.okhttp.internal.RouteDatabase;
import com.squareup.okhttp.internal.Util;
import com.squareup.okhttp.internal.io.RealConnection;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLPeerUnverifiedException;
import okio.Sink;

public final class StreamAllocation {
    public final Address address;
    private RealConnection connection;
    private final ConnectionPool connectionPool;
    private boolean released;
    private RouteSelector routeSelector;
    private HttpStream stream;

    public StreamAllocation(ConnectionPool connectionPool, Address address) {
        this.connectionPool = connectionPool;
        this.address = address;
    }

    public final HttpStream newStream(int connectTimeout, int readTimeout, int writeTimeout, boolean connectionRetryEnabled, boolean doExtensiveHealthChecks) throws RouteException, IOException {
        try {
            HttpStream resultStream;
            RealConnection resultConnection = findHealthyConnection(connectTimeout, readTimeout, writeTimeout, connectionRetryEnabled, doExtensiveHealthChecks);
            if (resultConnection.framedConnection != null) {
                resultStream = new Http2xStream(this, resultConnection.framedConnection);
            } else {
                resultConnection.socket.setSoTimeout(readTimeout);
                resultConnection.source.timeout().timeout((long) readTimeout, TimeUnit.MILLISECONDS);
                resultConnection.sink.timeout().timeout((long) writeTimeout, TimeUnit.MILLISECONDS);
                resultStream = new Http1xStream(this, resultConnection.source, resultConnection.sink);
            }
            synchronized (this.connectionPool) {
                resultConnection.streamCount++;
                this.stream = resultStream;
            }
            return resultStream;
        } catch (IOException e) {
            throw new RouteException(e);
        }
    }

    private RealConnection findHealthyConnection(int connectTimeout, int readTimeout, int writeTimeout, boolean connectionRetryEnabled, boolean doExtensiveHealthChecks) throws IOException, RouteException {
        RealConnection candidate;
        while (true) {
            candidate = findConnection(connectTimeout, readTimeout, writeTimeout, connectionRetryEnabled);
            synchronized (this.connectionPool) {
                if (candidate.streamCount != 0) {
                    if (candidate.isHealthy(doExtensiveHealthChecks)) {
                        break;
                    }
                    deallocate(true, false, true);
                } else {
                    break;
                }
            }
        }
        return candidate;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private com.squareup.okhttp.internal.io.RealConnection findConnection(int r10, int r11, int r12, boolean r13) throws java.io.IOException, com.squareup.okhttp.internal.http.RouteException {
        /*
        r9 = this;
        r2 = r9.connectionPool;
        monitor-enter(r2);
        r1 = r9.released;	 Catch:{ all -> 0x000f }
        if (r1 == 0) goto L_0x0012;
    L_0x0007:
        r1 = new java.lang.IllegalStateException;	 Catch:{ all -> 0x000f }
        r3 = "released";
        r1.<init>(r3);	 Catch:{ all -> 0x000f }
        throw r1;	 Catch:{ all -> 0x000f }
    L_0x000f:
        r1 = move-exception;
        monitor-exit(r2);	 Catch:{ all -> 0x000f }
        throw r1;
    L_0x0012:
        r1 = r9.stream;	 Catch:{ all -> 0x000f }
        if (r1 == 0) goto L_0x001e;
    L_0x0016:
        r1 = new java.lang.IllegalStateException;	 Catch:{ all -> 0x000f }
        r3 = "stream != null";
        r1.<init>(r3);	 Catch:{ all -> 0x000f }
        throw r1;	 Catch:{ all -> 0x000f }
    L_0x001e:
        r6 = r9.connection;	 Catch:{ all -> 0x000f }
        if (r6 == 0) goto L_0x0028;
    L_0x0022:
        r1 = r6.noNewStreams;	 Catch:{ all -> 0x000f }
        if (r1 != 0) goto L_0x0028;
    L_0x0026:
        monitor-exit(r2);	 Catch:{ all -> 0x000f }
    L_0x0027:
        return r6;
    L_0x0028:
        r1 = com.squareup.okhttp.internal.Internal.instance;	 Catch:{ all -> 0x000f }
        r3 = r9.connectionPool;	 Catch:{ all -> 0x000f }
        r4 = r9.address;	 Catch:{ all -> 0x000f }
        r7 = r1.get(r3, r4, r9);	 Catch:{ all -> 0x000f }
        if (r7 == 0) goto L_0x0039;
    L_0x0034:
        r9.connection = r7;	 Catch:{ all -> 0x000f }
        monitor-exit(r2);	 Catch:{ all -> 0x000f }
        r6 = r7;
        goto L_0x0027;
    L_0x0039:
        r1 = r9.routeSelector;	 Catch:{ all -> 0x000f }
        if (r1 != 0) goto L_0x004a;
    L_0x003d:
        r1 = new com.squareup.okhttp.internal.http.RouteSelector;	 Catch:{ all -> 0x000f }
        r3 = r9.address;	 Catch:{ all -> 0x000f }
        r4 = r9.routeDatabase();	 Catch:{ all -> 0x000f }
        r1.<init>(r3, r4);	 Catch:{ all -> 0x000f }
        r9.routeSelector = r1;	 Catch:{ all -> 0x000f }
    L_0x004a:
        monitor-exit(r2);	 Catch:{ all -> 0x000f }
        r1 = r9.routeSelector;
        r8 = r1.next();
        r0 = new com.squareup.okhttp.internal.io.RealConnection;
        r0.<init>(r8);
        r9.acquire(r0);
        r2 = r9.connectionPool;
        monitor-enter(r2);
        r1 = com.squareup.okhttp.internal.Internal.instance;	 Catch:{ all -> 0x0080 }
        r3 = r9.connectionPool;	 Catch:{ all -> 0x0080 }
        r1.put(r3, r0);	 Catch:{ all -> 0x0080 }
        r9.connection = r0;	 Catch:{ all -> 0x0080 }
        monitor-exit(r2);	 Catch:{ all -> 0x0080 }
        r1 = r9.address;
        r4 = r1.getConnectionSpecs();
        r1 = r10;
        r2 = r11;
        r3 = r12;
        r5 = r13;
        r0.connect(r1, r2, r3, r4, r5);
        r1 = r9.routeDatabase();
        r2 = r0.getRoute();
        r1.connected(r2);
        r6 = r0;
        goto L_0x0027;
    L_0x0080:
        r1 = move-exception;
        monitor-exit(r2);	 Catch:{ all -> 0x0080 }
        throw r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.squareup.okhttp.internal.http.StreamAllocation.findConnection(int, int, int, boolean):com.squareup.okhttp.internal.io.RealConnection");
    }

    public final void streamFinished(HttpStream stream) {
        synchronized (this.connectionPool) {
            if (stream != null) {
                if (stream == this.stream) {
                }
            }
            throw new IllegalStateException("expected " + this.stream + " but was " + stream);
        }
        deallocate(false, false, true);
    }

    private RouteDatabase routeDatabase() {
        return Internal.instance.routeDatabase(this.connectionPool);
    }

    public final synchronized RealConnection connection() {
        return this.connection;
    }

    public final void release() {
        deallocate(false, true, false);
    }

    public final void noNewStreams() {
        deallocate(true, false, false);
    }

    private void deallocate(boolean noNewStreams, boolean released, boolean streamFinished) {
        RealConnection connectionToClose = null;
        synchronized (this.connectionPool) {
            if (streamFinished) {
                this.stream = null;
            }
            if (released) {
                this.released = true;
            }
            if (this.connection != null) {
                if (noNewStreams) {
                    this.connection.noNewStreams = true;
                }
                if (this.stream == null && (this.released || this.connection.noNewStreams)) {
                    RealConnection realConnection = this.connection;
                    int size = realConnection.allocations.size();
                    int i = 0;
                    while (i < size) {
                        if (((Reference) realConnection.allocations.get(i)).get() == this) {
                            realConnection.allocations.remove(i);
                            if (this.connection.streamCount > 0) {
                                this.routeSelector = null;
                            }
                            if (this.connection.allocations.isEmpty()) {
                                this.connection.idleAtNanos = System.nanoTime();
                                if (Internal.instance.connectionBecameIdle(this.connectionPool, this.connection)) {
                                    connectionToClose = this.connection;
                                }
                            }
                            this.connection = null;
                        } else {
                            i++;
                        }
                    }
                    throw new IllegalStateException();
                }
            }
        }
        if (connectionToClose != null) {
            Util.closeQuietly(connectionToClose.socket);
        }
    }

    private void connectionFailed(IOException e) {
        synchronized (this.connectionPool) {
            if (this.routeSelector != null) {
                if (this.connection.streamCount == 0) {
                    this.routeSelector.connectFailed(this.connection.getRoute(), e);
                } else {
                    this.routeSelector = null;
                }
            }
        }
        deallocate(true, false, true);
    }

    public final void connectionFailed() {
        deallocate(true, false, true);
    }

    public final void acquire(RealConnection connection) {
        connection.allocations.add(new WeakReference(this));
    }

    public final boolean recover(RouteException e) {
        if (this.connection != null) {
            connectionFailed(e.getLastConnectException());
        }
        if (this.routeSelector == null || this.routeSelector.hasNext()) {
            boolean z;
            IOException lastConnectException = e.getLastConnectException();
            if (lastConnectException instanceof ProtocolException) {
                z = false;
            } else if (lastConnectException instanceof InterruptedIOException) {
                z = lastConnectException instanceof SocketTimeoutException;
            } else if ((lastConnectException instanceof SSLHandshakeException) && (lastConnectException.getCause() instanceof CertificateException)) {
                z = false;
            } else if (lastConnectException instanceof SSLPeerUnverifiedException) {
                z = false;
            } else {
                z = true;
            }
            if (z) {
                return true;
            }
        }
        return false;
    }

    public final boolean recover(IOException e, Sink requestBodyOut) {
        if (this.connection != null) {
            int streamCount = this.connection.streamCount;
            connectionFailed(e);
            if (streamCount == 1) {
                return false;
            }
        }
        boolean canRetryRequestBody;
        if (requestBodyOut == null || (requestBodyOut instanceof RetryableSink)) {
            canRetryRequestBody = true;
        } else {
            canRetryRequestBody = false;
        }
        if (this.routeSelector != null && !this.routeSelector.hasNext()) {
            return false;
        }
        boolean z;
        if (e instanceof ProtocolException) {
            z = false;
        } else if (e instanceof InterruptedIOException) {
            z = false;
        } else {
            z = true;
        }
        if (z && canRetryRequestBody) {
            return true;
        }
        return false;
    }

    public final String toString() {
        return this.address.toString();
    }
}
