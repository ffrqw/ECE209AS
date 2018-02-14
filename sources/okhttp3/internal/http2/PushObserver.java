package okhttp3.internal.http2;

import java.io.IOException;
import okio.BufferedSource;

public interface PushObserver {
    public static final PushObserver CANCEL = new PushObserver() {
        public final boolean onRequest$163bb723() {
            return true;
        }

        public final boolean onHeaders$4ec42067() {
            return true;
        }

        public final boolean onData$749b27ff(BufferedSource source, int byteCount) throws IOException {
            source.skip((long) byteCount);
            return true;
        }

        public final void onReset$613c779f() {
        }
    };

    boolean onData$749b27ff(BufferedSource bufferedSource, int i) throws IOException;

    boolean onHeaders$4ec42067();

    boolean onRequest$163bb723();

    void onReset$613c779f();
}
