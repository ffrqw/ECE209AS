package org.springframework.http.converter;

public final class HttpMessageNotReadableException extends HttpMessageConversionException {
    public HttpMessageNotReadableException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
