package okhttp3.internal.http1;

import java.io.EOFException;
import java.io.IOException;
import java.net.ProtocolException;
import java.util.concurrent.TimeUnit;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Response.Builder;
import okhttp3.ResponseBody;
import okhttp3.internal.Internal;
import okhttp3.internal.Util;
import okhttp3.internal.connection.StreamAllocation;
import okhttp3.internal.http.HttpCodec;
import okhttp3.internal.http.HttpHeaders;
import okhttp3.internal.http.RealResponseBody;
import okhttp3.internal.http.RequestLine;
import okhttp3.internal.http.StatusLine;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.ForwardingTimeout;
import okio.Okio;
import okio.Sink;
import okio.Source;
import okio.Timeout;

public final class Http1Codec implements HttpCodec {
    final OkHttpClient client;
    final BufferedSink sink;
    final BufferedSource source;
    int state = 0;
    final StreamAllocation streamAllocation;

    private abstract class AbstractSource implements Source {
        protected boolean closed;
        protected final ForwardingTimeout timeout;

        private AbstractSource() {
            this.timeout = new ForwardingTimeout(Http1Codec.this.source.timeout());
        }

        public final Timeout timeout() {
            return this.timeout;
        }

        protected final void endOfInput(boolean reuseConnection) throws IOException {
            if (Http1Codec.this.state != 6) {
                if (Http1Codec.this.state != 5) {
                    throw new IllegalStateException("state: " + Http1Codec.this.state);
                }
                Http1Codec.detachTimeout(this.timeout);
                Http1Codec.this.state = 6;
                if (Http1Codec.this.streamAllocation != null) {
                    Http1Codec.this.streamAllocation.streamFinished(!reuseConnection, Http1Codec.this);
                }
            }
        }
    }

    private final class ChunkedSink implements Sink {
        private boolean closed;
        private final ForwardingTimeout timeout = new ForwardingTimeout(Http1Codec.this.sink.timeout());

        ChunkedSink() {
        }

        public final Timeout timeout() {
            return this.timeout;
        }

        public final void write(Buffer source, long byteCount) throws IOException {
            if (this.closed) {
                throw new IllegalStateException("closed");
            } else if (byteCount != 0) {
                Http1Codec.this.sink.writeHexadecimalUnsignedLong(byteCount);
                Http1Codec.this.sink.writeUtf8("\r\n");
                Http1Codec.this.sink.write(source, byteCount);
                Http1Codec.this.sink.writeUtf8("\r\n");
            }
        }

        public final synchronized void flush() throws IOException {
            if (!this.closed) {
                Http1Codec.this.sink.flush();
            }
        }

        public final synchronized void close() throws IOException {
            if (!this.closed) {
                this.closed = true;
                Http1Codec.this.sink.writeUtf8("0\r\n\r\n");
                Http1Codec.detachTimeout(this.timeout);
                Http1Codec.this.state = 3;
            }
        }
    }

    private class ChunkedSource extends AbstractSource {
        private long bytesRemainingInChunk = -1;
        private boolean hasMoreChunks = true;
        private final HttpUrl url;

        ChunkedSource(HttpUrl url) {
            super();
            this.url = url;
        }

        public final long read(Buffer sink, long byteCount) throws IOException {
            if (byteCount < 0) {
                throw new IllegalArgumentException("byteCount < 0: " + byteCount);
            } else if (this.closed) {
                throw new IllegalStateException("closed");
            } else if (!this.hasMoreChunks) {
                return -1;
            } else {
                if (this.bytesRemainingInChunk == 0 || this.bytesRemainingInChunk == -1) {
                    if (this.bytesRemainingInChunk != -1) {
                        Http1Codec.this.source.readUtf8LineStrict();
                    }
                    try {
                        this.bytesRemainingInChunk = Http1Codec.this.source.readHexadecimalUnsignedLong();
                        String trim = Http1Codec.this.source.readUtf8LineStrict().trim();
                        if (this.bytesRemainingInChunk < 0 || !(trim.isEmpty() || trim.startsWith(";"))) {
                            throw new ProtocolException("expected chunk size and optional extensions but was \"" + this.bytesRemainingInChunk + trim + "\"");
                        }
                        if (this.bytesRemainingInChunk == 0) {
                            this.hasMoreChunks = false;
                            HttpHeaders.receiveHeaders(Http1Codec.this.client.cookieJar(), this.url, Http1Codec.this.readHeaders());
                            endOfInput(true);
                        }
                        if (!this.hasMoreChunks) {
                            return -1;
                        }
                    } catch (NumberFormatException e) {
                        throw new ProtocolException(e.getMessage());
                    }
                }
                long read = Http1Codec.this.source.read(sink, Math.min(byteCount, this.bytesRemainingInChunk));
                if (read == -1) {
                    endOfInput(false);
                    throw new ProtocolException("unexpected end of stream");
                }
                this.bytesRemainingInChunk -= read;
                return read;
            }
        }

