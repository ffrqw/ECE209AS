package com.squareup.okhttp;

import com.squareup.okhttp.Headers.Builder;
import com.squareup.okhttp.internal.Internal;
import com.squareup.okhttp.internal.InternalCache;
import com.squareup.okhttp.internal.RouteDatabase;
import com.squareup.okhttp.internal.Util;
import com.squareup.okhttp.internal.http.AuthenticatorAdapter;
import com.squareup.okhttp.internal.http.StreamAllocation;
import com.squareup.okhttp.internal.io.RealConnection;
import com.squareup.okhttp.internal.tls.OkHostnameVerifier;
import java.net.CookieHandler;
import java.net.Proxy;
import java.net.ProxySelector;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.net.SocketFactory;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class OkHttpClient implements Cloneable {
    private static final List<ConnectionSpec> DEFAULT_CONNECTION_SPECS = Util.immutableList(ConnectionSpec.MODERN_TLS, ConnectionSpec.COMPATIBLE_TLS, ConnectionSpec.CLEARTEXT);
    private static final List<Protocol> DEFAULT_PROTOCOLS = Util.immutableList(Protocol.HTTP_2, Protocol.SPDY_3, Protocol.HTTP_1_1);
    private static SSLSocketFactory defaultSslSocketFactory;
    private Authenticator authenticator;
    private Cache cache;
    private CertificatePinner certificatePinner;
    private int connectTimeout;
    private ConnectionPool connectionPool;
    private List<ConnectionSpec> connectionSpecs;
    private CookieHandler cookieHandler;
    private Dispatcher dispatcher;
    private Dns dns;
    private boolean followRedirects;
    private boolean followSslRedirects;
    private HostnameVerifier hostnameVerifier;
    private final List<Interceptor> interceptors;
    private InternalCache internalCache;
    private final List<Interceptor> networkInterceptors;
    private List<Protocol> protocols;
    private Proxy proxy;
    private ProxySelector proxySelector;
    private int readTimeout;
    private boolean retryOnConnectionFailure;
    private final RouteDatabase routeDatabase;
    private SocketFactory socketFactory;
    private SSLSocketFactory sslSocketFactory;
    private int writeTimeout;

    static {
        Internal.instance = new Internal() {
            public final void addLenient(Builder builder, String line) {
                builder.addLenient(line);
            }

            public final InternalCache internalCache(OkHttpClient client) {
                return client.internalCache();
            }

            public final boolean connectionBecameIdle(ConnectionPool pool, RealConnection connection) {
                return pool.connectionBecameIdle(connection);
            }

            public final RealConnection get(ConnectionPool pool, Address address, StreamAllocation streamAllocation) {
                return pool.get(address, streamAllocation);
            }

            public final void put(ConnectionPool pool, RealConnection connection) {
                pool.put(connection);
            }

            public final RouteDatabase routeDatabase(ConnectionPool connectionPool) {
                return connectionPool.routeDatabase;
            }

            public final void apply(ConnectionSpec tlsConfiguration, SSLSocket sslSocket, boolean isFallback) {
                tlsConfiguration.apply(sslSocket, isFallback);
            }
        };
    }

    public OkHttpClient() {
        this.interceptors = new ArrayList();
        this.networkInterceptors = new ArrayList();
        this.followSslRedirects = true;
        this.followRedirects = true;
        this.retryOnConnectionFailure = true;
        this.connectTimeout = 10000;
        this.readTimeout = 10000;
        this.writeTimeout = 10000;
        this.routeDatabase = new RouteDatabase();
        this.dispatcher = new Dispatcher();
    }

    private OkHttpClient(OkHttpClient okHttpClient) {
        this.interceptors = new ArrayList();
        this.networkInterceptors = new ArrayList();
        this.followSslRedirects = true;
        this.followRedirects = true;
        this.retryOnConnectionFailure = true;
        this.connectTimeout = 10000;
        this.readTimeout = 10000;
        this.writeTimeout = 10000;
        this.routeDatabase = okHttpClient.routeDatabase;
        this.dispatcher = okHttpClient.dispatcher;
        this.proxy = okHttpClient.proxy;
        this.protocols = okHttpClient.protocols;
        this.connectionSpecs = okHttpClient.connectionSpecs;
        this.interceptors.addAll(okHttpClient.interceptors);
        this.networkInterceptors.addAll(okHttpClient.networkInterceptors);
        this.proxySelector = okHttpClient.proxySelector;
        this.cookieHandler = okHttpClient.cookieHandler;
        this.cache = okHttpClient.cache;
        this.internalCache = this.cache != null ? this.cache.internalCache : okHttpClient.internalCache;
        this.socketFactory = okHttpClient.socketFactory;
        this.sslSocketFactory = okHttpClient.sslSocketFactory;
        this.hostnameVerifier = okHttpClient.hostnameVerifier;
        this.certificatePinner = okHttpClient.certificatePinner;
        this.authenticator = okHttpClient.authenticator;
        this.connectionPool = okHttpClient.connectionPool;
        this.dns = okHttpClient.dns;
        this.followSslRedirects = okHttpClient.followSslRedirects;
        this.followRedirects = okHttpClient.followRedirects;
        this.retryOnConnectionFailure = okHttpClient.retryOnConnectionFailure;
        this.connectTimeout = okHttpClient.connectTimeout;
        this.readTimeout = okHttpClient.readTimeout;
        this.writeTimeout = okHttpClient.writeTimeout;
    }

    public final void setConnectTimeout(long timeout, TimeUnit unit) {
        if (timeout < 0) {
            throw new IllegalArgumentException("timeout < 0");
        } else if (unit == null) {
            throw new IllegalArgumentException("unit == null");
        } else {
            long millis = unit.toMillis(timeout);
            if (millis > 2147483647L) {
                throw new IllegalArgumentException("Timeout too large.");
            } else if (millis != 0 || timeout <= 0) {
                this.connectTimeout = (int) millis;
            } else {
                throw new IllegalArgumentException("Timeout too small.");
            }
        }
    }

    public final int getConnectTimeout() {
        return this.connectTimeout;
    }

    public final void setReadTimeout(long timeout, TimeUnit unit) {
        if (timeout < 0) {
            throw new IllegalArgumentException("timeout < 0");
        } else if (unit == null) {
            throw new IllegalArgumentException("unit == null");
        } else {
            long millis = unit.toMillis(timeout);
            if (millis > 2147483647L) {
                throw new IllegalArgumentException("Timeout too large.");
            } else if (millis != 0 || timeout <= 0) {
                this.readTimeout = (int) millis;
            } else {
                throw new IllegalArgumentException("Timeout too small.");
            }
        }
    }

    public final int getReadTimeout() {
        return this.readTimeout;
    }

    public final int getWriteTimeout() {
        return this.writeTimeout;
    }

    public final Proxy getProxy() {
        return this.proxy;
    }

    public final ProxySelector getProxySelector() {
        return this.proxySelector;
    }

    public final CookieHandler getCookieHandler() {
        return this.cookieHandler;
    }

    final InternalCache internalCache() {
        return this.internalCache;
    }

    public final Dns getDns() {
        return this.dns;
    }

    public final SocketFactory getSocketFactory() {
        return this.socketFactory;
    }

    public final SSLSocketFactory getSslSocketFactory() {
        return this.sslSocketFactory;
    }

    public final HostnameVerifier getHostnameVerifier() {
        return this.hostnameVerifier;
    }

    public final CertificatePinner getCertificatePinner() {
        return this.certificatePinner;
    }

    public final Authenticator getAuthenticator() {
        return this.authenticator;
    }

    public final ConnectionPool getConnectionPool() {
        return this.connectionPool;
    }

    public final boolean getFollowSslRedirects() {
        return this.followSslRedirects;
    }

    public final boolean getFollowRedirects() {
        return this.followRedirects;
    }

    public final boolean getRetryOnConnectionFailure() {
        return this.retryOnConnectionFailure;
    }

    public final Dispatcher getDispatcher() {
        return this.dispatcher;
    }

    public final List<Protocol> getProtocols() {
        return this.protocols;
    }

    public final List<ConnectionSpec> getConnectionSpecs() {
        return this.connectionSpecs;
    }

    public final List<Interceptor> interceptors() {
        return this.interceptors;
    }

    public final List<Interceptor> networkInterceptors() {
        return this.networkInterceptors;
    }

    public final Call newCall(Request request) {
        return new Call(this, request);
    }

    final OkHttpClient copyWithDefaults() {
        OkHttpClient result = new OkHttpClient(this);
        if (result.proxySelector == null) {
            result.proxySelector = ProxySelector.getDefault();
        }
        if (result.cookieHandler == null) {
            result.cookieHandler = CookieHandler.getDefault();
        }
        if (result.socketFactory == null) {
            result.socketFactory = SocketFactory.getDefault();
        }
        if (result.sslSocketFactory == null) {
            result.sslSocketFactory = getDefaultSSLSocketFactory();
        }
        if (result.hostnameVerifier == null) {
            result.hostnameVerifier = OkHostnameVerifier.INSTANCE;
        }
        if (result.certificatePinner == null) {
            result.certificatePinner = CertificatePinner.DEFAULT;
        }
        if (result.authenticator == null) {
            result.authenticator = AuthenticatorAdapter.INSTANCE;
        }
        if (result.connectionPool == null) {
            result.connectionPool = ConnectionPool.getDefault();
        }
        if (result.protocols == null) {
            result.protocols = DEFAULT_PROTOCOLS;
        }
        if (result.connectionSpecs == null) {
            result.connectionSpecs = DEFAULT_CONNECTION_SPECS;
        }
        if (result.dns == null) {
            result.dns = Dns.SYSTEM;
        }
        return result;
    }

    private synchronized SSLSocketFactory getDefaultSSLSocketFactory() {
        if (defaultSslSocketFactory == null) {
            try {
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, null, null);
                defaultSslSocketFactory = sslContext.getSocketFactory();
            } catch (GeneralSecurityException e) {
                throw new AssertionError();
            }
        }
        return defaultSslSocketFactory;
    }

    public /* bridge */ /* synthetic */ Object clone() throws CloneNotSupportedException {
        return new OkHttpClient(this);
    }
}
