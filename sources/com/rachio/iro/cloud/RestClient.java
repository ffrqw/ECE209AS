package com.rachio.iro.cloud;

import android.net.Uri;
import android.net.Uri.Builder;
import android.text.TextUtils;
import android.util.Log;
import com.rachio.iro.IroApplication;
import com.rachio.iro.Keys;
import com.rachio.iro.cloud.security.HttpRequestEncoder;
import com.rachio.iro.gen2.model.BirthDeviceGeneration2;
import com.rachio.iro.model.ErrorMessage;
import com.rachio.iro.model.Event;
import com.rachio.iro.model.ModelObject;
import com.rachio.iro.model.ResponseCacheItem;
import com.rachio.iro.model.TransmittableView;
import com.rachio.iro.model.annotation.RestClientOptionOverrides;
import com.rachio.iro.model.annotation.RestClientOptions;
import com.rachio.iro.model.annotation.TimeToLive;
import com.rachio.iro.model.apionly.BaseResponse;
import com.rachio.iro.model.apionly.Collaborator;
import com.rachio.iro.model.apionly.DeviceResponse;
import com.rachio.iro.model.db.Database;
import com.rachio.iro.model.device.Zone;
import com.rachio.iro.utils.CrashReporterUtils;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatus.Series;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.OkHttpClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

public class RestClient extends BaseRestClient {
    private static final String BASE_URL = "https://api.rach.io";
    private static final String TAG = RestClient.class.getCanonicalName();
    private Map<String, String> appHeaders;
    private final RachioHttpMessageConverter mappingJackson2HttpMessageConverter = new RachioHttpMessageConverter(TransmittableView.class);
    private Map<String, String> userHeaders = null;

    public static class HttpResponseErrorHandler implements ResponseErrorHandler {
        public ErrorMessage errorMessage;
        public boolean hasError = false;
        public int responseCode = -1;

        public final boolean hasError(ClientHttpResponse response) {
            boolean isErrorStatus = true;
            try {
                HttpStatus status = response.getStatusCode();
                Series valueOf = Series.valueOf(status);
                if (!(valueOf == Series.CLIENT_ERROR || valueOf == Series.SERVER_ERROR)) {
                    this.responseCode = status.value();
                    isErrorStatus = false;
                }
                if (isErrorStatus) {
                    this.hasError = true;
                    this.responseCode = response.getRawStatusCode();
                    this.errorMessage = new ErrorMessage(response.getRawStatusCode(), response.getStatusText());
                }
            } catch (IOException iox) {
                Log.e(RestClient.TAG, "Error getting HttpResponse status", iox);
            }
            return this.hasError;
        }

        public void handleError(ClientHttpResponse response) throws IOException {
        }
    }

    private class ByteArrayResourceWithFileName extends ByteArrayResource {
        private final String filename;

        public ByteArrayResourceWithFileName(byte[] bytes, String filename) {
            super(bytes);
            this.filename = filename;
        }

        public final String getFilename() throws IllegalStateException {
            return this.filename;
        }
    }

    public static final class GetEventsResponse {
    }

    public RestClient() {
        Map hashMap = new HashMap();
        hashMap.put("api-key", Keys.ANDROID_API_KEY);
        hashMap.put("secret-key", Keys.ANDROID_SECRET_KEY);
        addAppVersionHeaders(hashMap);
        this.appHeaders = hashMap;
    }

    private static RestClientOptions getOptions(Class<?> type) {
        RestClientOptions options = (RestClientOptions) type.getAnnotation(RestClientOptions.class);
        if (options != null) {
            return options;
        }
        throw new IllegalArgumentException("class " + type.getCanonicalName() + " doesn't have RestClientOptions annotation");
    }

    private static void fixUpErrorHandlerForException$216d1bf8(HttpResponseErrorHandler handler) {
        if (!handler.hasError) {
            handler.hasError = true;
        }
    }

