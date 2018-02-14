package org.springframework.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;

public abstract class StringUtils {
    public static boolean hasLength(CharSequence str) {
        return str != null && str.length() > 0;
    }

    public static boolean hasText(String str) {
        if (!hasLength(str)) {
            return false;
        }
        int length = str.length();
        for (int i = 0; i < length; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    public static String[] tokenizeToStringArray(String str, String delimiters) {
        if (str == null) {
            return null;
        }
        StringTokenizer stringTokenizer = new StringTokenizer(str, delimiters);
        Collection arrayList = new ArrayList();
        while (stringTokenizer.hasMoreTokens()) {
            String trim = stringTokenizer.nextToken().trim();
            if (trim.length() > 0) {
                arrayList.add(trim);
            }
        }
        return (String[]) arrayList.toArray(new String[arrayList.size()]);
    }
}
