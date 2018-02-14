package okio;

import java.io.IOException;

public abstract class ForwardingSource implements Source {
    private final Source delegate;

    public ForwardingSource(Source delegate) {
        if (delegate == null) {
            throw new IllegalArgumentException("delegate == null");
        }
        this.delegate = delegate;
    }

    public long read(Buffer sink, long byteCount) throws IOException {
        return this.delegate.read(sink, byteCount);
    }

    public final Timeout timeout() {
        return this.delegate.timeout();
    }

    public void close() throws IOException {
        this.delegate.close();
    }

    public String toString() {
        return getClass().getSimpleName() + "(" + this.delegate.toString() + ")";
    }
}
