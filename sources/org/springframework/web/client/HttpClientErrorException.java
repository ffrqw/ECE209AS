package org.springframework.web.client;

import java.nio.charset.Charset;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

public final class HttpClientErrorException extends HttpStatusCodeException {
    public HttpClientErrorException(HttpStatus statusCode, String statusText) {
        super(statusCode, statusText);
    }

    public HttpClientErrorException(HttpStatus statusCode, String statusText, HttpHeaders responseHeaders, byte[] responseBody, Charset responseCharset) {
        super(statusCode, statusText, responseHeaders, responseBody, responseCharset);
    }
}
