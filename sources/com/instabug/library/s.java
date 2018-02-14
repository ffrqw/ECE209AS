package com.instabug.library;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.Date;

public final class s {
    private static SharedPreferences a;
    private static s b;

    private s(Context context) {
        a = context.getSharedPreferences("instabug", 0);
    }

    public static void a(Context context) {
        b = new s(context);
    }

    public static s a() {
        return b;
    }

    public static String b() {
        return a.getString("ib_app_token", null);
    }

    public static void a(String str) {
        a.edit().putString("ib_app_token", str).apply();
    }

    public static String c() {
        return a.getString("ib_default_email", "");
    }

    public static void b(String str) {
        a.edit().putString("ib_default_email", str).apply();
    }

    public static boolean d() {
        return a.getBoolean("ib_device_registered", false);
    }

    public static void a(boolean z) {
        a.edit().putBoolean("ib_device_registered", z).apply();
    }

    public static boolean e() {
        return a.getBoolean("ib_first_run", true);
    }

    public static void b(boolean z) {
        a.edit().putBoolean("ib_first_run", z).apply();
    }

    public static long f() {
        return a.getLong("last_contacted_at", 0);
    }

    public static void a(Date date) {
        a.edit().putLong("last_contacted_at", date.getTime()).apply();
    }

    public static boolean g() {
        return a.getBoolean("ib_pn", true);
    }

    public static void c(boolean z) {
        a.edit().putBoolean("ib_pn", z).apply();
    }

    public static int h() {
        return a.getInt("last_migration_version", 0);
    }

    public static void a(int i) {
        a.edit().putInt("last_migration_version", i).apply();
    }

    public static boolean i() {
        return a.getBoolean("ib_first_dismiss", true);
    }

    public static void d(boolean z) {
        a.edit().putBoolean("ib_first_dismiss", z).apply();
    }

    public final int j() {
        int i = a.getInt("ib_primary_color", -1);
        if (i != -1) {
            return i;
        }
        if (k() == IBGColorTheme.IBGColorThemeDark) {
            b(-7223553);
            return -7223553;
        }
        b(-13792043);
        return -13792043;
    }

    public static void b(int i) {
        a.edit().putInt("ib_primary_color", i).apply();
    }

    public static IBGColorTheme k() {
        return IBGColorTheme.valueOf(a.getString("ib_color_theme", IBGColorTheme.IBGColorThemeLight.name()));
    }

    public final void a(IBGColorTheme iBGColorTheme) {
        if (iBGColorTheme == IBGColorTheme.IBGColorThemeLight) {
            b(-13792043);
        } else {
            b(-7223553);
        }
        a.edit().putString("ib_color_theme", iBGColorTheme.name()).apply();
    }

    public static long l() {
        return a.getLong("TTL", 60);
    }

    public static void a(long j) {
        a.edit().putLong("TTL", j).apply();
    }

    public static String m() {
        return a.getString("ib_default_username", "");
    }

    public static void c(String str) {
        a.edit().putString("ib_default_username", str).apply();
    }

    public static boolean n() {
        return a.getBoolean("ib_audio_recording_visibility", true);
    }

    public static void e(boolean z) {
        a.edit().putBoolean("ib_audio_recording_visibility", z).apply();
    }

    public static boolean o() {
        return a.getBoolean("ib_conversation_sounds", false);
    }

    public static void f(boolean z) {
        a.edit().putBoolean("ib_conversation_sounds", z).apply();
    }

    public static String p() {
        return a.getString("ib_uuid", null);
    }

    public static void d(String str) {
        a.edit().putString("ib_uuid", str).apply();
    }

    public static String q() {
        return a.getString("ib_user_data", "");
    }

    public static void e(String str) {
        a.edit().putString("ib_user_data", str).apply();
    }

    public static boolean r() {
        return a.getBoolean("ib_is_email_enabled", true);
    }

    public static void g(boolean z) {
        a.edit().putBoolean("ib_is_email_enabled", z).apply();
    }

    public static boolean s() {
        return a.getBoolean("ib_should_show_intro_dialog", true);
    }

    public static void h(boolean z) {
        a.edit().putBoolean("ib_should_show_intro_dialog", z).apply();
    }

    public static boolean t() {
        return a.getBoolean("ib_is_email_required", true);
    }

    public static void i(boolean z) {
        a.edit().putBoolean("ib_is_email_required", z).apply();
    }

    public static boolean u() {
        return a.getBoolean("ib_should_take_screenshot", true);
    }

    public static void j(boolean z) {
        a.edit().putBoolean("ib_should_take_screenshot", z).apply();
    }

    public static void f(String str) {
        a.edit().putString("ib_gcm_reg_token", str).apply();
    }

    public static String v() {
        return a.getString("ib_gcm_reg_token", "");
    }

    public static void c(int i) {
        a.edit().putInt("push_notification_icon", i).apply();
    }

    public static int w() {
        return a.getInt("push_notification_icon", -1);
    }
}
