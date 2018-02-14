package com.squareup.picasso;

import android.content.Context;
import java.io.File;
import java.io.IOException;
import okhttp3.Cache;
import okhttp3.Call.Factory;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.Response;

public final class OkHttp3Downloader implements Downloader {
    private final Cache cache;
    final Factory client;
    private boolean sharedClient;

    public OkHttp3Downloader(Context context) {
        this(Utils.createDefaultCacheDir(context));
    }

    private OkHttp3Downloader(File cacheDir) {
        this(cacheDir, Utils.calculateDiskCacheSize(cacheDir));
    }

    private OkHttp3Downloader(File cacheDir, long maxSize) {
        this(new Builder().cache(new Cache(cacheDir, maxSize)).build());
        this.sharedClient = false;
    }

    private OkHttp3Downloader(OkHttpClient client) {
        this.sharedClient = true;
        this.client = client;
        this.cache = client.cache();
    }

    public final Response load(Request request) throws IOException {
        return this.client.newCall(request).execute();
    }
}
