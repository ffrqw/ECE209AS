package org.springframework.http.client.support;

import java.util.ArrayList;
import java.util.List;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.InterceptingClientHttpRequestFactory;
import org.springframework.util.CollectionUtils;

public abstract class InterceptingHttpAccessor extends HttpAccessor {
    private List<ClientHttpRequestInterceptor> interceptors = new ArrayList();

    public final void setInterceptors(List<ClientHttpRequestInterceptor> interceptors) {
        this.interceptors = interceptors;
    }

    public final ClientHttpRequestFactory getRequestFactory() {
        ClientHttpRequestFactory delegate = super.getRequestFactory();
        if (CollectionUtils.isEmpty(this.interceptors)) {
            return delegate;
        }
        return new InterceptingClientHttpRequestFactory(delegate, this.interceptors);
    }
}
