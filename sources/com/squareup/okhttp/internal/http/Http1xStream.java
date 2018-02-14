package com.squareup.okhttp.internal.http;

import com.squareup.okhttp.Headers;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.Response.Builder;
import com.squareup.okhttp.ResponseBody;
import com.squareup.okhttp.internal.Internal;
import com.squareup.okhttp.internal.Util;
import java.io.EOFException;
import java.io.IOException;
import java.net.ProtocolException;
import java.util.concurrent.TimeUnit;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.ForwardingTimeout;
import okio.Okio;
import okio.Sink;
import okio.Source;
import okio.Timeout;

public final class Http1xStream implements HttpStream {
    private HttpEngine httpEngine;
    private final BufferedSink sink;
    private final BufferedSource source;
    private int state = 0;
    private final StreamAllocation streamAllocation;

    private abstract class AbstractSource implements Source {
        protected boolean closed;
        protected final ForwardingTimeout timeout;

        private AbstractSource() {
            this.timeout = new ForwardingTimeout(Http1xStream.this.source.timeout());
        }

        public final Timeout timeout() {
            return this.timeout;
        }

        protected final void endOfInput() throws IOException {
            if (Http1xStream.this.state != 5) {
                throw new IllegalStateException("state: " + Http1xStream.this.state);
            }
            Http1xStream.access$400(Http1xStream.this, this.timeout);
            Http1xStream.this.state = 6;
            if (Http1xStream.this.streamAllocation != null) {
                Http1xStream.this.streamAllocation.streamFinished(Http1xStream.this);
            }
        }

        protected final void unexpectedEndOfInput() {
            if (Http1xStream.this.state != 6) {
                Http1xStream.this.state = 6;
                if (Http1xStream.this.streamAllocation != null) {
                    Http1xStream.this.streamAllocation.noNewStreams();
                    Http1xStream.this.streamAllocation.streamFinished(Http1xStream.this);
                }
            }
        }
    }

    private final class ChunkedSink implements Sink {
        private boolean closed;
        private final ForwardingTimeout timeout;

        private ChunkedSink() {
            this.timeout = new ForwardingTimeout(Http1xStream.this.sink.timeout());
        }

        public final Timeout timeout() {
            return this.timeout;
        }

        public final void write(Buffer source, long byteCount) throws IOException {
            if (this.closed) {
                throw new IllegalStateException("closed");
            } else if (byteCount != 0) {
                Http1xStream.this.sink.writeHexadecimalUnsignedLong(byteCount);
                Http1xStream.this.sink.writeUtf8("\r\n");
                Http1xStream.this.sink.write(source, byteCount);
                Http1xStream.this.sink.writeUtf8("\r\n");
            }
        }

        public final synchronized void flush() throws IOException {
            if (!this.closed) {
                Http1xStream.this.sink.flush();
            }
        }

        public final synchronized void close() throws IOException {
            if (!this.closed) {
                this.closed = true;
                Http1xStream.this.sink.writeUtf8("0\r\n\r\n");
                Http1xStream.access$400(Http1xStream.this, this.timeout);
                Http1xStream.this.state = 3;
            }
        }
    }

    private class ChunkedSource extends AbstractSource {
        private long bytesRemainingInChunk = -1;
        private boolean hasMoreChunks = true;
        private final HttpEngine httpEngine;

