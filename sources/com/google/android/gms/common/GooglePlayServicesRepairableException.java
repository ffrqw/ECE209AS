package com.google.android.gms.common;

import android.content.Intent;

public final class GooglePlayServicesRepairableException extends UserRecoverableException {
    private final int zzakt;

    public GooglePlayServicesRepairableException(int i, String str, Intent intent) {
        super(str, intent);
        this.zzakt = i;
    }
}
