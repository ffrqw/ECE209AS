package com.crashlytics.android.core;

import io.fabric.sdk.android.services.network.PinningInfoProvider;
import java.io.InputStream;

final class CrashlyticsPinningInfoProvider implements PinningInfoProvider {
    private final PinningInfoProvider pinningInfo;

    public CrashlyticsPinningInfoProvider(PinningInfoProvider pinningInfo) {
        this.pinningInfo = pinningInfo;
    }

    public final InputStream getKeyStoreStream() {
        return this.pinningInfo.getKeyStoreStream();
    }

    public final String getKeyStorePassword() {
        return this.pinningInfo.getKeyStorePassword();
    }

    public final String[] getPins() {
        return this.pinningInfo.getPins();
    }
}
