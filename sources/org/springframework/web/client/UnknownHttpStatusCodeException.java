package org.springframework.web.client;

import java.nio.charset.Charset;
import org.springframework.http.HttpHeaders;

public final class UnknownHttpStatusCodeException extends RestClientException {
    private final int rawStatusCode;
    private final byte[] responseBody;
    private final String responseCharset;
    private final HttpHeaders responseHeaders;
    private final String statusText;

    public UnknownHttpStatusCodeException(int rawStatusCode, String statusText, HttpHeaders responseHeaders, byte[] responseBody, Charset responseCharset) {
        super("Unknown status code [" + String.valueOf(rawStatusCode) + "] " + statusText);
        this.rawStatusCode = rawStatusCode;
        this.statusText = statusText;
        this.responseHeaders = responseHeaders;
        if (responseBody == null) {
            responseBody = new byte[0];
        }
        this.responseBody = responseBody;
        this.responseCharset = responseCharset != null ? responseCharset.name() : "ISO-8859-1";
    }
}
