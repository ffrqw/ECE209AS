package com.google.android.gms.location;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public final class LocationResult extends zza implements ReflectedParcelable {
    public static final Creator<LocationResult> CREATOR = new zzr();
    static final List<Location> zzbic = Collections.emptyList();
    private final List<Location> zzbid;

    LocationResult(List<Location> list) {
        this.zzbid = list;
    }

    public final boolean equals(Object obj) {
        if (!(obj instanceof LocationResult)) {
            return false;
        }
        LocationResult locationResult = (LocationResult) obj;
        if (locationResult.zzbid.size() != this.zzbid.size()) {
            return false;
        }
        Iterator it = this.zzbid.iterator();
        for (Location time : locationResult.zzbid) {
            if (((Location) it.next()).getTime() != time.getTime()) {
                return false;
            }
        }
        return true;
    }

    public final int hashCode() {
        int i = 17;
        for (Location time : this.zzbid) {
            long time2 = time.getTime();
            i = ((int) (time2 ^ (time2 >>> 32))) + (i * 31);
        }
        return i;
    }

    public final String toString() {
        String valueOf = String.valueOf(this.zzbid);
        return new StringBuilder(String.valueOf(valueOf).length() + 27).append("LocationResult[locations: ").append(valueOf).append("]").toString();
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zzc(parcel, 1, this.zzbid, false);
        zzd.zzI(parcel, zze);
    }
}
