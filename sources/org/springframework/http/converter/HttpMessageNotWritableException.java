package org.springframework.http.converter;

public final class HttpMessageNotWritableException extends HttpMessageConversionException {
    public HttpMessageNotWritableException(String msg) {
        super(msg);
    }

    public HttpMessageNotWritableException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
