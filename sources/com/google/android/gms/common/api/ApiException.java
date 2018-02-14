package com.google.android.gms.common.api;

public final class ApiException extends Exception {
    protected final Status mStatus;

    public ApiException(Status status) {
        super(status.getStatusMessage());
        this.mStatus = status;
    }
}
