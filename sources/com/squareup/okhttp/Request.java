package com.squareup.okhttp;

import com.squareup.okhttp.internal.http.HttpMethod;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

public final class Request {
    private final RequestBody body;
    private volatile CacheControl cacheControl;
    private final Headers headers;
    private volatile URI javaNetUri;
    private final String method;
    private final Object tag;
    private final HttpUrl url;

    public static class Builder {
        private RequestBody body;
        private com.squareup.okhttp.Headers.Builder headers;
        private String method;
        private Object tag;
        private HttpUrl url;

        public Builder() {
            this.method = "GET";
            this.headers = new com.squareup.okhttp.Headers.Builder();
        }

        private Builder(Request request) {
            this.url = request.url;
            this.method = request.method;
            this.body = request.body;
            this.tag = request.tag;
            this.headers = request.headers.newBuilder();
        }

        public final Builder url(HttpUrl url) {
            if (url == null) {
                throw new IllegalArgumentException("url == null");
            }
            this.url = url;
            return this;
        }

        public final Builder url(URL url) {
            if (url == null) {
                throw new IllegalArgumentException("url == null");
            }
            HttpUrl parsed = HttpUrl.get(url);
            if (parsed != null) {
                return url(parsed);
            }
            throw new IllegalArgumentException("unexpected url: " + url);
        }

        public final Builder header(String name, String value) {
            this.headers.set(name, value);
            return this;
        }

        public final Builder addHeader(String name, String value) {
            this.headers.add(name, value);
            return this;
        }

        public final Builder removeHeader(String name) {
            this.headers.removeAll(name);
            return this;
        }

        public final Builder method(String method, RequestBody body) {
            if (method == null || method.length() == 0) {
                throw new IllegalArgumentException("method == null || method.length() == 0");
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
                return new Request();
            }
            throw new IllegalStateException("url == null");
        }
    }

    private Request(Builder builder) {
        Object access$400;
        this.url = builder.url;
        this.method = builder.method;
        this.headers = builder.headers.build();
        this.body = builder.body;
        if (builder.tag != null) {
            access$400 = builder.tag;
        } else {
            Request request = this;
        }
        this.tag = access$400;
    }

    public final HttpUrl httpUrl() {
        return this.url;
    }

    public final URI uri() throws IOException {
        try {
            URI result = this.javaNetUri;
            if (result == null) {
                result = this.url.uri();
                this.javaNetUri = result;
            }
            return result;
        } catch (IllegalStateException e) {
            throw new IOException(e.getMessage());
        }
    }

    public final String urlString() {
        return this.url.toString();
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

    public final RequestBody body() {
        return this.body;
    }

    public final Builder newBuilder() {
        return new Builder();
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
