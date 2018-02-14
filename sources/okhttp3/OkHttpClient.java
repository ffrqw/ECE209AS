package okhttp3;

import java.net.Proxy;
import java.net.ProxySelector;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.net.SocketFactory;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import okhttp3.Call.Factory;
import okhttp3.internal.Internal;
import okhttp3.internal.Util;
import okhttp3.internal.cache.InternalCache;
import okhttp3.internal.connection.RealConnection;
import okhttp3.internal.connection.RouteDatabase;
import okhttp3.internal.connection.StreamAllocation;
import okhttp3.internal.platform.Platform;
import okhttp3.internal.tls.CertificateChainCleaner;
import okhttp3.internal.tls.OkHostnameVerifier;

public class OkHttpClient implements Cloneable, Factory {
    static final List<ConnectionSpec> DEFAULT_CONNECTION_SPECS = Util.immutableList(ConnectionSpec.MODERN_TLS, ConnectionSpec.COMPATIBLE_TLS, ConnectionSpec.CLEARTEXT);
    static final List<Protocol> DEFAULT_PROTOCOLS = Util.immutableList(Protocol.HTTP_2, Protocol.HTTP_1_1);
    final Authenticator authenticator;
    final Cache cache;
    final CertificateChainCleaner certificateChainCleaner;
    final CertificatePinner certificatePinner;
    final int connectTimeout;
    final ConnectionPool connectionPool;
    final List<ConnectionSpec> connectionSpecs;
    final CookieJar cookieJar;
    final Dispatcher dispatcher;
    final Dns dns;
    final boolean followRedirects;
    final boolean followSslRedirects;
    final HostnameVerifier hostnameVerifier;
    final List<Interceptor> interceptors;
    final InternalCache internalCache;
    final List<Interceptor> networkInterceptors;
    final int pingInterval;
    final List<Protocol> protocols;
    final Proxy proxy;
    final Authenticator proxyAuthenticator;
    final ProxySelector proxySelector;
    final int readTimeout;
    final boolean retryOnConnectionFailure;
    final SocketFactory socketFactory;
    final SSLSocketFactory sslSocketFactory;
    final int writeTimeout;

    public static final class Builder {
        Authenticator authenticator = Authenticator.NONE;
        Cache cache;
        CertificatePinner certificatePinner = CertificatePinner.DEFAULT;
        int connectTimeout = 10000;
        ConnectionPool connectionPool = new ConnectionPool();
        List<ConnectionSpec> connectionSpecs = OkHttpClient.DEFAULT_CONNECTION_SPECS;
        CookieJar cookieJar = CookieJar.NO_COOKIES;
        Dispatcher dispatcher = new Dispatcher();
        Dns dns = Dns.SYSTEM;
        boolean followRedirects = true;
        boolean followSslRedirects = true;
        HostnameVerifier hostnameVerifier = OkHostnameVerifier.INSTANCE;
        final List<Interceptor> interceptors = new ArrayList();
        InternalCache internalCache;
        final List<Interceptor> networkInterceptors = new ArrayList();
        int pingInterval = 0;
        List<Protocol> protocols = OkHttpClient.DEFAULT_PROTOCOLS;
        Authenticator proxyAuthenticator = Authenticator.NONE;
        ProxySelector proxySelector = ProxySelector.getDefault();
        int readTimeout = 10000;
        boolean retryOnConnectionFailure = true;
        SocketFactory socketFactory = SocketFactory.getDefault();
        int writeTimeout = 10000;

        public final Builder cache(Cache cache) {
            this.cache = cache;
            this.internalCache = null;
            return this;
        }

        public final OkHttpClient build() {
            return new OkHttpClient(this);
        }
    }

    static {
        Internal.instance = new Internal() {
            public final void addLenient(okhttp3.Headers.Builder builder, String line) {
                builder.addLenient(line);
            }

            public final void addLenient(okhttp3.Headers.Builder builder, String name, String value) {
                builder.addLenient(name, value);
            }

            public final boolean connectionBecameIdle(ConnectionPool pool, RealConnection connection) {
                return pool.connectionBecameIdle(connection);
            }

            public final RealConnection get(ConnectionPool pool, Address address, StreamAllocation streamAllocation) {
                return pool.get(address, streamAllocation);
            }

            public final Socket deduplicate(ConnectionPool pool, Address address, StreamAllocation streamAllocation) {
                return pool.deduplicate(address, streamAllocation);
            }

            public final void put(ConnectionPool pool, RealConnection connection) {
                pool.put(connection);
            }

            public final RouteDatabase routeDatabase(ConnectionPool connectionPool) {
                return connectionPool.routeDatabase;
            }

            public final int code(okhttp3.Response.Builder responseBuilder) {
                return responseBuilder.code;
            }

            public final void apply(ConnectionSpec tlsConfiguration, SSLSocket sslSocket, boolean isFallback) {
                tlsConfiguration.apply(sslSocket, isFallback);
            }
        };
    }

