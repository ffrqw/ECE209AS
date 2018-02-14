package org.springframework.http.client;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;
import org.springframework.http.ContentCodingType;
import org.springframework.http.HttpHeaders;
import org.springframework.util.Assert;

public abstract class AbstractClientHttpRequest implements ClientHttpRequest {
    private GZIPOutputStream compressedBody;
    private boolean executed = false;
    private final HttpHeaders headers = new HttpHeaders();

    protected abstract ClientHttpResponse executeInternal(HttpHeaders httpHeaders) throws IOException;

    protected abstract OutputStream getBodyInternal(HttpHeaders httpHeaders) throws IOException;

    public final HttpHeaders getHeaders() {
        return this.executed ? HttpHeaders.readOnlyHttpHeaders(this.headers) : this.headers;
    }

    public final OutputStream getBody() throws IOException {
        Object obj;
        assertNotExecuted();
        OutputStream body = getBodyInternal(this.headers);
        for (ContentCodingType equals : this.headers.getContentEncoding()) {
            if (equals.equals(ContentCodingType.GZIP)) {
                obj = 1;
                break;
            }
        }
        obj = null;
        if (obj == null) {
            return body;
        }
        if (this.compressedBody == null) {
            this.compressedBody = new GZIPOutputStream(body);
        }
        return this.compressedBody;
    }

    public final ClientHttpResponse execute() throws IOException {
        assertNotExecuted();
        if (this.compressedBody != null) {
            this.compressedBody.close();
        }
        ClientHttpResponse result = executeInternal(this.headers);
        this.executed = true;
        return result;
    }

    private void assertNotExecuted() {
        Assert.state(!this.executed, "ClientHttpRequest already executed");
    }
}
