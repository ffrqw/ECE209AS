package org.springframework.http.client;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map.Entry;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;

final class SimpleBufferingClientHttpRequest extends AbstractBufferingClientHttpRequest {
    private final HttpURLConnection connection;
    private final boolean outputStreaming;

    SimpleBufferingClientHttpRequest(HttpURLConnection connection, boolean outputStreaming) {
        this.connection = connection;
        this.outputStreaming = outputStreaming;
    }

    public final HttpMethod getMethod() {
        return HttpMethod.valueOf(this.connection.getRequestMethod());
    }

    public final URI getURI() {
        try {
            return this.connection.getURL().toURI();
        } catch (URISyntaxException ex) {
            throw new IllegalStateException("Could not get HttpURLConnection URI: " + ex.getMessage(), ex);
        }
    }

    protected final ClientHttpResponse executeInternal(HttpHeaders headers, byte[] bufferedOutput) throws IOException {
        for (Entry<String, List<String>> entry : headers.entrySet()) {
            String headerName = (String) entry.getKey();
            for (String headerValue : (List) entry.getValue()) {
                this.connection.addRequestProperty(headerName, headerValue);
            }
        }
        if (this.connection.getDoOutput() && this.outputStreaming) {
            this.connection.setFixedLengthStreamingMode(bufferedOutput.length);
        }
        this.connection.connect();
        if (this.connection.getDoOutput()) {
            OutputStream outputStream = this.connection.getOutputStream();
            Assert.notNull(bufferedOutput, "No input byte array specified");
            Assert.notNull(outputStream, "No OutputStream specified");
            try {
                outputStream.write(bufferedOutput);
            } finally {
                try {
                    outputStream.close();
                } catch (IOException e) {
                }
            }
        }
        return new SimpleClientHttpResponse(this.connection);
    }
}