        ChunkedSource(HttpEngine httpEngine) throws IOException {
            super();
            this.httpEngine = httpEngine;
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
                        Http1xStream.this.source.readUtf8LineStrict();
                    }
                    try {
                        this.bytesRemainingInChunk = Http1xStream.this.source.readHexadecimalUnsignedLong();
                        String trim = Http1xStream.this.source.readUtf8LineStrict().trim();
                        if (this.bytesRemainingInChunk < 0 || !(trim.isEmpty() || trim.startsWith(";"))) {
                            throw new ProtocolException("expected chunk size and optional extensions but was \"" + this.bytesRemainingInChunk + trim + "\"");
                        }
                        if (this.bytesRemainingInChunk == 0) {
                            this.hasMoreChunks = false;
                            this.httpEngine.receiveHeaders(Http1xStream.this.readHeaders());
                            endOfInput();
                        }
                        if (!this.hasMoreChunks) {
                            return -1;
                        }
                    } catch (NumberFormatException e) {
                        throw new ProtocolException(e.getMessage());
                    }
                }
                long read = Http1xStream.this.source.read(sink, Math.min(byteCount, this.bytesRemainingInChunk));
                if (read == -1) {
                    unexpectedEndOfInput();
                    throw new ProtocolException("unexpected end of stream");
                }
                this.bytesRemainingInChunk -= read;
                return read;
            }
        }

        public final void close() throws IOException {
            if (!this.closed) {
                if (this.hasMoreChunks && !Util.discard(this, 100, TimeUnit.MILLISECONDS)) {
                    unexpectedEndOfInput();
                }
                this.closed = true;
            }
        }
    }

    private final class FixedLengthSink implements Sink {
        private long bytesRemaining;
        private boolean closed;
        private final ForwardingTimeout timeout;

        private FixedLengthSink(long bytesRemaining) {
            this.timeout = new ForwardingTimeout(Http1xStream.this.sink.timeout());
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
            Http1xStream.this.sink.write(source, byteCount);
            this.bytesRemaining -= byteCount;
        }

        public final void flush() throws IOException {
            if (!this.closed) {
                Http1xStream.this.sink.flush();
            }
        }

        public final void close() throws IOException {
            if (!this.closed) {
                this.closed = true;
                if (this.bytesRemaining > 0) {
                    throw new ProtocolException("unexpected end of stream");
                }
                Http1xStream.access$400(Http1xStream.this, this.timeout);
                Http1xStream.this.state = 3;
            }
        }
    }

    private class FixedLengthSource extends AbstractSource {
        private long bytesRemaining;

        public FixedLengthSource(long length) throws IOException {
            super();
            this.bytesRemaining = length;
            if (this.bytesRemaining == 0) {
                endOfInput();
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
                long read = Http1xStream.this.source.read(sink, Math.min(this.bytesRemaining, byteCount));
                if (read == -1) {
                    unexpectedEndOfInput();
                    throw new ProtocolException("unexpected end of stream");
                }
                this.bytesRemaining -= read;
                if (this.bytesRemaining != 0) {
                    return read;
                }
                endOfInput();
                return read;
            }
        }

        public final void close() throws IOException {
            if (!this.closed) {
                if (!(this.bytesRemaining == 0 || Util.discard(this, 100, TimeUnit.MILLISECONDS))) {
                    unexpectedEndOfInput();
                }
                this.closed = true;
            }
        }
    }

    private class UnknownLengthSource extends AbstractSource {
        private boolean inputExhausted;

        private UnknownLengthSource() {
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
                long read = Http1xStream.this.source.read(sink, byteCount);
                if (read != -1) {
                    return read;
                }
                this.inputExhausted = true;
                endOfInput();
                return -1;
            }
        }

        public final void close() throws IOException {
            if (!this.closed) {
                if (!this.inputExhausted) {
                    unexpectedEndOfInput();
                }
                this.closed = true;
            }
        }
    }

    public Http1xStream(StreamAllocation streamAllocation, BufferedSource source, BufferedSink sink) {
        this.streamAllocation = streamAllocation;
        this.source = source;
        this.sink = sink;
    }

    public final void setHttpEngine(HttpEngine httpEngine) {
        this.httpEngine = httpEngine;
    }

    public final Sink createRequestBody(Request request, long contentLength) throws IOException {
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
        this.httpEngine.writingRequestHeaders();
        writeRequest(request.headers(), RequestLine.get(request, this.httpEngine.streamAllocation.connection().getRoute().getProxy().type()));
    }

    public final Builder readResponseHeaders() throws IOException {
        return readResponse();
    }

    public final ResponseBody openResponseBody(Response response) throws IOException {
        Source source;
        if (!HttpEngine.hasBody(response)) {
            source = newFixedLengthSource(0);
        } else if ("chunked".equalsIgnoreCase(response.header("Transfer-Encoding"))) {
            HttpEngine httpEngine = this.httpEngine;
            if (this.state != 4) {
                throw new IllegalStateException("state: " + this.state);
            }
            this.state = 5;
            source = new ChunkedSource(httpEngine);
        } else {
            long contentLength = OkHeaders.contentLength(response);
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

    public final Builder readResponse() throws IOException {
        if (this.state == 1 || this.state == 3) {
            Builder responseBuilder;
            StatusLine statusLine;
            do {
                try {
                    statusLine = StatusLine.parse(this.source.readUtf8LineStrict());
                    responseBuilder = new Builder().protocol(statusLine.protocol).code(statusLine.code).message(statusLine.message).headers(readHeaders());
                } catch (EOFException e) {
                    IOException exception = new IOException("unexpected end of stream on " + this.streamAllocation);
                    exception.initCause(e);
                    throw exception;
                }
            } while (statusLine.code == 100);
            this.state = 4;
            return responseBuilder;
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

    public final void writeRequestBody(RetryableSink requestBody) throws IOException {
        if (this.state != 1) {
            throw new IllegalStateException("state: " + this.state);
        }
        this.state = 3;
        requestBody.writeToSocket(this.sink);
    }

    public final Source newFixedLengthSource(long length) throws IOException {
        if (this.state != 4) {
            throw new IllegalStateException("state: " + this.state);
        }
        this.state = 5;
        return new FixedLengthSource(length);
    }

    static /* synthetic */ void access$400(Http1xStream x0, ForwardingTimeout x1) {
        Timeout delegate = x1.delegate();
        x1.setDelegate(Timeout.NONE);
        delegate.clearDeadline();
        delegate.clearTimeout();
    }
}
