package com.rachio.iro;

public final class AppVisibilityTracker {
    private static boolean inhibitToasts = false;
    private static boolean isOnScreen = false;

    public static void onActivityResume() {
        isOnScreen = true;
    }

    public static void onActivityPause() {
        isOnScreen = false;
    }

    public static void setInhibitToasts(boolean inhibit) {
        inhibitToasts = inhibit;
    }
}
