package com.instabug.library.util;

import java.util.regex.Pattern;

public final class e {
    private static Pattern a = Pattern.compile(".+@.+\\.[a-z]+");

    public static boolean a(String str) {
        return a.matcher(str).matches();
    }
}
