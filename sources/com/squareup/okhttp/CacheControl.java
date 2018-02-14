package com.squareup.okhttp;

import com.squareup.okhttp.internal.http.HeaderParser;
import java.util.concurrent.TimeUnit;

public final class CacheControl {
    public static final CacheControl FORCE_CACHE;
    public static final CacheControl FORCE_NETWORK;
    String headerValue;
    private final boolean isPrivate;
    private final boolean isPublic;
    private final int maxAgeSeconds;
    private final int maxStaleSeconds;
    private final int minFreshSeconds;
    private final boolean mustRevalidate;
    private final boolean noCache;
    private final boolean noStore;
    private final boolean noTransform;
    private final boolean onlyIfCached;
    private final int sMaxAgeSeconds;

    public static final class Builder {
        int maxAgeSeconds = -1;
        int maxStaleSeconds = -1;
        int minFreshSeconds = -1;
        boolean noCache;
        boolean onlyIfCached;

        public final CacheControl build() {
            return new CacheControl();
        }
    }

    static {
        int i;
        Builder builder = new Builder();
        builder.noCache = true;
        FORCE_NETWORK = builder.build();
        Builder builder2 = new Builder();
        builder2.onlyIfCached = true;
        long toSeconds = TimeUnit.SECONDS.toSeconds(2147483647L);
        if (toSeconds > 2147483647L) {
            i = ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        } else {
            i = (int) toSeconds;
        }
        builder2.maxStaleSeconds = i;
        FORCE_CACHE = builder2.build();
    }

    private CacheControl(boolean noCache, boolean noStore, int maxAgeSeconds, int sMaxAgeSeconds, boolean isPrivate, boolean isPublic, boolean mustRevalidate, int maxStaleSeconds, int minFreshSeconds, boolean onlyIfCached, boolean noTransform, String headerValue) {
        this.noCache = noCache;
        this.noStore = noStore;
        this.maxAgeSeconds = maxAgeSeconds;
        this.sMaxAgeSeconds = sMaxAgeSeconds;
        this.isPrivate = isPrivate;
        this.isPublic = isPublic;
        this.mustRevalidate = mustRevalidate;
        this.maxStaleSeconds = maxStaleSeconds;
        this.minFreshSeconds = minFreshSeconds;
        this.onlyIfCached = onlyIfCached;
        this.noTransform = noTransform;
        this.headerValue = headerValue;
    }

    private CacheControl(Builder builder) {
        this.noCache = builder.noCache;
        this.noStore = false;
        this.maxAgeSeconds = builder.maxAgeSeconds;
        this.sMaxAgeSeconds = -1;
        this.isPrivate = false;
        this.isPublic = false;
        this.mustRevalidate = false;
        this.maxStaleSeconds = builder.maxStaleSeconds;
        this.minFreshSeconds = builder.minFreshSeconds;
        this.onlyIfCached = builder.onlyIfCached;
        this.noTransform = false;
    }

    public final boolean noCache() {
        return this.noCache;
    }

    public final boolean noStore() {
        return this.noStore;
    }

    public final int maxAgeSeconds() {
        return this.maxAgeSeconds;
    }

    public final boolean isPrivate() {
        return this.isPrivate;
    }

    public final boolean isPublic() {
        return this.isPublic;
    }

    public final boolean mustRevalidate() {
        return this.mustRevalidate;
    }

    public final int maxStaleSeconds() {
        return this.maxStaleSeconds;
    }

    public final int minFreshSeconds() {
        return this.minFreshSeconds;
    }

    public final boolean onlyIfCached() {
        return this.onlyIfCached;
    }

