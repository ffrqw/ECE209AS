package okhttp3.internal.cache;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Interceptor.Chain;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Response.Builder;
import okhttp3.internal.Internal;
import okhttp3.internal.Util;
import okhttp3.internal.cache.CacheStrategy.Factory;
import okhttp3.internal.http.HttpHeaders;
import okhttp3.internal.http.HttpMethod;
import okhttp3.internal.http.RealResponseBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import okio.Sink;
import okio.Source;
import okio.Timeout;

public final class CacheInterceptor implements Interceptor {
    final InternalCache cache;

    public CacheInterceptor(InternalCache cache) {
        this.cache = cache;
    }

    public final Response intercept(Chain chain) throws IOException {
        Response cacheCandidate = this.cache != null ? this.cache.get(chain.request()) : null;
        CacheStrategy strategy = new Factory(System.currentTimeMillis(), chain.request(), cacheCandidate).get();
        Request networkRequest = strategy.networkRequest;
        Response cacheResponse = strategy.cacheResponse;
        if (this.cache != null) {
            this.cache.trackResponse(strategy);
        }
        if (cacheCandidate != null && cacheResponse == null) {
            Util.closeQuietly(cacheCandidate.body());
        }
        if (networkRequest == null && cacheResponse == null) {
            return new Builder().request(chain.request()).protocol(Protocol.HTTP_1_1).code(504).message("Unsatisfiable Request (only-if-cached)").body(Util.EMPTY_RESPONSE).sentRequestAtMillis(-1).receivedResponseAtMillis(System.currentTimeMillis()).build();
        }
        if (networkRequest == null) {
            return cacheResponse.newBuilder().cacheResponse(stripBody(cacheResponse)).build();
        }
        try {
            Response response;
            Response networkResponse = chain.proceed(networkRequest);
            if (networkResponse == null) {
            }
            if (cacheResponse != null) {
                if (networkResponse.code() == 304) {
                    int i;
                    Builder newBuilder = cacheResponse.newBuilder();
                    Headers headers = cacheResponse.headers();
                    Headers headers2 = networkResponse.headers();
                    Headers.Builder builder = new Headers.Builder();
                    int size = headers.size();
                    for (i = 0; i < size; i++) {
                        String name = headers.name(i);
                        String value = headers.value(i);
                        if (!("Warning".equalsIgnoreCase(name) && value.startsWith("1")) && (!isEndToEnd(name) || headers2.get(name) == null)) {
                            Internal.instance.addLenient(builder, name, value);
                        }
                    }
                    int size2 = headers2.size();
                    for (i = 0; i < size2; i++) {
                        String name2 = headers2.name(i);
                        if (!"Content-Length".equalsIgnoreCase(name2) && isEndToEnd(name2)) {
                            Internal.instance.addLenient(builder, name2, headers2.value(i));
                        }
                    }
                    response = newBuilder.headers(builder.build()).sentRequestAtMillis(networkResponse.sentRequestAtMillis()).receivedResponseAtMillis(networkResponse.receivedResponseAtMillis()).cacheResponse(stripBody(cacheResponse)).networkResponse(stripBody(networkResponse)).build();
                    networkResponse.body().close();
                    this.cache.trackConditionalCacheHit();
                    this.cache.update(cacheResponse, response);
                    return response;
                }
                Util.closeQuietly(cacheResponse.body());
            }
            response = networkResponse.newBuilder().cacheResponse(stripBody(cacheResponse)).networkResponse(stripBody(networkResponse)).build();
            if (!HttpHeaders.hasBody(response)) {
                return response;
            }
            final CacheRequest cacheRequest = maybeCache(response, networkResponse.request(), this.cache);
            if (cacheRequest != null) {
                Sink body = cacheRequest.body();
                if (body != null) {
                    final BufferedSource source = response.body().source();
                    final BufferedSink buffer = Okio.buffer(body);
                    response = response.newBuilder().body(new RealResponseBody(response.headers(), Okio.buffer(new Source() {
                        boolean cacheRequestClosed;

                        public final long read(Buffer sink, long byteCount) throws IOException {
                            try {
                                long bytesRead = source.read(sink, byteCount);
                                if (bytesRead == -1) {
                                    if (!this.cacheRequestClosed) {
                                        this.cacheRequestClosed = true;
                                        buffer.close();
                                    }
                                    return -1;
                                }
                                sink.copyTo(buffer.buffer(), sink.size() - bytesRead, bytesRead);
                                buffer.emitCompleteSegments();
                                return bytesRead;
                            } catch (IOException e) {
                                if (!this.cacheRequestClosed) {
                                    this.cacheRequestClosed = true;
                                    cacheRequest.abort();
                                }
                                throw e;
                            }
                        }

                        public final Timeout timeout() {
                            return source.timeout();
                        }

                        public final void close() throws IOException {
                            if (!(this.cacheRequestClosed || Util.discard(this, 100, TimeUnit.MILLISECONDS))) {
                                this.cacheRequestClosed = true;
                                cacheRequest.abort();
                            }
                            source.close();
                        }
                    }))).build();
                }
            }
            return response;
        } finally {
            if (cacheCandidate != null) {
                Util.closeQuietly(cacheCandidate.body());
            }
        }
    }

    private static Response stripBody(Response response) {
        return (response == null || response.body() == null) ? response : response.newBuilder().body(null).build();
    }

    private static CacheRequest maybeCache(Response userResponse, Request networkRequest, InternalCache responseCache) throws IOException {
        if (responseCache == null) {
            return null;
        }
        if (CacheStrategy.isCacheable(userResponse, networkRequest)) {
            return responseCache.put(userResponse);
        }
        if (!HttpMethod.invalidatesCache(networkRequest.method())) {
            return null;
        }
        try {
            responseCache.remove(networkRequest);
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    private static boolean isEndToEnd(String fieldName) {
        return ("Connection".equalsIgnoreCase(fieldName) || "Keep-Alive".equalsIgnoreCase(fieldName) || "Proxy-Authenticate".equalsIgnoreCase(fieldName) || "Proxy-Authorization".equalsIgnoreCase(fieldName) || "TE".equalsIgnoreCase(fieldName) || "Trailers".equalsIgnoreCase(fieldName) || "Transfer-Encoding".equalsIgnoreCase(fieldName) || "Upgrade".equalsIgnoreCase(fieldName)) ? false : true;
    }
}
