package okhttp3.internal.cache;

import android.support.v7.widget.helper.ItemTouchHelper.Callback;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import okhttp3.CacheControl;
import okhttp3.Headers;
import okhttp3.Headers.Builder;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.Internal;
import okhttp3.internal.http.HttpDate;
import okhttp3.internal.http.HttpHeaders;

public final class CacheStrategy {
    public final Response cacheResponse;
    public final Request networkRequest;

    public static class Factory {
        private int ageSeconds = -1;
        final Response cacheResponse;
        private String etag;
        private Date expires;
        private Date lastModified;
        private String lastModifiedString;
        final long nowMillis;
        private long receivedResponseMillis;
        final Request request;
        private long sentRequestMillis;
        private Date servedDate;
        private String servedDateString;

        public Factory(long nowMillis, Request request, Response cacheResponse) {
            this.nowMillis = nowMillis;
            this.request = request;
            this.cacheResponse = cacheResponse;
            if (cacheResponse != null) {
                this.sentRequestMillis = cacheResponse.sentRequestAtMillis();
                this.receivedResponseMillis = cacheResponse.receivedResponseAtMillis();
                Headers headers = cacheResponse.headers();
                int size = headers.size();
                for (int i = 0; i < size; i++) {
                    String fieldName = headers.name(i);
                    String value = headers.value(i);
                    if ("Date".equalsIgnoreCase(fieldName)) {
                        this.servedDate = HttpDate.parse(value);
                        this.servedDateString = value;
                    } else if ("Expires".equalsIgnoreCase(fieldName)) {
                        this.expires = HttpDate.parse(value);
                    } else if ("Last-Modified".equalsIgnoreCase(fieldName)) {
                        this.lastModified = HttpDate.parse(value);
                        this.lastModifiedString = value;
                    } else if ("ETag".equalsIgnoreCase(fieldName)) {
                        this.etag = value;
                    } else if ("Age".equalsIgnoreCase(fieldName)) {
                        this.ageSeconds = HttpHeaders.parseSeconds(value, -1);
                    }
                }
            }
        }

        public final CacheStrategy get() {
            CacheStrategy cacheStrategy;
            if (this.cacheResponse == null) {
                cacheStrategy = new CacheStrategy(this.request, null);
            } else if (this.request.isHttps() && this.cacheResponse.handshake() == null) {
                cacheStrategy = new CacheStrategy(this.request, null);
            } else if (CacheStrategy.isCacheable(this.cacheResponse, this.request)) {
                CacheControl cacheControl = this.request.cacheControl();
                if (!cacheControl.noCache()) {
                    Object obj;
                    Request request = this.request;
                    if (request.header("If-Modified-Since") == null && request.header("If-None-Match") == null) {
                        obj = null;
                    } else {
                        obj = 1;
                    }
                    if (obj == null) {
                        long max = this.servedDate != null ? Math.max(0, this.receivedResponseMillis - this.servedDate.getTime()) : 0;
                        if (this.ageSeconds != -1) {
                            max = Math.max(max, TimeUnit.SECONDS.toMillis((long) this.ageSeconds));
                        }
                        long j = (max + (this.receivedResponseMillis - this.sentRequestMillis)) + (this.nowMillis - this.receivedResponseMillis);
                        CacheControl cacheControl2 = this.cacheResponse.cacheControl();
                        if (cacheControl2.maxAgeSeconds() != -1) {
                            max = TimeUnit.SECONDS.toMillis((long) cacheControl2.maxAgeSeconds());
                        } else if (this.expires != null) {
                            max = this.expires.getTime() - (this.servedDate != null ? this.servedDate.getTime() : this.receivedResponseMillis);
                            if (max <= 0) {
                                max = 0;
                            }
                        } else if (this.lastModified == null || this.cacheResponse.request().url().query() != null) {
                            max = 0;
                        } else {
                            max = (this.servedDate != null ? this.servedDate.getTime() : this.sentRequestMillis) - this.lastModified.getTime();
                            max = max > 0 ? max / 10 : 0;
                        }
                        if (cacheControl.maxAgeSeconds() != -1) {
                            max = Math.min(max, TimeUnit.SECONDS.toMillis((long) cacheControl.maxAgeSeconds()));
                        }
                        long j2 = 0;
                        if (cacheControl.minFreshSeconds() != -1) {
                            j2 = TimeUnit.SECONDS.toMillis((long) cacheControl.minFreshSeconds());
                        }
                        long j3 = 0;
                        cacheControl2 = this.cacheResponse.cacheControl();
                        if (!(cacheControl2.mustRevalidate() || cacheControl.maxStaleSeconds() == -1)) {
                            j3 = TimeUnit.SECONDS.toMillis((long) cacheControl.maxStaleSeconds());
                        }
                        if (cacheControl2.noCache() || j + j2 >= r6 + max) {
                            String str;
                            String str2;
                            if (this.etag != null) {
                                str = "If-None-Match";
                                str2 = this.etag;
                            } else if (this.lastModified != null) {
                                str = "If-Modified-Since";
                                str2 = this.lastModifiedString;
                            } else if (this.servedDate != null) {
                                str = "If-Modified-Since";
                                str2 = this.servedDateString;
                            } else {
                                cacheStrategy = new CacheStrategy(this.request, null);
                            }
                            Builder newBuilder = this.request.headers().newBuilder();
                            Internal.instance.addLenient(newBuilder, str, str2);
                            cacheStrategy = new CacheStrategy(this.request.newBuilder().headers(newBuilder.build()).build(), this.cacheResponse);
                        } else {
                            Response.Builder newBuilder2 = this.cacheResponse.newBuilder();
                            if (j2 + j >= max) {
                                newBuilder2.addHeader("Warning", "110 HttpURLConnection \"Response is stale\"");
                            }
                            if (j > 86400000) {
                                if (this.cacheResponse.cacheControl().maxAgeSeconds() == -1 && this.expires == null) {
                                    obj = 1;
                                } else {
                                    obj = null;
                                }
                                if (obj != null) {
                                    newBuilder2.addHeader("Warning", "113 HttpURLConnection \"Heuristic expiration\"");
                                }
                            }
                            cacheStrategy = new CacheStrategy(null, newBuilder2.build());
                        }
                    }
                }
                cacheStrategy = new CacheStrategy(this.request, null);
            } else {
                cacheStrategy = new CacheStrategy(this.request, null);
            }
            if (cacheStrategy.networkRequest == null || !this.request.cacheControl().onlyIfCached()) {
                return cacheStrategy;
            }
            return new CacheStrategy(null, null);
        }
    }

    CacheStrategy(Request networkRequest, Response cacheResponse) {
        this.networkRequest = networkRequest;
        this.cacheResponse = cacheResponse;
    }

    public static boolean isCacheable(Response response, Request request) {
        switch (response.code()) {
            case Callback.DEFAULT_DRAG_ANIMATION_DURATION /*200*/:
            case 203:
            case 204:
            case 300:
            case 301:
            case 308:
            case 404:
            case 405:
            case 410:
            case 414:
            case 501:
                break;
            case 302:
            case 307:
                if (response.header("Expires") == null && response.cacheControl().maxAgeSeconds() == -1 && !response.cacheControl().isPublic() && !response.cacheControl().isPrivate()) {
                    return false;
                }
            default:
                return false;
        }
        return (response.cacheControl().noStore() || request.cacheControl().noStore()) ? false : true;
    }
}
