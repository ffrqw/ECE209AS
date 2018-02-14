package org.springframework.web.client;

import org.springframework.core.NestedRuntimeException;

public class RestClientException extends NestedRuntimeException {
    public RestClientException(String msg) {
        super(msg);
    }

    public RestClientException(String msg, Throwable ex) {
        super(msg, ex);
    }
}
