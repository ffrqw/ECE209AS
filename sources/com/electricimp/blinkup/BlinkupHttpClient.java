package com.electricimp.blinkup;

import android.util.Log;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import javax.net.ssl.TrustManagerFactory;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;

public final class BlinkupHttpClient extends DefaultHttpClient {
    private final X509Certificate cert;

    public static HttpClient newInstanceOrDefault() {
        BasicHttpParams params = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(params, 60000);
        HttpConnectionParams.setSoTimeout(params, 60000);
        try {
            KeyStore.getInstance("BKS");
            return new BlinkupHttpClient(params, getGeoTrustCertificate());
        } catch (KeyStoreException e) {
            Log.e("BlinkUp", e.getMessage());
            return new DefaultHttpClient(params);
        }
    }

    private BlinkupHttpClient(BasicHttpParams params, X509Certificate cert) {
        super(params);
        this.cert = cert;
    }

    protected final ClientConnectionManager createClientConnectionManager() {
        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        registry.register(new Scheme("https", newSslSocketFactory(), 443));
        return new SingleClientConnManager(getParams(), registry);
    }

    private SSLSocketFactory newSslSocketFactory() {
        InputStream in;
        try {
            KeyStore trusted = KeyStore.getInstance("BKS");
            if (this.cert != null) {
                trusted.load(null, null);
                trusted.setCertificateEntry("geotrust", this.cert);
                return new SSLSocketFactory(trusted);
            }
            in = new ByteArrayInputStream(Base64.decode("AAAAAQAAABS6X28gxSniRREDxJT9m6oB6RrfpgAABIwBAAEwAAABP6EdGqQAAAAAAAVYLjUwOQAAA1gwggNUMIICPKADAgECAgMCNFYwDQYJKoZIhvcNAQEFBQAwQjELMAkGA1UEBhMCVVMxFjAUBgNVBAoTDUdlb1RydXN0IEluYy4xGzAZBgNVBAMTEkdlb1RydXN0IEdsb2JhbCBDQTAeFw0wMjA1MjEwNDAwMDBaFw0yMjA1MjEwNDAwMDBaMEIxCzAJBgNVBAYTAlVTMRYwFAYDVQQKEw1HZW9UcnVzdCBJbmMuMRswGQYDVQQDExJHZW9UcnVzdCBHbG9iYWwgQ0EwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQDazBhjMP30FyMaVn5b3zxsOORxt3iR1Lyh2Ez4qEO2A+lNIQcIiNpYL2Y5Kb0FeIudOOgFt2p+caTmxGCmsO+A5IkoD54l1u2D862mkceYyUIYNRSdrZhGki5PyvGHQ8EWlVctUO+JLYB6V63y7l9r0gCNuRT4FBU12cBGo3tyyJG/yVUrzdCXPpwmZMzfzoMZccpO5tTVe6kZzVXeyOzSXjhT5VxPjC3+UCM2/Gbmy46kORkAt5UCOZELDv44LtEdBZr2TT5vDwcdrywej2A54vo2UxM51F4mK9s9qBS9MusYAyhSBHHlqzM94Ti7BzaEYpx56hYw9F/AK+hxa+T5AgMBAAGjUzBRMA8GA1UdEwEB/wQFMAMBAf8wHQYDVR0OBBYEFMB6mGiNifurBWQMEX2qfWW4ysxOMB8GA1UdIwQYMBaAFMB6mGiNifurBWQMEX2qfWW4ysxOMA0GCSqGSIb3DQEBBQUAA4IBAQA14ylq5S9dVI4pUJSfmRoU5I94KmKUoidnntDPGl5H6cGypM/dQRoFTptL7kpvVVKzJKE3CutkdiouLPP9O3WQv/px2Mc9N9K1BZViuabeiT02ezh3SJespiCPLqbJDMKymUUAx84RUSIi4KXqthVICWTqXk909wU+x4pSDNsVtL1tm+XGsVRoqeNpkLaapQ+4uT8gfa5Ktbic5B22q+aUpcHHg63b9SeHDgRs1f/doF3th1K3KxUCrjmmanTp2sTnvE00HqlcTTNfkgkviGZdd5fHHXYTqdXl8RYJETXVrNskcXAsmFYL2Re00eNRK1516NXQ3E807cIFZoChy+YzAFm1q3YzpHkoTwWGCssBrEN5A2Pu", 0));
            trusted.load(in, "geotrust".toCharArray());
            in.close();
            return new SSLSocketFactory(trusted);
        } catch (Exception e) {
            throw new AssertionError(e);
        } catch (Throwable th) {
            in.close();
        }
    }

    private static X509Certificate getGeoTrustCertificate() {
        try {
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(null);
            if (tmf.getTrustManagers().length == 0) {
                return null;
            }
            for (X509Certificate cert : tmf.getTrustManagers()[0].getAcceptedIssuers()) {
                if ("C=US,O=GeoTrust Inc.,CN=GeoTrust Global CA".equals(cert.getIssuerDN().getName())) {
                    return cert;
                }
            }
            return null;
        } catch (NoSuchAlgorithmException e) {
        } catch (KeyStoreException e2) {
        }
    }
}
