package com.squareup.okhttp.internal.http;

import android.support.v7.widget.helper.ItemTouchHelper.Callback;
import com.squareup.okhttp.Address;
import com.squareup.okhttp.CertificatePinner;
import com.squareup.okhttp.ConnectionPool;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Interceptor.Chain;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Protocol;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Request.Builder;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import com.squareup.okhttp.internal.Internal;
import com.squareup.okhttp.internal.InternalCache;
import com.squareup.okhttp.internal.Util;
import com.squareup.okhttp.internal.http.CacheStrategy.Factory;
import java.io.IOException;
import java.net.CookieHandler;
import java.net.ProtocolException;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.GzipSource;
import okio.Okio;
import okio.Sink;
import okio.Source;

public final class HttpEngine {
    private static final ResponseBody EMPTY_BODY = new ResponseBody() {
        public final MediaType contentType() {
            return null;
        }

        public final long contentLength() {
            return 0;
        }

        public final BufferedSource source() {
            return new Buffer();
        }
    };
    public final boolean bufferRequestBody;
    private Response cacheResponse;
    private CacheStrategy cacheStrategy;
    private final boolean callerWritesRequestBody;
    final OkHttpClient client;
    private final boolean forWebSocket;
    private HttpStream httpStream;
    private Request networkRequest;
    private final Response priorResponse;
    private Sink requestBodyOut;
    long sentRequestMillis = -1;
    private CacheRequest storeRequest;
    public final StreamAllocation streamAllocation;
    private boolean transparentGzip;
    private final Request userRequest;
    private Response userResponse;

    class NetworkInterceptorChain implements Chain {
        private int calls;
        private final int index;
        private final Request request;

        NetworkInterceptorChain(int index, Request request) {
            this.index = index;
            this.request = request;
        }

        public final Response proceed(Request request) throws IOException {
            this.calls++;
            if (this.index > 0) {
                Interceptor caller = (Interceptor) HttpEngine.this.client.networkInterceptors().get(this.index - 1);
                Address address = HttpEngine.this.streamAllocation.connection().getRoute().getAddress();
                if (!request.httpUrl().host().equals(address.getUriHost()) || request.httpUrl().port() != address.getUriPort()) {
                    throw new IllegalStateException("network interceptor " + caller + " must retain the same host and port");
                } else if (this.calls > 1) {
                    throw new IllegalStateException("network interceptor " + caller + " must call proceed() exactly once");
                }
            }
            if (this.index < HttpEngine.this.client.networkInterceptors().size()) {
                NetworkInterceptorChain chain = new NetworkInterceptorChain(this.index + 1, request);
                Interceptor interceptor = (Interceptor) HttpEngine.this.client.networkInterceptors().get(this.index);
                Response intercept$4449e3ea = interceptor.intercept$4449e3ea();
                if (chain.calls != 1) {
                    throw new IllegalStateException("network interceptor " + interceptor + " must call proceed() exactly once");
                } else if (intercept$4449e3ea != null) {
                    return intercept$4449e3ea;
                } else {
                    throw new NullPointerException("network interceptor " + interceptor + " returned null");
                }
            }
            HttpEngine.this.httpStream.writeRequestHeaders(request);
            HttpEngine.this.networkRequest = request;
            if (HttpEngine.permitsRequestBody(request) && request.body() != null) {
                BufferedSink bufferedRequestBody = Okio.buffer(HttpEngine.this.httpStream.createRequestBody(request, request.body().contentLength()));
                request.body().writeTo(bufferedRequestBody);
                bufferedRequestBody.close();
            }
            Response response = HttpEngine.this.readNetworkResponse();
            int code = response.code();
            if ((code != 204 && code != 205) || response.body().contentLength() <= 0) {
                return response;
            }
            throw new ProtocolException("HTTP " + code + " had non-zero Content-Length: " + response.body().contentLength());
        }
    }

