package com.squareup.mimecraft;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

final class Utils {
    static void copyStream(InputStream in, OutputStream out, byte[] buffer) throws IOException {
        while (true) {
            int count = in.read(buffer);
            if (count != -1) {
                out.write(buffer, 0, count);
            } else {
                return;
            }
        }
    }

    static void isNotNull(Object obj, String message) {
        if (obj == null) {
            throw new IllegalStateException(message);
        }
    }

    static void isNull(Object obj, String message) {
        if (obj != null) {
            throw new IllegalStateException(message);
        }
    }

    static void isNotEmpty(String thing, String message) {
        isNotNull(thing, message);
        if ("".equals(thing.trim())) {
            throw new IllegalStateException(message);
        }
    }
}
