package io.fabric.sdk.android.services.common;

import io.fabric.sdk.android.Kit;
import io.fabric.sdk.android.services.network.HttpRequest;
import io.fabric.sdk.android.services.network.HttpRequestFactory;
import java.util.Collections;
import java.util.Map;
import java.util.regex.Pattern;

public abstract class AbstractSpiCall {
    private static final Pattern PROTOCOL_AND_HOST_PATTERN = Pattern.compile("http(s?)://[^\\/]+", 2);
    protected final Kit kit;
    private final int method$6bc89afe;
    private final String protocolAndHostOverride;
    private final HttpRequestFactory requestFactory;
    private final String url;

    public AbstractSpiCall(Kit kit, String protocolAndHostOverride, String url, HttpRequestFactory requestFactory, int method) {
        if (url == null) {
            throw new IllegalArgumentException("url must not be null.");
        } else if (requestFactory == null) {
            throw new IllegalArgumentException("requestFactory must not be null.");
        } else {
            this.kit = kit;
            this.protocolAndHostOverride = protocolAndHostOverride;
            if (!CommonUtils.isNullOrEmpty(this.protocolAndHostOverride)) {
                url = PROTOCOL_AND_HOST_PATTERN.matcher(url).replaceFirst(this.protocolAndHostOverride);
            }
            this.url = url;
            this.requestFactory = requestFactory;
            this.method$6bc89afe = method;
        }
    }

    protected final String getUrl() {
        return this.url;
    }

    protected final HttpRequest getHttpRequest() {
        return getHttpRequest(Collections.emptyMap());
    }

    protected final HttpRequest getHttpRequest(Map<String, String> queryParams) {
        HttpRequest httpRequest = this.requestFactory.buildHttpRequest$5b7d0be6(this.method$6bc89afe, this.url, queryParams);
        httpRequest.getConnection().setUseCaches(false);
        httpRequest.getConnection().setConnectTimeout(10000);
        return httpRequest.header("User-Agent", "Crashlytics Android SDK/" + this.kit.getVersion()).header("X-CRASHLYTICS-DEVELOPER-TOKEN", "470fa2b4ae81cd56ecbcda9735803434cec591fa");
    }
}
