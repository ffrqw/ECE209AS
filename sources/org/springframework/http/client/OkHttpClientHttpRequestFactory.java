package org.springframework.http.client;

import com.squareup.okhttp.OkHttpClient;
import java.net.URI;
import java.util.concurrent.TimeUnit;
import org.springframework.http.HttpMethod;

public final class OkHttpClientHttpRequestFactory implements ClientHttpRequestFactory {
    private final OkHttpClient client = new OkHttpClient();
    private final boolean defaultClient = true;

    public final void setReadTimeout(int readTimeout) {
        this.client.setReadTimeout((long) readTimeout, TimeUnit.MILLISECONDS);
    }

    public final void setConnectTimeout(int connectTimeout) {
        this.client.setConnectTimeout((long) connectTimeout, TimeUnit.MILLISECONDS);
    }

    public final ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) {
        return new OkHttpClientHttpRequest(this.client, uri, httpMethod);
    }
}
