package com.squareup.okhttp.internal.framed;

import com.rachio.iro.gen2.MrvlProvService;
import com.squareup.okhttp.Protocol;
import com.squareup.okhttp.internal.NamedRunnable;
import com.squareup.okhttp.internal.Util;
import com.squareup.okhttp.internal.framed.FrameReader.Handler;
import java.io.Closeable;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.ByteString;

public final class FramedConnection implements Closeable {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static final ExecutorService executor = new ThreadPoolExecutor(0, ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED, 60, TimeUnit.SECONDS, new SynchronousQueue(), Util.threadFactory("OkHttp FramedConnection", true));
    long bytesLeftInWriteWindow;
    final boolean client;
    private final Set<Integer> currentPushRequests;
    final FrameWriter frameWriter;
    private final String hostName;
    private long idleStartTimeNs;
    private int lastGoodStreamId;
    private final Listener listener;
    private int nextPingId;
    private int nextStreamId;
    Settings okHttpSettings;
    final Settings peerSettings;
    final Protocol protocol;
    private final ExecutorService pushExecutor;
    private final PushObserver pushObserver;
    final Reader readerRunnable;
    private boolean receivedInitialPeerSettings;
    private boolean shutdown;
    final Socket socket;
    private final Map<Integer, FramedStream> streams;
    long unacknowledgedBytesRead;
    final Variant variant;

    public static class Builder {
        private boolean client = true;
        private String hostName;
        private Listener listener = Listener.REFUSE_INCOMING_STREAMS;
        private Protocol protocol = Protocol.SPDY_3;
        private PushObserver pushObserver = PushObserver.CANCEL;
        private BufferedSink sink;
        private Socket socket;
        private BufferedSource source;

        public Builder(boolean client) throws IOException {
        }

        public final Builder socket(Socket socket, String hostName, BufferedSource source, BufferedSink sink) {
            this.socket = socket;
            this.hostName = hostName;
            this.source = source;
            this.sink = sink;
            return this;
        }

        public final Builder protocol(Protocol protocol) {
            this.protocol = protocol;
            return this;
        }

        public final FramedConnection build() throws IOException {
            return new FramedConnection();
        }
    }

    public static abstract class Listener {
        public static final Listener REFUSE_INCOMING_STREAMS = new Listener() {
            public final void onStream(FramedStream stream) throws IOException {
                stream.close(ErrorCode.REFUSED_STREAM);
            }
        };

        public abstract void onStream(FramedStream framedStream) throws IOException;

        public static void onSettings$4c36b099() {
        }
    }

    class Reader extends NamedRunnable implements Handler {
        final FrameReader frameReader;

        private Reader(FrameReader frameReader) {
            super("OkHttp %s", this$0.hostName);
            this.frameReader = frameReader;
        }

        protected final void execute() {
            ErrorCode connectionErrorCode = ErrorCode.INTERNAL_ERROR;
            ErrorCode streamErrorCode = ErrorCode.INTERNAL_ERROR;
            try {
                if (!FramedConnection.this.client) {
                    this.frameReader.readConnectionPreface();
                }
                while (true) {
                    if (!this.frameReader.nextFrame(this)) {
                        break;
                    }
                }
                connectionErrorCode = ErrorCode.NO_ERROR;
                streamErrorCode = ErrorCode.CANCEL;
            } catch (IOException e) {
                connectionErrorCode = ErrorCode.PROTOCOL_ERROR;
                streamErrorCode = ErrorCode.PROTOCOL_ERROR;
            } finally {
                try {
                    FramedConnection.this.close(connectionErrorCode, streamErrorCode);
                } catch (IOException e2) {
                }
                Util.closeQuietly(this.frameReader);
            }
        }

