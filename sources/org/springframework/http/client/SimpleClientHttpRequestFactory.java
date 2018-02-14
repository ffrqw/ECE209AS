package org.springframework.http.client;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;

public final class SimpleClientHttpRequestFactory implements ClientHttpRequestFactory {
    private boolean bufferRequestBody = true;
    private int chunkSize = 0;
    private int connectTimeout = -1;
    private boolean outputStreaming = true;
    private int readTimeout = -1;
    private boolean reuseConnection = false;

    public final ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
        System.setProperty("http.keepAlive", Boolean.toString(false));
        URL toURL = uri.toURL();
        HttpURLConnection connection = null != null ? toURL.openConnection(null) : toURL.openConnection();
        Assert.isInstanceOf(HttpURLConnection.class, connection, "");
        connection = connection;
        String name = httpMethod.name();
        if (this.connectTimeout >= 0) {
            connection.setConnectTimeout(this.connectTimeout);
        }
        if (this.readTimeout >= 0) {
            connection.setReadTimeout(this.readTimeout);
        }
        connection.setDoInput(true);
        if ("GET".equals(name)) {
            connection.setInstanceFollowRedirects(true);
        } else {
            connection.setInstanceFollowRedirects(false);
        }
        if ("PUT".equals(name) || "POST".equals(name) || "PATCH".equals(name)) {
            connection.setDoOutput(true);
        } else {
            connection.setDoOutput(false);
        }
        connection.setRequestMethod(name);
        if (this.bufferRequestBody) {
            return new SimpleBufferingClientHttpRequest(connection, this.outputStreaming);
        }
        return new SimpleStreamingClientHttpRequest(connection, 0, this.outputStreaming, false);
    }
}
