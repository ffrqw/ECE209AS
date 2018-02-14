package okhttp3;

import android.support.v7.widget.helper.ItemTouchHelper.Callback;
import java.io.Closeable;

public final class Response implements Closeable {
    final ResponseBody body;
    private volatile CacheControl cacheControl;
    final Response cacheResponse;
    final int code;
    final Handshake handshake;
    final Headers headers;
    final String message;
    final Response networkResponse;
    final Response priorResponse;
    final Protocol protocol;
    final long receivedResponseAtMillis;
    final Request request;
    final long sentRequestAtMillis;

    public static class Builder {
        ResponseBody body;
        Response cacheResponse;
        int code;
        Handshake handshake;
        okhttp3.Headers.Builder headers;
        String message;
        Response networkResponse;
        Response priorResponse;
        Protocol protocol;
        long receivedResponseAtMillis;
        Request request;
        long sentRequestAtMillis;

        public Builder() {
            this.code = -1;
            this.headers = new okhttp3.Headers.Builder();
        }

        Builder(Response response) {
            this.code = -1;
            this.request = response.request;
            this.protocol = response.protocol;
            this.code = response.code;
            this.message = response.message;
            this.handshake = response.handshake;
            this.headers = response.headers.newBuilder();
            this.body = response.body;
            this.networkResponse = response.networkResponse;
            this.cacheResponse = response.cacheResponse;
            this.priorResponse = response.priorResponse;
            this.sentRequestAtMillis = response.sentRequestAtMillis;
            this.receivedResponseAtMillis = response.receivedResponseAtMillis;
        }

        public final Builder request(Request request) {
            this.request = request;
            return this;
        }

        public final Builder protocol(Protocol protocol) {
            this.protocol = protocol;
            return this;
        }

        public final Builder code(int code) {
            this.code = code;
            return this;
        }

        public final Builder message(String message) {
            this.message = message;
            return this;
        }

        public final Builder handshake(Handshake handshake) {
            this.handshake = handshake;
            return this;
        }

        public final Builder addHeader(String name, String value) {
            this.headers.add(name, value);
            return this;
        }

        public final Builder headers(Headers headers) {
            this.headers = headers.newBuilder();
            return this;
        }

        public final Builder body(ResponseBody body) {
            this.body = body;
            return this;
        }

        public final Builder networkResponse(Response networkResponse) {
            if (networkResponse != null) {
                checkSupportResponse("networkResponse", networkResponse);
            }
            this.networkResponse = networkResponse;
            return this;
        }

        public final Builder cacheResponse(Response cacheResponse) {
            if (cacheResponse != null) {
                checkSupportResponse("cacheResponse", cacheResponse);
            }
            this.cacheResponse = cacheResponse;
            return this;
        }

        private static void checkSupportResponse(String name, Response response) {
            if (response.body != null) {
                throw new IllegalArgumentException(name + ".body != null");
            } else if (response.networkResponse != null) {
                throw new IllegalArgumentException(name + ".networkResponse != null");
            } else if (response.cacheResponse != null) {
                throw new IllegalArgumentException(name + ".cacheResponse != null");
            } else if (response.priorResponse != null) {
                throw new IllegalArgumentException(name + ".priorResponse != null");
            }
        }

        public final Builder priorResponse(Response priorResponse) {
            if (priorResponse == null || priorResponse.body == null) {
                this.priorResponse = priorResponse;
                return this;
            }
            throw new IllegalArgumentException("priorResponse.body != null");
        }

        public final Builder sentRequestAtMillis(long sentRequestAtMillis) {
            this.sentRequestAtMillis = sentRequestAtMillis;
            return this;
        }

        public final Builder receivedResponseAtMillis(long receivedResponseAtMillis) {
            this.receivedResponseAtMillis = receivedResponseAtMillis;
            return this;
        }

        public final Response build() {
            if (this.request == null) {
                throw new IllegalStateException("request == null");
            } else if (this.protocol == null) {
                throw new IllegalStateException("protocol == null");
            } else if (this.code >= 0) {
                return new Response(this);
            } else {
                throw new IllegalStateException("code < 0: " + this.code);
            }
        }
    }

    Response(Builder builder) {
        this.request = builder.request;
        this.protocol = builder.protocol;
        this.code = builder.code;
        this.message = builder.message;
        this.handshake = builder.handshake;
        this.headers = builder.headers.build();
        this.body = builder.body;
        this.networkResponse = builder.networkResponse;
        this.cacheResponse = builder.cacheResponse;
        this.priorResponse = builder.priorResponse;
        this.sentRequestAtMillis = builder.sentRequestAtMillis;
        this.receivedResponseAtMillis = builder.receivedResponseAtMillis;
    }

    public final Request request() {
        return this.request;
    }

    public final int code() {
        return this.code;
    }

    public final boolean isSuccessful() {
        return this.code >= Callback.DEFAULT_DRAG_ANIMATION_DURATION && this.code < 300;
    }

    public final Handshake handshake() {
        return this.handshake;
    }

    public final Headers headers() {
        return this.headers;
    }

    public final ResponseBody body() {
        return this.body;
    }

    public final Builder newBuilder() {
        return new Builder(this);
    }

    public final Response networkResponse() {
        return this.networkResponse;
    }

    public final Response cacheResponse() {
        return this.cacheResponse;
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

    public final long sentRequestAtMillis() {
        return this.sentRequestAtMillis;
    }

    public final long receivedResponseAtMillis() {
        return this.receivedResponseAtMillis;
    }

    public final void close() {
        this.body.close();
    }

    public final String toString() {
        return "Response{protocol=" + this.protocol + ", code=" + this.code + ", message=" + this.message + ", url=" + this.request.url + '}';
    }

    public final String header(String name) {
        String str = this.headers.get(name);
        return str != null ? str : null;
    }
}
