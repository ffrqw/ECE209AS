package org.springframework.http.client;

import java.io.IOException;
import java.net.URI;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.Configurable;
import org.apache.http.client.methods.HttpDeleteHC4;
import org.apache.http.client.methods.HttpGetHC4;
import org.apache.http.client.methods.HttpHeadHC4;
import org.apache.http.client.methods.HttpOptionsHC4;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPostHC4;
import org.apache.http.client.methods.HttpPutHC4;
import org.apache.http.client.methods.HttpTraceHC4;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HttpContext;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;

public final class HttpComponentsClientHttpRequestFactory implements ClientHttpRequestFactory {
    private boolean bufferRequestBody;
    private CloseableHttpClient httpClient;

    public HttpComponentsClientHttpRequestFactory() {
        this(HttpClients.createSystem());
    }

    private HttpComponentsClientHttpRequestFactory(HttpClient httpClient) {
        this.bufferRequestBody = true;
        Assert.notNull(httpClient, "'httpClient' must not be null");
        Assert.isInstanceOf(CloseableHttpClient.class, httpClient, "'httpClient' is not of type CloseableHttpClient");
        this.httpClient = (CloseableHttpClient) httpClient;
    }

    public final ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
        boolean z;
        HttpUriRequest httpRequest;
        CloseableHttpClient client = this.httpClient;
        if (client != null) {
            z = true;
        } else {
            z = false;
        }
        Assert.state(z, "Synchronous execution requires an HttpClient to be set");
        switch (httpMethod) {
            case GET:
                httpRequest = new HttpGetHC4(uri);
                break;
            case DELETE:
                httpRequest = new HttpDeleteHC4(uri);
                break;
            case HEAD:
                httpRequest = new HttpHeadHC4(uri);
                break;
            case OPTIONS:
                httpRequest = new HttpOptionsHC4(uri);
                break;
            case POST:
                httpRequest = new HttpPostHC4(uri);
                break;
            case PUT:
                httpRequest = new HttpPutHC4(uri);
                break;
            case TRACE:
                httpRequest = new HttpTraceHC4(uri);
                break;
            case PATCH:
                httpRequest = new HttpPatch(uri);
                break;
            default:
                throw new IllegalArgumentException("Invalid HTTP method: " + httpMethod);
        }
        HttpContext context = HttpClientContext.create();
        if (context.getAttribute("http.request-config") == null) {
            RequestConfig config = null;
            if (httpRequest instanceof Configurable) {
                config = ((Configurable) httpRequest).getConfig();
            }
            if (config == null) {
                config = RequestConfig.DEFAULT;
            }
            context.setAttribute("http.request-config", config);
        }
        if (this.bufferRequestBody) {
            return new HttpComponentsClientHttpRequest(client, httpRequest, context);
        }
        return new HttpComponentsStreamingClientHttpRequest(client, httpRequest, context);
    }
}
