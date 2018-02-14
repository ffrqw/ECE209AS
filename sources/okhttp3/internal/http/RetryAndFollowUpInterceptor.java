package okhttp3.internal.http;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.security.cert.CertificateException;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSocketFactory;
import okhttp3.Address;
import okhttp3.CertificatePinner;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.connection.StreamAllocation;

public final class RetryAndFollowUpInterceptor implements Interceptor {
    private Object callStackTrace;
    private volatile boolean canceled;
    private final OkHttpClient client;
    private final boolean forWebSocket;
    private StreamAllocation streamAllocation;

    public RetryAndFollowUpInterceptor(OkHttpClient client, boolean forWebSocket) {
        this.client = client;
        this.forWebSocket = forWebSocket;
    }

    public final boolean isCanceled() {
        return this.canceled;
    }

    public final void setCallStackTrace(Object callStackTrace) {
        this.callStackTrace = callStackTrace;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final okhttp3.Response intercept(okhttp3.Interceptor.Chain r14) throws java.io.IOException {
        /*
        r13 = this;
        r5 = r14.request();
        r8 = new okhttp3.internal.connection.StreamAllocation;
        r9 = r13.client;
        r9 = r9.connectionPool();
        r10 = r5.url();
        r10 = r13.createAddress(r10);
        r11 = r13.callStackTrace;
        r8.<init>(r9, r10, r11);
        r13.streamAllocation = r8;
        r3 = 0;
        r4 = 0;
    L_0x001d:
        r8 = r13.canceled;
        r0 = r14;
        r0 = (okhttp3.internal.http.RealInterceptorChain) r0;	 Catch:{ RouteException -> 0x004e, IOException -> 0x006c }
        r8 = r0;
        r9 = r13.streamAllocation;	 Catch:{ RouteException -> 0x004e, IOException -> 0x006c }
        r10 = 0;
        r11 = 0;
        r7 = r8.proceed(r5, r9, r10, r11);	 Catch:{ RouteException -> 0x004e, IOException -> 0x006c }
        if (r4 == 0) goto L_0x0046;
    L_0x002d:
        r8 = r7.newBuilder();
        r9 = r4.newBuilder();
        r10 = 0;
        r9 = r9.body(r10);
        r9 = r9.build();
        r8 = r8.priorResponse(r9);
        r7 = r8.build();
    L_0x0046:
        if (r7 != 0) goto L_0x007b;
    L_0x0048:
        r8 = new java.lang.IllegalStateException;
        r8.<init>();
        throw r8;
    L_0x004e:
        r1 = move-exception;
        r8 = r1.getLastConnectException();	 Catch:{ all -> 0x005f }
        r9 = 0;
        r8 = r13.recover(r8, r9, r5);	 Catch:{ all -> 0x005f }
        if (r8 != 0) goto L_0x001d;
    L_0x005a:
        r8 = r1.getLastConnectException();	 Catch:{ all -> 0x005f }
        throw r8;	 Catch:{ all -> 0x005f }
    L_0x005f:
        r8 = move-exception;
        r9 = r13.streamAllocation;
        r10 = 0;
        r9.streamFailed(r10);
        r9 = r13.streamAllocation;
        r9.release();
        throw r8;
    L_0x006c:
        r1 = move-exception;
        r8 = r1 instanceof okhttp3.internal.http2.ConnectionShutdownException;	 Catch:{ all -> 0x005f }
        if (r8 != 0) goto L_0x0079;
    L_0x0071:
        r6 = 1;
    L_0x0072:
        r8 = r13.recover(r1, r6, r5);	 Catch:{ all -> 0x005f }
        if (r8 != 0) goto L_0x001d;
    L_0x0078:
        throw r1;	 Catch:{ all -> 0x005f }
    L_0x0079:
        r6 = 0;
        goto L_0x0072;
    L_0x007b:
        r8 = r13.streamAllocation;
        r8 = r8.connection();
        if (r8 == 0) goto L_0x00a3;
    L_0x0083:
        r8 = r8.route();
    L_0x0087:
        r9 = r7.code();
        r10 = r7.request();
        r10 = r10.method();
        switch(r9) {
            case 300: goto L_0x00e8;
            case 301: goto L_0x00e8;
            case 302: goto L_0x00e8;
            case 303: goto L_0x00e8;
            case 307: goto L_0x00d8;
            case 308: goto L_0x00d8;
            case 401: goto L_0x00cd;
            case 407: goto L_0x00a5;
            case 408: goto L_0x0181;
            default: goto L_0x0096;
        };
    L_0x0096:
        r2 = 0;
    L_0x0097:
        if (r2 != 0) goto L_0x0193;
    L_0x0099:
        r8 = r13.forWebSocket;
        if (r8 != 0) goto L_0x00a2;
    L_0x009d:
        r8 = r13.streamAllocation;
        r8.release();
    L_0x00a2:
        return r7;
    L_0x00a3:
        r8 = 0;
        goto L_0x0087;
    L_0x00a5:
        if (r8 == 0) goto L_0x00bb;
    L_0x00a7:
        r8 = r8.proxy();
    L_0x00ab:
        r8 = r8.type();
        r9 = java.net.Proxy.Type.HTTP;
        if (r8 == r9) goto L_0x00c2;
    L_0x00b3:
        r8 = new java.net.ProtocolException;
        r9 = "Received HTTP_PROXY_AUTH (407) code while not using proxy";
        r8.<init>(r9);
        throw r8;
    L_0x00bb:
        r8 = r13.client;
        r8 = r8.proxy();
        goto L_0x00ab;
    L_0x00c2:
        r8 = r13.client;
        r8 = r8.proxyAuthenticator();
        r2 = r8.authenticate$31deecb3();
        goto L_0x0097;
    L_0x00cd:
        r8 = r13.client;
        r8 = r8.authenticator();
        r2 = r8.authenticate$31deecb3();
        goto L_0x0097;
    L_0x00d8:
        r8 = "GET";
        r8 = r10.equals(r8);
        if (r8 != 0) goto L_0x00e8;
    L_0x00e0:
        r8 = "HEAD";
        r8 = r10.equals(r8);
        if (r8 == 0) goto L_0x0096;
    L_0x00e8:
        r8 = r13.client;
        r8 = r8.followRedirects();
        if (r8 == 0) goto L_0x0096;
    L_0x00f0:
        r8 = "Location";
        r8 = r7.header(r8);
        if (r8 == 0) goto L_0x0096;
    L_0x00f8:
        r9 = r7.request();
        r9 = r9.url();
        r9 = r9.resolve(r8);
        if (r9 == 0) goto L_0x0096;
    L_0x0106:
        r8 = r9.scheme();
        r11 = r7.request();
        r11 = r11.url();
        r11 = r11.scheme();
        r8 = r8.equals(r11);
        if (r8 != 0) goto L_0x0124;
    L_0x011c:
        r8 = r13.client;
        r8 = r8.followSslRedirects();
        if (r8 == 0) goto L_0x0096;
    L_0x0124:
        r8 = r7.request();
        r11 = r8.newBuilder();
        r8 = okhttp3.internal.http.HttpMethod.permitsRequestBody(r10);
        if (r8 == 0) goto L_0x015a;
    L_0x0132:
        r8 = "PROPFIND";
        r12 = r10.equals(r8);
        r8 = "PROPFIND";
        r8 = r10.equals(r8);
        if (r8 != 0) goto L_0x016f;
    L_0x0140:
        r8 = 1;
    L_0x0141:
        if (r8 == 0) goto L_0x0171;
    L_0x0143:
        r8 = "GET";
        r10 = 0;
        r11.method(r8, r10);
    L_0x0149:
        if (r12 != 0) goto L_0x015a;
    L_0x014b:
        r8 = "Transfer-Encoding";
        r11.removeHeader(r8);
        r8 = "Content-Length";
        r11.removeHeader(r8);
        r8 = "Content-Type";
        r11.removeHeader(r8);
    L_0x015a:
        r8 = sameConnection(r7, r9);
        if (r8 != 0) goto L_0x0165;
    L_0x0160:
        r8 = "Authorization";
        r11.removeHeader(r8);
    L_0x0165:
        r8 = r11.url(r9);
        r2 = r8.build();
        goto L_0x0097;
    L_0x016f:
        r8 = 0;
        goto L_0x0141;
    L_0x0171:
        if (r12 == 0) goto L_0x017f;
    L_0x0173:
        r8 = r7.request();
        r8 = r8.body();
    L_0x017b:
        r11.method(r10, r8);
        goto L_0x0149;
    L_0x017f:
        r8 = 0;
        goto L_0x017b;
    L_0x0181:
        r8 = r7.request();
        r8 = r8.body();
        r8 = r8 instanceof okhttp3.internal.http.UnrepeatableRequestBody;
        if (r8 != 0) goto L_0x0096;
    L_0x018d:
        r2 = r7.request();
        goto L_0x0097;
    L_0x0193:
        r8 = r7.body();
        okhttp3.internal.Util.closeQuietly(r8);
        r3 = r3 + 1;
        r8 = 20;
        if (r3 <= r8) goto L_0x01ba;
    L_0x01a0:
        r8 = r13.streamAllocation;
        r8.release();
        r8 = new java.net.ProtocolException;
        r9 = new java.lang.StringBuilder;
        r10 = "Too many follow-up requests: ";
        r9.<init>(r10);
        r9 = r9.append(r3);
        r9 = r9.toString();
        r8.<init>(r9);
        throw r8;
    L_0x01ba:
        r8 = r2.body();
        r8 = r8 instanceof okhttp3.internal.http.UnrepeatableRequestBody;
        if (r8 == 0) goto L_0x01d3;
    L_0x01c2:
        r8 = r13.streamAllocation;
        r8.release();
        r8 = new java.net.HttpRetryException;
        r9 = "Cannot retry streamed HTTP body";
        r10 = r7.code();
        r8.<init>(r9, r10);
        throw r8;
    L_0x01d3:
        r8 = r2.url();
        r8 = sameConnection(r7, r8);
        if (r8 != 0) goto L_0x01fd;
    L_0x01dd:
        r8 = r13.streamAllocation;
        r8.release();
        r8 = new okhttp3.internal.connection.StreamAllocation;
        r9 = r13.client;
        r9 = r9.connectionPool();
        r10 = r2.url();
        r10 = r13.createAddress(r10);
        r11 = r13.callStackTrace;
        r8.<init>(r9, r10, r11);
        r13.streamAllocation = r8;
    L_0x01f9:
        r5 = r2;
        r4 = r7;
        goto L_0x001d;
    L_0x01fd:
        r8 = r13.streamAllocation;
        r8 = r8.codec();
        if (r8 == 0) goto L_0x01f9;
    L_0x0205:
        r8 = new java.lang.IllegalStateException;
        r9 = new java.lang.StringBuilder;
        r10 = "Closing the body of ";
        r9.<init>(r10);
        r9 = r9.append(r7);
        r10 = " didn't close its backing stream. Bad interceptor?";
        r9 = r9.append(r10);
        r9 = r9.toString();
        r8.<init>(r9);
        throw r8;
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.http.RetryAndFollowUpInterceptor.intercept(okhttp3.Interceptor$Chain):okhttp3.Response");
    }

    private Address createAddress(HttpUrl url) {
        SSLSocketFactory sslSocketFactory = null;
        HostnameVerifier hostnameVerifier = null;
        CertificatePinner certificatePinner = null;
        if (url.isHttps()) {
            sslSocketFactory = this.client.sslSocketFactory();
            hostnameVerifier = this.client.hostnameVerifier();
            certificatePinner = this.client.certificatePinner();
        }
        return new Address(url.host(), url.port(), this.client.dns(), this.client.socketFactory(), sslSocketFactory, hostnameVerifier, certificatePinner, this.client.proxyAuthenticator(), this.client.proxy(), this.client.protocols(), this.client.connectionSpecs(), this.client.proxySelector());
    }

    private boolean recover(IOException e, boolean requestSendStarted, Request userRequest) {
        this.streamAllocation.streamFailed(e);
        if (!this.client.retryOnConnectionFailure()) {
            return false;
        }
        if (requestSendStarted && (userRequest.body() instanceof UnrepeatableRequestBody)) {
            return false;
        }
        boolean z = e instanceof ProtocolException ? false : e instanceof InterruptedIOException ? (e instanceof SocketTimeoutException) && !requestSendStarted : ((e instanceof SSLHandshakeException) && (e.getCause() instanceof CertificateException)) ? false : !(e instanceof SSLPeerUnverifiedException);
        if (z && this.streamAllocation.hasMoreRoutes()) {
            return true;
        }
        return false;
    }

    private static boolean sameConnection(Response response, HttpUrl followUp) {
        HttpUrl url = response.request().url();
        return url.host().equals(followUp.host()) && url.port() == followUp.port() && url.scheme().equals(followUp.scheme());
    }
}
