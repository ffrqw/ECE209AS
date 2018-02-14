package com.instabug.library.model;

import com.instabug.library.util.InstabugSDKLogger;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;

public final class g implements Serializable {
    private String a;
    private String b;
    private String c;
    private boolean d;
    private IssueType e;
    private String f;
    private String g;
    private String h;
    private String i;
    private b j;
    private ArrayList<b> k;
    private c l;

    public static class a implements Comparator<g> {
        private int a;

        public final /* synthetic */ int compare(Object obj, Object obj2) {
            g gVar = (g) obj;
            g gVar2 = (g) obj2;
            switch (this.a) {
                case 1:
                    return gVar.g().compareTo(gVar2.g());
                case 2:
                    return com.instabug.library.util.g.b(gVar.e()).compareTo(com.instabug.library.util.g.b(gVar2.e()));
                default:
                    throw new IllegalStateException("Message comparator wasn't provided comparison messageIssueType");
            }
        }

        public a() {
            this.a = 2;
        }

        public a(int i) {
            this.a = 2;
            this.a = 1;
        }
    }

    public enum b {
        inbound,
        outbound
    }

    public enum c {
        NOT_SENT,
        SENT,
        SYNCED
    }

    public g(String str, String str2, IssueType issueType, String str3, String str4, String str5, String str6, String str7, b bVar) {
        this.a = str;
        this.b = str3;
        this.e = issueType;
        this.c = str5;
        this.j = bVar;
        InstabugSDKLogger.v(this, "Message with ID " + str + " created with direction " + bVar + " and readAt " + str5);
        if (bVar == b.inbound || !(str5 == null || str5.equals("null"))) {
            InstabugSDKLogger.v(this, "Setting Message with ID " + str + " read");
            this.d = true;
        }
        this.f = str4;
        this.g = str6;
        this.k = new ArrayList();
        this.h = str2;
        this.i = str7;
    }

    public final String a() {
        return this.a;
    }

    public final IssueType b() {
        return this.e;
    }

    public final String c() {
        return this.b;
    }

    public final boolean d() {
        return this.d;
    }

    public final String e() {
        return this.f;
    }

    public final String f() {
        return this.g;
    }

    public final String g() {
        return this.h;
    }

    public final String h() {
        return this.i;
    }

    public final void a(boolean z) {
        this.d = true;
    }

    public final c i() {
        return this.l;
    }

    public final g a(c cVar) {
        this.l = cVar;
        return this;
    }

    public final ArrayList<b> j() {
        return this.k;
    }

    public final boolean k() {
        if (this.j == null || this.j != b.inbound) {
            return false;
        }
        return true;
    }

    public final String toString() {
        return "Message:[" + this.a + ", " + this.h + ", " + this.e + ", " + this.b + ", " + this.f + ", " + this.c + ", " + this.g + ", " + this.i + ", " + this.j + ", " + this.d + ", " + this.k + "]";
    }

    public final boolean l() {
        return this.j != null && this.j == b.inbound;
    }
}
