package okhttp3.internal.http2;

import java.io.Closeable;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.Socket;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import okhttp3.internal.NamedRunnable;
import okhttp3.internal.Util;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.ByteString;

public final class Http2Connection implements Closeable {
    static final /* synthetic */ boolean $assertionsDisabled;
    static final ExecutorService executor = new ThreadPoolExecutor(0, ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED, 60, TimeUnit.SECONDS, new SynchronousQueue(), Util.threadFactory("OkHttp Http2Connection", true));
    long bytesLeftInWriteWindow;
    final boolean client;
    final Set<Integer> currentPushRequests = new LinkedHashSet();
    final String hostname;
    int lastGoodStreamId;
    final Listener listener;
    private int nextPingId;
    int nextStreamId;
    Settings okHttpSettings = new Settings();
    final Settings peerSettings = new Settings();
    private final ExecutorService pushExecutor;
    final PushObserver pushObserver;
    final ReaderRunnable readerRunnable;
    boolean receivedInitialPeerSettings = false;
    boolean shutdown;
    final Socket socket;
    final Map<Integer, Http2Stream> streams = new LinkedHashMap();
    long unacknowledgedBytesRead = 0;
    final Http2Writer writer;

    public static abstract class Listener {
        public static final Listener REFUSE_INCOMING_STREAMS = new Listener() {
            public final void onStream(Http2Stream stream) throws IOException {
                stream.close(ErrorCode.REFUSED_STREAM);
            }
        };

        public abstract void onStream(Http2Stream http2Stream) throws IOException;

        public void onSettings(Http2Connection connection) {
        }
    }

    /* renamed from: okhttp3.internal.http2.Http2Connection$3 */
    class AnonymousClass3 extends NamedRunnable {
        final /* synthetic */ int val$payload1;
        final /* synthetic */ int val$payload2;
        final /* synthetic */ Ping val$ping;
        final /* synthetic */ boolean val$reply;

        AnonymousClass3(String format, Object[] args, boolean z, int i, int i2, Ping ping) {
            this.val$reply = z;
            this.val$payload1 = i;
            this.val$payload2 = i2;
            this.val$ping = ping;
            super(format, args);
        }

        public final void execute() {
            try {
                Http2Connection http2Connection = Http2Connection.this;
                boolean z = this.val$reply;
                int i = this.val$payload1;
                int i2 = this.val$payload2;
                Ping ping = this.val$ping;
                synchronized (http2Connection.writer) {
                    if (ping != null) {
                        ping.send();
                    }
                    http2Connection.writer.ping(z, i, i2);
                }
            } catch (IOException e) {
            }
        }
    }

    public static class Builder {
        boolean client = true;
        String hostname;
        Listener listener = Listener.REFUSE_INCOMING_STREAMS;
        PushObserver pushObserver = PushObserver.CANCEL;
        BufferedSink sink;
        Socket socket;
        BufferedSource source;

        public Builder(boolean client) {
        }

        public final Builder socket(Socket socket, String hostname, BufferedSource source, BufferedSink sink) {
            this.socket = socket;
            this.hostname = hostname;
            this.source = source;
            this.sink = sink;
            return this;
        }

        public final Builder listener(Listener listener) {
            this.listener = listener;
            return this;
        }

        public final Http2Connection build() throws IOException {
            return new Http2Connection(this);
        }
    }

    class ReaderRunnable extends NamedRunnable implements Handler {
        final Http2Reader reader;

        ReaderRunnable(Http2Reader reader) {
            super("OkHttp %s", this$0.hostname);
            this.reader = reader;
        }

