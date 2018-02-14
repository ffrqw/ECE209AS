package com.google.android.gms.location;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import java.util.Arrays;

public final class LocationAvailability extends zza implements ReflectedParcelable {
    public static final Creator<LocationAvailability> CREATOR = new zzp();
    @Deprecated
    private int zzbhS;
    @Deprecated
    private int zzbhT;
    private long zzbhU;
    private int zzbhV;
    private zzy[] zzbhW;

    LocationAvailability(int i, int i2, int i3, long j, zzy[] zzyArr) {
        this.zzbhV = i;
        this.zzbhS = i2;
        this.zzbhT = i3;
        this.zzbhU = j;
        this.zzbhW = zzyArr;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        LocationAvailability locationAvailability = (LocationAvailability) obj;
        return this.zzbhS == locationAvailability.zzbhS && this.zzbhT == locationAvailability.zzbhT && this.zzbhU == locationAvailability.zzbhU && this.zzbhV == locationAvailability.zzbhV && Arrays.equals(this.zzbhW, locationAvailability.zzbhW);
    }

    public final int hashCode() {
        return Arrays.hashCode(new Object[]{Integer.valueOf(this.zzbhV), Integer.valueOf(this.zzbhS), Integer.valueOf(this.zzbhT), Long.valueOf(this.zzbhU), this.zzbhW});
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zzc(parcel, 1, this.zzbhS);
        zzd.zzc(parcel, 2, this.zzbhT);
        zzd.zza(parcel, 3, this.zzbhU);
        zzd.zzc(parcel, 4, this.zzbhV);
        zzd.zza$2d7953c6(parcel, 5, this.zzbhW, i);
        zzd.zzI(parcel, zze);
    }

    public final String toString() {
        return "LocationAvailability[isLocationAvailable: " + (this.zzbhV < 1000) + "]";
    }
}
