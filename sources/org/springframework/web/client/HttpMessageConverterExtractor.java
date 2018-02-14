package org.springframework.web.client;

import android.util.Log;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.Assert;

public final class HttpMessageConverterExtractor<T> implements ResponseExtractor<T> {
    private final List<HttpMessageConverter<?>> messageConverters;
    private final Class<T> responseClass;
    private final Type responseType;

    public HttpMessageConverterExtractor(Type responseType, List<HttpMessageConverter<?>> messageConverters) {
        Assert.notNull(responseType, "'responseType' must not be null");
        Assert.notEmpty(messageConverters, "'messageConverters' must not be empty");
        this.responseType = responseType;
        this.responseClass = responseType instanceof Class ? (Class) responseType : null;
        this.messageConverters = messageConverters;
    }

    public final T extractData(ClientHttpResponse response) throws IOException {
        Object obj = null;
        HttpStatus statusCode = response.getStatusCode();
        if (!(statusCode == HttpStatus.NO_CONTENT || statusCode == HttpStatus.NOT_MODIFIED || response.getHeaders().getContentLength() == 0)) {
            obj = 1;
        }
        if (obj == null) {
            return null;
        }
        MediaType contentType = response.getHeaders().getContentType();
        if (contentType == null) {
            if (Log.isLoggable("RestTemplate", 2)) {
                Log.v("RestTemplate", "No Content-Type header found, defaulting to application/octet-stream");
            }
            contentType = MediaType.APPLICATION_OCTET_STREAM;
        }
        for (HttpMessageConverter messageConverter : this.messageConverters) {
            if (messageConverter instanceof GenericHttpMessageConverter) {
                GenericHttpMessageConverter genericMessageConverter = (GenericHttpMessageConverter) messageConverter;
                if (genericMessageConverter.canRead(this.responseType, null, contentType)) {
                    if (Log.isLoggable("RestTemplate", 3)) {
                        Log.d("RestTemplate", "Reading [" + this.responseType + "] as \"" + contentType + "\" using [" + messageConverter + "]");
                    }
                    return genericMessageConverter.read(this.responseType, null, response);
                }
            }
            if (this.responseClass != null && messageConverter.canRead(this.responseClass, contentType)) {
                if (Log.isLoggable("RestTemplate", 3)) {
                    Log.d("RestTemplate", "Reading [" + this.responseClass.getName() + "] as \"" + contentType + "\" using [" + messageConverter + "]");
                }
                return messageConverter.read(this.responseClass, response);
            }
        }
        throw new RestClientException("Could not extract response: no suitable HttpMessageConverter found for response type [" + this.responseType + "] and content type [" + contentType + "]");
    }
}