    public HttpEngine(OkHttpClient client, Request request, boolean bufferRequestBody, boolean callerWritesRequestBody, boolean forWebSocket, StreamAllocation streamAllocation, RetryableSink requestBodyOut, Response priorResponse) {
        this.client = client;
        this.userRequest = request;
        this.bufferRequestBody = bufferRequestBody;
        this.callerWritesRequestBody = callerWritesRequestBody;
        this.forWebSocket = forWebSocket;
        if (streamAllocation == null) {
            ConnectionPool connectionPool = client.getConnectionPool();
            SSLSocketFactory sSLSocketFactory = null;
            HostnameVerifier hostnameVerifier = null;
            CertificatePinner certificatePinner = null;
            if (request.isHttps()) {
                sSLSocketFactory = client.getSslSocketFactory();
                hostnameVerifier = client.getHostnameVerifier();
                certificatePinner = client.getCertificatePinner();
            }
            StreamAllocation streamAllocation2 = new StreamAllocation(connectionPool, new Address(request.httpUrl().host(), request.httpUrl().port(), client.getDns(), client.getSocketFactory(), sSLSocketFactory, hostnameVerifier, certificatePinner, client.getAuthenticator(), client.getProxy(), client.getProtocols(), client.getConnectionSpecs(), client.getProxySelector()));
        }
        this.streamAllocation = streamAllocation;
        this.requestBodyOut = requestBodyOut;
        this.priorResponse = priorResponse;
    }

    public final void sendRequest() throws RequestException, RouteException, IOException {
        Response cacheCandidate = null;
        boolean z = true;
        if (this.cacheStrategy == null) {
            if (this.httpStream != null) {
                throw new IllegalStateException();
            }
            Request request = this.userRequest;
            Builder newBuilder = request.newBuilder();
            if (request.header("Host") == null) {
                newBuilder.header("Host", Util.hostHeader(request.httpUrl()));
            }
            if (request.header("Connection") == null) {
                newBuilder.header("Connection", "Keep-Alive");
            }
            if (request.header("Accept-Encoding") == null) {
                this.transparentGzip = true;
                newBuilder.header("Accept-Encoding", "gzip");
            }
            CookieHandler cookieHandler = this.client.getCookieHandler();
            if (cookieHandler != null) {
                OkHeaders.addCookies(newBuilder, cookieHandler.get(request.uri(), OkHeaders.toMultimap(newBuilder.build().headers(), null)));
            }
            if (request.header("User-Agent") == null) {
                newBuilder.header("User-Agent", "okhttp/2.7.5");
            }
            Request request2 = newBuilder.build();
            InternalCache responseCache = Internal.instance.internalCache(this.client);
            if (responseCache != null) {
                cacheCandidate = responseCache.get$7633b7c3();
            }
            this.cacheStrategy = new Factory(System.currentTimeMillis(), request2, cacheCandidate).get();
            this.networkRequest = this.cacheStrategy.networkRequest;
            this.cacheResponse = this.cacheStrategy.cacheResponse;
            if (cacheCandidate != null && this.cacheResponse == null) {
                Util.closeQuietly(cacheCandidate.body());
            }
            if (this.networkRequest != null) {
                if (this.networkRequest.method().equals("GET")) {
                    z = false;
                }
                this.httpStream = this.streamAllocation.newStream(this.client.getConnectTimeout(), this.client.getReadTimeout(), this.client.getWriteTimeout(), this.client.getRetryOnConnectionFailure(), z);
                this.httpStream.setHttpEngine(this);
                if (this.callerWritesRequestBody && HttpMethod.permitsRequestBody(this.networkRequest.method()) && this.requestBodyOut == null) {
                    long contentLength = OkHeaders.contentLength(request2);
                    if (!this.bufferRequestBody) {
                        this.httpStream.writeRequestHeaders(this.networkRequest);
                        this.requestBodyOut = this.httpStream.createRequestBody(this.networkRequest, contentLength);
                        return;
                    } else if (contentLength > 2147483647L) {
                        throw new IllegalStateException("Use setFixedLengthStreamingMode() or setChunkedStreamingMode() for requests larger than 2 GiB.");
                    } else if (contentLength != -1) {
                        this.httpStream.writeRequestHeaders(this.networkRequest);
                        this.requestBodyOut = new RetryableSink((int) contentLength);
                        return;
                    } else {
                        this.requestBodyOut = new RetryableSink();
                        return;
                    }
                }
                return;
            }
            if (this.cacheResponse != null) {
                this.userResponse = this.cacheResponse.newBuilder().request(this.userRequest).priorResponse(stripBody(this.priorResponse)).cacheResponse(stripBody(this.cacheResponse)).build();
            } else {
                this.userResponse = new Response.Builder().request(this.userRequest).priorResponse(stripBody(this.priorResponse)).protocol(Protocol.HTTP_1_1).code(504).message("Unsatisfiable Request (only-if-cached)").body(EMPTY_BODY).build();
            }
            this.userResponse = unzip(this.userResponse);
        }
    }

