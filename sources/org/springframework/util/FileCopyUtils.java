package org.springframework.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class FileCopyUtils {
    public static int copy(InputStream in, OutputStream out) throws IOException {
        Assert.notNull(in, "No InputStream specified");
        Assert.notNull(out, "No OutputStream specified");
        try {
            int copy = StreamUtils.copy(in, out);
            try {
                out.close();
            } catch (IOException e) {
            }
            return copy;
        } finally {
            try {
                in.close();
            } catch (IOException e2) {
            }
            try {
                out.close();
            } catch (IOException e3) {
            }
        }
    }
}
