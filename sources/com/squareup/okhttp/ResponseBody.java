package com.squareup.okhttp;

import com.squareup.okhttp.internal.Util;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import okio.BufferedSource;

public abstract class ResponseBody implements Closeable {
    public abstract long contentLength() throws IOException;

    public abstract MediaType contentType();

    public abstract BufferedSource source() throws IOException;

    public final InputStream byteStream() throws IOException {
        return source().inputStream();
    }

    private byte[] bytes() throws IOException {
        long contentLength = contentLength();
        if (contentLength > 2147483647L) {
            throw new IOException("Cannot buffer entire body for content length: " + contentLength);
        }
        Closeable source = source();
        try {
            byte[] bytes = source.readByteArray();
            if (contentLength == -1 || contentLength == ((long) bytes.length)) {
                return bytes;
            }
            throw new IOException("Content-Length and stream length disagree");
        } finally {
            Util.closeQuietly(source);
        }
    }

    public final String string() throws IOException {
        Charset charset;
        byte[] bytes = bytes();
        MediaType contentType = contentType();
        if (contentType != null) {
            charset = contentType.charset(Util.UTF_8);
        } else {
            charset = Util.UTF_8;
        }
        return new String(bytes, charset.name());
    }

    public void close() throws IOException {
        source().close();
    }
}
