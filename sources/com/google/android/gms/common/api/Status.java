package com.google.android.gms.common.api;

import android.app.PendingIntent;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import com.google.android.gms.common.internal.zzbe;
import java.util.Arrays;

public final class Status extends zza implements Result, ReflectedParcelable {
    public static final Creator<Status> CREATOR = new zzf();
    public static final Status zzaBm = new Status(0);
    public static final Status zzaBn = new Status(14);
    public static final Status zzaBo = new Status(8);
    public static final Status zzaBp = new Status(15);
    public static final Status zzaBq = new Status(16);
    private static Status zzaBr = new Status(17);
    private static Status zzaBs = new Status(18);
    private final PendingIntent mPendingIntent;
    private int zzaku;
    private final int zzaxu;
    private final String zzazY;

    public Status(int i) {
        this(i, null);
    }

    Status(int i, int i2, String str, PendingIntent pendingIntent) {
        this.zzaku = i;
        this.zzaxu = i2;
        this.zzazY = str;
        this.mPendingIntent = pendingIntent;
    }

    public Status(int i, String str) {
        this(1, i, str, null);
    }

    public Status(int i, String str, PendingIntent pendingIntent) {
        this(1, i, str, pendingIntent);
    }

    public final boolean equals(Object obj) {
        if (!(obj instanceof Status)) {
            return false;
        }
        Status status = (Status) obj;
        return this.zzaku == status.zzaku && this.zzaxu == status.zzaxu && zzbe.equal(this.zzazY, status.zzazY) && zzbe.equal(this.mPendingIntent, status.mPendingIntent);
    }

    public final Status getStatus() {
        return this;
    }

    public final int getStatusCode() {
        return this.zzaxu;
    }

    public final String getStatusMessage() {
        return this.zzazY;
    }

    public final int hashCode() {
        return Arrays.hashCode(new Object[]{Integer.valueOf(this.zzaku), Integer.valueOf(this.zzaxu), this.zzazY, this.mPendingIntent});
    }

    public final boolean isSuccess() {
        return this.zzaxu <= 0;
    }

    public final String toString() {
        return zzbe.zzt(this).zzg("statusCode", this.zzazY != null ? this.zzazY : CommonStatusCodes.getStatusCodeString(this.zzaxu)).zzg("resolution", this.mPendingIntent).toString();
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zzc(parcel, 1, this.zzaxu);
        zzd.zza(parcel, 2, this.zzazY, false);
        zzd.zza(parcel, 3, this.mPendingIntent, i, false);
        zzd.zzc(parcel, 1000, this.zzaku);
        zzd.zzI(parcel, zze);
    }
}
