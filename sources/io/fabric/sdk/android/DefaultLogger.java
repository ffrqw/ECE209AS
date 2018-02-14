package io.fabric.sdk.android;

import android.util.Log;

public final class DefaultLogger implements Logger {
    private int logLevel = 4;

    public final boolean isLoggable$505cff18(int level) {
        return this.logLevel <= level;
    }

    public final void d(String tag, String text, Throwable throwable) {
        if (isLoggable$505cff18(3)) {
            Log.d(tag, text, throwable);
        }
    }

    public final void w(String tag, String text, Throwable throwable) {
        if (isLoggable$505cff18(5)) {
            Log.w(tag, text, throwable);
        }
    }

    public final void e(String tag, String text, Throwable throwable) {
        if (isLoggable$505cff18(6)) {
            Log.e(tag, text, throwable);
        }
    }

    public final void d(String tag, String text) {
        d(tag, text, null);
    }

    public final void w(String tag, String text) {
        w(tag, text, null);
    }

    public final void e(String tag, String text) {
        e(tag, text, null);
    }

    public final void log(int priority, String tag, String msg) {
        log(priority, tag, msg, false);
    }

    public final void log(int priority, String tag, String msg, boolean forceLog) {
        if (forceLog || isLoggable$505cff18(priority)) {
            Log.println(priority, tag, msg);
        }
    }

    public final void v(String tag, String text) {
        if (isLoggable$505cff18(2)) {
            Log.v(tag, text, null);
        }
    }

    public final void i(String tag, String text) {
        if (isLoggable$505cff18(4)) {
            Log.i(tag, text, null);
        }
    }
}
