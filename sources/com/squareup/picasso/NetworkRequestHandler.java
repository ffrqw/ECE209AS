package com.squareup.picasso;

import android.net.NetworkInfo;
import com.squareup.picasso.Picasso.LoadedFrom;
import com.squareup.picasso.RequestHandler.Result;
import java.io.IOException;
import okhttp3.CacheControl;
import okhttp3.CacheControl.Builder;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

final class NetworkRequestHandler extends RequestHandler {
    private final Downloader downloader;
    private final Stats stats;

    static class ContentLengthException extends IOException {
        ContentLengthException(String message) {
            super(message);
        }
    }

    static final class ResponseException extends IOException {
        final int code;
        final int networkPolicy;

        ResponseException(int code, int networkPolicy) {
            super("HTTP " + code);
            this.code = code;
            this.networkPolicy = networkPolicy;
        }
    }

    public NetworkRequestHandler(Downloader downloader, Stats stats) {
        this.downloader = downloader;
        this.stats = stats;
    }

    public final boolean canHandleRequest(Request data) {
        String scheme = data.uri.getScheme();
        return "http".equals(scheme) || "https".equals(scheme);
    }

    public final Result load(Request request, int networkPolicy) throws IOException {
        CacheControl cacheControl = null;
        if (networkPolicy != 0) {
            if (NetworkPolicy.isOfflineOnly(networkPolicy)) {
                cacheControl = CacheControl.FORCE_CACHE;
            } else {
                Builder builder = new Builder();
                if (!NetworkPolicy.shouldReadFromDiskCache(networkPolicy)) {
                    builder.noCache();
                }
                if (!NetworkPolicy.shouldWriteToDiskCache(networkPolicy)) {
                    builder.noStore();
                }
                cacheControl = builder.build();
            }
        }
        Request.Builder url = new Request.Builder().url(request.uri.toString());
        if (cacheControl != null) {
            String cacheControl2 = cacheControl.toString();
            if (cacheControl2.isEmpty()) {
                url.removeHeader("Cache-Control");
            } else {
                url.header("Cache-Control", cacheControl2);
            }
        }
        Response response = this.downloader.load(url.build());
        ResponseBody body = response.body();
        if (response.isSuccessful()) {
            LoadedFrom loadedFrom = response.cacheResponse() == null ? LoadedFrom.NETWORK : LoadedFrom.DISK;
            if (loadedFrom == LoadedFrom.DISK && body.contentLength() == 0) {
                body.close();
                throw new ContentLengthException("Received response with 0 content-length header.");
            }
            if (loadedFrom == LoadedFrom.NETWORK && body.contentLength() > 0) {
                Stats stats = this.stats;
                stats.handler.sendMessage(stats.handler.obtainMessage(4, Long.valueOf(body.contentLength())));
            }
            return new Result(body.source(), loadedFrom);
        }
        body.close();
        throw new ResponseException(response.code(), request.networkPolicy);
    }

    final int getRetryCount() {
        return 2;
    }

    final boolean shouldRetry$552f0f64(NetworkInfo info) {
        return info == null || info.isConnected();
    }

    final boolean supportsReplay() {
        return true;
    }
}