    public static CacheControl parse(Headers headers) {
        boolean noCache = false;
        boolean noStore = false;
        int maxAgeSeconds = -1;
        int sMaxAgeSeconds = -1;
        boolean isPrivate = false;
        boolean isPublic = false;
        boolean mustRevalidate = false;
        int maxStaleSeconds = -1;
        int minFreshSeconds = -1;
        boolean onlyIfCached = false;
        boolean noTransform = false;
        boolean canUseHeaderValue = true;
        String headerValue = null;
        int size = headers.size();
        for (int i = 0; i < size; i++) {
            String name = headers.name(i);
            String value = headers.value(i);
            if (!name.equalsIgnoreCase("Cache-Control")) {
                if (name.equalsIgnoreCase("Pragma")) {
                    canUseHeaderValue = false;
                } else {
                }
            } else if (headerValue != null) {
                canUseHeaderValue = false;
            } else {
                headerValue = value;
            }
            int pos = 0;
            while (pos < value.length()) {
                String parameter;
                int tokenStart = pos;
                pos = HeaderParser.skipUntil(value, pos, "=,;");
                String directive = value.substring(tokenStart, pos).trim();
                if (pos == value.length() || value.charAt(pos) == ',' || value.charAt(pos) == ';') {
                    pos++;
                    parameter = null;
                } else {
                    pos = HeaderParser.skipWhitespace(value, pos + 1);
                    int parameterStart;
                    if (pos >= value.length() || value.charAt(pos) != '\"') {
                        parameterStart = pos;
                        pos = HeaderParser.skipUntil(value, pos, ",;");
                        parameter = value.substring(parameterStart, pos).trim();
                    } else {
                        pos++;
                        parameterStart = pos;
                        pos = HeaderParser.skipUntil(value, pos, "\"");
                        parameter = value.substring(parameterStart, pos);
                        pos++;
                    }
                }
                if ("no-cache".equalsIgnoreCase(directive)) {
                    noCache = true;
                } else if ("no-store".equalsIgnoreCase(directive)) {
                    noStore = true;
                } else if ("max-age".equalsIgnoreCase(directive)) {
                    maxAgeSeconds = HeaderParser.parseSeconds(parameter, -1);
                } else if ("s-maxage".equalsIgnoreCase(directive)) {
                    sMaxAgeSeconds = HeaderParser.parseSeconds(parameter, -1);
                } else if ("private".equalsIgnoreCase(directive)) {
                    isPrivate = true;
                } else if ("public".equalsIgnoreCase(directive)) {
                    isPublic = true;
                } else if ("must-revalidate".equalsIgnoreCase(directive)) {
                    mustRevalidate = true;
                } else if ("max-stale".equalsIgnoreCase(directive)) {
                    maxStaleSeconds = HeaderParser.parseSeconds(parameter, ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED);
                } else if ("min-fresh".equalsIgnoreCase(directive)) {
                    minFreshSeconds = HeaderParser.parseSeconds(parameter, -1);
                } else if ("only-if-cached".equalsIgnoreCase(directive)) {
                    onlyIfCached = true;
                } else if ("no-transform".equalsIgnoreCase(directive)) {
                    noTransform = true;
                }
            }
        }
        if (!canUseHeaderValue) {
            headerValue = null;
        }
        return new CacheControl(noCache, noStore, maxAgeSeconds, sMaxAgeSeconds, isPrivate, isPublic, mustRevalidate, maxStaleSeconds, minFreshSeconds, onlyIfCached, noTransform, headerValue);
    }

    public final String toString() {
        String result = this.headerValue;
        if (result != null) {
            return result;
        }
        String str;
        StringBuilder stringBuilder = new StringBuilder();
        if (this.noCache) {
            stringBuilder.append("no-cache, ");
        }
        if (this.noStore) {
            stringBuilder.append("no-store, ");
        }
        if (this.maxAgeSeconds != -1) {
            stringBuilder.append("max-age=").append(this.maxAgeSeconds).append(", ");
        }
        if (this.sMaxAgeSeconds != -1) {
            stringBuilder.append("s-maxage=").append(this.sMaxAgeSeconds).append(", ");
        }
        if (this.isPrivate) {
            stringBuilder.append("private, ");
        }
        if (this.isPublic) {
            stringBuilder.append("public, ");
        }
        if (this.mustRevalidate) {
            stringBuilder.append("must-revalidate, ");
        }
        if (this.maxStaleSeconds != -1) {
            stringBuilder.append("max-stale=").append(this.maxStaleSeconds).append(", ");
        }
        if (this.minFreshSeconds != -1) {
            stringBuilder.append("min-fresh=").append(this.minFreshSeconds).append(", ");
        }
        if (this.onlyIfCached) {
            stringBuilder.append("only-if-cached, ");
        }
        if (this.noTransform) {
            stringBuilder.append("no-transform, ");
        }
        if (stringBuilder.length() == 0) {
            str = "";
        } else {
            stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());
            str = stringBuilder.toString();
        }
        this.headerValue = str;
        return str;
    }
}
