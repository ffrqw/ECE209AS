package org.springframework.http.client;

import java.io.Closeable;
import java.io.IOException;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;

public interface ClientHttpResponse extends Closeable, HttpInputMessage {
    void close();

    int getRawStatusCode() throws IOException;

    HttpStatus getStatusCode() throws IOException;

    String getStatusText() throws IOException;
}