    public OkHttpClient() {
        this(new Builder());
    }

    OkHttpClient(Builder builder) {
        this.dispatcher = builder.dispatcher;
        this.proxy = null;
        this.protocols = builder.protocols;
        this.connectionSpecs = builder.connectionSpecs;
        this.interceptors = Util.immutableList(builder.interceptors);
        this.networkInterceptors = Util.immutableList(builder.networkInterceptors);
        this.proxySelector = builder.proxySelector;
        this.cookieJar = builder.cookieJar;
        this.cache = builder.cache;
        this.internalCache = null;
        this.socketFactory = builder.socketFactory;
        boolean isTLS = false;
        for (ConnectionSpec spec : this.connectionSpecs) {
            if (isTLS || spec.tls) {
                isTLS = true;
            } else {
                isTLS = false;
            }
        }
        if (isTLS) {
            X509TrustManager trustManager = systemDefaultTrustManager();
            this.sslSocketFactory = systemDefaultSslSocketFactory(trustManager);
            this.certificateChainCleaner = Platform.get().buildCertificateChainCleaner(trustManager);
        } else {
            this.sslSocketFactory = null;
            this.certificateChainCleaner = null;
        }
        this.hostnameVerifier = builder.hostnameVerifier;
        this.certificatePinner = builder.certificatePinner.withCertificateChainCleaner(this.certificateChainCleaner);
        this.proxyAuthenticator = builder.proxyAuthenticator;
        this.authenticator = builder.authenticator;
        this.connectionPool = builder.connectionPool;
        this.dns = builder.dns;
        this.followSslRedirects = builder.followSslRedirects;
        this.followRedirects = builder.followRedirects;
        this.retryOnConnectionFailure = builder.retryOnConnectionFailure;
        this.connectTimeout = builder.connectTimeout;
        this.readTimeout = builder.readTimeout;
        this.writeTimeout = builder.writeTimeout;
        this.pingInterval = 0;
    }

    private static X509TrustManager systemDefaultTrustManager() {
        try {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(null);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            if (trustManagers.length == 1 && (trustManagers[0] instanceof X509TrustManager)) {
                return (X509TrustManager) trustManagers[0];
            }
            throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
        } catch (GeneralSecurityException e) {
            throw new AssertionError();
        }
    }

    private static SSLSocketFactory systemDefaultSslSocketFactory(X509TrustManager trustManager) {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{trustManager}, null);
            return sslContext.getSocketFactory();
        } catch (GeneralSecurityException e) {
            throw new AssertionError();
        }
    }

    public final int connectTimeoutMillis() {
        return this.connectTimeout;
    }

    public final int readTimeoutMillis() {
        return this.readTimeout;
    }

    public final int writeTimeoutMillis() {
        return this.writeTimeout;
    }

    public final Proxy proxy() {
        return this.proxy;
    }

    public final ProxySelector proxySelector() {
        return this.proxySelector;
    }

    public final CookieJar cookieJar() {
        return this.cookieJar;
    }

    public final Cache cache() {
        return this.cache;
    }

    public final Dns dns() {
        return this.dns;
    }

    public final SocketFactory socketFactory() {
        return this.socketFactory;
    }

    public final SSLSocketFactory sslSocketFactory() {
        return this.sslSocketFactory;
    }

    public final HostnameVerifier hostnameVerifier() {
        return this.hostnameVerifier;
    }

    public final CertificatePinner certificatePinner() {
        return this.certificatePinner;
    }

    public final Authenticator authenticator() {
        return this.authenticator;
    }

    public final Authenticator proxyAuthenticator() {
        return this.proxyAuthenticator;
    }

    public final ConnectionPool connectionPool() {
        return this.connectionPool;
    }

    public final boolean followSslRedirects() {
        return this.followSslRedirects;
    }

    public final boolean followRedirects() {
        return this.followRedirects;
    }

    public final boolean retryOnConnectionFailure() {
        return this.retryOnConnectionFailure;
    }

    public final List<Protocol> protocols() {
        return this.protocols;
    }

    public final List<ConnectionSpec> connectionSpecs() {
        return this.connectionSpecs;
    }

    public final Call newCall(Request request) {
        return new RealCall(this, request, false);
    }
}
