package com.instabug.library.util;

import com.instabug.library.IBGCustomTextPlaceHolder;
import com.instabug.library.IBGCustomTextPlaceHolder.Key;
import com.instabug.library.q;

public final class l {
    public static String a(Key key, String str) {
        IBGCustomTextPlaceHolder n = q.a().n();
        if (n == null) {
            return str;
        }
        String str2 = n.get(key);
        if (str2 == null || str2.trim().equals("")) {
            return str;
        }
        return str2;
    }
}