        public final void data(boolean inFinished, int streamId, BufferedSource source, int length) throws IOException {
            if (FramedConnection.access$1300(FramedConnection.this, streamId)) {
                FramedConnection.access$1400(FramedConnection.this, streamId, source, length, inFinished);
                return;
            }
            FramedStream dataStream = FramedConnection.this.getStream(streamId);
            if (dataStream == null) {
                FramedConnection.this.writeSynResetLater(streamId, ErrorCode.INVALID_STREAM);
                source.skip((long) length);
                return;
            }
            dataStream.receiveData(source, length);
            if (inFinished) {
                dataStream.receiveFin();
            }
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public final void headers$37c2d766(boolean r10, boolean r11, int r12, java.util.List<com.squareup.okhttp.internal.framed.Header> r13, com.squareup.okhttp.internal.framed.HeadersMode r14) {
            /*
            r9 = this;
            r1 = 1;
            r2 = 0;
            r3 = com.squareup.okhttp.internal.framed.FramedConnection.this;
            r3 = com.squareup.okhttp.internal.framed.FramedConnection.access$1300(r3, r12);
            if (r3 == 0) goto L_0x0010;
        L_0x000a:
            r1 = com.squareup.okhttp.internal.framed.FramedConnection.this;
            com.squareup.okhttp.internal.framed.FramedConnection.access$1500(r1, r12, r13, r11);
        L_0x000f:
            return;
        L_0x0010:
            r7 = com.squareup.okhttp.internal.framed.FramedConnection.this;
            monitor-enter(r7);
            r3 = com.squareup.okhttp.internal.framed.FramedConnection.this;	 Catch:{ all -> 0x001d }
            r3 = r3.shutdown;	 Catch:{ all -> 0x001d }
            if (r3 == 0) goto L_0x0020;
        L_0x001b:
            monitor-exit(r7);	 Catch:{ all -> 0x001d }
            goto L_0x000f;
        L_0x001d:
            r1 = move-exception;
            monitor-exit(r7);	 Catch:{ all -> 0x001d }
            throw r1;
        L_0x0020:
            r3 = com.squareup.okhttp.internal.framed.FramedConnection.this;	 Catch:{ all -> 0x001d }
            r6 = r3.getStream(r12);	 Catch:{ all -> 0x001d }
            if (r6 != 0) goto L_0x0095;
        L_0x0028:
            r3 = com.squareup.okhttp.internal.framed.HeadersMode.SPDY_REPLY;	 Catch:{ all -> 0x001d }
            if (r14 == r3) goto L_0x0030;
        L_0x002c:
            r3 = com.squareup.okhttp.internal.framed.HeadersMode.SPDY_HEADERS;	 Catch:{ all -> 0x001d }
            if (r14 != r3) goto L_0x0031;
        L_0x0030:
            r2 = r1;
        L_0x0031:
            if (r2 == 0) goto L_0x003c;
        L_0x0033:
            r1 = com.squareup.okhttp.internal.framed.FramedConnection.this;	 Catch:{ all -> 0x001d }
            r2 = com.squareup.okhttp.internal.framed.ErrorCode.INVALID_STREAM;	 Catch:{ all -> 0x001d }
            r1.writeSynResetLater(r12, r2);	 Catch:{ all -> 0x001d }
            monitor-exit(r7);	 Catch:{ all -> 0x001d }
            goto L_0x000f;
        L_0x003c:
            r1 = com.squareup.okhttp.internal.framed.FramedConnection.this;	 Catch:{ all -> 0x001d }
            r1 = r1.lastGoodStreamId;	 Catch:{ all -> 0x001d }
            if (r12 > r1) goto L_0x0046;
        L_0x0044:
            monitor-exit(r7);	 Catch:{ all -> 0x001d }
            goto L_0x000f;
        L_0x0046:
            r1 = r12 % 2;
            r2 = com.squareup.okhttp.internal.framed.FramedConnection.this;	 Catch:{ all -> 0x001d }
            r2 = r2.nextStreamId;	 Catch:{ all -> 0x001d }
            r2 = r2 % 2;
            if (r1 != r2) goto L_0x0054;
        L_0x0052:
            monitor-exit(r7);	 Catch:{ all -> 0x001d }
            goto L_0x000f;
        L_0x0054:
            r0 = new com.squareup.okhttp.internal.framed.FramedStream;	 Catch:{ all -> 0x001d }
            r2 = com.squareup.okhttp.internal.framed.FramedConnection.this;	 Catch:{ all -> 0x001d }
            r1 = r12;
            r3 = r10;
            r4 = r11;
            r5 = r13;
            r0.<init>(r1, r2, r3, r4, r5);	 Catch:{ all -> 0x001d }
            r1 = com.squareup.okhttp.internal.framed.FramedConnection.this;	 Catch:{ all -> 0x001d }
            r1.lastGoodStreamId = r12;	 Catch:{ all -> 0x001d }
            r1 = com.squareup.okhttp.internal.framed.FramedConnection.this;	 Catch:{ all -> 0x001d }
            r1 = r1.streams;	 Catch:{ all -> 0x001d }
            r2 = java.lang.Integer.valueOf(r12);	 Catch:{ all -> 0x001d }
            r1.put(r2, r0);	 Catch:{ all -> 0x001d }
            r1 = com.squareup.okhttp.internal.framed.FramedConnection.executor;	 Catch:{ all -> 0x001d }
            r2 = new com.squareup.okhttp.internal.framed.FramedConnection$Reader$1;	 Catch:{ all -> 0x001d }
            r3 = "OkHttp %s stream %d";
            r4 = 2;
            r4 = new java.lang.Object[r4];	 Catch:{ all -> 0x001d }
            r5 = 0;
            r8 = com.squareup.okhttp.internal.framed.FramedConnection.this;	 Catch:{ all -> 0x001d }
            r8 = r8.hostName;	 Catch:{ all -> 0x001d }
            r4[r5] = r8;	 Catch:{ all -> 0x001d }
            r5 = 1;
            r8 = java.lang.Integer.valueOf(r12);	 Catch:{ all -> 0x001d }
            r4[r5] = r8;	 Catch:{ all -> 0x001d }
            r2.<init>(r3, r4, r0);	 Catch:{ all -> 0x001d }
            r1.execute(r2);	 Catch:{ all -> 0x001d }
            monitor-exit(r7);	 Catch:{ all -> 0x001d }
            goto L_0x000f;
        L_0x0095:
            monitor-exit(r7);	 Catch:{ all -> 0x001d }
            r3 = com.squareup.okhttp.internal.framed.HeadersMode.SPDY_SYN_STREAM;
            if (r14 != r3) goto L_0x00a8;
        L_0x009a:
            if (r1 == 0) goto L_0x00aa;
        L_0x009c:
            r1 = com.squareup.okhttp.internal.framed.ErrorCode.PROTOCOL_ERROR;
            r6.closeLater(r1);
            r1 = com.squareup.okhttp.internal.framed.FramedConnection.this;
            r1.removeStream(r12);
            goto L_0x000f;
        L_0x00a8:
            r1 = r2;
            goto L_0x009a;
        L_0x00aa:
            r6.receiveHeaders(r13, r14);
            if (r11 == 0) goto L_0x000f;
        L_0x00af:
            r6.receiveFin();
            goto L_0x000f;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.squareup.okhttp.internal.framed.FramedConnection.Reader.headers$37c2d766(boolean, boolean, int, java.util.List, com.squareup.okhttp.internal.framed.HeadersMode):void");
        }

        public final void rstStream(int streamId, ErrorCode errorCode) {
            if (FramedConnection.access$1300(FramedConnection.this, streamId)) {
                FramedConnection.access$2200(FramedConnection.this, streamId, errorCode);
                return;
            }
            FramedStream rstStream = FramedConnection.this.removeStream(streamId);
            if (rstStream != null) {
                rstStream.receiveRstStream(errorCode);
            }
        }

        public final void settings(boolean clearPrevious, Settings newSettings) {
            long delta = 0;
            FramedStream[] streamsToNotify = null;
            synchronized (FramedConnection.this) {
                int priorWriteWindowSize = FramedConnection.this.peerSettings.getInitialWindowSize(65536);
                if (clearPrevious) {
                    FramedConnection.this.peerSettings.clear();
                }
                Settings settings = FramedConnection.this.peerSettings;
                for (int i = 0; i < 10; i++) {
                    if (newSettings.isSet(i)) {
                        settings.set(i, newSettings.flags(i), newSettings.get(i));
                    }
                }
                if (FramedConnection.this.protocol == Protocol.HTTP_2) {
                    final Settings settings2 = newSettings;
                    FramedConnection.executor.execute(new NamedRunnable("OkHttp %s ACK Settings", new Object[]{FramedConnection.this.hostName}) {
                        public final void execute() {
                            try {
                                FramedConnection.this.frameWriter.ackSettings(settings2);
                            } catch (IOException e) {
                            }
                        }
                    });
                }
                int peerInitialWindowSize = FramedConnection.this.peerSettings.getInitialWindowSize(65536);
                if (!(peerInitialWindowSize == -1 || peerInitialWindowSize == priorWriteWindowSize)) {
                    delta = (long) (peerInitialWindowSize - priorWriteWindowSize);
                    if (!FramedConnection.this.receivedInitialPeerSettings) {
                        FramedConnection framedConnection = FramedConnection.this;
                        framedConnection.bytesLeftInWriteWindow += delta;
                        if (delta > 0) {
                            framedConnection.notifyAll();
                        }
                        FramedConnection.this.receivedInitialPeerSettings = true;
                    }
                    if (!FramedConnection.this.streams.isEmpty()) {
                        streamsToNotify = (FramedStream[]) FramedConnection.this.streams.values().toArray(new FramedStream[FramedConnection.this.streams.size()]);
                    }
                }
                FramedConnection.executor.execute(new NamedRunnable("OkHttp %s settings", FramedConnection.this.hostName) {
                    public final void execute() {
                        FramedConnection.this.listener;
                        Listener.onSettings$4c36b099();
                    }
                });
            }
            if (streamsToNotify != null && delta != 0) {
                for (FramedStream stream : streamsToNotify) {
                    synchronized (stream) {
                        stream.addBytesToWriteWindow(delta);
                    }
                }
            }
        }

        public final void ping(boolean reply, int payload1, int payload2) {
            if (reply) {
                Ping ping = FramedConnection.this.removePing(payload1);
                if (ping != null) {
                    ping.receive();
                    return;
                }
                return;
            }
            FramedConnection.access$2500(FramedConnection.this, true, payload1, payload2, null);
        }

        public final void goAway$4b4c5c6b(int lastGoodStreamId, ByteString debugData) {
            debugData.size();
            synchronized (FramedConnection.this) {
                FramedStream[] streamsCopy = (FramedStream[]) FramedConnection.this.streams.values().toArray(new FramedStream[FramedConnection.this.streams.size()]);
                FramedConnection.this.shutdown = true;
            }
            for (FramedStream framedStream : streamsCopy) {
                if (framedStream.getId() > lastGoodStreamId && framedStream.isLocallyInitiated()) {
                    framedStream.receiveRstStream(ErrorCode.REFUSED_STREAM);
                    FramedConnection.this.removeStream(framedStream.getId());
                }
            }
        }

        public final void windowUpdate(int streamId, long windowSizeIncrement) {
            if (streamId == 0) {
                synchronized (FramedConnection.this) {
                    FramedConnection framedConnection = FramedConnection.this;
                    framedConnection.bytesLeftInWriteWindow += windowSizeIncrement;
                    FramedConnection.this.notifyAll();
                }
                return;
            }
            FramedStream stream = FramedConnection.this.getStream(streamId);
            if (stream != null) {
                synchronized (stream) {
                    stream.addBytesToWriteWindow(windowSizeIncrement);
                }
            }
        }

        public final void pushPromise$16014a7a(int promisedStreamId, List<Header> requestHeaders) {
            FramedConnection.access$2600(FramedConnection.this, promisedStreamId, requestHeaders);
        }
    }

    static {
        boolean z;
        if (FramedConnection.class.desiredAssertionStatus()) {
            z = false;
        } else {
            z = true;
        }
        $assertionsDisabled = z;
    }

    static /* synthetic */ void access$2500(FramedConnection x0, boolean x1, int x2, int x3, Ping x4) {
        FramedConnection framedConnection = x0;
        final int i = x2;
        final int i2 = x3;
        executor.execute(new NamedRunnable("OkHttp %s ping %08x%08x", new Object[]{x0.hostName, Integer.valueOf(x2), Integer.valueOf(x3)}, true, null) {
            public final void execute() {
                try {
                    FramedConnection.access$900(FramedConnection.this, true, i, i2, null);
                } catch (IOException e) {
                }
            }
        });
    }

    private FramedConnection(Builder builder) throws IOException {
        int i = 2;
        this.streams = new HashMap();
        this.idleStartTimeNs = System.nanoTime();
        this.unacknowledgedBytesRead = 0;
        this.okHttpSettings = new Settings();
        this.peerSettings = new Settings();
        this.receivedInitialPeerSettings = false;
        this.currentPushRequests = new LinkedHashSet();
        this.protocol = builder.protocol;
        this.pushObserver = builder.pushObserver;
        this.client = builder.client;
        this.listener = builder.listener;
        this.nextStreamId = builder.client ? 1 : 2;
        if (builder.client && this.protocol == Protocol.HTTP_2) {
            this.nextStreamId += 2;
        }
        if (builder.client) {
            i = 1;
        }
        this.nextPingId = i;
        if (builder.client) {
            this.okHttpSettings.set(7, 0, 16777216);
        }
        this.hostName = builder.hostName;
        if (this.protocol == Protocol.HTTP_2) {
            this.variant = new Http2();
            this.pushExecutor = new ThreadPoolExecutor(0, 1, 60, TimeUnit.SECONDS, new LinkedBlockingQueue(), Util.threadFactory(String.format("OkHttp %s Push Observer", new Object[]{this.hostName}), true));
            this.peerSettings.set(7, 0, 65535);
            this.peerSettings.set(5, 0, 16384);
        } else if (this.protocol == Protocol.SPDY_3) {
            this.variant = new Spdy3();
            this.pushExecutor = null;
        } else {
            throw new AssertionError(this.protocol);
        }
        this.bytesLeftInWriteWindow = (long) this.peerSettings.getInitialWindowSize(65536);
        this.socket = builder.socket;
        this.frameWriter = this.variant.newWriter(builder.sink, this.client);
        this.readerRunnable = new Reader(this.variant.newReader(builder.source, this.client));
        new Thread(this.readerRunnable).start();
    }

    public final Protocol getProtocol() {
        return this.protocol;
    }

    final synchronized FramedStream getStream(int id) {
        return (FramedStream) this.streams.get(Integer.valueOf(id));
    }

    final synchronized FramedStream removeStream(int streamId) {
        FramedStream stream;
        stream = (FramedStream) this.streams.remove(Integer.valueOf(streamId));
        if (stream != null && this.streams.isEmpty()) {
            setIdle(true);
        }
        notifyAll();
        return stream;
    }

    private synchronized void setIdle(boolean value) {
        this.idleStartTimeNs = value ? System.nanoTime() : Long.MAX_VALUE;
    }

    public final synchronized int maxConcurrentStreams() {
        return this.peerSettings.getMaxConcurrentStreams(ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED);
    }

    public final FramedStream newStream(List<Header> requestHeaders, boolean out, boolean in) throws IOException {
        return newStream(0, requestHeaders, out, true);
    }

    private FramedStream newStream(int associatedStreamId, List<Header> requestHeaders, boolean out, boolean in) throws IOException {
        boolean outFinished;
        FramedStream stream;
        boolean inFinished = true;
        if (out) {
            outFinished = false;
        } else {
            outFinished = true;
        }
        if (in) {
            inFinished = false;
        }
        synchronized (this.frameWriter) {
            int streamId;
            synchronized (this) {
                if (this.shutdown) {
                    throw new IOException(MrvlProvService.ACTION_SHUTDOWN);
                }
                streamId = this.nextStreamId;
                this.nextStreamId += 2;
                stream = new FramedStream(streamId, this, outFinished, inFinished, requestHeaders);
                if (stream.isOpen()) {
                    this.streams.put(Integer.valueOf(streamId), stream);
                    setIdle(false);
                }
            }
            this.frameWriter.synStream(outFinished, inFinished, streamId, 0, requestHeaders);
        }
        if (!out) {
            this.frameWriter.flush();
        }
        return stream;
    }

    public final void writeData(int streamId, boolean outFinished, Buffer buffer, long byteCount) throws IOException {
        if (byteCount == 0) {
            this.frameWriter.data(outFinished, streamId, buffer, 0);
            return;
        }
        while (byteCount > 0) {
            int toWrite;
            boolean z;
            synchronized (this) {
                while (this.bytesLeftInWriteWindow <= 0) {
                    try {
                        if (this.streams.containsKey(Integer.valueOf(streamId))) {
                            wait();
                        } else {
                            throw new IOException("stream closed");
                        }
                    } catch (InterruptedException e) {
                        throw new InterruptedIOException();
                    }
                }
                toWrite = Math.min((int) Math.min(byteCount, this.bytesLeftInWriteWindow), this.frameWriter.maxDataLength());
                this.bytesLeftInWriteWindow -= (long) toWrite;
            }
            byteCount -= (long) toWrite;
            FrameWriter frameWriter = this.frameWriter;
            if (outFinished && byteCount == 0) {
                z = true;
            } else {
                z = false;
            }
            frameWriter.data(z, streamId, buffer, toWrite);
        }
    }

    final void writeSynResetLater(int streamId, ErrorCode errorCode) {
        final int i = streamId;
        final ErrorCode errorCode2 = errorCode;
        executor.submit(new NamedRunnable("OkHttp %s stream %d", new Object[]{this.hostName, Integer.valueOf(streamId)}) {
            public final void execute() {
                try {
                    FramedConnection.this.writeSynReset(i, errorCode2);
                } catch (IOException e) {
                }
            }
        });
    }

    final void writeSynReset(int streamId, ErrorCode statusCode) throws IOException {
        this.frameWriter.rstStream(streamId, statusCode);
    }

    final void writeWindowUpdateLater(int streamId, long unacknowledgedBytesRead) {
        final int i = streamId;
        final long j = unacknowledgedBytesRead;
        executor.execute(new NamedRunnable("OkHttp Window Update %s stream %d", new Object[]{this.hostName, Integer.valueOf(streamId)}) {
            public final void execute() {
                try {
                    FramedConnection.this.frameWriter.windowUpdate(i, j);
                } catch (IOException e) {
                }
            }
        });
    }

    private synchronized Ping removePing(int id) {
        return null;
    }

    public final void flush() throws IOException {
        this.frameWriter.flush();
    }

    public final void close() throws IOException {
        close(ErrorCode.NO_ERROR, ErrorCode.CANCEL);
    }

    private void close(ErrorCode connectionCode, ErrorCode streamCode) throws IOException {
        if ($assertionsDisabled || !Thread.holdsLock(this)) {
            IOException thrown = null;
            try {
                synchronized (this.frameWriter) {
                    synchronized (this) {
                        if (this.shutdown) {
                        } else {
                            this.shutdown = true;
                            this.frameWriter.goAway(this.lastGoodStreamId, connectionCode, Util.EMPTY_BYTE_ARRAY);
                        }
                    }
                    if (streamsToClose != null) {
                        for (FramedStream stream : streamsToClose) {
                            try {
                                stream.close(streamCode);
                            } catch (IOException e) {
                                if (thrown != null) {
                                    thrown = e;
                                }
                            }
                        }
                    }
                    this.frameWriter.close();
                    this.socket.close();
                    if (thrown != null) {
                        throw thrown;
                    }
                    return;
                }
            } catch (IOException e2) {
                thrown = e2;
            }
            FramedStream[] streamsToClose = null;
            synchronized (this) {
                if (!this.streams.isEmpty()) {
                    streamsToClose = (FramedStream[]) this.streams.values().toArray(new FramedStream[this.streams.size()]);
                    this.streams.clear();
                    setIdle(false);
                }
            }
            if (streamsToClose != null) {
                while (r5 < r7) {
                    stream.close(streamCode);
                }
            }
            try {
                this.frameWriter.close();
            } catch (IOException e22) {
                if (thrown == null) {
                    thrown = e22;
                }
            }
            try {
                this.socket.close();
            } catch (IOException e222) {
                thrown = e222;
            }
            if (thrown != null) {
                throw thrown;
            }
            return;
        }
        throw new AssertionError();
    }

    public final void sendConnectionPreface() throws IOException {
        this.frameWriter.connectionPreface();
        this.frameWriter.settings(this.okHttpSettings);
        int windowSize = this.okHttpSettings.getInitialWindowSize(65536);
        if (windowSize != 65536) {
            this.frameWriter.windowUpdate(0, (long) (windowSize - 65536));
        }
    }

    static /* synthetic */ void access$900(FramedConnection x0, boolean x1, int x2, int x3, Ping x4) throws IOException {
        synchronized (x0.frameWriter) {
            if (x4 != null) {
                x4.send();
            }
            x0.frameWriter.ping(x1, x2, x3);
        }
    }

    static /* synthetic */ boolean access$1300(FramedConnection x0, int x1) {
        return x0.protocol == Protocol.HTTP_2 && x1 != 0 && (x1 & 1) == 0;
    }

    static /* synthetic */ void access$1400(FramedConnection x0, int x1, BufferedSource x2, int x3, boolean x4) throws IOException {
        final Buffer buffer = new Buffer();
        x2.require((long) x3);
        x2.read(buffer, (long) x3);
        if (buffer.size() != ((long) x3)) {
            throw new IOException(buffer.size() + " != " + x3);
        }
        FramedConnection framedConnection = x0;
        final int i = x1;
        final int i2 = x3;
        final boolean z = x4;
        x0.pushExecutor.execute(new NamedRunnable("OkHttp %s Push Data[%s]", new Object[]{x0.hostName, Integer.valueOf(x1)}) {
            public final void execute() {
                try {
                    FramedConnection.this.pushObserver.onData$749b27ff(buffer, i2);
                    FramedConnection.this.frameWriter.rstStream(i, ErrorCode.CANCEL);
                    synchronized (FramedConnection.this) {
                        FramedConnection.this.currentPushRequests.remove(Integer.valueOf(i));
                    }
                } catch (IOException e) {
                }
            }
        });
    }

    static /* synthetic */ void access$1500(FramedConnection x0, int x1, List x2, boolean x3) {
        FramedConnection framedConnection = x0;
        final int i = x1;
        final List list = x2;
        final boolean z = x3;
        x0.pushExecutor.execute(new NamedRunnable("OkHttp %s Push Headers[%s]", new Object[]{x0.hostName, Integer.valueOf(x1)}) {
            public final void execute() {
                FramedConnection.this.pushObserver.onHeaders$4ec42067();
                try {
                    FramedConnection.this.frameWriter.rstStream(i, ErrorCode.CANCEL);
                    synchronized (FramedConnection.this) {
                        FramedConnection.this.currentPushRequests.remove(Integer.valueOf(i));
                    }
                } catch (IOException e) {
                }
            }
        });
    }

    static /* synthetic */ void access$2200(FramedConnection x0, int x1, ErrorCode x2) {
        FramedConnection framedConnection = x0;
        final int i = x1;
        final ErrorCode errorCode = x2;
        x0.pushExecutor.execute(new NamedRunnable("OkHttp %s Push Reset[%s]", new Object[]{x0.hostName, Integer.valueOf(x1)}) {
            public final void execute() {
                FramedConnection.this.pushObserver.onReset$6b03c5a6();
                synchronized (FramedConnection.this) {
                    FramedConnection.this.currentPushRequests.remove(Integer.valueOf(i));
                }
            }
        });
    }

    static /* synthetic */ void access$2600(FramedConnection x0, int x1, List x2) {
        synchronized (x0) {
            if (x0.currentPushRequests.contains(Integer.valueOf(x1))) {
                x0.writeSynResetLater(x1, ErrorCode.PROTOCOL_ERROR);
                return;
            }
            x0.currentPushRequests.add(Integer.valueOf(x1));
            FramedConnection framedConnection = x0;
            final int i = x1;
            final List list = x2;
            x0.pushExecutor.execute(new NamedRunnable("OkHttp %s Push Request[%s]", new Object[]{x0.hostName, Integer.valueOf(x1)}) {
                public final void execute() {
                    FramedConnection.this.pushObserver.onRequest$163bb723();
                    try {
                        FramedConnection.this.frameWriter.rstStream(i, ErrorCode.CANCEL);
                        synchronized (FramedConnection.this) {
                            FramedConnection.this.currentPushRequests.remove(Integer.valueOf(i));
                        }
                    } catch (IOException e) {
                    }
                }
            });
        }
    }
}
