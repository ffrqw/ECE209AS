package com.instabug.library;

import android.net.Uri;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public final class q {
    private static q k;
    private Uri a;
    private Locale b = null;
    private Runnable d;
    private String e;
    private OnSdkInvokedCallback f;
    private OnSdkDismissedCallback g;
    private StringBuilder h = new StringBuilder();
    private IBGInvocationEvent i = IBGInvocationEvent.IBGInvocationEventShake;
    private IBGCustomTextPlaceHolder j;
    private ArrayList<String> l = new ArrayList();

    private q() {
    }

    public static q a() {
        if (k == null) {
            k = new q();
        }
        return k;
    }

    public final Runnable b() {
        return this.d;
    }

    public final void a(Runnable runnable) {
        this.d = runnable;
    }

    public final OnSdkInvokedCallback c() {
        return this.f;
    }

    public final void a(OnSdkInvokedCallback onSdkInvokedCallback) {
        this.f = onSdkInvokedCallback;
    }

    public final OnSdkDismissedCallback d() {
        return this.g;
    }

    public final void a(OnSdkDismissedCallback onSdkDismissedCallback) {
        this.g = onSdkDismissedCallback;
    }

    public final IBGInvocationEvent e() {
        return this.i;
    }

    public final void a(IBGInvocationEvent iBGInvocationEvent) {
        this.i = iBGInvocationEvent;
    }

    public final Locale f() {
        if (this.b != null) {
            return this.b;
        }
        return Locale.getDefault();
    }

    public final void a(Locale locale) {
        this.b = locale;
    }

    public final Uri h() {
        return this.a;
    }

    public final void a(Uri uri) {
        this.a = uri;
    }

    public final String i() {
        return this.e;
    }

    public final void a(String str) {
        this.e = str;
    }

    public final StringBuilder j() {
        return this.h;
    }

    public final ArrayList<String> l() {
        return this.l;
    }

    public final void a(String... strArr) {
        Collections.addAll(this.l, strArr);
    }

    public final void m() {
        this.l = new ArrayList();
    }

    public final IBGCustomTextPlaceHolder n() {
        return this.j;
    }

    public final void a(IBGCustomTextPlaceHolder iBGCustomTextPlaceHolder) {
        this.j = iBGCustomTextPlaceHolder;
    }
}
