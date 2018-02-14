package okhttp3;

import java.io.IOException;

public interface Call extends Cloneable {

    public interface Factory {
        Call newCall(Request request);
    }

    Response execute() throws IOException;
}
