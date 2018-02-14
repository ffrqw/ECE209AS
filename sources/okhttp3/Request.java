package okhttp3;

import java.util.List;
import okhttp3.internal.http.HttpMethod;

public final class Request {
    final RequestBody body;
    private volatile CacheControl cacheControl;
    final Headers headers;
    final String method;
    final Object tag;
    final HttpUrl url;

    public static class Builder {
        RequestBody body;
        okhttp3.Headers.Builder headers;
        String method;
        Object tag;
        HttpUrl url;

        public Builder() {
            this.method = "GET";
            this.headers = new okhttp3.Headers.Builder();
        }

        Builder(Request request) {
            this.url = request.url;
            this.method = request.method;
            this.body = request.body;
            this.tag = request.tag;
            this.headers = request.headers.newBuilder();
        }

        public final Builder url(HttpUrl url) {
            if (url == null) {
                throw new NullPointerException("url == null");
            }
            this.url = url;
            return this;
        }

        public final Builder url(String url) {
            if (url == null) {
                throw new NullPointerException("url == null");
            }
            if (url.regionMatches(true, 0, "ws:", 0, 3)) {
                url = "http:" + url.substring(3);
            } else {
                if (url.regionMatches(true, 0, "wss:", 0, 4)) {
                    url = "https:" + url.substring(4);
                }
            }
            HttpUrl parsed = HttpUrl.parse(url);
            if (parsed != null) {
                return url(parsed);
            }
            throw new IllegalArgumentException("unexpected url: " + url);
        }

        public final Builder header(String name, String value) {
            this.headers.set(name, value);
            return this;
        }

        public final Builder removeHeader(String name) {
            this.headers.removeAll(name);
            return this;
        }

        public final Builder headers(Headers headers) {
            this.headers = headers.newBuilder();
            return this;
        }

        public final Builder method(String method, RequestBody body) {
            if (method == null) {
                throw new NullPointerException("method == null");
            } else if (method.length() == 0) {
                throw new IllegalArgumentException("method.length() == 0");
            } else if (body != null && !HttpMethod.permitsRequestBody(method)) {
                throw new IllegalArgumentException("method " + method + " must not have a request body.");
            } else if (body == null && HttpMethod.requiresRequestBody(method)) {
                throw new IllegalArgumentException("method " + method + " must have a request body.");
            } else {
                this.method = method;
                this.body = body;
                return this;
            }
        }

        public final Request build() {
            if (this.url != null) {
                return new Request(this);
            }
            throw new IllegalStateException("url == null");
        }
    }

    Request(Builder builder) {
        Object obj;
        this.url = builder.url;
        this.method = builder.method;
        this.headers = builder.headers.build();
        this.body = builder.body;
        if (builder.tag != null) {
            obj = builder.tag;
        } else {
            Request request = this;
        }
        this.tag = obj;
    }

    public final HttpUrl url() {
        return this.url;
    }

    public final String method() {
        return this.method;
    }

    public final Headers headers() {
        return this.headers;
    }

    public final String header(String name) {
        return this.headers.get(name);
    }

    public final List<String> headers(String name) {
        return this.headers.values(name);
    }

    public final RequestBody body() {
        return this.body;
    }

    public final Builder newBuilder() {
        return new Builder(this);
    }

    public final CacheControl cacheControl() {
        CacheControl result = this.cacheControl;
        if (result != null) {
            return result;
        }
        result = CacheControl.parse(this.headers);
        this.cacheControl = result;
        return result;
    }

    public final boolean isHttps() {
        return this.url.isHttps();
    }

    public final String toString() {
        return "Request{method=" + this.method + ", url=" + this.url + ", tag=" + (this.tag != this ? this.tag : null) + '}';
    }
}
