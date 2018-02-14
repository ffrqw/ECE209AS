package com.crashlytics.android.core.internal.models;

public final class SignalData {
    public final String code;
    public final long faultAddress = 0;
    public final String name;

    public SignalData(String name, String code, long faultAddress) {
        this.name = name;
        this.code = code;
    }
}
