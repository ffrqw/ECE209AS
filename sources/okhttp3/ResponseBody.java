package okhttp3;

import java.io.Closeable;
import okhttp3.internal.Util;
import okio.Buffer;
import okio.BufferedSource;

public abstract class ResponseBody implements Closeable {
    public abstract long contentLength();

    public abstract BufferedSource source();

    public void close() {
        Util.closeQuietly(source());
    }

    public static ResponseBody create(MediaType contentType, byte[] content) {
        final Buffer buffer = new Buffer().write(content);
        final long length = (long) content.length;
        if (buffer != null) {
            return new ResponseBody(null) {
                public final long contentLength() {
                    return length;
                }

                public final BufferedSource source() {
                    return buffer;
                }
            };
        }
        throw new NullPointerException("source == null");
    }
}
