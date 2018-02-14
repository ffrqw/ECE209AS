package okhttp3;

import java.io.IOException;

public interface Authenticator {
    public static final Authenticator NONE = new Authenticator() {
        public final Request authenticate$31deecb3() {
            return null;
        }
    };

    Request authenticate$31deecb3() throws IOException;
}
