package okhttp3;

import java.util.Collections;
import java.util.List;

public interface CookieJar {
    public static final CookieJar NO_COOKIES = new CookieJar() {
        public final void saveFromResponse$2fcdfa96() {
        }

        public final List<Cookie> loadForRequest$792063fe() {
            return Collections.emptyList();
        }
    };

    List<Cookie> loadForRequest$792063fe();

    void saveFromResponse$2fcdfa96();
}
