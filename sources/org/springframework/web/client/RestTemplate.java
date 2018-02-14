package org.springframework.web.client;

import android.util.Log;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.support.InterceptingHttpAccessor;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.http.converter.xml.SimpleXmlHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.web.util.UriTemplate;

public class RestTemplate extends InterceptingHttpAccessor {
    private ResponseErrorHandler errorHandler = new DefaultResponseErrorHandler();
    private final ResponseExtractor<HttpHeaders> headersExtractor = new HeadersExtractor();
    private final List<HttpMessageConverter<?>> messageConverters = new ArrayList();

    private class AcceptHeaderRequestCallback implements RequestCallback {
        private final Type responseType;

        private AcceptHeaderRequestCallback(Type responseType) {
            this.responseType = responseType;
        }

        public void doWithRequest(ClientHttpRequest request) throws IOException {
            if (this.responseType != null) {
                Class<?> responseClass = null;
                if (this.responseType instanceof Class) {
                    responseClass = this.responseType;
                }
                List<MediaType> allSupportedMediaTypes = new ArrayList();
                for (HttpMessageConverter<?> converter : RestTemplate.this.getMessageConverters()) {
                    if (responseClass != null) {
                        if (converter.canRead(responseClass, null)) {
                            allSupportedMediaTypes.addAll(getSupportedMediaTypes(converter));
                        }
                    } else if ((converter instanceof GenericHttpMessageConverter) && ((GenericHttpMessageConverter) converter).canRead(this.responseType, null, null)) {
                        allSupportedMediaTypes.addAll(getSupportedMediaTypes(converter));
                    }
                }
                if (!allSupportedMediaTypes.isEmpty()) {
                    MediaType.sortBySpecificity(allSupportedMediaTypes);
                    if (Log.isLoggable("RestTemplate", 3)) {
                        Log.d("RestTemplate", "Setting request Accept header to " + allSupportedMediaTypes);
                    }
                    request.getHeaders().setAccept(allSupportedMediaTypes);
                }
            }
        }

        private static List<MediaType> getSupportedMediaTypes(HttpMessageConverter<?> messageConverter) {
            List<MediaType> supportedMediaTypes = messageConverter.getSupportedMediaTypes();
            List<MediaType> result = new ArrayList(supportedMediaTypes.size());
            for (MediaType supportedMediaType : supportedMediaTypes) {
                MediaType supportedMediaType2;
                if (supportedMediaType2.getCharSet() != null) {
                    supportedMediaType2 = new MediaType(supportedMediaType2.getType(), supportedMediaType2.getSubtype());
                }
                result.add(supportedMediaType2);
            }
            return result;
        }
    }

    private static class DefaultMessageConverters {
        private static final boolean gsonPresent = ClassUtils.isPresent("com.google.gson.Gson", RestTemplate.class.getClassLoader());
        private static final boolean jackson2Present;
        private static final boolean javaxXmlTransformPresent = ClassUtils.isPresent("javax.xml.transform.Source", RestTemplate.class.getClassLoader());
        private static final boolean simpleXmlPresent = ClassUtils.isPresent("org.simpleframework.xml.Serializer", RestTemplate.class.getClassLoader());

        static {
            boolean z = ClassUtils.isPresent("com.fasterxml.jackson.databind.ObjectMapper", RestTemplate.class.getClassLoader()) && ClassUtils.isPresent("com.fasterxml.jackson.core.JsonGenerator", RestTemplate.class.getClassLoader());
            jackson2Present = z;
        }

        public static void init(List<HttpMessageConverter<?>> messageConverters) {
            messageConverters.add(new ByteArrayHttpMessageConverter());
            messageConverters.add(new StringHttpMessageConverter());
            messageConverters.add(new ResourceHttpMessageConverter());
            if (javaxXmlTransformPresent) {
                messageConverters.add(new SourceHttpMessageConverter());
                messageConverters.add(new AllEncompassingFormHttpMessageConverter());
            } else {
                messageConverters.add(new FormHttpMessageConverter());
            }
            if (simpleXmlPresent) {
                messageConverters.add(new SimpleXmlHttpMessageConverter());
            }
            if (jackson2Present) {
                messageConverters.add(new MappingJackson2HttpMessageConverter());
            } else if (gsonPresent) {
                messageConverters.add(new GsonHttpMessageConverter());
            }
        }
    }

    private static class HeadersExtractor implements ResponseExtractor<HttpHeaders> {
        private HeadersExtractor() {
        }

        public final /* bridge */ /* synthetic */ Object extractData(ClientHttpResponse x0) throws IOException {
            return x0.getHeaders();
        }
    }

    private class HttpEntityRequestCallback extends AcceptHeaderRequestCallback {
        private final HttpEntity<?> requestEntity;

        private HttpEntityRequestCallback(Object requestBody, Type responseType) {
            super(responseType);
            if (requestBody instanceof HttpEntity) {
                this.requestEntity = (HttpEntity) requestBody;
            } else if (requestBody != null) {
                this.requestEntity = new HttpEntity(requestBody);
            } else {
                this.requestEntity = HttpEntity.EMPTY;
            }
        }

