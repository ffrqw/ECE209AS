package okhttp3;

import java.net.Proxy;
import java.net.ProxySelector;
import java.util.List;
import javax.net.SocketFactory;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import okhttp3.HttpUrl.Builder;
import okhttp3.internal.Util;

public final class Address {
    final CertificatePinner certificatePinner;
    final List<ConnectionSpec> connectionSpecs;
    final Dns dns;
    final HostnameVerifier hostnameVerifier;
    final List<Protocol> protocols;
    final Proxy proxy;
    final Authenticator proxyAuthenticator;
    final ProxySelector proxySelector;
    final SocketFactory socketFactory;
    final SSLSocketFactory sslSocketFactory;
    final HttpUrl url;

    public Address(String uriHost, int uriPort, Dns dns, SocketFactory socketFactory, SSLSocketFactory sslSocketFactory, HostnameVerifier hostnameVerifier, CertificatePinner certificatePinner, Authenticator proxyAuthenticator, Proxy proxy, List<Protocol> protocols, List<ConnectionSpec> connectionSpecs, ProxySelector proxySelector) {
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
            throw new NullPointerException("dns == null");
        }
        this.dns = dns;
        if (socketFactory == null) {
            throw new NullPointerException("socketFactory == null");
        }
        this.socketFactory = socketFactory;
        if (proxyAuthenticator == null) {
            throw new NullPointerException("proxyAuthenticator == null");
        }
        this.proxyAuthenticator = proxyAuthenticator;
        if (protocols == null) {
            throw new NullPointerException("protocols == null");
        }
        this.protocols = Util.immutableList((List) protocols);
        if (connectionSpecs == null) {
            throw new NullPointerException("connectionSpecs == null");
        }
        this.connectionSpecs = Util.immutableList((List) connectionSpecs);
        if (proxySelector == null) {
            throw new NullPointerException("proxySelector == null");
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

    public final Dns dns() {
        return this.dns;
    }

    public final SocketFactory socketFactory() {
        return this.socketFactory;
    }

    public final Authenticator proxyAuthenticator() {
        return this.proxyAuthenticator;
    }

    public final List<Protocol> protocols() {
        return this.protocols;
    }

    public final List<ConnectionSpec> connectionSpecs() {
        return this.connectionSpecs;
    }

    public final ProxySelector proxySelector() {
        return this.proxySelector;
    }

    public final Proxy proxy() {
        return this.proxy;
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

    public final boolean equals(Object other) {
        if (!(other instanceof Address)) {
            return false;
        }
        Address that = (Address) other;
        if (this.url.equals(that.url) && this.dns.equals(that.dns) && this.proxyAuthenticator.equals(that.proxyAuthenticator) && this.protocols.equals(that.protocols) && this.connectionSpecs.equals(that.connectionSpecs) && this.proxySelector.equals(that.proxySelector) && Util.equal(this.proxy, that.proxy) && Util.equal(this.sslSocketFactory, that.sslSocketFactory) && Util.equal(this.hostnameVerifier, that.hostnameVerifier) && Util.equal(this.certificatePinner, that.certificatePinner)) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        int hashCode;
        int i = 0;
        int hashCode2 = (((((((((((this.url.hashCode() + 527) * 31) + this.dns.hashCode()) * 31) + this.proxyAuthenticator.hashCode()) * 31) + this.protocols.hashCode()) * 31) + this.connectionSpecs.hashCode()) * 31) + this.proxySelector.hashCode()) * 31;
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

    public final String toString() {
        StringBuilder result = new StringBuilder("Address{").append(this.url.host).append(":").append(this.url.port);
        if (this.proxy != null) {
            result.append(", proxy=").append(this.proxy);
        } else {
            result.append(", proxySelector=").append(this.proxySelector);
        }
        result.append("}");
        return result.toString();
    }
}
