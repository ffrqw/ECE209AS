package com.squareup.mimecraft;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

public final class Multipart implements Part {
    private final String boundary;
    private final Map<String, String> headers;
    private final List<Part> parts;

    public static class Builder {
        private final String boundary;
        private final List<Part> parts;
        private Type type;

        public Builder() {
            this(UUID.randomUUID().toString());
        }

        private Builder(String boundary) {
            this.parts = new ArrayList();
            this.type = Type.MIXED;
            this.boundary = boundary;
        }

        public final Builder type(Type type) {
            Utils.isNotNull(type, "Type must not be null.");
            this.type = type;
            return this;
        }

        public final Builder addPart(Part part) {
            Utils.isNotNull(part, "Part must not be null.");
            this.parts.add(part);
            return this;
        }

        public final Multipart build() {
            if (!this.parts.isEmpty()) {
                return new Multipart(this.type, this.parts, this.boundary);
            }
            throw new IllegalStateException("Multipart body must have at least one part.");
        }
    }

    public enum Type {
        MIXED("mixed"),
        ALTERNATIVE("alternative"),
        DIGEST("digest"),
        PARALLEL("parallel"),
        FORM("form-data");
        
        final String contentType;

        private Type(String contentType) {
            this.contentType = contentType;
        }
    }

    private Multipart(Type type, List<Part> parts, String boundary) {
        Utils.isNotNull(type, "Multipart type must not be null.");
        this.parts = parts;
        this.headers = Collections.singletonMap("Content-Type", "multipart/" + type.contentType + "; boundary=" + boundary);
        this.boundary = boundary;
    }

    public final Map<String, String> getHeaders() {
        return this.headers;
    }

    public final void writeBodyTo(OutputStream stream) throws IOException {
        byte[] boundary = this.boundary.getBytes("UTF-8");
        boolean first = true;
        for (Part part : this.parts) {
            writeBoundary(stream, boundary, first, false);
            Map headers = part.getHeaders();
            if (headers != null) {
                for (Entry entry : headers.entrySet()) {
                    stream.write(((String) entry.getKey()).getBytes("UTF-8"));
                    stream.write(58);
                    stream.write(32);
                    stream.write(((String) entry.getValue()).getBytes("UTF-8"));
                    stream.write(13);
                    stream.write(10);
                }
            }
            stream.write(13);
            stream.write(10);
            part.writeBodyTo(stream);
            first = false;
        }
        writeBoundary(stream, boundary, false, true);
    }

    private static void writeBoundary(OutputStream out, byte[] boundary, boolean first, boolean last) throws IOException {
        if (!first) {
            out.write(13);
            out.write(10);
        }
        out.write(45);
        out.write(45);
        out.write(boundary);
        if (last) {
            out.write(45);
            out.write(45);
            return;
        }
        out.write(13);
        out.write(10);
    }
}
