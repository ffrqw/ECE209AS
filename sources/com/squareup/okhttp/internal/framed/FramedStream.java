package com.squareup.okhttp.internal.framed;

import java.io.EOFException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import okio.AsyncTimeout;
import okio.Buffer;
import okio.BufferedSource;
import okio.Sink;
import okio.Source;
import okio.Timeout;

public final class FramedStream {
    static final /* synthetic */ boolean $assertionsDisabled = (!FramedStream.class.desiredAssertionStatus());
    long bytesLeftInWriteWindow;
    private final FramedConnection connection;
    private ErrorCode errorCode = null;
    private final int id;
    private final StreamTimeout readTimeout = new StreamTimeout();
    private final List<Header> requestHeaders;
    private List<Header> responseHeaders;
    final FramedDataSink sink;
    private final FramedDataSource source;
    long unacknowledgedBytesRead = 0;
    private final StreamTimeout writeTimeout = new StreamTimeout();

    final class FramedDataSink implements Sink {
        static final /* synthetic */ boolean $assertionsDisabled = (!FramedStream.class.desiredAssertionStatus());
        private boolean closed;
        private boolean finished;
        private final Buffer sendBuffer = new Buffer();

        FramedDataSink() {
        }

        public final void write(Buffer source, long byteCount) throws IOException {
            if ($assertionsDisabled || !Thread.holdsLock(FramedStream.this)) {
                this.sendBuffer.write(source, byteCount);
                while (this.sendBuffer.size() >= 16384) {
                    emitDataFrame(false);
                }
                return;
            }
            throw new AssertionError();
        }

        private void emitDataFrame(boolean outFinished) throws IOException {
            synchronized (FramedStream.this) {
                FramedStream.this.writeTimeout.enter();
                while (FramedStream.this.bytesLeftInWriteWindow <= 0 && !this.finished && !this.closed && FramedStream.this.errorCode == null) {
                    try {
                        FramedStream.this.waitForIo();
                    } catch (Throwable th) {
                        FramedStream.this.writeTimeout.exitAndThrowIfTimedOut();
                    }
                }
                FramedStream.this.writeTimeout.exitAndThrowIfTimedOut();
                FramedStream.access$1200(FramedStream.this);
                long toWrite = Math.min(FramedStream.this.bytesLeftInWriteWindow, this.sendBuffer.size());
                FramedStream framedStream = FramedStream.this;
                framedStream.bytesLeftInWriteWindow -= toWrite;
            }
            FramedStream.this.writeTimeout.enter();
            try {
                FramedConnection access$500 = FramedStream.this.connection;
                int access$600 = FramedStream.this.id;
                boolean z = outFinished && toWrite == this.sendBuffer.size();
                access$500.writeData(access$600, z, this.sendBuffer, toWrite);
            } finally {
                FramedStream.this.writeTimeout.exitAndThrowIfTimedOut();
            }
        }

        public final void flush() throws IOException {
            if ($assertionsDisabled || !Thread.holdsLock(FramedStream.this)) {
                synchronized (FramedStream.this) {
                    FramedStream.access$1200(FramedStream.this);
                }
                while (this.sendBuffer.size() > 0) {
                    emitDataFrame(false);
                    FramedStream.this.connection.flush();
                }
                return;
            }
            throw new AssertionError();
        }

