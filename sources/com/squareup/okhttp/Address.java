package com.squareup.okhttp;

import com.squareup.okhttp.HttpUrl.Builder;
import com.squareup.okhttp.internal.Util;
import java.net.Proxy;
import java.net.ProxySelector;
import java.util.List;
import javax.net.SocketFactory;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

public final class Address {
    final Authenticator authenticator;
    final CertificatePinner certificatePinner;
    final List<ConnectionSpec> connectionSpecs;
    final Dns dns;
    final HostnameVerifier hostnameVerifier;
    final List<Protocol> protocols;
    final Proxy proxy;
    final ProxySelector proxySelector;
    final SocketFactory socketFactory;
    final SSLSocketFactory sslSocketFactory;
    final HttpUrl url;

    public Address(String uriHost, int uriPort, Dns dns, SocketFactory socketFactory, SSLSocketFactory sslSocketFactory, HostnameVerifier hostnameVerifier, CertificatePinner certificatePinner, Authenticator authenticator, Proxy proxy, List<Protocol> protocols, List<ConnectionSpec> connectionSpecs, ProxySelector proxySelector) {
        String str;
        Builder builder = new Builder();
        if (sslSocketFactory != null) {
            str = "https";
        } else {
            str = "http";
        }
        if (str.equalsIgnoreCase("http")) {
            builder.scheme = "http";
        } else if (str.equalsIgnoreCase("https")) {
            builder.scheme = "https";
        } else {
            throw new IllegalArgumentException("unexpected scheme: " + str);
        }
        Builder host = builder.host(uriHost);
        if (uriPort <= 0 || uriPort > 65535) {
            throw new IllegalArgumentException("unexpected port: " + uriPort);
        }
        host.port = uriPort;
        this.url = host.build();
        if (dns == null) {
            throw new IllegalArgumentException("dns == null");
        }
        this.dns = dns;
        if (socketFactory == null) {
            throw new IllegalArgumentException("socketFactory == null");
        }
        this.socketFactory = socketFactory;
        if (authenticator == null) {
            throw new IllegalArgumentException("authenticator == null");
        }
        this.authenticator = authenticator;
        if (protocols == null) {
            throw new IllegalArgumentException("protocols == null");
        }
        this.protocols = Util.immutableList((List) protocols);
        if (connectionSpecs == null) {
            throw new IllegalArgumentException("connectionSpecs == null");
        }
        this.connectionSpecs = Util.immutableList((List) connectionSpecs);
        if (proxySelector == null) {
            throw new IllegalArgumentException("proxySelector == null");
        }
        this.proxySelector = proxySelector;
        this.proxy = proxy;
        this.sslSocketFactory = sslSocketFactory;
        this.hostnameVerifier = hostnameVerifier;
        this.certificatePinner = certificatePinner;
    }

    public final HttpUrl url() {
        return this.url;
    }

    @Deprecated
    public final String getUriHost() {
        return this.url.host();
    }

    @Deprecated
    public final int getUriPort() {
        return this.url.port();
    }

    public final Dns getDns() {
        return this.dns;
    }

    public final SocketFactory getSocketFactory() {
        return this.socketFactory;
    }

    public final Authenticator getAuthenticator() {
        return this.authenticator;
    }

    public final List<Protocol> getProtocols() {
        return this.protocols;
    }

    public final List<ConnectionSpec> getConnectionSpecs() {
        return this.connectionSpecs;
    }

    public final ProxySelector getProxySelector() {
        return this.proxySelector;
    }

    public final Proxy getProxy() {
        return this.proxy;
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

    public final boolean equals(Object other) {
        if (!(other instanceof Address)) {
            return false;
        }
        Address that = (Address) other;
        if (this.url.equals(that.url) && this.dns.equals(that.dns) && this.authenticator.equals(that.authenticator) && this.protocols.equals(that.protocols) && this.connectionSpecs.equals(that.connectionSpecs) && this.proxySelector.equals(that.proxySelector) && Util.equal(this.proxy, that.proxy) && Util.equal(this.sslSocketFactory, that.sslSocketFactory) && Util.equal(this.hostnameVerifier, that.hostnameVerifier) && Util.equal(this.certificatePinner, that.certificatePinner)) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        int hashCode;
        int i = 0;
        int hashCode2 = (((((((((((this.url.hashCode() + 527) * 31) + this.dns.hashCode()) * 31) + this.authenticator.hashCode()) * 31) + this.protocols.hashCode()) * 31) + this.connectionSpecs.hashCode()) * 31) + this.proxySelector.hashCode()) * 31;
        if (this.proxy != null) {
            hashCode = this.proxy.hashCode();
        } else {
            hashCode = 0;
        }
        hashCode2 = (hashCode2 + hashCode) * 31;
        if (this.sslSocketFactory != null) {
            hashCode = this.sslSocketFactory.hashCode();
        } else {
            hashCode = 0;
        }
        hashCode2 = (hashCode2 + hashCode) * 31;
        if (this.hostnameVerifier != null) {
            hashCode = this.hostnameVerifier.hashCode();
        } else {
            hashCode = 0;
        }
        hashCode = (hashCode2 + hashCode) * 31;
        if (this.certificatePinner != null) {
            i = this.certificatePinner.hashCode();
        }
        return hashCode + i;
    }
}
