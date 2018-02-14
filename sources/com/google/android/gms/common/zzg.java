package com.google.android.gms.common;

import android.util.Log;
import com.google.android.gms.common.internal.zzar;
import com.google.android.gms.common.internal.zzas;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.dynamic.zzn;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

abstract class zzg extends zzas {
    private int zzaAg;

    protected zzg(byte[] bArr) {
        boolean z = false;
        if (bArr.length != 25) {
            Object obj;
            String str = "GoogleCertificates";
            int length = bArr.length;
            int length2 = bArr.length;
            if (bArr == null || bArr.length == 0 || length2 <= 0 || length2 > bArr.length) {
                obj = null;
            } else {
                StringBuilder stringBuilder = new StringBuilder((((length2 + 16) - 1) / 16) * 57);
                int i = 0;
                int i2 = length2;
                int i3 = 0;
                while (i2 > 0) {
                    if (i3 == 0) {
                        if (length2 < 65536) {
                            stringBuilder.append(String.format("%04X:", new Object[]{Integer.valueOf(i)}));
                        } else {
                            stringBuilder.append(String.format("%08X:", new Object[]{Integer.valueOf(i)}));
                        }
                    } else if (i3 == 8) {
                        stringBuilder.append(" -");
                    }
                    stringBuilder.append(String.format(" %02X", new Object[]{Integer.valueOf(bArr[i] & 255)}));
                    int i4 = i2 - 1;
                    i2 = i3 + 1;
                    if (i2 == 16 || i4 == 0) {
                        stringBuilder.append('\n');
                        i2 = 0;
                    }
                    i++;
                    i3 = i2;
                    i2 = i4;
                }
                obj = stringBuilder.toString();
            }
            String valueOf = String.valueOf(obj);
            Log.wtf(str, new StringBuilder(String.valueOf(valueOf).length() + 51).append("Cert hash data has incorrect length (").append(length).append("):\n").append(valueOf).toString(), new Exception());
            bArr = Arrays.copyOfRange(bArr, 0, 25);
            if (bArr.length == 25) {
                z = true;
            }
            zzbo.zzb(z, "cert hash data has incorrect length. length=" + bArr.length);
        }
        this.zzaAg = Arrays.hashCode(bArr);
    }

    protected static byte[] zzcs(String str) {
        try {
            return str.getBytes("ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError(e);
        }
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof zzar)) {
            return false;
        }
        try {
            zzar zzar = (zzar) obj;
            if (zzar.zzoZ() != hashCode()) {
                return false;
            }
            IObjectWrapper zzoY = zzar.zzoY();
            if (zzoY == null) {
                return false;
            }
            return Arrays.equals(getBytes(), (byte[]) zzn.zzE(zzoY));
        } catch (Throwable e) {
            Log.e("GoogleCertificates", "Failed to get Google certificates from remote", e);
            return false;
        }
    }

    abstract byte[] getBytes();

    public int hashCode() {
        return this.zzaAg;
    }

    public final IObjectWrapper zzoY() {
        return zzn.zzw(getBytes());
    }

    public final int zzoZ() {
        return hashCode();
    }
}
