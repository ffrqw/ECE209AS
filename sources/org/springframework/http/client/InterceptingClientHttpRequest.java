package org.springframework.http.client;

import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.util.StreamUtils;

final class InterceptingClientHttpRequest extends AbstractBufferingClientHttpRequest {
    private final List<ClientHttpRequestInterceptor> interceptors;
    private HttpMethod method;
    private final ClientHttpRequestFactory requestFactory;
    private URI uri;

    private class RequestExecution implements ClientHttpRequestExecution {
        private final Iterator<ClientHttpRequestInterceptor> iterator;

        private RequestExecution() {
            this.iterator = InterceptingClientHttpRequest.this.interceptors.iterator();
        }

        public final ClientHttpResponse execute(HttpRequest request, byte[] body) throws IOException {
            if (this.iterator.hasNext()) {
                return ((ClientHttpRequestInterceptor) this.iterator.next()).intercept(request, body, this);
            }
            ClientHttpRequest delegate = InterceptingClientHttpRequest.this.requestFactory.createRequest(request.getURI(), request.getMethod());
            delegate.getHeaders().putAll(request.getHeaders());
            if (body.length > 0) {
                StreamUtils.copy(body, delegate.getBody());
            }
            return delegate.execute();
        }
    }

    protected InterceptingClientHttpRequest(ClientHttpRequestFactory requestFactory, List<ClientHttpRequestInterceptor> interceptors, URI uri, HttpMethod method) {
        this.requestFactory = requestFactory;
        this.interceptors = interceptors;
        this.method = method;
        this.uri = uri;
    }

    public final HttpMethod getMethod() {
        return this.method;
    }

    public final URI getURI() {
        return this.uri;
    }

    protected final ClientHttpResponse executeInternal(HttpHeaders headers, byte[] bufferedOutput) throws IOException {
        return new RequestExecution().execute(this, bufferedOutput);
    }
}
