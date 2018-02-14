package com.squareup.okhttp.internal.http;

import java.io.IOException;

public final class RequestException extends Exception {
    public final IOException getCause() {
        return (IOException) super.getCause();
    }
}
