package org.springframework.web.client;

import java.io.IOException;

public final class ResourceAccessException extends RestClientException {
    public ResourceAccessException(String msg, IOException ex) {
        super(msg, ex);
    }
}
