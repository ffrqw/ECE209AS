package okhttp3.internal.http;

import okhttp3.Headers;
import okhttp3.ResponseBody;
import okio.BufferedSource;

public final class RealResponseBody extends ResponseBody {
    private final Headers headers;
    private final BufferedSource source;

    public RealResponseBody(Headers headers, BufferedSource source) {
        this.headers = headers;
        this.source = source;
    }

    public final long contentLength() {
        return HttpHeaders.contentLength(this.headers);
    }

    public final BufferedSource source() {
        return this.source;
    }
}