    private static Response stripBody(Response response) {
        return (response == null || response.body() == null) ? response : response.newBuilder().body(null).build();
    }

    public final void writingRequestHeaders() {
        if (this.sentRequestMillis != -1) {
            throw new IllegalStateException();
        }
        this.sentRequestMillis = System.currentTimeMillis();
    }

    static boolean permitsRequestBody(Request request) {
        return HttpMethod.permitsRequestBody(request.method());
    }

    public final Response getResponse() {
        if (this.userResponse != null) {
            return this.userResponse;
        }
        throw new IllegalStateException();
    }

    public final HttpEngine recover(RouteException e) {
        if (!this.streamAllocation.recover(e) || !this.client.getRetryOnConnectionFailure()) {
            return null;
        }
        return new HttpEngine(this.client, this.userRequest, this.bufferRequestBody, this.callerWritesRequestBody, this.forWebSocket, close(), (RetryableSink) this.requestBodyOut, this.priorResponse);
    }

    public final HttpEngine recover(IOException e, Sink requestBodyOut) {
        if (!this.streamAllocation.recover(e, null) || !this.client.getRetryOnConnectionFailure()) {
            return null;
        }
        return new HttpEngine(this.client, this.userRequest, this.bufferRequestBody, this.callerWritesRequestBody, this.forWebSocket, close(), null, this.priorResponse);
    }

    public final StreamAllocation close() {
        if (this.requestBodyOut != null) {
            Util.closeQuietly(this.requestBodyOut);
        }
        if (this.userResponse != null) {
            Util.closeQuietly(this.userResponse.body());
        } else {
            this.streamAllocation.connectionFailed();
        }
        return this.streamAllocation;
    }

    private Response unzip(Response response) throws IOException {
        if (!this.transparentGzip || !"gzip".equalsIgnoreCase(this.userResponse.header("Content-Encoding")) || response.body() == null) {
            return response;
        }
        Source responseBody = new GzipSource(response.body().source());
        Headers strippedHeaders = response.headers().newBuilder().removeAll("Content-Encoding").removeAll("Content-Length").build();
        return response.newBuilder().headers(strippedHeaders).body(new RealResponseBody(strippedHeaders, Okio.buffer(responseBody))).build();
    }

