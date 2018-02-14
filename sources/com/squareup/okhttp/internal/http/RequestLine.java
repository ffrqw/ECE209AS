package com.squareup.okhttp.internal.http;

import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.Request;
import java.net.Proxy.Type;

public final class RequestLine {
    static String get(Request request, Type proxyType) {
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
            result.append(request.httpUrl());
        } else {
            result.append(requestPath(request.httpUrl()));
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
