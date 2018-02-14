package org.springframework.util;

import android.support.v7.widget.RecyclerView.ItemAnimator;
import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;

public abstract class StreamUtils {

    private static class NonClosingOutputStream extends FilterOutputStream {
        public NonClosingOutputStream(OutputStream out) {
            super(out);
        }

        public final void write(byte[] b, int off, int let) throws IOException {
            this.out.write(b, off, let);
        }

        public final void close() throws IOException {
        }
    }

    public static byte[] copyToByteArray(InputStream in) throws IOException {
        OutputStream out = new ByteArrayOutputStream(ItemAnimator.FLAG_APPEARED_IN_PRE_LAYOUT);
        copy(in, out);
        return out.toByteArray();
    }

    public static String copyToString(InputStream in, Charset charset) throws IOException {
        Assert.notNull(in, "No InputStream specified");
        StringBuilder out = new StringBuilder();
        InputStreamReader reader = new InputStreamReader(in, charset);
        char[] buffer = new char[ItemAnimator.FLAG_APPEARED_IN_PRE_LAYOUT];
        while (true) {
            int bytesRead = reader.read(buffer);
            if (bytesRead == -1) {
                return out.toString();
            }
            out.append(buffer, 0, bytesRead);
        }
    }

    public static void copy(byte[] in, OutputStream out) throws IOException {
        Assert.notNull(in, "No input byte array specified");
        Assert.notNull(out, "No OutputStream specified");
        out.write(in);
    }

    public static int copy(InputStream in, OutputStream out) throws IOException {
        Assert.notNull(in, "No InputStream specified");
        Assert.notNull(out, "No OutputStream specified");
        int byteCount = 0;
        byte[] buffer = new byte[ItemAnimator.FLAG_APPEARED_IN_PRE_LAYOUT];
        while (true) {
            int bytesRead = in.read(buffer);
            if (bytesRead != -1) {
                out.write(buffer, 0, bytesRead);
                byteCount += bytesRead;
            } else {
                out.flush();
                return byteCount;
            }
        }
    }

    public static OutputStream nonClosing(OutputStream out) {
        Assert.notNull(out, "No OutputStream specified");
        return new NonClosingOutputStream(out);
    }
}