        protected final void execute() {
            ErrorCode connectionErrorCode = ErrorCode.INTERNAL_ERROR;
            ErrorCode streamErrorCode = ErrorCode.INTERNAL_ERROR;
            try {
                this.reader.readConnectionPreface(this);
                while (true) {
                    if (!this.reader.nextFrame(false, this)) {
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
                    Http2Connection.this.close(connectionErrorCode, streamErrorCode);
                } catch (IOException e2) {
                }
                Util.closeQuietly(this.reader);
            }
        }

        public final void data(boolean inFinished, int streamId, BufferedSource source, int length) throws IOException {
            if (Http2Connection.pushedStream(streamId)) {
                Http2Connection.this.pushDataLater(streamId, source, length, inFinished);
                return;
            }
            Http2Stream dataStream = Http2Connection.this.getStream(streamId);
            if (dataStream == null) {
                Http2Connection.this.writeSynResetLater(streamId, ErrorCode.PROTOCOL_ERROR);
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
        public final void headers$64c3d190(boolean r10, int r11, java.util.List<okhttp3.internal.http2.Header> r12) {
            /*
            r9 = this;
            r1 = okhttp3.internal.http2.Http2Connection.pushedStream(r11);
            if (r1 == 0) goto L_0x000c;
        L_0x0006:
            r1 = okhttp3.internal.http2.Http2Connection.this;
            r1.pushHeadersLater(r11, r12, r10);
        L_0x000b:
            return;
        L_0x000c:
            r7 = okhttp3.internal.http2.Http2Connection.this;
            monitor-enter(r7);
            r1 = okhttp3.internal.http2.Http2Connection.this;	 Catch:{ all -> 0x0017 }
            r1 = r1.shutdown;	 Catch:{ all -> 0x0017 }
            if (r1 == 0) goto L_0x001a;
        L_0x0015:
            monitor-exit(r7);	 Catch:{ all -> 0x0017 }
            goto L_0x000b;
        L_0x0017:
            r1 = move-exception;
            monitor-exit(r7);	 Catch:{ all -> 0x0017 }
            throw r1;
        L_0x001a:
            r1 = okhttp3.internal.http2.Http2Connection.this;	 Catch:{ all -> 0x0017 }
            r6 = r1.getStream(r11);	 Catch:{ all -> 0x0017 }
            if (r6 != 0) goto L_0x006f;
        L_0x0022:
            r1 = okhttp3.internal.http2.Http2Connection.this;	 Catch:{ all -> 0x0017 }
            r1 = r1.lastGoodStreamId;	 Catch:{ all -> 0x0017 }
            if (r11 > r1) goto L_0x002a;
        L_0x0028:
            monitor-exit(r7);	 Catch:{ all -> 0x0017 }
            goto L_0x000b;
        L_0x002a:
            r1 = r11 % 2;
            r2 = okhttp3.internal.http2.Http2Connection.this;	 Catch:{ all -> 0x0017 }
            r2 = r2.nextStreamId;	 Catch:{ all -> 0x0017 }
            r2 = r2 % 2;
            if (r1 != r2) goto L_0x0036;
        L_0x0034:
            monitor-exit(r7);	 Catch:{ all -> 0x0017 }
            goto L_0x000b;
        L_0x0036:
            r0 = new okhttp3.internal.http2.Http2Stream;	 Catch:{ all -> 0x0017 }
            r2 = okhttp3.internal.http2.Http2Connection.this;	 Catch:{ all -> 0x0017 }
            r3 = 0;
            r1 = r11;
            r4 = r10;
            r5 = r12;
            r0.<init>(r1, r2, r3, r4, r5);	 Catch:{ all -> 0x0017 }
            r1 = okhttp3.internal.http2.Http2Connection.this;	 Catch:{ all -> 0x0017 }
            r1.lastGoodStreamId = r11;	 Catch:{ all -> 0x0017 }
            r1 = okhttp3.internal.http2.Http2Connection.this;	 Catch:{ all -> 0x0017 }
            r1 = r1.streams;	 Catch:{ all -> 0x0017 }
            r2 = java.lang.Integer.valueOf(r11);	 Catch:{ all -> 0x0017 }
            r1.put(r2, r0);	 Catch:{ all -> 0x0017 }
            r1 = okhttp3.internal.http2.Http2Connection.executor;	 Catch:{ all -> 0x0017 }
            r2 = new okhttp3.internal.http2.Http2Connection$ReaderRunnable$1;	 Catch:{ all -> 0x0017 }
            r3 = "OkHttp %s stream %d";
            r4 = 2;
            r4 = new java.lang.Object[r4];	 Catch:{ all -> 0x0017 }
            r5 = 0;
            r8 = okhttp3.internal.http2.Http2Connection.this;	 Catch:{ all -> 0x0017 }
            r8 = r8.hostname;	 Catch:{ all -> 0x0017 }
            r4[r5] = r8;	 Catch:{ all -> 0x0017 }
            r5 = 1;
            r8 = java.lang.Integer.valueOf(r11);	 Catch:{ all -> 0x0017 }
            r4[r5] = r8;	 Catch:{ all -> 0x0017 }
            r2.<init>(r3, r4, r0);	 Catch:{ all -> 0x0017 }
            r1.execute(r2);	 Catch:{ all -> 0x0017 }
            monitor-exit(r7);	 Catch:{ all -> 0x0017 }
            goto L_0x000b;
        L_0x006f:
            monitor-exit(r7);	 Catch:{ all -> 0x0017 }
            r6.receiveHeaders(r12);
            if (r10 == 0) goto L_0x000b;
        L_0x0075:
            r6.receiveFin();
            goto L_0x000b;
            */
            throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.http2.Http2Connection.ReaderRunnable.headers$64c3d190(boolean, int, java.util.List):void");
        }

        public final void rstStream(int streamId, ErrorCode errorCode) {
            if (Http2Connection.pushedStream(streamId)) {
                Http2Connection.this.pushResetLater(streamId, errorCode);
                return;
            }
            Http2Stream rstStream = Http2Connection.this.removeStream(streamId);
            if (rstStream != null) {
                rstStream.receiveRstStream(errorCode);
            }
        }

        public final void settings(boolean clearPrevious, Settings newSettings) {
            long delta = 0;
            Http2Stream[] streamsToNotify = null;
            synchronized (Http2Connection.this) {
                int priorWriteWindowSize = Http2Connection.this.peerSettings.getInitialWindowSize();
                Settings settings = Http2Connection.this.peerSettings;
                for (int i = 0; i < 10; i++) {
                    if (newSettings.isSet(i)) {
                        settings.set(i, newSettings.get(i));
                    }
                }
                final Settings settings2 = newSettings;
                Http2Connection.executor.execute(new NamedRunnable("OkHttp %s ACK Settings", new Object[]{Http2Connection.this.hostname}) {
                    public final void execute() {
                        try {
                            Http2Connection.this.writer.applyAndAckSettings(settings2);
                        } catch (IOException e) {
                        }
                    }
                });
                int peerInitialWindowSize = Http2Connection.this.peerSettings.getInitialWindowSize();
                if (!(peerInitialWindowSize == -1 || peerInitialWindowSize == priorWriteWindowSize)) {
                    delta = (long) (peerInitialWindowSize - priorWriteWindowSize);
                    if (!Http2Connection.this.receivedInitialPeerSettings) {
                        Http2Connection http2Connection = Http2Connection.this;
                        http2Connection.bytesLeftInWriteWindow += delta;
                        if (delta > 0) {
                            http2Connection.notifyAll();
                        }
                        Http2Connection.this.receivedInitialPeerSettings = true;
                    }
                    if (!Http2Connection.this.streams.isEmpty()) {
                        streamsToNotify = (Http2Stream[]) Http2Connection.this.streams.values().toArray(new Http2Stream[Http2Connection.this.streams.size()]);
                    }
                }
                Http2Connection.executor.execute(new NamedRunnable("OkHttp %s settings", Http2Connection.this.hostname) {
                    public final void execute() {
                        Http2Connection.this.listener.onSettings(Http2Connection.this);
                    }
                });
            }
            if (streamsToNotify != null && delta != 0) {
                for (Http2Stream stream : streamsToNotify) {
                    synchronized (stream) {
                        stream.addBytesToWriteWindow(delta);
                    }
                }
            }
        }

        public final void ping(boolean reply, int payload1, int payload2) {
            if (reply) {
                Ping ping = Http2Connection.this.removePing(payload1);
                if (ping != null) {
                    ping.receive();
                    return;
                }
                return;
            }
            Http2Connection http2Connection = Http2Connection.this;
            Http2Connection.executor.execute(new AnonymousClass3("OkHttp %s ping %08x%08x", new Object[]{http2Connection.hostname, Integer.valueOf(payload1), Integer.valueOf(payload2)}, true, payload1, payload2, null));
        }

        public final void goAway$4b802bc(int lastGoodStreamId, ByteString debugData) {
            debugData.size();
            synchronized (Http2Connection.this) {
                Http2Stream[] streamsCopy = (Http2Stream[]) Http2Connection.this.streams.values().toArray(new Http2Stream[Http2Connection.this.streams.size()]);
                Http2Connection.this.shutdown = true;
            }
            for (Http2Stream http2Stream : streamsCopy) {
                if (http2Stream.id > lastGoodStreamId && http2Stream.isLocallyInitiated()) {
                    http2Stream.receiveRstStream(ErrorCode.REFUSED_STREAM);
                    Http2Connection.this.removeStream(http2Stream.id);
                }
            }
        }

        public final void windowUpdate(int streamId, long windowSizeIncrement) {
            if (streamId == 0) {
                synchronized (Http2Connection.this) {
                    Http2Connection http2Connection = Http2Connection.this;
                    http2Connection.bytesLeftInWriteWindow += windowSizeIncrement;
                    Http2Connection.this.notifyAll();
                }
                return;
            }
            Http2Stream stream = Http2Connection.this.getStream(streamId);
            if (stream != null) {
                synchronized (stream) {
                    stream.addBytesToWriteWindow(windowSizeIncrement);
                }
            }
        }

        public final void pushPromise$16014a7a(int promisedStreamId, List<Header> requestHeaders) {
            Http2Connection.this.pushRequestLater(promisedStreamId, requestHeaders);
        }
    }

    static {
        boolean z;
        if (Http2Connection.class.desiredAssertionStatus()) {
            z = false;
        } else {
            z = true;
        }
        $assertionsDisabled = z;
    }

    Http2Connection(Builder builder) {
        int i = 2;
        this.pushObserver = builder.pushObserver;
        this.client = builder.client;
        this.listener = builder.listener;
        this.nextStreamId = builder.client ? 1 : 2;
        if (builder.client) {
            this.nextStreamId += 2;
        }
        if (builder.client) {
            i = 1;
        }
        this.nextPingId = i;
        if (builder.client) {
            this.okHttpSettings.set(7, 16777216);
        }
        this.hostname = builder.hostname;
        this.pushExecutor = new ThreadPoolExecutor(0, 1, 60, TimeUnit.SECONDS, new LinkedBlockingQueue(), Util.threadFactory(Util.format("OkHttp %s Push Observer", this.hostname), true));
        this.peerSettings.set(7, 65535);
        this.peerSettings.set(5, 16384);
        this.bytesLeftInWriteWindow = (long) this.peerSettings.getInitialWindowSize();
        this.socket = builder.socket;
        this.writer = new Http2Writer(builder.sink, this.client);
        this.readerRunnable = new ReaderRunnable(new Http2Reader(builder.source, this.client));
    }

    final synchronized Http2Stream getStream(int id) {
        return (Http2Stream) this.streams.get(Integer.valueOf(id));
    }

    final synchronized Http2Stream removeStream(int streamId) {
        Http2Stream stream;
        stream = (Http2Stream) this.streams.remove(Integer.valueOf(streamId));
        notifyAll();
        return stream;
    }

    public final synchronized int maxConcurrentStreams() {
        return this.peerSettings.getMaxConcurrentStreams(ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED);
    }

    public final Http2Stream newStream(List<Header> requestHeaders, boolean out) throws IOException {
        return newStream(0, requestHeaders, out);
    }

    private Http2Stream newStream(int associatedStreamId, List<Header> requestHeaders, boolean out) throws IOException {
        boolean outFinished;
        Http2Stream stream;
        boolean flushHeaders;
        if (out) {
            outFinished = false;
        } else {
            outFinished = true;
        }
        synchronized (this.writer) {
            int streamId;
            synchronized (this) {
                if (this.shutdown) {
                    throw new ConnectionShutdownException();
                }
                streamId = this.nextStreamId;
                this.nextStreamId += 2;
                stream = new Http2Stream(streamId, this, outFinished, false, requestHeaders);
                if (!out || this.bytesLeftInWriteWindow == 0 || stream.bytesLeftInWriteWindow == 0) {
                    flushHeaders = true;
                } else {
                    flushHeaders = false;
                }
                if (stream.isOpen()) {
                    this.streams.put(Integer.valueOf(streamId), stream);
                }
            }
            this.writer.synStream$64c3d190(outFinished, streamId, requestHeaders);
        }
        if (flushHeaders) {
            this.writer.flush();
        }
        return stream;
    }

    public final void writeData(int streamId, boolean outFinished, Buffer buffer, long byteCount) throws IOException {
        if (byteCount == 0) {
            this.writer.data(outFinished, streamId, buffer, 0);
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
                toWrite = Math.min((int) Math.min(byteCount, this.bytesLeftInWriteWindow), this.writer.maxDataLength());
                this.bytesLeftInWriteWindow -= (long) toWrite;
            }
            byteCount -= (long) toWrite;
            Http2Writer http2Writer = this.writer;
            if (outFinished && byteCount == 0) {
                z = true;
            } else {
                z = false;
            }
            http2Writer.data(z, streamId, buffer, toWrite);
        }
    }

    final void writeSynResetLater(int streamId, ErrorCode errorCode) {
        final int i = streamId;
        final ErrorCode errorCode2 = errorCode;
        executor.execute(new NamedRunnable("OkHttp %s stream %d", new Object[]{this.hostname, Integer.valueOf(streamId)}) {
            public final void execute() {
                try {
                    Http2Connection.this.writeSynReset(i, errorCode2);
                } catch (IOException e) {
                }
            }
        });
    }

    final void writeSynReset(int streamId, ErrorCode statusCode) throws IOException {
        this.writer.rstStream(streamId, statusCode);
    }

    final void writeWindowUpdateLater(int streamId, long unacknowledgedBytesRead) {
        final int i = streamId;
        final long j = unacknowledgedBytesRead;
        executor.execute(new NamedRunnable("OkHttp Window Update %s stream %d", new Object[]{this.hostname, Integer.valueOf(streamId)}) {
            public final void execute() {
                try {
                    Http2Connection.this.writer.windowUpdate(i, j);
                } catch (IOException e) {
                }
            }
        });
    }

    final synchronized Ping removePing(int id) {
        return null;
    }

    public final void close() throws IOException {
        close(ErrorCode.NO_ERROR, ErrorCode.CANCEL);
    }

    final void close(ErrorCode connectionCode, ErrorCode streamCode) throws IOException {
        if ($assertionsDisabled || !Thread.holdsLock(this)) {
            IOException thrown = null;
            try {
                synchronized (this.writer) {
                    synchronized (this) {
                        if (this.shutdown) {
                        } else {
                            this.shutdown = true;
                            this.writer.goAway(this.lastGoodStreamId, connectionCode, Util.EMPTY_BYTE_ARRAY);
                        }
                    }
                    if (streamsToClose != null) {
                        for (Http2Stream stream : streamsToClose) {
                            try {
                                stream.close(streamCode);
                            } catch (IOException e) {
                                if (thrown != null) {
                                    thrown = e;
                                }
                            }
                        }
                    }
                    this.writer.close();
                    this.socket.close();
                    if (thrown != null) {
                        throw thrown;
                    }
                    return;
                }
            } catch (IOException e2) {
                thrown = e2;
            }
            Http2Stream[] streamsToClose = null;
            synchronized (this) {
                if (!this.streams.isEmpty()) {
                    streamsToClose = (Http2Stream[]) this.streams.values().toArray(new Http2Stream[this.streams.size()]);
                    this.streams.clear();
                }
            }
            if (streamsToClose != null) {
                while (r5 < r6) {
                    stream.close(streamCode);
                }
            }
            try {
                this.writer.close();
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

    public final void start() throws IOException {
        this.writer.connectionPreface();
        this.writer.settings(this.okHttpSettings);
        int initialWindowSize = this.okHttpSettings.getInitialWindowSize();
        if (initialWindowSize != 65535) {
            this.writer.windowUpdate(0, (long) (initialWindowSize - 65535));
        }
        new Thread(this.readerRunnable).start();
    }

    public final synchronized boolean isShutdown() {
        return this.shutdown;
    }

    static boolean pushedStream(int streamId) {
        return streamId != 0 && (streamId & 1) == 0;
    }

    final void pushRequestLater(int streamId, List<Header> requestHeaders) {
        synchronized (this) {
            if (this.currentPushRequests.contains(Integer.valueOf(streamId))) {
                writeSynResetLater(streamId, ErrorCode.PROTOCOL_ERROR);
                return;
            }
            this.currentPushRequests.add(Integer.valueOf(streamId));
            final int i = streamId;
            final List<Header> list = requestHeaders;
            this.pushExecutor.execute(new NamedRunnable("OkHttp %s Push Request[%s]", new Object[]{this.hostname, Integer.valueOf(streamId)}) {
                public final void execute() {
                    Http2Connection.this.pushObserver.onRequest$163bb723();
                    try {
                        Http2Connection.this.writer.rstStream(i, ErrorCode.CANCEL);
                        synchronized (Http2Connection.this) {
                            Http2Connection.this.currentPushRequests.remove(Integer.valueOf(i));
                        }
                    } catch (IOException e) {
                    }
                }
            });
        }
    }

    final void pushHeadersLater(int streamId, List<Header> requestHeaders, boolean inFinished) {
        final int i = streamId;
        final List<Header> list = requestHeaders;
        final boolean z = inFinished;
        this.pushExecutor.execute(new NamedRunnable("OkHttp %s Push Headers[%s]", new Object[]{this.hostname, Integer.valueOf(streamId)}) {
            public final void execute() {
                Http2Connection.this.pushObserver.onHeaders$4ec42067();
                try {
                    Http2Connection.this.writer.rstStream(i, ErrorCode.CANCEL);
                    synchronized (Http2Connection.this) {
                        Http2Connection.this.currentPushRequests.remove(Integer.valueOf(i));
                    }
                } catch (IOException e) {
                }
            }
        });
    }

    final void pushDataLater(int streamId, BufferedSource source, int byteCount, boolean inFinished) throws IOException {
        final Buffer buffer = new Buffer();
        source.require((long) byteCount);
        source.read(buffer, (long) byteCount);
        if (buffer.size() != ((long) byteCount)) {
            throw new IOException(buffer.size() + " != " + byteCount);
        }
        final int i = streamId;
        final int i2 = byteCount;
        final boolean z = inFinished;
        this.pushExecutor.execute(new NamedRunnable("OkHttp %s Push Data[%s]", new Object[]{this.hostname, Integer.valueOf(streamId)}) {
            public final void execute() {
                try {
                    Http2Connection.this.pushObserver.onData$749b27ff(buffer, i2);
                    Http2Connection.this.writer.rstStream(i, ErrorCode.CANCEL);
                    synchronized (Http2Connection.this) {
                        Http2Connection.this.currentPushRequests.remove(Integer.valueOf(i));
                    }
                } catch (IOException e) {
                }
            }
        });
    }

    final void pushResetLater(int streamId, ErrorCode errorCode) {
        final int i = streamId;
        final ErrorCode errorCode2 = errorCode;
        this.pushExecutor.execute(new NamedRunnable("OkHttp %s Push Reset[%s]", new Object[]{this.hostname, Integer.valueOf(streamId)}) {
            public final void execute() {
                Http2Connection.this.pushObserver.onReset$613c779f();
                synchronized (Http2Connection.this) {
                    Http2Connection.this.currentPushRequests.remove(Integer.valueOf(i));
                }
            }
        });
    }
}