    private RestTemplate createRestTemplate(ResponseErrorHandler errorHandler, int timeout) {
        RestTemplate restTemplate = new RestTemplate();
        OkHttpClientHttpRequestFactory rf = new OkHttpClientHttpRequestFactory();
        rf.setConnectTimeout(timeout);
        rf.setReadTimeout(timeout);
        restTemplate.setRequestFactory(rf);
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        List<HttpMessageConverter<?>> converters = restTemplate.getMessageConverters();
        converters.clear();
        converters.add(this.mappingJackson2HttpMessageConverter);
        converters.add(new FormHttpMessageConverter());
        if (errorHandler != null) {
            restTemplate.setErrorHandler(errorHandler);
        }
        ClientHttpRequestInterceptor interceptor = new ClientHttpRequestInterceptor() {
            public final ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
                boolean shouldSendDataMD5;
                HttpHeaders header = request.getHeaders();
                if (body.length <= 0 || !header.getContentType().isCompatibleWith(MediaType.APPLICATION_JSON)) {
                    shouldSendDataMD5 = false;
                } else {
                    shouldSendDataMD5 = true;
                }
                String path = request.getURI().getPath();
                String secretKey = (String) header.get((Object) "secret-key").get(0);
                header.remove((Object) "secret-key");
                HttpRequestEncoder.encodeHttpHeaders(header, path, request.getMethod(), shouldSendDataMD5 ? body : null, secretKey);
                return execution.execute(request, body);
            }
        };
        AnonymousClass2 anonymousClass2 = new ClientHttpRequestInterceptor() {
            public final ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
                long start = System.currentTimeMillis();
                ClientHttpResponse response = execution.execute(request, body);
                long timeTaken = System.currentTimeMillis() - start;
                Log.d(RestClient.TAG, String.format("request took %dms", new Object[]{Long.valueOf(timeTaken)}));
                return response;
            }
        };
        ArrayList<ClientHttpRequestInterceptor> interceptors = new ArrayList(3);
        interceptors.add(interceptor);
        restTemplate.setInterceptors(interceptors);
        return restTemplate;
    }

    private static HttpHeaders createHttpHeaders(Map<String, String> headers, MediaType contentType) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(contentType);
        if (headers != null) {
            for (Entry<String, String> e : headers.entrySet()) {
                httpHeaders.add((String) e.getKey(), (String) e.getValue());
            }
        }
        httpHeaders.add("Accept-Encoding", "gzip");
        return httpHeaders;
    }

    public final DeviceResponse birthGeneration2(String serialNumber, BirthDeviceGeneration2 device, HttpResponseErrorHandler errorHandler) {
        RestClientOptions options = getOptions(BirthDeviceGeneration2.class);
        Map<String, String> queryParameters = new TreeMap();
        queryParameters.put("serialNumber", serialNumber);
        return (DeviceResponse) doIt(options.path(), null, queryParameters, HttpMethod.POST, this.userHeaders, device, DeviceResponse.class, errorHandler, options.timeout());
    }

    public final Zone resetZone(Zone zone, HttpResponseErrorHandler errorHandler) {
        return (Zone) doIt("/1/zone/model/reset", null, null, HttpMethod.PUT, this.userHeaders, zone, Zone.class, errorHandler, 3000);
    }

    private static String generateUrl(String path, Map<String, String> queuryParameters) {
        Uri uri = Uri.parse(BASE_URL + path);
        if (queuryParameters != null) {
            Builder builder = uri.buildUpon();
            for (String k : queuryParameters.keySet()) {
                builder.appendQueryParameter(k, (String) queuryParameters.get(k));
            }
            uri = builder.build();
        }
        return uri.toString();
    }

    private <E extends ModelObject, R> R doIt(String path, Map<String, String> pathTokens, Map<String, String> queryParameters, HttpMethod method, Map<String, String> headers, E entity, Class<R> responseType, HttpResponseErrorHandler errorHandler, int timeout) {
        return doIt(null, null, path, (Map) pathTokens, (Map) queryParameters, method, (Map) headers, (Object) entity, (Class) responseType, errorHandler, timeout);
    }

    private <E, R> R doIt(Database database, String cacheAnchor, String path, Map<String, String> pathTokens, Map<String, String> queryParameters, HttpMethod method, Map<String, String> headers, E entity, Class<R> responseType, HttpResponseErrorHandler errorHandler, int timeout) {
        return doIt(database, null, path, pathTokens, queryParameters, method, headers, entity, responseType, errorHandler, timeout, 0);
    }

    private <E, R> R doIt(Database database, String cacheAnchor, String path, Map<String, String> pathTokens, Map<String, String> queryParameters, HttpMethod method, Map<String, String> headers, E entity, Class<R> responseType, HttpResponseErrorHandler errorHandler, int timeout, long newerThan) {
        return doIt(database, cacheAnchor, path, (Map) pathTokens, (Map) queryParameters, method, new HttpEntity(entity, createHttpHeaders(headers, MediaType.APPLICATION_JSON)), (Class) responseType, errorHandler, timeout, newerThan);
    }

    private <R> R doIt(Database database, String cacheAnchor, String path, Map<String, String> pathTokens, Map<String, String> queryParameters, HttpMethod method, HttpEntity<?> httpEntity, Class<R> responseType, HttpResponseErrorHandler errorHandler, int timeout, long newerThan) {
        if (pathTokens != null) {
            for (String k : pathTokens.keySet()) {
                String str = path;
                path = str.replace("{" + k + "}", (String) pathTokens.get(k));
            }
        }
        String url = generateUrl(path, queryParameters);
        R cachedResponse = BaseRestClient.getResponseFromCache(database, responseType, url, newerThan);
        if (cachedResponse != null) {
            return cachedResponse;
        }
        try {
            R response = createRestTemplate(errorHandler, timeout).exchange(url, method, httpEntity, responseType, new Object[0]).getBody();
            if (!(responseType.getAnnotation(TimeToLive.class) == null || database == null || errorHandler.hasError)) {
                ResponseCacheItem.put(database, cacheAnchor, url, responseType, (Serializable) response);
            }
            return response;
        } catch (Exception ex) {
            fixUpErrorHandlerForException$216d1bf8(errorHandler);
            CrashReporterUtils.silentException(ex);
            return null;
        }
    }

    public final Event[] getEvents(String deviceid, String topic, int first, int max, HttpResponseErrorHandler errorHandler) {
        Map<String, String> queryParams = new TreeMap();
        queryParams.put("topic", topic);
        queryParams.put("firstResult", Integer.toString(first));
        queryParams.put("maxResult", Integer.toString(max));
        String str = "/1/event/{id}";
        if (deviceid == null) {
            throw new IllegalArgumentException();
        }
        Map treeMap = new TreeMap();
        treeMap.put("id", deviceid);
        return (Event[]) doIt(str, treeMap, queryParams, HttpMethod.GET, this.userHeaders, null, Event[].class, errorHandler, 30000);
    }

    public final void removeCollaborator(String email, String deviceId, HttpResponseErrorHandler errorHandler) {
        Collaborator c = new Collaborator(email, deviceId);
        RestClientOptions options = getOptions(Collaborator.class);
        String url = BASE_URL + options.path();
        HttpEntity<Collaborator> requestEntity = new HttpEntity(c, createHttpHeaders(this.userHeaders, MediaType.APPLICATION_JSON));
        try {
            createRestTemplate(errorHandler, options.timeout()).exchange(url, HttpMethod.DELETE, requestEntity, String.class, new Object[0]).getBody();
        } catch (Exception ex) {
            fixUpErrorHandlerForException$216d1bf8(errorHandler);
            CrashReporterUtils.silentException(ex);
        }
    }

    private static void addAppVersionHeaders(Map<String, String> headers) {
        headers.put("mobile-version", "android " + IroApplication.VERSIONCODE);
    }

    public final String addZoneImage(String zoneId, byte[] image, HttpResponseErrorHandler errorHandler) {
        String PATH = "/1/zone/add_image/" + zoneId;
        LinkedMultiValueMap<String, Object> parts = new LinkedMultiValueMap();
        LinkedMultiValueMap<String, Object> linkedMultiValueMap = parts;
        linkedMultiValueMap.add("file", new ByteArrayResourceWithFileName(image, "image.png"));
        return (String) doIt(null, null, PATH, null, null, HttpMethod.POST, new HttpEntity(parts, createHttpHeaders(this.userHeaders, MediaType.MULTIPART_FORM_DATA)), String.class, errorHandler, 30000, 0);
    }

    public final void setUserHeaders(Map<String, String> headers) {
        addAppVersionHeaders(headers);
        this.userHeaders = headers;
    }

    public final void onLogout() {
        this.userHeaders = null;
    }

    private Map<String, String> getHeadersByOptions(RestClientOptions options) {
        return options.appHeaders() ? this.appHeaders : this.userHeaders;
    }

    public final <T> T getObject(Database database, Class<T> type, HttpResponseErrorHandler errorHandler) {
        RestClientOptions options = (RestClientOptions) type.getAnnotation(RestClientOptions.class);
        return doIt(database, null, options.path(), null, null, HttpMethod.GET, getHeadersByOptions(options), null, (Class) type, errorHandler, options.timeout());
    }

    public final <T> T getObjectById(Database database, String id, Class<T> type, HttpResponseErrorHandler errorHandler) {
        return getObjectById(database, id, type, null, errorHandler);
    }

    public final <T> T getObjectById(Database database, String id, Class<T> type, Map<String, String> queryParameters, HttpResponseErrorHandler errorHandler) {
        return getObjectById(database, id, type, queryParameters, 0, errorHandler);
    }

    public final <T> T getObjectById(Database database, String id, Class<T> type, Map<String, String> queryParameters, long newerThan, HttpResponseErrorHandler errorHandler) {
        RestClientOptions options;
        String path;
        if (type.isArray()) {
            options = (RestClientOptions) type.getComponentType().getAnnotation(RestClientOptions.class);
            path = options.arrayPath() + "/" + id;
        } else {
            options = (RestClientOptions) type.getAnnotation(RestClientOptions.class);
            path = options.path() + "/" + id;
        }
        if (options.shallow()) {
            path = path + "/shallow";
        }
        return doIt(database, id, path, null, queryParameters, HttpMethod.GET, getHeadersByOptions(options), null, type, errorHandler, options.timeout(), newerThan);
    }

    public final <T> T putObject(Class<T> responseType, Class<? extends ModelObject> type, ModelObject object, HttpResponseErrorHandler errorHandler) {
        RestClientOptions options = getOptions(type);
        return doIt(options.path(), null, null, HttpMethod.PUT, getHeadersByOptions(options), object, responseType, errorHandler, options.timeout());
    }

    public final <T> T putObject(Class<T> responseType, ModelObject object, HttpResponseErrorHandler errorHandler) {
        return putObject(responseType, object.getClass(), object, errorHandler);
    }

    public final <T> T postObject(Class<T> responseType, ModelObject object, HttpResponseErrorHandler errorHandler) {
        return postObject(responseType, object, null, errorHandler);
    }

    public final <T> T postObject(Class<T> responseType, ModelObject object, Map<String, String> queryParameters, HttpResponseErrorHandler errorHandler) {
        return postObject(responseType, object.getClass(), object, queryParameters, errorHandler);
    }

    public final <T> T postObject(Class<T> responseType, Class<? extends ModelObject> type, ModelObject object, Map<String, String> queryParameters, HttpResponseErrorHandler errorHandler) {
        RestClientOptions options = getOptions(type);
        RestClientOptionOverrides overrides = (RestClientOptionOverrides) type.getAnnotation(RestClientOptionOverrides.class);
        String path = options.path();
        if (overrides != null) {
            String override = overrides.pathForPost();
            if (!TextUtils.isEmpty(override)) {
                path = override;
            }
        }
        if (TextUtils.isEmpty(path)) {
            path = options.path();
        }
        return doIt(path, null, queryParameters, HttpMethod.POST, getHeadersByOptions(options), object, responseType, errorHandler, options.timeout());
    }

    public final BaseResponse deleteObjectById(Class<?> type, String id, HttpResponseErrorHandler errorHandler) {
        RestClientOptions options = getOptions(type);
        return (BaseResponse) doIt(options.path() + "/" + id, null, null, HttpMethod.DELETE, getHeadersByOptions(options), null, BaseResponse.class, errorHandler, options.timeout());
    }
}
