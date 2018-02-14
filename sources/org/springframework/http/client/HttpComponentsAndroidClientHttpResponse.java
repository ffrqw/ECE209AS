package org.springframework.http.client;

import java.io.IOException;
import java.io.InputStream;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.springframework.http.HttpHeaders;

@Deprecated
final class HttpComponentsAndroidClientHttpResponse extends AbstractClientHttpResponse {
    private HttpHeaders headers;
    private final HttpResponse httpResponse;

    HttpComponentsAndroidClientHttpResponse(HttpResponse httpResponse) {
        this.httpResponse = httpResponse;
    }

    public final int getRawStatusCode() throws IOException {
        return this.httpResponse.getStatusLine().getStatusCode();
    }

    public final String getStatusText() throws IOException {
        return this.httpResponse.getStatusLine().getReasonPhrase();
    }

    public final HttpHeaders getHeaders() {
        if (this.headers == null) {
            this.headers = new HttpHeaders();
            for (Header header : this.httpResponse.getAllHeaders()) {
                this.headers.add(header.getName(), header.getValue());
            }
        }
        return this.headers;
    }

    protected final InputStream getBodyInternal() throws IOException {
        HttpEntity entity = this.httpResponse.getEntity();
        return entity != null ? entity.getContent() : null;
    }

    protected final void closeInternal() {
        HttpEntity entity = this.httpResponse.getEntity();
        if (entity != null) {
            try {
                entity.consumeContent();
            } catch (IOException e) {
            }
        }
    }
}