        public final void close() throws IOException {
            if (!this.closed) {
                if (this.hasMoreChunks && !Util.discard(this, 100, TimeUnit.MILLISECONDS)) {
                    endOfInput(false);
                }
                this.closed = true;
            }
        }
    }

    private final class FixedLengthSink implements Sink {
        private long bytesRemaining;
        private boolean closed;
        private final ForwardingTimeout timeout = new ForwardingTimeout(Http1Codec.this.sink.timeout());

        FixedLengthSink(long bytesRemaining) {
            this.bytesRemaining = bytesRemaining;
        }

        public final Timeout timeout() {
            return this.timeout;
        }

        public final void write(Buffer source, long byteCount) throws IOException {
            if (this.closed) {
                throw new IllegalStateException("closed");
            }
            Util.checkOffsetAndCount(source.size(), 0, byteCount);
            if (byteCount > this.bytesRemaining) {
                throw new ProtocolException("expected " + this.bytesRemaining + " bytes but received " + byteCount);
            }
            Http1Codec.this.sink.write(source, byteCount);
            this.bytesRemaining -= byteCount;
        }

        public final void flush() throws IOException {
            if (!this.closed) {
                Http1Codec.this.sink.flush();
            }
        }

        public final void close() throws IOException {
            if (!this.closed) {
                this.closed = true;
                if (this.bytesRemaining > 0) {
                    throw new ProtocolException("unexpected end of stream");
                }
                Http1Codec.detachTimeout(this.timeout);
                Http1Codec.this.state = 3;
            }
        }
    }

    private class FixedLengthSource extends AbstractSource {
        private long bytesRemaining;

        public FixedLengthSource(long length) throws IOException {
            super();
            this.bytesRemaining = length;
            if (this.bytesRemaining == 0) {
                endOfInput(true);
            }
        }

        public final long read(Buffer sink, long byteCount) throws IOException {
            if (byteCount < 0) {
                throw new IllegalArgumentException("byteCount < 0: " + byteCount);
            } else if (this.closed) {
                throw new IllegalStateException("closed");
            } else if (this.bytesRemaining == 0) {
                return -1;
            } else {
                long read = Http1Codec.this.source.read(sink, Math.min(this.bytesRemaining, byteCount));
                if (read == -1) {
                    endOfInput(false);
                    throw new ProtocolException("unexpected end of stream");
                }
                this.bytesRemaining -= read;
                if (this.bytesRemaining != 0) {
                    return read;
                }
                endOfInput(true);
                return read;
            }
        }

        public final void close() throws IOException {
            if (!this.closed) {
                if (!(this.bytesRemaining == 0 || Util.discard(this, 100, TimeUnit.MILLISECONDS))) {
                    endOfInput(false);
                }
                this.closed = true;
            }
        }
    }

    private class UnknownLengthSource extends AbstractSource {
        private boolean inputExhausted;

        UnknownLengthSource() {
            super();
        }

        public final long read(Buffer sink, long byteCount) throws IOException {
            if (byteCount < 0) {
                throw new IllegalArgumentException("byteCount < 0: " + byteCount);
            } else if (this.closed) {
                throw new IllegalStateException("closed");
            } else if (this.inputExhausted) {
                return -1;
            } else {
                long read = Http1Codec.this.source.read(sink, byteCount);
                if (read != -1) {
                    return read;
                }
                this.inputExhausted = true;
                endOfInput(true);
                return -1;
            }
        }

        public final void close() throws IOException {
            if (!this.closed) {
                if (!this.inputExhausted) {
                    endOfInput(false);
                }
                this.closed = true;
            }
        }
    }

