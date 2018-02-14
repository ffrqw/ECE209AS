package com.squareup.okhttp;

import com.squareup.okhttp.internal.Util;
import java.io.IOException;
import okio.BufferedSink;

public abstract class RequestBody {
    public abstract MediaType contentType();

    public abstract void writeTo(BufferedSink bufferedSink) throws IOException;

    public long contentLength() throws IOException {
        return -1;
    }

    public static RequestBody create(final MediaType contentType, final byte[] content) {
        final int length = content.length;
        if (content == null) {
            throw new NullPointerException("content == null");
        }
        Util.checkOffsetAndCount((long) content.length, 0, (long) length);
        return new RequestBody(0) {
            public final MediaType contentType() {
                return contentType;
            }

            public final long contentLength() {
                return (long) length;
            }

            public final void writeTo(BufferedSink sink) throws IOException {
                sink.write(content, 0, length);
            }
        };
    }
}
