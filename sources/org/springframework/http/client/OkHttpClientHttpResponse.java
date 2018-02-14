package org.springframework.http.client;

import com.squareup.okhttp.Response;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.http.HttpHeaders;
import org.springframework.util.Assert;

final class OkHttpClientHttpResponse extends AbstractClientHttpResponse {
    private HttpHeaders headers;
    private final Response response;

    public OkHttpClientHttpResponse(Response response) {
        Assert.notNull(response, "'response' must not be null");
        this.response = response;
    }

    public final int getRawStatusCode() {
        return this.response.code();
    }

    public final String getStatusText() {
        return this.response.message();
    }

    public final InputStream getBodyInternal() throws IOException {
        return this.response.body().byteStream();
    }

    public final HttpHeaders getHeaders() {
        if (this.headers == null) {
            HttpHeaders headers = new HttpHeaders();
            for (String headerName : this.response.headers().names()) {
                for (String headerValue : this.response.headers(headerName)) {
                    headers.add(headerName, headerValue);
                }
            }
            this.headers = headers;
        }
        return this.headers;
    }

    public final void closeInternal() {
        try {
            this.response.body().close();
        } catch (IOException e) {
        }
    }
}