    public static boolean hasBody(Response response) {
        if (response.request().method().equals("HEAD")) {
            return false;
        }
        int responseCode = response.code();
        if ((responseCode < 100 || responseCode >= Callback.DEFAULT_DRAG_ANIMATION_DURATION) && responseCode != 204 && responseCode != 304) {
            return true;
        }
        if (OkHeaders.contentLength(response) != -1 || "chunked".equalsIgnoreCase(response.header("Transfer-Encoding"))) {
            return true;
        }
        return false;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void readResponse() throws java.io.IOException {
        /*
        r10 = this;
        r8 = -1;
        r4 = 1;
        r5 = 0;
        r3 = r10.userResponse;
        if (r3 == 0) goto L_0x0009;
    L_0x0008:
        return;
    L_0x0009:
        r3 = r10.networkRequest;
        if (r3 != 0) goto L_0x0019;
    L_0x000d:
        r3 = r10.cacheResponse;
        if (r3 != 0) goto L_0x0019;
    L_0x0011:
        r3 = new java.lang.IllegalStateException;
        r4 = "call sendRequest() first!";
        r3.<init>(r4);
        throw r3;
    L_0x0019:
        r3 = r10.networkRequest;
        if (r3 == 0) goto L_0x0008;
    L_0x001d:
        r3 = r10.forWebSocket;
        if (r3 == 0) goto L_0x00a6;
    L_0x0021:
        r3 = r10.httpStream;
        r6 = r10.networkRequest;
        r3.writeRequestHeaders(r6);
    L_0x0028:
        r2 = r10.readNetworkResponse();
    L_0x002c:
        r3 = r2.headers();
        r10.receiveHeaders(r3);
        r3 = r10.cacheResponse;
        if (r3 == 0) goto L_0x0141;
    L_0x0037:
        r3 = r10.cacheResponse;
        r6 = r2.code();
        r7 = 304; // 0x130 float:4.26E-43 double:1.5E-321;
        if (r6 != r7) goto L_0x010e;
    L_0x0041:
        r3 = r4;
    L_0x0042:
        if (r3 == 0) goto L_0x0138;
    L_0x0044:
        r3 = r10.cacheResponse;
        r3 = r3.newBuilder();
        r4 = r10.userRequest;
        r3 = r3.request(r4);
        r4 = r10.priorResponse;
        r4 = stripBody(r4);
        r3 = r3.priorResponse(r4);
        r4 = r10.cacheResponse;
        r4 = r4.headers();
        r5 = r2.headers();
        r4 = combine(r4, r5);
        r3 = r3.headers(r4);
        r4 = r10.cacheResponse;
        r4 = stripBody(r4);
        r3 = r3.cacheResponse(r4);
        r4 = stripBody(r2);
        r3 = r3.networkResponse(r4);
        r3 = r3.build();
        r10.userResponse = r3;
        r3 = r2.body();
        r3.close();
        r3 = r10.streamAllocation;
        r3.release();
        r3 = com.squareup.okhttp.internal.Internal.instance;
        r4 = r10.client;
        r3.internalCache(r4);
        r3 = r10.userResponse;
        stripBody(r3);
        r3 = r10.userResponse;
        r3 = r10.unzip(r3);
        r10.userResponse = r3;
        goto L_0x0008;
    L_0x00a6:
        r3 = r10.callerWritesRequestBody;
        if (r3 != 0) goto L_0x00b9;
    L_0x00aa:
        r3 = new com.squareup.okhttp.internal.http.HttpEngine$NetworkInterceptorChain;
        r6 = r10.networkRequest;
        r3.<init>(r5, r6);
        r6 = r10.networkRequest;
        r2 = r3.proceed(r6);
        goto L_0x002c;
    L_0x00b9:
        r6 = r10.sentRequestMillis;
        r3 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r3 != 0) goto L_0x00f4;
    L_0x00bf:
        r3 = r10.networkRequest;
        r6 = com.squareup.okhttp.internal.http.OkHeaders.contentLength(r3);
        r3 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r3 != 0) goto L_0x00ed;
    L_0x00c9:
        r3 = r10.requestBodyOut;
        r3 = r3 instanceof com.squareup.okhttp.internal.http.RetryableSink;
        if (r3 == 0) goto L_0x00ed;
    L_0x00cf:
        r3 = r10.requestBodyOut;
        r3 = (com.squareup.okhttp.internal.http.RetryableSink) r3;
        r0 = r3.contentLength();
        r3 = r10.networkRequest;
        r3 = r3.newBuilder();
        r6 = "Content-Length";
        r7 = java.lang.Long.toString(r0);
        r3 = r3.header(r6, r7);
        r3 = r3.build();
        r10.networkRequest = r3;
    L_0x00ed:
        r3 = r10.httpStream;
        r6 = r10.networkRequest;
        r3.writeRequestHeaders(r6);
    L_0x00f4:
        r3 = r10.requestBodyOut;
        if (r3 == 0) goto L_0x0028;
    L_0x00f8:
        r3 = r10.requestBodyOut;
        r3.close();
        r3 = r10.requestBodyOut;
        r3 = r3 instanceof com.squareup.okhttp.internal.http.RetryableSink;
        if (r3 == 0) goto L_0x0028;
    L_0x0103:
        r6 = r10.httpStream;
        r3 = r10.requestBodyOut;
        r3 = (com.squareup.okhttp.internal.http.RetryableSink) r3;
        r6.writeRequestBody(r3);
        goto L_0x0028;
    L_0x010e:
        r3 = r3.headers();
        r6 = "Last-Modified";
        r3 = r3.getDate(r6);
        if (r3 == 0) goto L_0x0135;
    L_0x011a:
        r6 = r2.headers();
        r7 = "Last-Modified";
        r6 = r6.getDate(r7);
        if (r6 == 0) goto L_0x0135;
    L_0x0126:
        r6 = r6.getTime();
        r8 = r3.getTime();
        r3 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r3 >= 0) goto L_0x0135;
    L_0x0132:
        r3 = r4;
        goto L_0x0042;
    L_0x0135:
        r3 = r5;
        goto L_0x0042;
    L_0x0138:
        r3 = r10.cacheResponse;
        r3 = r3.body();
        com.squareup.okhttp.internal.Util.closeQuietly(r3);
    L_0x0141:
        r3 = r2.newBuilder();
        r4 = r10.userRequest;
        r3 = r3.request(r4);
        r4 = r10.priorResponse;
        r4 = stripBody(r4);
        r3 = r3.priorResponse(r4);
        r4 = r10.cacheResponse;
        r4 = stripBody(r4);
        r3 = r3.cacheResponse(r4);
        r4 = stripBody(r2);
        r3 = r3.networkResponse(r4);
        r3 = r3.build();
        r10.userResponse = r3;
        r3 = r10.userResponse;
        r3 = hasBody(r3);
        if (r3 == 0) goto L_0x0008;
    L_0x0175:
        r3 = com.squareup.okhttp.internal.Internal.instance;
        r4 = r10.client;
        r3 = r3.internalCache(r4);
        if (r3 == 0) goto L_0x01b7;
    L_0x017f:
        r4 = r10.userResponse;
        r5 = r10.networkRequest;
        r4 = com.squareup.okhttp.internal.http.CacheStrategy.isCacheable(r4, r5);
        if (r4 != 0) goto L_0x01c5;
    L_0x0189:
        r3 = r10.networkRequest;
        r3 = r3.method();
        r4 = "POST";
        r4 = r3.equals(r4);
        if (r4 != 0) goto L_0x01b7;
    L_0x0197:
        r4 = "PATCH";
        r4 = r3.equals(r4);
        if (r4 != 0) goto L_0x01b7;
    L_0x019f:
        r4 = "PUT";
        r4 = r3.equals(r4);
        if (r4 != 0) goto L_0x01b7;
    L_0x01a7:
        r4 = "DELETE";
        r4 = r3.equals(r4);
        if (r4 != 0) goto L_0x01b7;
    L_0x01af:
        r4 = "MOVE";
        r3 = r3.equals(r4);
        if (r3 == 0) goto L_0x01b7;
    L_0x01b7:
        r4 = r10.storeRequest;
        r3 = r10.userResponse;
        if (r4 != 0) goto L_0x01d1;
    L_0x01bd:
        r3 = r10.unzip(r3);
        r10.userResponse = r3;
        goto L_0x0008;
    L_0x01c5:
        r4 = r10.userResponse;
        stripBody(r4);
        r3 = r3.put$3be241a0();
        r10.storeRequest = r3;
        goto L_0x01b7;
    L_0x01d1:
        r5 = r4.body();
        if (r5 == 0) goto L_0x01bd;
    L_0x01d7:
        r6 = r3.body();
        r6 = r6.source();
        r5 = okio.Okio.buffer(r5);
        r7 = new com.squareup.okhttp.internal.http.HttpEngine$2;
        r7.<init>(r6, r4, r5);
        r4 = r3.newBuilder();
        r5 = new com.squareup.okhttp.internal.http.RealResponseBody;
        r3 = r3.headers();
        r6 = okio.Okio.buffer(r7);
        r5.<init>(r3, r6);
        r3 = r4.body(r5);
        r3 = r3.build();
        goto L_0x01bd;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.squareup.okhttp.internal.http.HttpEngine.readResponse():void");
    }

    private Response readNetworkResponse() throws IOException {
        this.httpStream.finishRequest();
        Response networkResponse = this.httpStream.readResponseHeaders().request(this.networkRequest).handshake(this.streamAllocation.connection().getHandshake()).header(OkHeaders.SENT_MILLIS, Long.toString(this.sentRequestMillis)).header(OkHeaders.RECEIVED_MILLIS, Long.toString(System.currentTimeMillis())).build();
        if (!this.forWebSocket) {
            networkResponse = networkResponse.newBuilder().body(this.httpStream.openResponseBody(networkResponse)).build();
        }
        if ("close".equalsIgnoreCase(networkResponse.request().header("Connection")) || "close".equalsIgnoreCase(networkResponse.header("Connection"))) {
            this.streamAllocation.noNewStreams();
        }
        return networkResponse;
    }

    private static Headers combine(Headers cachedHeaders, Headers networkHeaders) throws IOException {
        int i;
        Headers.Builder result = new Headers.Builder();
        int size = cachedHeaders.size();
        for (i = 0; i < size; i++) {
            String fieldName = cachedHeaders.name(i);
            String value = cachedHeaders.value(i);
            if (!("Warning".equalsIgnoreCase(fieldName) && value.startsWith("1")) && (!OkHeaders.isEndToEnd(fieldName) || networkHeaders.get(fieldName) == null)) {
                result.add(fieldName, value);
            }
        }
        size = networkHeaders.size();
        for (i = 0; i < size; i++) {
            fieldName = networkHeaders.name(i);
            if (!"Content-Length".equalsIgnoreCase(fieldName) && OkHeaders.isEndToEnd(fieldName)) {
                result.add(fieldName, networkHeaders.value(i));
            }
        }
        return result.build();
    }

    public final void receiveHeaders(Headers headers) throws IOException {
        CookieHandler cookieHandler = this.client.getCookieHandler();
        if (cookieHandler != null) {
            cookieHandler.put(this.userRequest.uri(), OkHeaders.toMultimap(headers, null));
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final com.squareup.okhttp.Request followUpRequest() throws java.io.IOException {
        /*
        r12 = this;
        r9 = 0;
        r10 = r12.userResponse;
        if (r10 != 0) goto L_0x000b;
    L_0x0005:
        r9 = new java.lang.IllegalStateException;
        r9.<init>();
        throw r9;
    L_0x000b:
        r10 = r12.streamAllocation;
        r0 = r10.connection();
        if (r0 == 0) goto L_0x002d;
    L_0x0013:
        r5 = r0.getRoute();
    L_0x0017:
        if (r5 == 0) goto L_0x002f;
    L_0x0019:
        r7 = r5.getProxy();
    L_0x001d:
        r10 = r12.userResponse;
        r4 = r10.code();
        r10 = r12.userRequest;
        r2 = r10.method();
        switch(r4) {
            case 300: goto L_0x0063;
            case 301: goto L_0x0063;
            case 302: goto L_0x0063;
            case 303: goto L_0x0063;
            case 307: goto L_0x0053;
            case 308: goto L_0x0053;
            case 401: goto L_0x0046;
            case 407: goto L_0x0036;
            default: goto L_0x002c;
        };
    L_0x002c:
        return r9;
    L_0x002d:
        r5 = r9;
        goto L_0x0017;
    L_0x002f:
        r10 = r12.client;
        r7 = r10.getProxy();
        goto L_0x001d;
    L_0x0036:
        r9 = r7.type();
        r10 = java.net.Proxy.Type.HTTP;
        if (r9 == r10) goto L_0x0046;
    L_0x003e:
        r9 = new java.net.ProtocolException;
        r10 = "Received HTTP_PROXY_AUTH (407) code while not using proxy";
        r9.<init>(r10);
        throw r9;
    L_0x0046:
        r9 = r12.client;
        r9 = r9.getAuthenticator();
        r10 = r12.userResponse;
        r9 = com.squareup.okhttp.internal.http.OkHeaders.processAuthHeader(r9, r10, r7);
        goto L_0x002c;
    L_0x0053:
        r10 = "GET";
        r10 = r2.equals(r10);
        if (r10 != 0) goto L_0x0063;
    L_0x005b:
        r10 = "HEAD";
        r10 = r2.equals(r10);
        if (r10 == 0) goto L_0x002c;
    L_0x0063:
        r10 = r12.client;
        r10 = r10.getFollowRedirects();
        if (r10 == 0) goto L_0x002c;
    L_0x006b:
        r10 = r12.userResponse;
        r11 = "Location";
        r1 = r10.header(r11);
        if (r1 == 0) goto L_0x002c;
    L_0x0075:
        r10 = r12.userRequest;
        r10 = r10.httpUrl();
        r8 = r10.resolve(r1);
        if (r8 == 0) goto L_0x002c;
    L_0x0081:
        r10 = r8.scheme();
        r11 = r12.userRequest;
        r11 = r11.httpUrl();
        r11 = r11.scheme();
        r6 = r10.equals(r11);
        if (r6 != 0) goto L_0x009d;
    L_0x0095:
        r10 = r12.client;
        r10 = r10.getFollowSslRedirects();
        if (r10 == 0) goto L_0x002c;
    L_0x009d:
        r10 = r12.userRequest;
        r3 = r10.newBuilder();
        r10 = com.squareup.okhttp.internal.http.HttpMethod.permitsRequestBody(r2);
        if (r10 == 0) goto L_0x00c8;
    L_0x00a9:
        r10 = "PROPFIND";
        r10 = r2.equals(r10);
        if (r10 != 0) goto L_0x00dd;
    L_0x00b1:
        r10 = 1;
    L_0x00b2:
        if (r10 == 0) goto L_0x00df;
    L_0x00b4:
        r10 = "GET";
        r3.method(r10, r9);
    L_0x00b9:
        r9 = "Transfer-Encoding";
        r3.removeHeader(r9);
        r9 = "Content-Length";
        r3.removeHeader(r9);
        r9 = "Content-Type";
        r3.removeHeader(r9);
    L_0x00c8:
        r9 = r12.sameConnection(r8);
        if (r9 != 0) goto L_0x00d3;
    L_0x00ce:
        r9 = "Authorization";
        r3.removeHeader(r9);
    L_0x00d3:
        r9 = r3.url(r8);
        r9 = r9.build();
        goto L_0x002c;
    L_0x00dd:
        r10 = 0;
        goto L_0x00b2;
    L_0x00df:
        r3.method(r2, r9);
        goto L_0x00b9;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.squareup.okhttp.internal.http.HttpEngine.followUpRequest():com.squareup.okhttp.Request");
    }

    public final boolean sameConnection(HttpUrl followUp) {
        HttpUrl url = this.userRequest.httpUrl();
        return url.host().equals(followUp.host()) && url.port() == followUp.port() && url.scheme().equals(followUp.scheme());
    }
}
