package com.squareup.okhttp.internal.framed;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import okio.BufferedSource;
import okio.ByteString;

public interface FrameReader extends Closeable {

    public interface Handler {
        void data(boolean z, int i, BufferedSource bufferedSource, int i2) throws IOException;

        void goAway$4b4c5c6b(int i, ByteString byteString);

        void headers$37c2d766(boolean z, boolean z2, int i, List<Header> list, HeadersMode headersMode);

        void ping(boolean z, int i, int i2);

        void pushPromise$16014a7a(int i, List<Header> list) throws IOException;

        void rstStream(int i, ErrorCode errorCode);

        void settings(boolean z, Settings settings);

        void windowUpdate(int i, long j);
    }

    boolean nextFrame(Handler handler) throws IOException;

    void readConnectionPreface() throws IOException;
}
