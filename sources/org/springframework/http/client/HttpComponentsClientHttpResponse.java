package org.springframework.http.client;

import java.io.IOException;
import java.io.InputStream;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtilsHC4;
import org.springframework.http.HttpHeaders;

final class HttpComponentsClientHttpResponse extends AbstractClientHttpResponse {
    private HttpHeaders headers;
    private final CloseableHttpResponse httpResponse;

    HttpComponentsClientHttpResponse(CloseableHttpResponse httpResponse) {
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

    public final InputStream getBodyInternal() throws IOException {
        HttpEntity entity = this.httpResponse.getEntity();
        return entity != null ? entity.getContent() : null;
    }

    public final void closeInternal() {
        try {
            EntityUtilsHC4.consume(this.httpResponse.getEntity());
            this.httpResponse.close();
        } catch (IOException e) {
        } catch (Throwable th) {
            this.httpResponse.close();
        }
    }
}
