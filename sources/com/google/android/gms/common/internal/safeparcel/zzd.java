package com.google.android.gms.common.internal.safeparcel;

import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.List;

public final class zzd {
    private static int zzG(Parcel parcel, int i) {
        parcel.writeInt(-65536 | i);
        parcel.writeInt(0);
        return parcel.dataPosition();
    }

    private static void zzH(Parcel parcel, int i) {
        int dataPosition = parcel.dataPosition();
        int i2 = dataPosition - i;
        parcel.setDataPosition(i - 4);
        parcel.writeInt(i2);
        parcel.setDataPosition(dataPosition);
    }

    public static void zzI(Parcel parcel, int i) {
        zzH(parcel, i);
    }

    public static void zza(Parcel parcel, int i, byte b) {
        zzb(parcel, i, 4);
        parcel.writeInt(b);
    }

    public static void zza(Parcel parcel, int i, double d) {
        zzb(parcel, i, 8);
        parcel.writeDouble(d);
    }

    public static void zza(Parcel parcel, int i, float f) {
        zzb(parcel, i, 4);
        parcel.writeFloat(f);
    }

    public static void zza(Parcel parcel, int i, long j) {
        zzb(parcel, i, 8);
        parcel.writeLong(j);
    }

    public static void zza(Parcel parcel, int i, Parcelable parcelable, int i2, boolean z) {
        if (parcelable != null) {
            int zzG = zzG(parcel, i);
            parcelable.writeToParcel(parcel, i2);
            zzH(parcel, zzG);
        }
    }

    public static void zza(Parcel parcel, int i, String str, boolean z) {
        if (str != null) {
            int zzG = zzG(parcel, i);
            parcel.writeString(str);
            zzH(parcel, zzG);
        }
    }

    public static void zza(Parcel parcel, int i, boolean z) {
        zzb(parcel, i, 4);
        parcel.writeInt(z ? 1 : 0);
    }

    private static <T extends Parcelable> void zza(Parcel parcel, T t, int i) {
        int dataPosition = parcel.dataPosition();
        parcel.writeInt(1);
        int dataPosition2 = parcel.dataPosition();
        t.writeToParcel(parcel, i);
        int dataPosition3 = parcel.dataPosition();
        parcel.setDataPosition(dataPosition);
        parcel.writeInt(dataPosition3 - dataPosition2);
        parcel.setDataPosition(dataPosition3);
    }

    public static <T extends Parcelable> void zza$2d7953c6(Parcel parcel, int i, T[] tArr, int i2) {
        if (tArr != null) {
            int zzG = zzG(parcel, i);
            parcel.writeInt(r3);
            for (Parcelable parcelable : tArr) {
                if (parcelable == null) {
                    parcel.writeInt(0);
                } else {
                    zza(parcel, parcelable, i2);
                }
            }
            zzH(parcel, zzG);
        }
    }

    public static void zza$46d65b81(Parcel parcel, Double d) {
        if (d != null) {
            zzb(parcel, 8, 8);
            parcel.writeDouble(d.doubleValue());
        }
    }

    public static void zza$53422a(Parcel parcel, int i, Long l) {
        if (l != null) {
            zzb(parcel, 4, 8);
            parcel.writeLong(l.longValue());
        }
    }

    public static void zza$796a1efa(Parcel parcel, int i, Float f) {
        if (f != null) {
            zzb(parcel, i, 4);
            parcel.writeFloat(f.floatValue());
        }
    }

    public static void zza$cdac282(Parcel parcel, int i, IBinder iBinder) {
        if (iBinder != null) {
            int zzG = zzG(parcel, i);
            parcel.writeStrongBinder(iBinder);
            zzH(parcel, zzG);
        }
    }

    public static void zza$f7bef55(Parcel parcel, int i, Bundle bundle) {
        if (bundle != null) {
            int zzG = zzG(parcel, i);
            parcel.writeBundle(bundle);
            zzH(parcel, zzG);
        }
    }

    private static void zzb(Parcel parcel, int i, int i2) {
        if (i2 >= 65535) {
            parcel.writeInt(-65536 | i);
            parcel.writeInt(i2);
            return;
        }
        parcel.writeInt((i2 << 16) | i);
    }

    public static void zzb$62107c48(Parcel parcel, int i, List<String> list) {
        if (list != null) {
            int zzG = zzG(parcel, 6);
            parcel.writeStringList(list);
            zzH(parcel, zzG);
        }
    }

    public static void zzc(Parcel parcel, int i, int i2) {
        zzb(parcel, i, 4);
        parcel.writeInt(i2);
    }

    public static <T extends Parcelable> void zzc(Parcel parcel, int i, List<T> list, boolean z) {
        if (list != null) {
            int zzG = zzG(parcel, i);
            int size = list.size();
            parcel.writeInt(size);
            for (int i2 = 0; i2 < size; i2++) {
                Parcelable parcelable = (Parcelable) list.get(i2);
                if (parcelable == null) {
                    parcel.writeInt(0);
                } else {
                    zza(parcel, parcelable, 0);
                }
            }
            zzH(parcel, zzG);
        }
    }

    public static int zze(Parcel parcel) {
        return zzG(parcel, 20293);
    }
}
