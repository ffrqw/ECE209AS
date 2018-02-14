package io.fabric.sdk.android.services.network;

import java.util.Map;

public interface HttpRequestFactory {
    HttpRequest buildHttpRequest$5b7d0be6(int i, String str, Map<String, String> map);

    void setPinningInfoProvider(PinningInfoProvider pinningInfoProvider);
}
