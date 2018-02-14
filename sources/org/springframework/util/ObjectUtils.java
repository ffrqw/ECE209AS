package org.springframework.util;

import java.util.Arrays;

public abstract class ObjectUtils {
    public static boolean nullSafeEquals(Object o1, Object o2) {
        if (o1 == o2) {
            return true;
        }
        if (o1 == null || o2 == null) {
            return false;
        }
        if (o1.equals(o2)) {
            return true;
        }
        if (o1.getClass().isArray() && o2.getClass().isArray()) {
            if ((o1 instanceof Object[]) && (o2 instanceof Object[])) {
                return Arrays.equals((Object[]) o1, (Object[]) o2);
            }
            if ((o1 instanceof boolean[]) && (o2 instanceof boolean[])) {
                return Arrays.equals((boolean[]) o1, (boolean[]) o2);
            }
            if ((o1 instanceof byte[]) && (o2 instanceof byte[])) {
                return Arrays.equals((byte[]) o1, (byte[]) o2);
            }
            if ((o1 instanceof char[]) && (o2 instanceof char[])) {
                return Arrays.equals((char[]) o1, (char[]) o2);
            }
            if ((o1 instanceof double[]) && (o2 instanceof double[])) {
                return Arrays.equals((double[]) o1, (double[]) o2);
            }
            if ((o1 instanceof float[]) && (o2 instanceof float[])) {
                return Arrays.equals((float[]) o1, (float[]) o2);
            }
            if ((o1 instanceof int[]) && (o2 instanceof int[])) {
                return Arrays.equals((int[]) o1, (int[]) o2);
            }
            if ((o1 instanceof long[]) && (o2 instanceof long[])) {
                return Arrays.equals((long[]) o1, (long[]) o2);
            }
            if ((o1 instanceof short[]) && (o2 instanceof short[])) {
                return Arrays.equals((short[]) o1, (short[]) o2);
            }
        }
        return false;
    }

    public static int nullSafeHashCode(Object obj) {
        if (obj == null) {
            return 0;
        }
        if (obj.getClass().isArray()) {
            int i;
            int i2;
            int nullSafeHashCode;
            if (obj instanceof Object[]) {
                Object[] objArr = (Object[]) obj;
                if (objArr == null) {
                    return 0;
                }
                i = 7;
                i2 = 0;
                while (i2 < objArr.length) {
                    nullSafeHashCode = nullSafeHashCode(objArr[i2]) + (i * 31);
                    i2++;
                    i = nullSafeHashCode;
                }
                return i;
            } else if (obj instanceof boolean[]) {
                boolean[] zArr = (boolean[]) obj;
                if (zArr == null) {
                    return 0;
                }
                r3 = zArr.length;
                i = 7;
                i2 = 0;
                while (i2 < r3) {
                    int i3 = i * 31;
                    if (zArr[i2]) {
                        i = 1231;
                    } else {
                        i = 1237;
                    }
                    i2++;
                    i = i3 + i;
                }
                return i;
            } else if (obj instanceof byte[]) {
                byte[] bArr = (byte[]) obj;
                if (bArr == null) {
                    return 0;
                }
                i = 7;
                i2 = 0;
                while (i2 < bArr.length) {
                    nullSafeHashCode = bArr[i2] + (i * 31);
                    i2++;
                    i = nullSafeHashCode;
                }
                return i;
            } else if (obj instanceof char[]) {
                char[] cArr = (char[]) obj;
                if (cArr == null) {
                    return 0;
                }
                i = 7;
                i2 = 0;
                while (i2 < cArr.length) {
                    nullSafeHashCode = cArr[i2] + (i * 31);
                    i2++;
                    i = nullSafeHashCode;
                }
                return i;
            } else if (obj instanceof double[]) {
                double[] dArr = (double[]) obj;
                if (dArr == null) {
                    return 0;
                }
                r3 = dArr.length;
                i = 7;
                i2 = 0;
                while (i2 < r3) {
                    i *= 31;
                    r4 = Double.doubleToLongBits(dArr[i2]);
                    i2++;
                    i = ((int) (r4 ^ (r4 >>> 32))) + i;
                }
                return i;
            } else if (obj instanceof float[]) {
                float[] fArr = (float[]) obj;
                if (fArr == null) {
                    return 0;
                }
                i = 7;
                i2 = 0;
                while (i2 < fArr.length) {
                    nullSafeHashCode = Float.floatToIntBits(fArr[i2]) + (i * 31);
                    i2++;
                    i = nullSafeHashCode;
                }
                return i;
            } else if (obj instanceof int[]) {
                int[] iArr = (int[]) obj;
                if (iArr == null) {
                    return 0;
                }
                i = 7;
                i2 = 0;
                while (i2 < iArr.length) {
                    nullSafeHashCode = iArr[i2] + (i * 31);
                    i2++;
                    i = nullSafeHashCode;
                }
                return i;
            } else if (obj instanceof long[]) {
                long[] jArr = (long[]) obj;
                if (jArr == null) {
                    return 0;
                }
                r3 = jArr.length;
                i = 7;
                i2 = 0;
                while (i2 < r3) {
                    r4 = jArr[i2];
                    i2++;
                    i = ((int) (r4 ^ (r4 >>> 32))) + (i * 31);
                }
                return i;
            } else if (obj instanceof short[]) {
                short[] sArr = (short[]) obj;
                if (sArr == null) {
                    return 0;
                }
                i = 7;
                i2 = 0;
                while (i2 < sArr.length) {
                    nullSafeHashCode = sArr[i2] + (i * 31);
                    i2++;
                    i = nullSafeHashCode;
                }
                return i;
            }
        }
        return obj.hashCode();
    }
}
