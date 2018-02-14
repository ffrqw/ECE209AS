package com.instabug.library.model;

import java.io.File;
import java.io.Serializable;

public final class a implements Serializable {
    private String a;
    private int b$6ed2d6d3;
    private String c;
    private File d;

    public enum a {
        ;

        static {
            a$6ed2d6d3 = 1;
            b$6ed2d6d3 = 2;
            c$6ed2d6d3 = 3;
            d$2a82ab2e = new int[]{1, 2, 3};
        }
    }

    public a(String str, int i, String str2, File file) {
        this.a = str;
        this.b$6ed2d6d3 = i;
        this.c = str2;
        this.d = file;
    }

    public final String a() {
        return this.a;
    }

    public final String b() {
        return this.c;
    }

    public final File c() {
        return this.d;
    }
}
