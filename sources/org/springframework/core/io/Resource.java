package org.springframework.core.io;

import java.io.IOException;

public interface Resource extends InputStreamSource {
    long contentLength() throws IOException;

    String getDescription();

    String getFilename();
}
