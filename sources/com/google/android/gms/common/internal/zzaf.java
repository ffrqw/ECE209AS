package com.google.android.gms.common.internal;

import android.content.ComponentName;
import android.content.Intent;
import java.util.Arrays;

public final class zzaf {
    private final String zzaHN;
    private final ComponentName zzaHO = null;
    private final String zzaeX;

    public zzaf(String str, String str2) {
        this.zzaeX = zzbo.zzcF(str);
        this.zzaHN = zzbo.zzcF(str2);
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof zzaf)) {
            return false;
        }
        zzaf zzaf = (zzaf) obj;
        return zzbe.equal(this.zzaeX, zzaf.zzaeX) && zzbe.equal(this.zzaHN, zzaf.zzaHN) && zzbe.equal(null, null);
    }

    public final String getPackage() {
        return this.zzaHN;
    }

    public final int hashCode() {
        return Arrays.hashCode(new Object[]{this.zzaeX, this.zzaHN, null});
    }

    public final String toString() {
        if (this.zzaeX != null) {
            return this.zzaeX;
        }
        ComponentName componentName = null;
        return componentName.flattenToString();
    }

    public final Intent zzrB() {
        return this.zzaeX != null ? new Intent(this.zzaeX).setPackage(this.zzaHN) : new Intent().setComponent(null);
    }
}
