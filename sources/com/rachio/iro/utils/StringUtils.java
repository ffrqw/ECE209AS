package com.rachio.iro.utils;

import android.text.TextUtils;
import java.text.DecimalFormat;
import java.util.List;

public class StringUtils {
    public static final String arrayToCommaList(Object[] array) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < array.length; i++) {
            sb.append(array[i].toString());
            if (i != array.length - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    public static boolean equals(CharSequence cs1, CharSequence cs2) {
        if (cs1 == cs2) {
            return true;
        }
        if (cs1 == null || cs2 == null) {
            return false;
        }
        if ((cs1 instanceof String) && (cs2 instanceof String)) {
            return cs1.equals(cs2);
        }
        int max = Math.max(cs1.length(), cs2.length());
        if ((cs1 instanceof String) && (cs2 instanceof String)) {
            return ((String) cs1).regionMatches(false, 0, (String) cs2, 0, max);
        }
        int i = 0;
        int i2 = 0;
        while (true) {
            int i3 = max - 1;
            if (max <= 0) {
                return true;
            }
            max = i2 + 1;
            char charAt = cs1.charAt(i2);
            i2 = i + 1;
            if (charAt != cs2.charAt(i)) {
                return false;
            }
            i = i2;
            i2 = max;
            max = i3;
        }
    }

    public static boolean isNotBlank(CharSequence cs) {
        return !TextUtils.isEmpty(cs);
    }

    public static String join(String delimiter, List<String> values) {
        return join(delimiter, (String[]) values.toArray(new String[values.size()]));
    }

    public static String join(String delimiter, String... values) {
        if (values == null) {
            return null;
        }
        if (delimiter == null) {
            delimiter = "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (String value : values) {
            if (!(value == null || value.length() == 0)) {
                if (stringBuilder.length() > 0) {
                    stringBuilder.append(delimiter);
                }
                stringBuilder.append(value);
            }
        }
        return stringBuilder.toString();
    }

    public static String nullEmptyString(String string) {
        if (TextUtils.isEmpty(string)) {
            return null;
        }
        return string;
    }

    public static String readableNumber(int number) {
        return new DecimalFormat("###,###,##0").format((long) number);
    }
}
