package org.springframework.http.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

final class SimpleClientHttpResponse extends AbstractClientHttpResponse {
    private final HttpURLConnection connection;
    private HttpHeaders headers;

    SimpleClientHttpResponse(HttpURLConnection connection) {
        this.connection = connection;
    }

    public final int getRawStatusCode() throws IOException {
        try {
            return this.connection.getResponseCode();
        } catch (IOException ex) {
            return handleIOException(ex);
        }
    }

    private static int handleIOException(IOException ex) throws IOException {
        if ("Received authentication challenge is null".equals(ex.getMessage()) || "No authentication challenges found".equals(ex.getMessage())) {
            return HttpStatus.UNAUTHORIZED.value();
        }
        if ("Received HTTP_PROXY_AUTH (407) code while not using proxy".equals(ex.getMessage())) {
            return HttpStatus.PROXY_AUTHENTICATION_REQUIRED.value();
        }
        throw ex;
    }

    public final String getStatusText() throws IOException {
        try {
            return this.connection.getResponseMessage();
        } catch (IOException ex) {
            return HttpStatus.valueOf(handleIOException(ex)).getReasonPhrase();
        }
    }

    public final HttpHeaders getHeaders() {
        if (this.headers == null) {
            this.headers = new HttpHeaders();
            String name = this.connection.getHeaderFieldKey(0);
            if (StringUtils.hasLength(name)) {
                this.headers.add(name, this.connection.getHeaderField(0));
            }
            int i = 1;
            while (true) {
                name = this.connection.getHeaderFieldKey(i);
                if (!StringUtils.hasLength(name)) {
                    break;
                }
                this.headers.add(name, this.connection.getHeaderField(i));
                i++;
            }
        }
        return this.headers;
    }

    protected final InputStream getBodyInternal() throws IOException {
        InputStream errorStream = this.connection.getErrorStream();
        return errorStream != null ? errorStream : this.connection.getInputStream();
    }

    protected final void closeInternal() {
        this.connection.disconnect();
    }
}
