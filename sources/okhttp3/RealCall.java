package okhttp3;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.HttpUrl.Builder;
import okhttp3.internal.NamedRunnable;
import okhttp3.internal.cache.CacheInterceptor;
import okhttp3.internal.cache.InternalCache;
import okhttp3.internal.connection.ConnectInterceptor;
import okhttp3.internal.http.BridgeInterceptor;
import okhttp3.internal.http.CallServerInterceptor;
import okhttp3.internal.http.RealInterceptorChain;
import okhttp3.internal.http.RetryAndFollowUpInterceptor;
import okhttp3.internal.platform.Platform;

final class RealCall implements Call {
    final OkHttpClient client;
    private boolean executed;
    final boolean forWebSocket;
    final Request originalRequest;
    final RetryAndFollowUpInterceptor retryAndFollowUpInterceptor;

    final class AsyncCall extends NamedRunnable {
        final /* synthetic */ RealCall this$0;

        final String host() {
            return this.this$0.originalRequest.url.host;
        }

        protected final void execute() {
            try {
                this.this$0.getResponseWithInterceptorChain();
                if (this.this$0.retryAndFollowUpInterceptor.isCanceled()) {
                    IOException iOException = new IOException("Canceled");
                }
                this.this$0.client.dispatcher.finished(this);
            } catch (IOException e) {
                if (false) {
                    String str;
                    Platform platform = Platform.get();
                    StringBuilder stringBuilder = new StringBuilder("Callback failure for ");
                    RealCall realCall = this.this$0;
                    StringBuilder stringBuilder2 = new StringBuilder();
                    if (realCall.retryAndFollowUpInterceptor.isCanceled()) {
                        str = "canceled ";
                    } else {
                        str = "";
                    }
                    stringBuilder2 = stringBuilder2.append(str);
                    if (realCall.forWebSocket) {
                        str = "web socket";
                    } else {
                        str = "call";
                    }
                    StringBuilder append = stringBuilder2.append(str).append(" to ");
                    Builder newBuilder = realCall.originalRequest.url.newBuilder("/...");
                    newBuilder.encodedUsername = HttpUrl.canonicalize("", " \"':;<=>@[]^`{}|/\\?#", false, false, false, true);
                    newBuilder.encodedPassword = HttpUrl.canonicalize("", " \"':;<=>@[]^`{}|/\\?#", false, false, false, true);
                    platform.log(4, stringBuilder.append(append.append(newBuilder.build().toString()).toString()).toString(), e);
                }
                this.this$0.client.dispatcher.finished(this);
            } catch (Throwable th) {
                this.this$0.client.dispatcher.finished(this);
            }
        }
    }

    RealCall(OkHttpClient client, Request originalRequest, boolean forWebSocket) {
        this.client = client;
        this.originalRequest = originalRequest;
        this.forWebSocket = forWebSocket;
        this.retryAndFollowUpInterceptor = new RetryAndFollowUpInterceptor(client, forWebSocket);
    }

    public final Response execute() throws IOException {
        synchronized (this) {
            if (this.executed) {
                throw new IllegalStateException("Already Executed");
            }
            this.executed = true;
        }
        this.retryAndFollowUpInterceptor.setCallStackTrace(Platform.get().getStackTraceForCloseable("response.body().close()"));
        try {
            this.client.dispatcher.executed(this);
            Response result = getResponseWithInterceptorChain();
            if (result != null) {
                return result;
            }
            throw new IOException("Canceled");
        } finally {
            this.client.dispatcher.finished(this);
        }
    }

    final Response getResponseWithInterceptorChain() throws IOException {
        InternalCache internalCache;
        List<Interceptor> interceptors = new ArrayList();
        interceptors.addAll(this.client.interceptors);
        interceptors.add(this.retryAndFollowUpInterceptor);
        interceptors.add(new BridgeInterceptor(this.client.cookieJar));
        OkHttpClient okHttpClient = this.client;
        if (okHttpClient.cache != null) {
            internalCache = okHttpClient.cache.internalCache;
        } else {
            internalCache = okHttpClient.internalCache;
        }
        interceptors.add(new CacheInterceptor(internalCache));
        interceptors.add(new ConnectInterceptor(this.client));
        if (!this.forWebSocket) {
            interceptors.addAll(this.client.networkInterceptors);
        }
        interceptors.add(new CallServerInterceptor(this.forWebSocket));
        return new RealInterceptorChain(interceptors, null, null, null, 0, this.originalRequest).proceed(this.originalRequest);
    }

    public final /* bridge */ /* synthetic */ Object clone() throws CloneNotSupportedException {
        return new RealCall(this.client, this.originalRequest, this.forWebSocket);
    }
}
