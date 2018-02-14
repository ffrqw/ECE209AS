package org.springframework.web.client;

import java.nio.charset.Charset;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

public abstract class HttpStatusCodeException extends RestClientException {
    private final byte[] responseBody;
    private final String responseCharset;
    private final HttpHeaders responseHeaders;
    private final HttpStatus statusCode;
    private final String statusText;

    protected HttpStatusCodeException(HttpStatus statusCode, String statusText) {
        this(statusCode, statusText, null, null, null);
    }

    protected HttpStatusCodeException(HttpStatus statusCode, String statusText, HttpHeaders responseHeaders, byte[] responseBody, Charset responseCharset) {
        super(statusCode.value() + " " + statusText);
        this.statusCode = statusCode;
        this.statusText = statusText;
        this.responseHeaders = responseHeaders;
        if (responseBody == null) {
            responseBody = new byte[0];
        }
        this.responseBody = responseBody;
        this.responseCharset = responseCharset != null ? responseCharset.name() : "ISO-8859-1";
    }
}
