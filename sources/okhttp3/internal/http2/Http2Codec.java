package okhttp3.internal.http2;

import java.io.IOException;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Response.Builder;
import okhttp3.ResponseBody;
import okhttp3.internal.Internal;
import okhttp3.internal.Util;
import okhttp3.internal.connection.StreamAllocation;
import okhttp3.internal.http.HttpCodec;
import okhttp3.internal.http.RealResponseBody;
import okhttp3.internal.http.RequestLine;
import okhttp3.internal.http.StatusLine;
import okio.ByteString;
import okio.ForwardingSource;
import okio.Okio;
import okio.Sink;
import okio.Source;

public final class Http2Codec implements HttpCodec {
    private static final ByteString CONNECTION = ByteString.encodeUtf8("connection");
    private static final ByteString ENCODING = ByteString.encodeUtf8("encoding");
    private static final ByteString HOST = ByteString.encodeUtf8("host");
    private static final List<ByteString> HTTP_2_SKIPPED_REQUEST_HEADERS = Util.immutableList(CONNECTION, HOST, KEEP_ALIVE, PROXY_CONNECTION, TE, TRANSFER_ENCODING, ENCODING, UPGRADE, Header.TARGET_METHOD, Header.TARGET_PATH, Header.TARGET_SCHEME, Header.TARGET_AUTHORITY);
    private static final List<ByteString> HTTP_2_SKIPPED_RESPONSE_HEADERS = Util.immutableList(CONNECTION, HOST, KEEP_ALIVE, PROXY_CONNECTION, TE, TRANSFER_ENCODING, ENCODING, UPGRADE);
    private static final ByteString KEEP_ALIVE = ByteString.encodeUtf8("keep-alive");
    private static final ByteString PROXY_CONNECTION = ByteString.encodeUtf8("proxy-connection");
    private static final ByteString TE = ByteString.encodeUtf8("te");
    private static final ByteString TRANSFER_ENCODING = ByteString.encodeUtf8("transfer-encoding");
    private static final ByteString UPGRADE = ByteString.encodeUtf8("upgrade");
    private final OkHttpClient client;
    private final Http2Connection connection;
    private Http2Stream stream;
    final StreamAllocation streamAllocation;

    class StreamFinishingSource extends ForwardingSource {
        public StreamFinishingSource(Source delegate) {
            super(delegate);
        }

        public final void close() throws IOException {
            Http2Codec.this.streamAllocation.streamFinished(false, Http2Codec.this);
            super.close();
        }
    }

    public Http2Codec(OkHttpClient client, StreamAllocation streamAllocation, Http2Connection connection) {
        this.client = client;
        this.streamAllocation = streamAllocation;
        this.connection = connection;
    }

    public final Sink createRequestBody(Request request, long contentLength) {
        return this.stream.getSink();
    }

    public final void writeRequestHeaders(Request request) throws IOException {
        int i = 0;
        if (this.stream == null) {
            boolean hasRequestBody;
            if (request.body() != null) {
                hasRequestBody = true;
            } else {
                hasRequestBody = false;
            }
            Headers headers = request.headers();
            List<Header> requestHeaders = new ArrayList(headers.size() + 4);
            requestHeaders.add(new Header(Header.TARGET_METHOD, request.method()));
            requestHeaders.add(new Header(Header.TARGET_PATH, RequestLine.requestPath(request.url())));
            String header = request.header("Host");
            if (header != null) {
                requestHeaders.add(new Header(Header.TARGET_AUTHORITY, header));
            }
            requestHeaders.add(new Header(Header.TARGET_SCHEME, request.url().scheme()));
            int size = headers.size();
            while (i < size) {
                ByteString encodeUtf8 = ByteString.encodeUtf8(headers.name(i).toLowerCase(Locale.US));
                if (!HTTP_2_SKIPPED_REQUEST_HEADERS.contains(encodeUtf8)) {
                    requestHeaders.add(new Header(encodeUtf8, headers.value(i)));
                }
                i++;
            }
            this.stream = this.connection.newStream(requestHeaders, hasRequestBody);
            this.stream.readTimeout.timeout((long) this.client.readTimeoutMillis(), TimeUnit.MILLISECONDS);
            this.stream.writeTimeout.timeout((long) this.client.writeTimeoutMillis(), TimeUnit.MILLISECONDS);
        }
    }

    public final void flushRequest() throws IOException {
        this.connection.writer.flush();
    }

    public final void finishRequest() throws IOException {
        this.stream.getSink().close();
    }

    public final Builder readResponseHeaders(boolean expectContinue) throws IOException {
        List<Header> headers = this.stream.takeResponseHeaders();
        Headers.Builder builder = new Headers.Builder();
        int size = headers.size();
        int i = 0;
        StatusLine statusLine = null;
        while (i < size) {
            Headers.Builder builder2;
            Builder builder3;
            Header header = (Header) headers.get(i);
            Object obj;
            if (header == null) {
                if (statusLine != null && statusLine.code == 100) {
                    builder2 = new Headers.Builder();
                    builder3 = null;
                }
                builder2 = builder;
                obj = statusLine;
            } else {
                ByteString byteString = header.name;
                String utf8 = header.value.utf8();
                if (byteString.equals(Header.RESPONSE_STATUS)) {
                    Headers.Builder builder4 = builder;
                    obj = StatusLine.parse("HTTP/1.1 " + utf8);
                    builder2 = builder4;
                } else {
                    if (!HTTP_2_SKIPPED_RESPONSE_HEADERS.contains(byteString)) {
                        Internal.instance.addLenient(builder, byteString.utf8(), utf8);
                    }
                    builder2 = builder;
                    obj = statusLine;
                }
            }
            i++;
            Object obj2 = builder3;
            builder = builder2;
        }
        if (statusLine == null) {
            throw new ProtocolException("Expected ':status' header not present");
        }
        Builder responseBuilder = new Builder().protocol(Protocol.HTTP_2).code(statusLine.code).message(statusLine.message).headers(builder.build());
        if (expectContinue && Internal.instance.code(responseBuilder) == 100) {
            return null;
        }
        return responseBuilder;
    }

    public final ResponseBody openResponseBody(Response response) throws IOException {
        return new RealResponseBody(response.headers(), Okio.buffer(new StreamFinishingSource(this.stream.getSource())));
    }
}
