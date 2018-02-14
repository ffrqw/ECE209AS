package com.instabug.library.model;

import android.net.Uri;
import com.instabug.library.e.c.e;
import com.instabug.library.model.e.a;
import com.instabug.library.util.InstabugSDKLogger;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public final class d implements Serializable {
    private String a;
    private String b;
    private String c;
    private String d;
    private IssueType e;
    private ArrayList<e> f;
    private ArrayList<e> g = new ArrayList(6);

    public d(String str) {
        this.a = str;
        this.b = "offline_issue_occurrence_id";
    }

    public final IssueType a() {
        return this.e;
    }

    public final void a(IssueType issueType) {
        this.e = issueType;
    }

    public final void a(Uri uri, a aVar) {
        if (uri == null) {
            InstabugSDKLogger.w(this, "Adding attachment with a null Uri, ignored.");
            return;
        }
        e eVar = new e();
        eVar.b(uri.getLastPathSegment());
        eVar.c(uri.getPath());
        eVar.a(aVar);
        this.g.add(eVar);
    }

    public final List<e> b() {
        return this.g;
    }

    public final void a(ArrayList<e> arrayList) {
        this.f = arrayList;
    }

    public final ArrayList<e> c() {
        return this.f;
    }

    public final String d() {
        return String.valueOf(this.a);
    }

    public final void a(String str) {
        this.b = str;
    }

    public final String e() {
        return this.b;
    }

    public final String f() {
        return this.c;
    }

    public final void b(String str) {
        if (str != null && str.length() > 190) {
            InstabugSDKLogger.w(this, "Email field too long, sending first set of characters only");
            str = str.substring(0, 190);
        }
        this.c = str;
    }

    public final String g() {
        return this.d;
    }

    public final void c(String str) {
        this.d = str;
    }

    public final String toString() {
        return "Internal Id: " + this.a + " Instabug Occurrence Id:" + this.b;
    }
}
