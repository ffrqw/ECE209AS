package com.instabug.library.model;

import java.io.Serializable;

public final class j implements Serializable {
    private int a;
    private String b;
    private long c;

    public j(int i, String str, long j) {
        this.a = i;
        this.b = str;
        this.c = j;
    }

    public final int a() {
        return this.a;
    }

    public final String b() {
        return this.b;
    }

    public final long c() {
        return this.c;
    }

    public final String toString() {
        return "id: " + this.a + ", startedAt: " + this.b + ", duration: " + this.c;
    }
}
