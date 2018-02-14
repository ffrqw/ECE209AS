package com.squareup.okhttp;

import com.squareup.okhttp.Interceptor.Chain;
import com.squareup.okhttp.Request.Builder;
import com.squareup.okhttp.internal.http.HttpEngine;
import com.squareup.okhttp.internal.http.RequestException;
import com.squareup.okhttp.internal.http.RouteException;
import com.squareup.okhttp.internal.http.StreamAllocation;
import java.io.IOException;
import java.net.ProtocolException;

public final class Call {
    volatile boolean canceled;
    private final OkHttpClient client;
    HttpEngine engine;
    private boolean executed;
    Request originalRequest;

    class ApplicationInterceptorChain implements Chain {
        private final boolean forWebSocket;
        private final int index;
        private final Request request;

        ApplicationInterceptorChain(int index, Request request, boolean forWebSocket) {
            this.index = index;
            this.request = request;
            this.forWebSocket = forWebSocket;
        }

        public final Response proceed(Request request) throws IOException {
            if (this.index >= Call.this.client.interceptors().size()) {
                return Call.this.getResponse(request, this.forWebSocket);
            }
            ApplicationInterceptorChain applicationInterceptorChain = new ApplicationInterceptorChain(this.index + 1, request, this.forWebSocket);
            Interceptor interceptor = (Interceptor) Call.this.client.interceptors().get(this.index);
            Response intercept$4449e3ea = interceptor.intercept$4449e3ea();
            if (intercept$4449e3ea != null) {
                return intercept$4449e3ea;
            }
            throw new NullPointerException("application interceptor " + interceptor + " returned null");
        }
    }

    protected Call(OkHttpClient client, Request originalRequest) {
        this.client = client.copyWithDefaults();
        this.originalRequest = originalRequest;
    }

    public final Response execute() throws IOException {
        synchronized (this) {
            if (this.executed) {
                throw new IllegalStateException("Already Executed");
            }
            this.executed = true;
        }
        try {
            this.client.getDispatcher().executed(this);
            Response result = new ApplicationInterceptorChain(0, this.originalRequest, false).proceed(this.originalRequest);
            if (result != null) {
                return result;
            }
            throw new IOException("Canceled");
        } finally {
            this.client.getDispatcher().finished(this);
        }
    }

    final Response getResponse(Request request, boolean forWebSocket) throws IOException {
        Response response;
        HttpEngine retryEngine;
        RequestBody body = request.body();
        if (body != null) {
            Builder requestBuilder = request.newBuilder();
            MediaType contentType = body.contentType();
            if (contentType != null) {
                requestBuilder.header("Content-Type", contentType.toString());
            }
            long contentLength = body.contentLength();
            if (contentLength != -1) {
                requestBuilder.header("Content-Length", Long.toString(contentLength));
                requestBuilder.removeHeader("Transfer-Encoding");
            } else {
                requestBuilder.header("Transfer-Encoding", "chunked");
                requestBuilder.removeHeader("Content-Length");
            }
            request = requestBuilder.build();
        }
        this.engine = new HttpEngine(this.client, request, false, false, forWebSocket, null, null, null);
        int followUpCount = 0;
        while (true) {
            boolean z = this.canceled;
            try {
                this.engine.sendRequest();
                this.engine.readResponse();
                response = this.engine.getResponse();
                Request followUp = this.engine.followUpRequest();
                if (followUp == null) {
                    break;
                }
                StreamAllocation streamAllocation = this.engine.close();
                followUpCount++;
                if (followUpCount > 20) {
                    streamAllocation.release();
                    throw new ProtocolException("Too many follow-up requests: " + followUpCount);
                }
                if (!this.engine.sameConnection(followUp.httpUrl())) {
                    streamAllocation.release();
                    streamAllocation = null;
                }
                this.engine = new HttpEngine(this.client, followUp, false, false, forWebSocket, streamAllocation, null, response);
            } catch (RequestException e) {
                throw e.getCause();
            } catch (RouteException e2) {
                retryEngine = this.engine.recover(e2);
                if (retryEngine != null) {
                    this.engine = retryEngine;
                } else {
                    throw e2.getLastConnectException();
                }
            } catch (IOException e3) {
                retryEngine = this.engine.recover(e3, null);
                if (retryEngine != null) {
                    this.engine = retryEngine;
                } else {
                    throw e3;
                }
            } catch (Throwable th) {
                if (true) {
                    this.engine.close().release();
                }
            }
        }
        if (!forWebSocket) {
            this.engine.streamAllocation.release();
        }
        return response;
    }
}
