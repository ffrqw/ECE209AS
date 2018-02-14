package org.springframework.http.client;

import java.io.IOException;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.HttpRequest;

public interface ClientHttpRequest extends HttpOutputMessage, HttpRequest {
    ClientHttpResponse execute() throws IOException;
}
