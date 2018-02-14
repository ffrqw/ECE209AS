package com.google.android.gms.location;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;

public final class zzp implements Creator<LocationAvailability> {
    public final /* synthetic */ Object createFromParcel(Parcel parcel) {
        int i = 1;
        int zzd = zzb.zzd(parcel);
        int i2 = 1000;
        long j = 0;
        zzy[] zzyArr = null;
        int i3 = 1;
        while (parcel.dataPosition() < zzd) {
            int readInt = parcel.readInt();
            switch (65535 & readInt) {
                case 1:
                    i3 = zzb.zzg(parcel, readInt);
                    break;
                case 2:
                    i = zzb.zzg(parcel, readInt);
                    break;
                case 3:
                    j = zzb.zzi(parcel, readInt);
                    break;
                case 4:
                    i2 = zzb.zzg(parcel, readInt);
                    break;
                case 5:
                    zzyArr = (zzy[]) zzb.zzb(parcel, readInt, zzy.CREATOR);
                    break;
                default:
                    zzb.zzb(parcel, readInt);
                    break;
            }
        }
        zzb.zzF(parcel, zzd);
        return new LocationAvailability(i2, i3, i, j, zzyArr);
    }

    public final /* synthetic */ Object[] newArray(int i) {
        return new LocationAvailability[i];
    }
}