    public Http1Codec(OkHttpClient client, StreamAllocation streamAllocation, BufferedSource source, BufferedSink sink) {
        this.client = client;
        this.streamAllocation = streamAllocation;
        this.source = source;
        this.sink = sink;
    }

    public final Sink createRequestBody(Request request, long contentLength) {
        if ("chunked".equalsIgnoreCase(request.header("Transfer-Encoding"))) {
            if (this.state != 1) {
                throw new IllegalStateException("state: " + this.state);
            }
            this.state = 2;
            return new ChunkedSink();
        } else if (contentLength == -1) {
            throw new IllegalStateException("Cannot stream a request body without chunked encoding or a known content length!");
        } else if (this.state != 1) {
            throw new IllegalStateException("state: " + this.state);
        } else {
            this.state = 2;
            return new FixedLengthSink(contentLength);
        }
    }

    public final void writeRequestHeaders(Request request) throws IOException {
        writeRequest(request.headers(), RequestLine.get(request, this.streamAllocation.connection().route().proxy().type()));
    }

    public final ResponseBody openResponseBody(Response response) throws IOException {
        Source source;
        if (!HttpHeaders.hasBody(response)) {
            source = newFixedLengthSource(0);
        } else if ("chunked".equalsIgnoreCase(response.header("Transfer-Encoding"))) {
            HttpUrl url = response.request().url();
            if (this.state != 4) {
                throw new IllegalStateException("state: " + this.state);
            }
            this.state = 5;
            source = new ChunkedSource(url);
        } else {
            long contentLength = HttpHeaders.contentLength(response);
            if (contentLength != -1) {
                source = newFixedLengthSource(contentLength);
            } else if (this.state != 4) {
                throw new IllegalStateException("state: " + this.state);
            } else if (this.streamAllocation == null) {
                throw new IllegalStateException("streamAllocation == null");
            } else {
                this.state = 5;
                this.streamAllocation.noNewStreams();
                source = new UnknownLengthSource();
            }
        }
        return new RealResponseBody(response.headers(), Okio.buffer(source));
    }

    public final void flushRequest() throws IOException {
        this.sink.flush();
    }

    public final void finishRequest() throws IOException {
        this.sink.flush();
    }

    public final void writeRequest(Headers headers, String requestLine) throws IOException {
        if (this.state != 0) {
            throw new IllegalStateException("state: " + this.state);
        }
        this.sink.writeUtf8(requestLine).writeUtf8("\r\n");
        int size = headers.size();
        for (int i = 0; i < size; i++) {
            this.sink.writeUtf8(headers.name(i)).writeUtf8(": ").writeUtf8(headers.value(i)).writeUtf8("\r\n");
        }
        this.sink.writeUtf8("\r\n");
        this.state = 1;
    }

    public final Builder readResponseHeaders(boolean expectContinue) throws IOException {
        if (this.state == 1 || this.state == 3) {
            try {
                StatusLine statusLine = StatusLine.parse(this.source.readUtf8LineStrict());
                Builder responseBuilder = new Builder().protocol(statusLine.protocol).code(statusLine.code).message(statusLine.message).headers(readHeaders());
                if (expectContinue && statusLine.code == 100) {
                    return null;
                }
                this.state = 4;
                return responseBuilder;
            } catch (EOFException e) {
                IOException exception = new IOException("unexpected end of stream on " + this.streamAllocation);
                exception.initCause(e);
                throw exception;
            }
        }
        throw new IllegalStateException("state: " + this.state);
    }

    public final Headers readHeaders() throws IOException {
        Headers.Builder headers = new Headers.Builder();
        while (true) {
            String line = this.source.readUtf8LineStrict();
            if (line.length() == 0) {
                return headers.build();
            }
            Internal.instance.addLenient(headers, line);
        }
    }

    public final Source newFixedLengthSource(long length) throws IOException {
        if (this.state != 4) {
            throw new IllegalStateException("state: " + this.state);
        }
        this.state = 5;
        return new FixedLengthSource(length);
    }

    static void detachTimeout(ForwardingTimeout timeout) {
        Timeout oldDelegate = timeout.delegate();
        timeout.setDelegate(Timeout.NONE);
        oldDelegate.clearDeadline();
        oldDelegate.clearTimeout();
    }
}
