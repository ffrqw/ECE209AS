package org.springframework.core.io;

import java.io.IOException;
import java.io.InputStream;

public class InputStreamResource extends AbstractResource {
    private final String description;
    private final InputStream inputStream;
    private boolean read;

    public final InputStream getInputStream() throws IOException, IllegalStateException {
        if (this.read) {
            throw new IllegalStateException("InputStream has already been read - do not use InputStreamResource if a stream needs to be read multiple times");
        }
        this.read = true;
        return this.inputStream;
    }

    public final String getDescription() {
        return this.description;
    }

    public boolean equals(Object obj) {
        return obj == this || ((obj instanceof InputStreamResource) && ((InputStreamResource) obj).inputStream.equals(this.inputStream));
    }

    public int hashCode() {
        return this.inputStream.hashCode();
    }
}
