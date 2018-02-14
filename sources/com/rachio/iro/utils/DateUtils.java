package com.rachio.iro.utils;

import java.util.Date;

public class DateUtils {
    public static boolean equals(Date l, Date r) {
        if (l == null && r == null) {
            return true;
        }
        if (l != null && r == null) {
            return false;
        }
        if (l != null || r == null) {
            return l.equals(r);
        }
        return false;
    }
}
