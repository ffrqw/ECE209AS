package org.springframework.http.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import org.springframework.http.ContentCodingType;
import org.springframework.http.HttpStatus;

public abstract class AbstractClientHttpResponse implements ClientHttpResponse {
    private InputStream compressedBody;

    protected abstract void closeInternal();

    protected abstract InputStream getBodyInternal() throws IOException;

    public final HttpStatus getStatusCode() throws IOException {
        return HttpStatus.valueOf(getRawStatusCode());
    }

    public final InputStream getBody() throws IOException {
        Object obj;
        InputStream body = getBodyInternal();
        for (ContentCodingType equals : getHeaders().getContentEncoding()) {
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
            this.compressedBody = new GZIPInputStream(body);
        }
        return this.compressedBody;
    }

    public void close() {
        if (this.compressedBody != null) {
            try {
                this.compressedBody.close();
            } catch (IOException e) {
            }
        }
        closeInternal();
    }
}
