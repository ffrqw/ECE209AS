package com.instabug.library.model;

import java.io.Serializable;

public final class b implements Serializable {
    private String a;
    private String b;
    private boolean c;
    private String d;
    private String e;
    private boolean f = false;

    public b(String str, String str2) {
        this.b = str;
        this.a = str2;
    }

    public final String a() {
        return this.b;
    }

    public final String b() {
        return this.a;
    }

    public final boolean c() {
        return this.f;
    }

    public final void a(boolean z) {
        this.f = z;
    }

    public final String toString() {
        return "Attachment:[" + this.b + ", " + this.a + ", " + this.f + "]";
    }

    public final void b(boolean z) {
        this.c = z;
    }

    public final boolean d() {
        return this.c;
    }

    public final void a(String str) {
        this.e = str;
    }

    public final String e() {
        return this.e;
    }

    public final void b(String str) {
        this.d = str;
    }

    public final String f() {
        return this.d;
    }
}
