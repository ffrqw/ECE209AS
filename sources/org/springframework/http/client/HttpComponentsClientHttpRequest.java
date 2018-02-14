package org.springframework.http.client;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntityHC4;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.HttpContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.CollectionUtils;

final class HttpComponentsClientHttpRequest extends AbstractBufferingClientHttpRequest {
    private final CloseableHttpClient httpClient;
    private final HttpContext httpContext;
    private final HttpUriRequest httpRequest;

    HttpComponentsClientHttpRequest(CloseableHttpClient httpClient, HttpUriRequest httpRequest, HttpContext httpContext) {
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

    protected final ClientHttpResponse executeInternal(HttpHeaders headers, byte[] bufferedOutput) throws IOException {
        addHeaders(this.httpRequest, headers);
        if (this.httpRequest instanceof HttpEntityEnclosingRequest) {
            this.httpRequest.setEntity(new ByteArrayEntityHC4(bufferedOutput));
        }
        return new HttpComponentsClientHttpResponse(this.httpClient.execute(this.httpRequest, this.httpContext));
    }

    static void addHeaders(HttpUriRequest httpRequest, HttpHeaders headers) {
        for (Entry<String, List<String>> entry : headers.entrySet()) {
            String headerName = (String) entry.getKey();
            String headerValue;
            if ("Cookie".equalsIgnoreCase(headerName)) {
                Collection collection = (Collection) entry.getValue();
                String str = "; ";
                String str2 = "";
                String str3 = "";
                if (CollectionUtils.isEmpty(collection)) {
                    headerValue = "";
                } else {
                    StringBuilder stringBuilder = new StringBuilder();
                    Iterator it = collection.iterator();
                    while (it.hasNext()) {
                        stringBuilder.append(str2).append(it.next()).append(str3);
                        if (it.hasNext()) {
                            stringBuilder.append(str);
                        }
                    }
                    headerValue = stringBuilder.toString();
                }
                httpRequest.addHeader(headerName, headerValue);
            } else if (!("Content-Length".equalsIgnoreCase(headerName) || "Transfer-Encoding".equalsIgnoreCase(headerName))) {
                for (String headerValue2 : (List) entry.getValue()) {
                    httpRequest.addHeader(headerName, headerValue2);
                }
            }
        }
    }
}
