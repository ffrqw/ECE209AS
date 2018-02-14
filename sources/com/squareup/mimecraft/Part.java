package com.squareup.mimecraft;

import android.support.v7.widget.RecyclerView.ItemAnimator;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.Map;

public interface Part {

    public static class Builder {
        private byte[] bodyBytes;
        private File bodyFile;
        private boolean hasBody = false;
        private String headerDisposition;
        int headerLength;
        private String headerType;

        private static abstract class PartImpl implements Part {
            private final Map<String, String> headers;

            protected PartImpl(Map<String, String> headers) {
                this.headers = headers;
            }

            public final Map<String, String> getHeaders() {
                return this.headers;
            }
        }

        static final class BytesPart extends PartImpl {
            private final byte[] contents;

            BytesPart(Map<String, String> headers, byte[] contents) {
                super(headers);
                this.contents = contents;
            }

            public final void writeBodyTo(OutputStream out) throws IOException {
                out.write(this.contents);
            }
        }

        private static final class FilePart extends PartImpl {
            private final byte[] buffer;
            private final File file;

            private FilePart(Map<String, String> headers, File file) {
                super(headers);
                this.buffer = new byte[ItemAnimator.FLAG_APPEARED_IN_PRE_LAYOUT];
                this.file = file;
            }

            public final void writeBodyTo(OutputStream out) throws IOException {
                Throwable th;
                InputStream in = null;
                try {
                    InputStream in2 = new FileInputStream(this.file);
                    try {
                        Utils.copyStream(in2, out, this.buffer);
                        try {
                            in2.close();
                        } catch (IOException e) {
                        }
                    } catch (Throwable th2) {
                        th = th2;
                        in = in2;
                        if (in != null) {
                            try {
                                in.close();
                            } catch (IOException e2) {
                            }
                        }
                        throw th;
                    }
                } catch (Throwable th3) {
                    th = th3;
                    if (in != null) {
                        in.close();
                    }
                    throw th;
                }
            }
        }

        private void checkSetBody() {
            if (this.hasBody) {
                throw new IllegalStateException("Only one body per part.");
            }
            this.hasBody = true;
        }

        public final Builder contentType(String type) {
            Utils.isNotEmpty(type, "Type must not be empty.");
            Utils.isNull(this.headerType, "Type header already set.");
            Utils.isNull(null, "Type cannot be set with multipart body.");
            this.headerType = type;
            return this;
        }

        public final Builder contentDisposition(String disposition) {
            Utils.isNotEmpty(disposition, "Disposition must not be empty.");
            Utils.isNull(this.headerDisposition, "Disposition header already set.");
            this.headerDisposition = disposition;
            return this;
        }

        public final Builder body(File body) {
            Utils.isNotNull(body, "File body must not be null.");
            checkSetBody();
            this.bodyFile = body;
            return this;
        }

        public final Builder body(String body) {
            Utils.isNotNull(body, "String body must not be null.");
            checkSetBody();
            try {
                byte[] bytes = body.getBytes("UTF-8");
                this.bodyBytes = bytes;
                this.headerLength = bytes.length;
                return this;
            } catch (UnsupportedEncodingException e) {
                throw new IllegalArgumentException("Unable to convert input to UTF-8: " + body, e);
            }
        }

        public final Part build() {
            Map<String, String> headers = new LinkedHashMap();
            if (this.headerDisposition != null) {
                headers.put("Content-Disposition", this.headerDisposition);
            }
            if (this.headerType != null) {
                headers.put("Content-Type", this.headerType);
            }
            if (this.headerLength != 0) {
                headers.put("Content-Length", Integer.toString(this.headerLength));
            }
            if (this.bodyBytes != null) {
                return new BytesPart(headers, this.bodyBytes);
            }
            if (this.bodyFile != null) {
                return new FilePart(headers, this.bodyFile);
            }
            throw new IllegalStateException("Part required body to be set.");
        }
    }

    Map<String, String> getHeaders();

    void writeBodyTo(OutputStream outputStream) throws IOException;
}
