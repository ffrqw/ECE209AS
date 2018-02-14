package org.springframework.http.client;

import java.io.IOException;
import java.net.URI;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;

@Deprecated
public final class HttpComponentsAndroidClientHttpRequestFactory implements ClientHttpRequestFactory {
    private HttpClient httpClient;

    public HttpComponentsAndroidClientHttpRequestFactory() {
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
        HttpParams params = new BasicHttpParams();
        ThreadSafeClientConnManager connectionManager = new ThreadSafeClientConnManager(params, schemeRegistry);
        ConnManagerParams.setMaxTotalConnections(params, 100);
        ConnManagerParams.setMaxConnectionsPerRoute(params, new ConnPerRouteBean(5));
        this.httpClient = new DefaultHttpClient(connectionManager, null);
        Assert.isTrue(true, "Timeout must be a non-negative value");
        this.httpClient.getParams().setIntParameter("http.socket.timeout", 60000);
    }

    public final ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
        HttpUriRequest httpRequest;
        switch (httpMethod) {
            case GET:
                httpRequest = new HttpGet(uri);
                break;
            case DELETE:
                httpRequest = new HttpDelete(uri);
                break;
            case HEAD:
                httpRequest = new HttpHead(uri);
                break;
            case OPTIONS:
                httpRequest = new HttpOptions(uri);
                break;
            case POST:
                httpRequest = new HttpPost(uri);
                break;
            case PUT:
                httpRequest = new HttpPut(uri);
                break;
            case TRACE:
                httpRequest = new HttpTrace(uri);
                break;
            default:
                throw new IllegalArgumentException("Invalid HTTP method: " + httpMethod);
        }
        HttpProtocolParams.setUseExpectContinue(httpRequest.getParams(), false);
        return new HttpComponentsAndroidClientHttpRequest(this.httpClient, httpRequest, null);
    }
}
