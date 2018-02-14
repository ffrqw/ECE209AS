package com.rachio.iro.cloud;

import android.support.v7.widget.RecyclerView.ItemAnimator;
import android.util.Log;
import com.rachio.iro.model.ResponseCacheItem;
import com.rachio.iro.model.annotation.TimeToLive;
import com.rachio.iro.model.db.Database;
import java.io.IOException;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

public class BaseRestClient {
    private static final String TAG = BaseRestClient.class.getName();

    public static class DebuggingInterceptor implements ClientHttpRequestInterceptor {
        public final ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
            boolean print;
            Log.d(BaseRestClient.TAG, request.getMethod().toString() + " to " + request.getURI().toString());
            MediaType mediaType = request.getHeaders().getContentType();
            if (mediaType != null) {
                print = mediaType.equals(MediaType.APPLICATION_JSON);
            } else {
                print = false;
            }
            if (print) {
                Log.d(BaseRestClient.TAG, "request: " + new String(body, "UTF-8"));
            }
            ClientHttpResponse response = execution.execute(request, body);
            MultiUseResponse copy = new MultiUseResponse(response);
            MediaType responseContentType = response.getHeaders().getContentType();
            if (responseContentType == null || !(responseContentType.isCompatibleWith(MediaType.APPLICATION_JSON) || responseContentType.isCompatibleWith(MediaType.TEXT_PLAIN) || responseContentType.isCompatibleWith(MediaType.TEXT_HTML))) {
                print = false;
            } else {
                print = true;
            }
            if (print) {
                byte[] bodyContent = copy.getBodyArray();
                if (bodyContent != null) {
                    String json = new String(bodyContent, "UTF-8");
                    if (json.length() > ItemAnimator.FLAG_APPEARED_IN_PRE_LAYOUT) {
                        json = json.substring(0, ItemAnimator.FLAG_APPEARED_IN_PRE_LAYOUT);
                    }
                    Log.d(BaseRestClient.TAG, "response(" + response.getStatusText() + "): " + json);
                }
            } else {
                Log.d(BaseRestClient.TAG, "response(" + response.getStatusText() + ")");
            }
            return copy;
        }
    }

    public static <T> T getResponseFromCache(Database database, Class<T> responseType, String url, long newerThan) {
        if (database != null) {
            TimeToLive ttl;
            if (responseType.isArray()) {
                ttl = (TimeToLive) responseType.getComponentType().getAnnotation(TimeToLive.class);
            } else {
                ttl = (TimeToLive) responseType.getAnnotation(TimeToLive.class);
            }
            if (ttl != null) {
                ResponseCacheItem cacheItem = ResponseCacheItem.get(database, url, ttl.timeToLive(), ttl.timeToEviction());
                if (cacheItem != null && (newerThan <= 0 || cacheItem.timestamp >= newerThan)) {
                    return cacheItem.data;
                }
            }
        }
        return null;
    }
}
