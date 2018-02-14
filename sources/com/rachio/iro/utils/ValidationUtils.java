package com.rachio.iro.utils;

import android.text.TextUtils;
import java.util.regex.Pattern;

public class ValidationUtils {
    private static final Pattern emailPattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", 2);
    private static final Pattern passwordPattern = Pattern.compile("(.{8,40})");
    private static final Pattern usernamePattern = Pattern.compile("^[\\.a-zA-Z0-9_-]{3,50}$");
    private static final Pattern zipCodePattern = Pattern.compile("(^\\d{5}(-\\d{4})?$)|(^[ABCEGHJKLMNPRSTVXY]{1}\\d{1}[A-Z]{1} *\\d{1}[A-Z]{1}\\d{1}$)");

    public static final boolean isValidUsername(String username) {
        return usernamePattern.matcher(username).matches();
    }

    public static final boolean isValidFullname$552c4dfd() {
        return true;
    }

    public static final boolean isValidZoneName(String zoneName) {
        return !TextUtils.isEmpty(zoneName);
    }

    public static final boolean isValidEmail(String email) {
        return emailPattern.matcher(email).matches();
    }

    public static final boolean isValidPassword(String password) {
        return passwordPattern.matcher(password).matches();
    }

    public static final boolean isValidZipCode(String zipCode) {
        return !TextUtils.isEmpty(zipCode);
    }
}