        public final void doWithRequest(ClientHttpRequest httpRequest) throws IOException {
            super.doWithRequest(httpRequest);
            HttpHeaders requestHeaders;
            if (this.requestEntity.hasBody()) {
                Object requestBody = this.requestEntity.getBody();
                Class<?> requestType = requestBody.getClass();
                requestHeaders = this.requestEntity.getHeaders();
                MediaType requestContentType = requestHeaders.getContentType();
                for (HttpMessageConverter<?> messageConverter : RestTemplate.this.getMessageConverters()) {
                    if (messageConverter.canWrite(requestType, requestContentType)) {
                        if (!requestHeaders.isEmpty()) {
                            httpRequest.getHeaders().putAll(requestHeaders);
                        }
                        if (Log.isLoggable("RestTemplate", 3)) {
                            if (requestContentType != null) {
                                Log.d("RestTemplate", "Writing [" + requestBody + "] as \"" + requestContentType + "\" using [" + messageConverter + "]");
                            } else {
                                Log.d("RestTemplate", "Writing [" + requestBody + "] using [" + messageConverter + "]");
                            }
                        }
                        messageConverter.write(requestBody, requestContentType, httpRequest);
                        return;
                    }
                }
                String message = "Could not write request: no suitable HttpMessageConverter found for request type [" + requestType.getName() + "]";
                if (requestContentType != null) {
                    message = message + " and content type [" + requestContentType + "]";
                }
                throw new RestClientException(message);
            }
            HttpHeaders httpHeaders = httpRequest.getHeaders();
            requestHeaders = this.requestEntity.getHeaders();
            if (!requestHeaders.isEmpty()) {
                httpHeaders.putAll(requestHeaders);
            }
            if (httpHeaders.getContentLength() == -1) {
                httpHeaders.setContentLength(0);
            }
        }
    }

    private class ResponseEntityResponseExtractor<T> implements ResponseExtractor<ResponseEntity<T>> {
        private final HttpMessageConverterExtractor<T> delegate;

        public ResponseEntityResponseExtractor(Type responseType) {
            if (responseType == null || Void.class.equals(responseType)) {
                this.delegate = null;
            } else {
                this.delegate = new HttpMessageConverterExtractor(responseType, RestTemplate.this.getMessageConverters());
            }
        }

        public final /* bridge */ /* synthetic */ Object extractData(ClientHttpResponse x0) throws IOException {
            if (this.delegate != null) {
                return new ResponseEntity(this.delegate.extractData(x0), x0.getHeaders(), x0.getStatusCode());
            }
            return new ResponseEntity(x0.getHeaders(), x0.getStatusCode());
        }
    }

    public RestTemplate() {
        DefaultMessageConverters.init(this.messageConverters);
    }

    public final List<HttpMessageConverter<?>> getMessageConverters() {
        return this.messageConverters;
    }

    public final void setErrorHandler(ResponseErrorHandler errorHandler) {
        Assert.notNull(errorHandler, "'errorHandler' must not be null");
        this.errorHandler = errorHandler;
    }

    public final <T> ResponseEntity<T> exchange(String url, HttpMethod method, HttpEntity<?> requestEntity, Class<T> responseType, Object... uriVariables) throws RestClientException {
        return (ResponseEntity) doExecute(new UriTemplate(url).expand(uriVariables), method, new HttpEntityRequestCallback(requestEntity, responseType), new ResponseEntityResponseExtractor(responseType));
    }

    public final <T> ResponseEntity<T> exchange(URI url, HttpMethod method, HttpEntity<?> requestEntity, Class<T> responseType) throws RestClientException {
        return (ResponseEntity) doExecute(url, method, new HttpEntityRequestCallback(requestEntity, responseType), new ResponseEntityResponseExtractor(responseType));
    }

    private <T> T doExecute(URI url, HttpMethod method, RequestCallback requestCallback, ResponseExtractor<T> responseExtractor) throws RestClientException {
        Assert.notNull(url, "'url' must not be null");
        Assert.notNull(method, "'method' must not be null");
        ClientHttpResponse response = null;
        try {
            ClientHttpRequest request = createRequest(url, method);
            if (requestCallback != null) {
                requestCallback.doWithRequest(request);
            }
            response = request.execute();
            if (this.errorHandler.hasError(response)) {
                if (Log.isLoggable("RestTemplate", 5)) {
                    try {
                        Log.w("RestTemplate", method.name() + " request for \"" + url + "\" resulted in " + response.getStatusCode() + " (" + response.getStatusText() + "); invoking error handler");
                    } catch (IOException e) {
                    }
                }
                this.errorHandler.handleError(response);
            } else if (Log.isLoggable("RestTemplate", 3)) {
                try {
                    Log.d("RestTemplate", method.name() + " request for \"" + url + "\" resulted in " + response.getStatusCode() + " (" + response.getStatusText() + ")");
                } catch (IOException e2) {
                }
            }
            if (responseExtractor != null) {
                T extractData = responseExtractor.extractData(response);
                if (response == null) {
                    return extractData;
                }
                response.close();
                return extractData;
            }
            if (response != null) {
                response.close();
            }
            return null;
        } catch (IOException ex) {
            throw new ResourceAccessException("I/O error on " + method.name() + " request for \"" + url + "\": " + ex.getMessage(), ex);
        } catch (Throwable th) {
            if (response != null) {
                response.close();
            }
        }
    }
}
