package com.squareup.okhttp.internal.http;

public final class HttpMethod {
    public static boolean requiresRequestBody(String method) {
        return method.equals("POST") || method.equals("PUT") || method.equals("PATCH") || method.equals("PROPPATCH") || method.equals("REPORT");
    }

    public static boolean permitsRequestBody(String method) {
        return requiresRequestBody(method) || method.equals("OPTIONS") || method.equals("DELETE") || method.equals("PROPFIND") || method.equals("MKCOL") || method.equals("LOCK");
    }
}
