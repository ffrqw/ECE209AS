package okhttp3;

import java.util.regex.Pattern;

public final class MediaType {
    private static final Pattern PARAMETER = Pattern.compile(";\\s*(?:([a-zA-Z0-9-!#$%&'*+.^_`{|}~]+)=(?:([a-zA-Z0-9-!#$%&'*+.^_`{|}~]+)|\"([^\"]*)\"))?");
    private static final Pattern TYPE_SUBTYPE = Pattern.compile("([a-zA-Z0-9-!#$%&'*+.^_`{|}~]+)/([a-zA-Z0-9-!#$%&'*+.^_`{|}~]+)");
    private final String mediaType;

    public final String toString() {
        return this.mediaType;
    }

    public final boolean equals(Object o) {
        return (o instanceof MediaType) && ((MediaType) o).mediaType.equals(this.mediaType);
    }

    public final int hashCode() {
        return this.mediaType.hashCode();
    }
}
