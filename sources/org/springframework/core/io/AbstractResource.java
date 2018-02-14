package org.springframework.core.io;

import java.io.IOException;
import java.io.InputStream;
import org.springframework.util.Assert;

public abstract class AbstractResource implements Resource {
    public long contentLength() throws IOException {
        InputStream is = getInputStream();
        Assert.state(is != null, "resource input stream must not be null");
        long size = 0;
        try {
            byte[] buf = new byte[255];
            while (true) {
                int read = is.read(buf);
                if (read == -1) {
                    break;
                }
                size += (long) read;
            }
            return size;
        } finally {
            try {
                is.close();
            } catch (IOException e) {
            }
        }
    }

    public String getFilename() {
        return null;
    }

    public String toString() {
        return getDescription();
    }

    public boolean equals(Object obj) {
        return obj == this || ((obj instanceof Resource) && ((Resource) obj).getDescription().equals(getDescription()));
    }

    public int hashCode() {
        return getDescription().hashCode();
    }
}
