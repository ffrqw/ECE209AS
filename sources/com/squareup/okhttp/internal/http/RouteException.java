package com.squareup.okhttp.internal.http;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class RouteException extends Exception {
    private static final Method addSuppressedExceptionMethod;
    private IOException lastException;

    static {
        Method m;
        try {
            m = Throwable.class.getDeclaredMethod("addSuppressed", new Class[]{Throwable.class});
        } catch (Exception e) {
            m = null;
        }
        addSuppressedExceptionMethod = m;
    }

    public RouteException(IOException cause) {
        super(cause);
        this.lastException = cause;
    }

    public final IOException getLastConnectException() {
        return this.lastException;
    }

    public final void addConnectException(IOException e) {
        IOException iOException = this.lastException;
        if (addSuppressedExceptionMethod != null) {
            try {
                addSuppressedExceptionMethod.invoke(e, new Object[]{iOException});
            } catch (InvocationTargetException e2) {
            } catch (IllegalAccessException e3) {
            }
        }
        this.lastException = e;
    }
}
