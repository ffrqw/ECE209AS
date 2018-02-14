package com.instabug.library.model;

import java.io.Serializable;

public final class i implements Serializable {
    private int a;
    private String b;
    private int c;

    public final int a() {
        return this.a;
    }

    public final void a(int i) {
        this.a = i;
    }

    public final String b() {
        return this.b;
    }

    public final void a(String str) {
        this.b = str;
    }

    public final int c() {
        return this.c;
    }

    public final void b(int i) {
        this.c = i;
    }

    public final boolean equals(Object obj) {
        if (obj != null && (obj instanceof i) && this.a == ((i) obj).a) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return this.a;
    }
}