        public final Timeout timeout() {
            return FramedStream.this.writeTimeout;
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public final void close() throws java.io.IOException {
            /*
            r6 = this;
            r4 = 0;
            r2 = 1;
            r0 = $assertionsDisabled;
            if (r0 != 0) goto L_0x0015;
        L_0x0007:
            r0 = com.squareup.okhttp.internal.framed.FramedStream.this;
            r0 = java.lang.Thread.holdsLock(r0);
            if (r0 == 0) goto L_0x0015;
        L_0x000f:
            r0 = new java.lang.AssertionError;
            r0.<init>();
            throw r0;
        L_0x0015:
            r1 = com.squareup.okhttp.internal.framed.FramedStream.this;
            monitor-enter(r1);
            r0 = r6.closed;	 Catch:{ all -> 0x003f }
            if (r0 == 0) goto L_0x001e;
        L_0x001c:
            monitor-exit(r1);	 Catch:{ all -> 0x003f }
        L_0x001d:
            return;
        L_0x001e:
            monitor-exit(r1);	 Catch:{ all -> 0x003f }
            r0 = com.squareup.okhttp.internal.framed.FramedStream.this;
            r0 = r0.sink;
            r0 = r0.finished;
            if (r0 != 0) goto L_0x0052;
        L_0x0027:
            r0 = r6.sendBuffer;
            r0 = r0.size();
            r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1));
            if (r0 <= 0) goto L_0x0042;
        L_0x0031:
            r0 = r6.sendBuffer;
            r0 = r0.size();
            r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1));
            if (r0 <= 0) goto L_0x0052;
        L_0x003b:
            r6.emitDataFrame(r2);
            goto L_0x0031;
        L_0x003f:
            r0 = move-exception;
            monitor-exit(r1);	 Catch:{ all -> 0x003f }
            throw r0;
        L_0x0042:
            r0 = com.squareup.okhttp.internal.framed.FramedStream.this;
            r0 = r0.connection;
            r1 = com.squareup.okhttp.internal.framed.FramedStream.this;
            r1 = r1.id;
            r3 = 0;
            r0.writeData(r1, r2, r3, r4);
        L_0x0052:
            r1 = com.squareup.okhttp.internal.framed.FramedStream.this;
            monitor-enter(r1);
            r0 = 1;
            r6.closed = r0;	 Catch:{ all -> 0x0068 }
            monitor-exit(r1);	 Catch:{ all -> 0x0068 }
            r0 = com.squareup.okhttp.internal.framed.FramedStream.this;
            r0 = r0.connection;
            r0.flush();
            r0 = com.squareup.okhttp.internal.framed.FramedStream.this;
            com.squareup.okhttp.internal.framed.FramedStream.access$1000(r0);
            goto L_0x001d;
        L_0x0068:
            r0 = move-exception;
            monitor-exit(r1);	 Catch:{ all -> 0x0068 }
            throw r0;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.squareup.okhttp.internal.framed.FramedStream.FramedDataSink.close():void");
        }
    }

    private final class FramedDataSource implements Source {
        static final /* synthetic */ boolean $assertionsDisabled = (!FramedStream.class.desiredAssertionStatus());
        private boolean closed;
        private boolean finished;
        private final long maxByteCount;
        private final Buffer readBuffer;
        private final Buffer receiveBuffer;

        private FramedDataSource(long maxByteCount) {
            this.receiveBuffer = new Buffer();
            this.readBuffer = new Buffer();
            this.maxByteCount = maxByteCount;
        }

        public final long read(Buffer sink, long byteCount) throws IOException {
            if (byteCount < 0) {
                throw new IllegalArgumentException("byteCount < 0: " + byteCount);
            }
            long j;
            synchronized (FramedStream.this) {
                waitUntilReadable();
                if (this.closed) {
                    throw new IOException("stream closed");
                } else if (FramedStream.this.errorCode != null) {
                    throw new IOException("stream was reset: " + FramedStream.this.errorCode);
                } else if (this.readBuffer.size() == 0) {
                    j = -1;
                } else {
                    j = this.readBuffer.read(sink, Math.min(byteCount, this.readBuffer.size()));
                    FramedStream framedStream = FramedStream.this;
                    framedStream.unacknowledgedBytesRead += j;
                    if (FramedStream.this.unacknowledgedBytesRead >= ((long) (FramedStream.this.connection.okHttpSettings.getInitialWindowSize(65536) / 2))) {
                        FramedStream.this.connection.writeWindowUpdateLater(FramedStream.this.id, FramedStream.this.unacknowledgedBytesRead);
                        FramedStream.this.unacknowledgedBytesRead = 0;
                    }
                    synchronized (FramedStream.this.connection) {
                        FramedConnection access$500 = FramedStream.this.connection;
                        access$500.unacknowledgedBytesRead += j;
                        if (FramedStream.this.connection.unacknowledgedBytesRead >= ((long) (FramedStream.this.connection.okHttpSettings.getInitialWindowSize(65536) / 2))) {
                            FramedStream.this.connection.writeWindowUpdateLater(0, FramedStream.this.connection.unacknowledgedBytesRead);
                            FramedStream.this.connection.unacknowledgedBytesRead = 0;
                        }
                    }
                }
            }
            return j;
        }

        private void waitUntilReadable() throws IOException {
            FramedStream.this.readTimeout.enter();
            while (this.readBuffer.size() == 0 && !this.finished && !this.closed && FramedStream.this.errorCode == null) {
                try {
                    FramedStream.this.waitForIo();
                } catch (Throwable th) {
                    FramedStream.this.readTimeout.exitAndThrowIfTimedOut();
                }
            }
            FramedStream.this.readTimeout.exitAndThrowIfTimedOut();
        }

        final void receive(BufferedSource in, long byteCount) throws IOException {
            if ($assertionsDisabled || !Thread.holdsLock(FramedStream.this)) {
                while (byteCount > 0) {
                    boolean finished;
                    boolean flowControlError;
                    synchronized (FramedStream.this) {
                        finished = this.finished;
                        flowControlError = this.readBuffer.size() + byteCount > this.maxByteCount;
                    }
                    if (flowControlError) {
                        in.skip(byteCount);
                        FramedStream.this.closeLater(ErrorCode.FLOW_CONTROL_ERROR);
                        return;
                    } else if (finished) {
                        in.skip(byteCount);
                        return;
                    } else {
                        long read = in.read(this.receiveBuffer, byteCount);
                        if (read == -1) {
                            throw new EOFException();
                        }
                        byteCount -= read;
                        synchronized (FramedStream.this) {
                            boolean wasEmpty = this.readBuffer.size() == 0;
                            this.readBuffer.writeAll(this.receiveBuffer);
                            if (wasEmpty) {
                                FramedStream.this.notifyAll();
                            }
                        }
                    }
                }
                return;
            }
            throw new AssertionError();
        }

        public final Timeout timeout() {
            return FramedStream.this.readTimeout;
        }

        public final void close() throws IOException {
            synchronized (FramedStream.this) {
                this.closed = true;
                this.readBuffer.clear();
                FramedStream.this.notifyAll();
            }
            FramedStream.access$1000(FramedStream.this);
        }
    }

    class StreamTimeout extends AsyncTimeout {
        StreamTimeout() {
        }

        protected final void timedOut() {
            FramedStream.this.closeLater(ErrorCode.CANCEL);
        }

        protected final IOException newTimeoutException(IOException cause) {
            SocketTimeoutException socketTimeoutException = new SocketTimeoutException("timeout");
            if (cause != null) {
                socketTimeoutException.initCause(cause);
            }
            return socketTimeoutException;
        }

        public final void exitAndThrowIfTimedOut() throws IOException {
            if (exit()) {
                throw newTimeoutException(null);
            }
        }
    }

    FramedStream(int id, FramedConnection connection, boolean outFinished, boolean inFinished, List<Header> requestHeaders) {
        if (connection == null) {
            throw new NullPointerException("connection == null");
        } else if (requestHeaders == null) {
            throw new NullPointerException("requestHeaders == null");
        } else {
            this.id = id;
            this.connection = connection;
            this.bytesLeftInWriteWindow = (long) connection.peerSettings.getInitialWindowSize(65536);
            this.source = new FramedDataSource((long) connection.okHttpSettings.getInitialWindowSize(65536));
            this.sink = new FramedDataSink();
            this.source.finished = inFinished;
            this.sink.finished = outFinished;
            this.requestHeaders = requestHeaders;
        }
    }

    public final int getId() {
        return this.id;
    }

    public final synchronized boolean isOpen() {
        boolean z = false;
        synchronized (this) {
            if (this.errorCode == null) {
                if (!(this.source.finished || this.source.closed) || (!(this.sink.finished || this.sink.closed) || this.responseHeaders == null)) {
                    z = true;
                }
            }
        }
        return z;
    }

    public final boolean isLocallyInitiated() {
        boolean streamIsClient;
        if ((this.id & 1) == 1) {
            streamIsClient = true;
        } else {
            streamIsClient = false;
        }
        return this.connection.client == streamIsClient;
    }

    public final synchronized List<Header> getResponseHeaders() throws IOException {
        this.readTimeout.enter();
        while (this.responseHeaders == null && this.errorCode == null) {
            try {
                waitForIo();
            } catch (Throwable th) {
                this.readTimeout.exitAndThrowIfTimedOut();
            }
        }
        this.readTimeout.exitAndThrowIfTimedOut();
        if (this.responseHeaders != null) {
        } else {
            throw new IOException("stream was reset: " + this.errorCode);
        }
        return this.responseHeaders;
    }

    public final Timeout readTimeout() {
        return this.readTimeout;
    }

    public final Timeout writeTimeout() {
        return this.writeTimeout;
    }

    public final Source getSource() {
        return this.source;
    }

    public final Sink getSink() {
        synchronized (this) {
            if (this.responseHeaders != null || isLocallyInitiated()) {
            } else {
                throw new IllegalStateException("reply before requesting the sink");
            }
        }
        return this.sink;
    }

    public final void close(ErrorCode rstStatusCode) throws IOException {
        if (closeInternal(rstStatusCode)) {
            this.connection.writeSynReset(this.id, rstStatusCode);
        }
    }

    public final void closeLater(ErrorCode errorCode) {
        if (closeInternal(errorCode)) {
            this.connection.writeSynResetLater(this.id, errorCode);
        }
    }

    private boolean closeInternal(ErrorCode errorCode) {
        if ($assertionsDisabled || !Thread.holdsLock(this)) {
            synchronized (this) {
                if (this.errorCode != null) {
                    return false;
                } else if (this.source.finished && this.sink.finished) {
                    return false;
                } else {
                    this.errorCode = errorCode;
                    notifyAll();
                    this.connection.removeStream(this.id);
                    return true;
                }
            }
        }
        throw new AssertionError();
    }

    final void receiveHeaders(List<Header> headers, HeadersMode headersMode) {
        Object obj = 1;
        if ($assertionsDisabled || !Thread.holdsLock(this)) {
            ErrorCode errorCode = null;
            boolean open = true;
            synchronized (this) {
                if (this.responseHeaders == null) {
                    if (headersMode != HeadersMode.SPDY_HEADERS) {
                        obj = null;
                    }
                    if (obj != null) {
                        errorCode = ErrorCode.PROTOCOL_ERROR;
                    } else {
                        this.responseHeaders = headers;
                        open = isOpen();
                        notifyAll();
                    }
                } else {
                    if (headersMode != HeadersMode.SPDY_REPLY) {
                        obj = null;
                    }
                    if (obj != null) {
                        errorCode = ErrorCode.STREAM_IN_USE;
                    } else {
                        List<Header> newHeaders = new ArrayList();
                        newHeaders.addAll(this.responseHeaders);
                        newHeaders.addAll(headers);
                        this.responseHeaders = newHeaders;
                    }
                }
            }
            if (errorCode != null) {
                closeLater(errorCode);
                return;
            } else if (!open) {
                this.connection.removeStream(this.id);
                return;
            } else {
                return;
            }
        }
        throw new AssertionError();
    }

    final void receiveData(BufferedSource in, int length) throws IOException {
        if ($assertionsDisabled || !Thread.holdsLock(this)) {
            this.source.receive(in, (long) length);
            return;
        }
        throw new AssertionError();
    }

    final void receiveFin() {
        if ($assertionsDisabled || !Thread.holdsLock(this)) {
            boolean open;
            synchronized (this) {
                this.source.finished = true;
                open = isOpen();
                notifyAll();
            }
            if (!open) {
                this.connection.removeStream(this.id);
                return;
            }
            return;
        }
        throw new AssertionError();
    }

    final synchronized void receiveRstStream(ErrorCode errorCode) {
        if (this.errorCode == null) {
            this.errorCode = errorCode;
            notifyAll();
        }
    }

    final void addBytesToWriteWindow(long delta) {
        this.bytesLeftInWriteWindow += delta;
        if (delta > 0) {
            notifyAll();
        }
    }

    private void waitForIo() throws InterruptedIOException {
        try {
            wait();
        } catch (InterruptedException e) {
            throw new InterruptedIOException();
        }
    }

    static /* synthetic */ void access$1000(FramedStream x0) throws IOException {
        if ($assertionsDisabled || !Thread.holdsLock(x0)) {
            Object obj;
            boolean isOpen;
            synchronized (x0) {
                obj = (!x0.source.finished && x0.source.closed && (x0.sink.finished || x0.sink.closed)) ? 1 : null;
                isOpen = x0.isOpen();
            }
            if (obj != null) {
                x0.close(ErrorCode.CANCEL);
                return;
            } else if (!isOpen) {
                x0.connection.removeStream(x0.id);
                return;
            } else {
                return;
            }
        }
        throw new AssertionError();
    }

    static /* synthetic */ void access$1200(FramedStream x0) throws IOException {
        if (x0.sink.closed) {
            throw new IOException("stream closed");
        } else if (x0.sink.finished) {
            throw new IOException("stream finished");
        } else if (x0.errorCode != null) {
            throw new IOException("stream was reset: " + x0.errorCode);
        }
    }
}
