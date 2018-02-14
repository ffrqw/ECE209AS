package com.rachio.iro.cloud;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;

public class MultiUseResponse implements ClientHttpResponse {
    private byte[] body;
    private boolean bodyRead = false;
    private final ClientHttpResponse original;

    public MultiUseResponse(ClientHttpResponse original) {
        this.original = original;
    }

    public final HttpStatus getStatusCode() throws IOException {
        return this.original.getStatusCode();
    }

    public final int getRawStatusCode() throws IOException {
        return this.original.getRawStatusCode();
    }

    public final String getStatusText() throws IOException {
        return this.original.getStatusText();
    }

    public void close() {
        this.original.close();
    }

    public final synchronized InputStream getBody() throws IOException {
        InputStream byteArrayInputStream;
        getBodyArray();
        if (this.body != null) {
            byteArrayInputStream = new ByteArrayInputStream(this.body);
        } else {
            byteArrayInputStream = null;
        }
        return byteArrayInputStream;
    }

    public final HttpHeaders getHeaders() {
        return this.original.getHeaders();
    }

    public final synchronized byte[] getBodyArray() throws IOException {
        if (!this.bodyRead) {
            this.bodyRead = true;
            InputStream inputStream = this.original.getBody();
            if (inputStream != null) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[64];
                while (true) {
                    int read = inputStream.read(buffer);
                    if (read <= 0) {
                        break;
                    }
                    outputStream.write(buffer, 0, read);
                }
                this.body = outputStream.toByteArray();
            }
        }
        return this.body;
    }
}
