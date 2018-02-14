package com.instabug.library.model;

import java.io.Serializable;

public final class e implements Serializable {
    private String a;
    private String b;
    private String c;
    private a d;

    public enum a {
        MAIN_SCREENSHOT("main-screenshot"),
        IMAGE("image"),
        AUDIO("audio"),
        ATTACHMENT_FILE("attachment-file");
        
        private final String e;

        private a(String str) {
            this.e = str;
        }

        public final String toString() {
            return this.e;
        }
    }

    public final void a(a aVar) {
        this.d = aVar;
    }

    public final a b() {
        return this.d;
    }

    public final void a(String str) {
        this.c = str;
    }

    public final String c() {
        return this.c;
    }

    public final String d() {
        return this.a;
    }

    public final e b(String str) {
        this.a = str;
        return this;
    }

    public final String e() {
        return this.b;
    }

    public final e c(String str) {
        this.b = str;
        return this;
    }
}
