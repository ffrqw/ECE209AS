package okhttp3.internal.http;

import java.net.Proxy.Type;
import okhttp3.HttpUrl;
import okhttp3.Request;

public final class RequestLine {
    public static String get(Request request, Type proxyType) {
        Object obj;
        StringBuilder result = new StringBuilder();
        result.append(request.method());
        result.append(' ');
        if (request.isHttps() || proxyType != Type.HTTP) {
            obj = null;
        } else {
            obj = 1;
        }
        if (obj != null) {
            result.append(request.url());
        } else {
            result.append(requestPath(request.url()));
        }
        result.append(" HTTP/1.1");
        return result.toString();
    }

    public static String requestPath(HttpUrl url) {
        String path = url.encodedPath();
        String query = url.encodedQuery();
        return query != null ? path + '?' + query : path;
    }
}
