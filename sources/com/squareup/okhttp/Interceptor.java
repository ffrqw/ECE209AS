package com.squareup.okhttp;

import java.io.IOException;

public interface Interceptor {

    public interface Chain {
        Response proceed(Request request) throws IOException;
    }

    Response intercept$4449e3ea() throws IOException;
}
