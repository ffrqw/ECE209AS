package android.support.v4.util;

import android.util.Log;
import java.io.Writer;

public final class LogWriter extends Writer {
    private StringBuilder mBuilder = new StringBuilder(128);
    private final String mTag;

    public LogWriter(String tag) {
        this.mTag = tag;
    }

    public final void close() {
        flushBuilder();
    }

    public final void flush() {
        flushBuilder();
    }

    public final void write(char[] buf, int offset, int count) {
        for (int i = 0; i < count; i++) {
            char c = buf[offset + i];
            if (c == '\n') {
                flushBuilder();
            } else {
                this.mBuilder.append(c);
            }
        }
    }

    private void flushBuilder() {
        if (this.mBuilder.length() > 0) {
            Log.d(this.mTag, this.mBuilder.toString());
            this.mBuilder.delete(0, this.mBuilder.length());
        }
    }
}
