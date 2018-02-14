package com.google.android.gms.location;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.SystemClock;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import java.util.Arrays;

public final class LocationRequest extends zza implements ReflectedParcelable {
    public static final Creator<LocationRequest> CREATOR = new zzq();
    private int mPriority;
    private boolean zzaXd;
    private long zzbhG;
    private long zzbhX;
    private long zzbhY;
    private int zzbhZ;
    private float zzbia;
    private long zzbib;

    public LocationRequest() {
        this.mPriority = 102;
        this.zzbhX = 3600000;
        this.zzbhY = 600000;
        this.zzaXd = false;
        this.zzbhG = Long.MAX_VALUE;
        this.zzbhZ = ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        this.zzbia = 0.0f;
        this.zzbib = 0;
    }

    LocationRequest(int i, long j, long j2, boolean z, long j3, int i2, float f, long j4) {
        this.mPriority = i;
        this.zzbhX = j;
        this.zzbhY = j2;
        this.zzaXd = z;
        this.zzbhG = j3;
        this.zzbhZ = i2;
        this.zzbia = f;
        this.zzbib = j4;
    }

    private long getMaxWaitTime() {
        long j = this.zzbib;
        return j < this.zzbhX ? this.zzbhX : j;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof LocationRequest)) {
            return false;
        }
        LocationRequest locationRequest = (LocationRequest) obj;
        return this.mPriority == locationRequest.mPriority && this.zzbhX == locationRequest.zzbhX && this.zzbhY == locationRequest.zzbhY && this.zzaXd == locationRequest.zzaXd && this.zzbhG == locationRequest.zzbhG && this.zzbhZ == locationRequest.zzbhZ && this.zzbia == locationRequest.zzbia && getMaxWaitTime() == locationRequest.getMaxWaitTime();
    }

    public final int hashCode() {
        return Arrays.hashCode(new Object[]{Integer.valueOf(this.mPriority), Long.valueOf(this.zzbhX), Float.valueOf(this.zzbia), Long.valueOf(this.zzbib)});
    }

    public final String toString() {
        String str;
        StringBuilder stringBuilder = new StringBuilder();
        StringBuilder append = stringBuilder.append("Request[");
        switch (this.mPriority) {
            case 100:
                str = "PRIORITY_HIGH_ACCURACY";
                break;
            case 102:
                str = "PRIORITY_BALANCED_POWER_ACCURACY";
                break;
            case 104:
                str = "PRIORITY_LOW_POWER";
                break;
            case 105:
                str = "PRIORITY_NO_POWER";
                break;
            default:
                str = "???";
                break;
        }
        append.append(str);
        if (this.mPriority != 105) {
            stringBuilder.append(" requested=");
            stringBuilder.append(this.zzbhX).append("ms");
        }
        stringBuilder.append(" fastest=");
        stringBuilder.append(this.zzbhY).append("ms");
        if (this.zzbib > this.zzbhX) {
            stringBuilder.append(" maxWait=");
            stringBuilder.append(this.zzbib).append("ms");
        }
        if (this.zzbia > 0.0f) {
            stringBuilder.append(" smallestDisplacement=");
            stringBuilder.append(this.zzbia).append("m");
        }
        if (this.zzbhG != Long.MAX_VALUE) {
            long elapsedRealtime = this.zzbhG - SystemClock.elapsedRealtime();
            stringBuilder.append(" expireIn=");
            stringBuilder.append(elapsedRealtime).append("ms");
        }
        if (this.zzbhZ != ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED) {
            stringBuilder.append(" num=").append(this.zzbhZ);
        }
        stringBuilder.append(']');
        return stringBuilder.toString();
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zzc(parcel, 1, this.mPriority);
        zzd.zza(parcel, 2, this.zzbhX);
        zzd.zza(parcel, 3, this.zzbhY);
        zzd.zza(parcel, 4, this.zzaXd);
        zzd.zza(parcel, 5, this.zzbhG);
        zzd.zzc(parcel, 6, this.zzbhZ);
        zzd.zza(parcel, 7, this.zzbia);
        zzd.zza(parcel, 8, this.zzbib);
        zzd.zzI(parcel, zze);
    }
}
