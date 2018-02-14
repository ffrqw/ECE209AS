package org.springframework.http.client;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request.Builder;
import com.squareup.okhttp.RequestBody;
import java.io.IOException;
import java.net.ProtocolException;
import java.net.URI;
import java.util.List;
import java.util.Map.Entry;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;

final class OkHttpClientHttpRequest extends AbstractBufferingClientHttpRequest implements ClientHttpRequest {
    private final OkHttpClient client;
    private final HttpMethod method;
    private final URI uri;

    public OkHttpClientHttpRequest(OkHttpClient client, URI uri, HttpMethod method) {
        this.client = client;
        this.uri = uri;
        this.method = method;
    }

    public final HttpMethod getMethod() {
        return this.method;
    }

    public final URI getURI() {
        return this.uri;
    }

    protected final ClientHttpResponse executeInternal(HttpHeaders headers, byte[] content) throws IOException {
        MediaType contentType;
        String first = headers.getFirst("Content-Type");
        if (StringUtils.hasText(first)) {
            contentType = MediaType.parse(first);
        } else {
            contentType = null;
        }
        RequestBody body = content.length > 0 ? RequestBody.create(contentType, content) : null;
        Builder builder = new Builder().url(this.uri.toURL()).method(this.method.name(), body);
        for (Entry<String, List<String>> entry : headers.entrySet()) {
            String headerName = (String) entry.getKey();
            for (String headerValue : (List) entry.getValue()) {
                builder.addHeader(headerName, headerValue);
            }
        }
        try {
            return new OkHttpClientHttpResponse(this.client.newCall(builder.build()).execute());
        } catch (ProtocolException e) {
            if ("Received HTTP_PROXY_AUTH (407) code while not using proxy".equals(e.getMessage())) {
                throw new HttpClientErrorException(HttpStatus.PROXY_AUTHENTICATION_REQUIRED, HttpStatus.PROXY_AUTHENTICATION_REQUIRED.getReasonPhrase());
            }
            throw e;
        }
    }
}
