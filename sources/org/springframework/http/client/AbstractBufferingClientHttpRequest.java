package org.springframework.http.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.springframework.http.HttpHeaders;

abstract class AbstractBufferingClientHttpRequest extends AbstractClientHttpRequest {
    private ByteArrayOutputStream bufferedOutput = new ByteArrayOutputStream();

    protected abstract ClientHttpResponse executeInternal(HttpHeaders httpHeaders, byte[] bArr) throws IOException;

    AbstractBufferingClientHttpRequest() {
    }

    protected final OutputStream getBodyInternal(HttpHeaders headers) throws IOException {
        return this.bufferedOutput;
    }

    protected final ClientHttpResponse executeInternal(HttpHeaders headers) throws IOException {
        byte[] bytes = this.bufferedOutput.toByteArray();
        if (headers.getContentLength() == -1) {
            headers.setContentLength((long) bytes.length);
        }
        ClientHttpResponse result = executeInternal(headers, bytes);
        this.bufferedOutput = null;
        return result;
    }
}
