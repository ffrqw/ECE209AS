package org.springframework.http.client;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.HttpContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.StreamingHttpOutputMessage;

final class HttpComponentsStreamingClientHttpRequest extends AbstractClientHttpRequest implements StreamingHttpOutputMessage {
    private final CloseableHttpClient httpClient;
    private final HttpContext httpContext;
    private final HttpUriRequest httpRequest;

    HttpComponentsStreamingClientHttpRequest(CloseableHttpClient httpClient, HttpUriRequest httpRequest, HttpContext httpContext) {
        this.httpClient = httpClient;
        this.httpRequest = httpRequest;
        this.httpContext = httpContext;
    }

    public final HttpMethod getMethod() {
        return HttpMethod.valueOf(this.httpRequest.getMethod());
    }

    public final URI getURI() {
        return this.httpRequest.getURI();
    }

    protected final OutputStream getBodyInternal(HttpHeaders headers) throws IOException {
        throw new UnsupportedOperationException("getBody not supported");
    }

    protected final ClientHttpResponse executeInternal(HttpHeaders headers) throws IOException {
        HttpComponentsClientHttpRequest.addHeaders(this.httpRequest, headers);
        return new HttpComponentsClientHttpResponse(this.httpClient.execute(this.httpRequest, this.httpContext));
    }
}
