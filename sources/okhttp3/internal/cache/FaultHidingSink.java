package okhttp3.internal.cache;

import java.io.IOException;
import okio.Buffer;
import okio.ForwardingSink;
import okio.Sink;

class FaultHidingSink extends ForwardingSink {
    private boolean hasErrors;

    public FaultHidingSink(Sink delegate) {
        super(delegate);
    }

    public final void write(Buffer source, long byteCount) throws IOException {
        if (this.hasErrors) {
            source.skip(byteCount);
            return;
        }
        try {
            super.write(source, byteCount);
        } catch (IOException e) {
            this.hasErrors = true;
            onException$8ec0fdf();
        }
    }

    public void flush() throws IOException {
        if (!this.hasErrors) {
            try {
                super.flush();
            } catch (IOException e) {
                this.hasErrors = true;
                onException$8ec0fdf();
            }
        }
    }

    public void close() throws IOException {
        if (!this.hasErrors) {
            try {
                super.close();
            } catch (IOException e) {
                this.hasErrors = true;
                onException$8ec0fdf();
            }
        }
    }

    protected void onException$8ec0fdf() {
    }
}
