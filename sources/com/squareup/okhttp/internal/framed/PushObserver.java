package com.squareup.okhttp.internal.framed;

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

        public final void onReset$6b03c5a6() {
        }
    };

    boolean onData$749b27ff(BufferedSource bufferedSource, int i) throws IOException;

    boolean onHeaders$4ec42067();

    boolean onRequest$163bb723();

    void onReset$6b03c5a6();
}
