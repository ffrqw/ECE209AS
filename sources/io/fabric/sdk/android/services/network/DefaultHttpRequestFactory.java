package io.fabric.sdk.android.services.network;

import io.fabric.sdk.android.DefaultLogger;
import io.fabric.sdk.android.Logger;
import java.util.Locale;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

public final class DefaultHttpRequestFactory implements HttpRequestFactory {
    private boolean attemptedSslInit;
    private final Logger logger;
    private PinningInfoProvider pinningInfo;
    private SSLSocketFactory sslSocketFactory;

    /* renamed from: io.fabric.sdk.android.services.network.DefaultHttpRequestFactory$1 */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$io$fabric$sdk$android$services$network$HttpMethod = new int[4];

        static {
            HttpMethod.values$6d1784b8();
            try {
                int[] iArr = $SwitchMap$io$fabric$sdk$android$services$network$HttpMethod;
                int i = HttpMethod.GET$6bc89afe;
                iArr[0] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr = $SwitchMap$io$fabric$sdk$android$services$network$HttpMethod;
                i = HttpMethod.POST$6bc89afe;
                iArr[1] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                iArr = $SwitchMap$io$fabric$sdk$android$services$network$HttpMethod;
                i = HttpMethod.PUT$6bc89afe;
                iArr[2] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                iArr = $SwitchMap$io$fabric$sdk$android$services$network$HttpMethod;
                i = HttpMethod.DELETE$6bc89afe;
                iArr[3] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    public DefaultHttpRequestFactory() {
        this(new DefaultLogger());
    }

    public DefaultHttpRequestFactory(Logger logger) {
        this.logger = logger;
    }

    public final void setPinningInfoProvider(PinningInfoProvider pinningInfo) {
        if (this.pinningInfo != pinningInfo) {
            this.pinningInfo = pinningInfo;
            resetSSLSocketFactory();
        }
    }

    private synchronized void resetSSLSocketFactory() {
        this.attemptedSslInit = false;
        this.sslSocketFactory = null;
    }

    public final HttpRequest buildHttpRequest$5b7d0be6(int method, String url, Map<String, String> queryParams) {
        HttpRequest httpRequest;
        boolean z = true;
        switch (AnonymousClass1.$SwitchMap$io$fabric$sdk$android$services$network$HttpMethod[method - 1]) {
            case 1:
                httpRequest = HttpRequest.get(url, queryParams, true);
                break;
            case 2:
                httpRequest = HttpRequest.post(url, queryParams, true);
                break;
            case 3:
                httpRequest = HttpRequest.put(url);
                break;
            case 4:
                httpRequest = HttpRequest.delete(url);
                break;
            default:
                throw new IllegalArgumentException("Unsupported HTTP method!");
        }
        if (url == null || !url.toLowerCase(Locale.US).startsWith("https")) {
            z = false;
        }
        if (z && this.pinningInfo != null) {
            SSLSocketFactory sslSocketFactory = getSSLSocketFactory();
            if (sslSocketFactory != null) {
                ((HttpsURLConnection) httpRequest.getConnection()).setSSLSocketFactory(sslSocketFactory);
            }
        }
        return httpRequest;
    }

    private synchronized SSLSocketFactory getSSLSocketFactory() {
        if (this.sslSocketFactory == null && !this.attemptedSslInit) {
            this.sslSocketFactory = initSSLSocketFactory();
        }
        return this.sslSocketFactory;
    }

    private synchronized SSLSocketFactory initSSLSocketFactory() {
        SSLSocketFactory sslSocketFactory;
        this.attemptedSslInit = true;
        try {
            PinningInfoProvider pinningInfoProvider = this.pinningInfo;
            SSLContext instance = SSLContext.getInstance("TLS");
            PinningTrustManager pinningTrustManager = new PinningTrustManager(new SystemKeyStore(pinningInfoProvider.getKeyStoreStream(), pinningInfoProvider.getKeyStorePassword()), pinningInfoProvider);
            instance.init(null, new TrustManager[]{pinningTrustManager}, null);
            sslSocketFactory = instance.getSocketFactory();
            this.logger.d("Fabric", "Custom SSL pinning enabled");
        } catch (Exception e) {
            this.logger.e("Fabric", "Exception while validating pinned certs", e);
            sslSocketFactory = null;
        }
        return sslSocketFactory;
    }
}
