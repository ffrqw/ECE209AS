package com.instabug.library.e;

import android.net.Uri.Builder;
import com.instabug.library.util.InstabugSDKLogger;
import com.squareup.mimecraft.Multipart;
import com.squareup.mimecraft.Multipart.Type;
import com.squareup.mimecraft.Part;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import org.json.JSONException;
import org.json.JSONObject;

public final class c {
    private String a;
    private String b;
    private d c;
    private int d$656280e9;
    private ArrayList<e> e;
    private ArrayList<e> f;
    private c g;
    private File h;

    public interface a<T, K> {
        void a(K k);

        void b(T t);
    }

    public enum b {
        ReportIssue("/issues"),
        UploadFile("/attachments"),
        RegisterPushNotifications("/push_tokens"),
        AppSettings("/features"),
        SendSession("/sessions"),
        SendMessage("/issues/:issue_number/emails"),
        SyncMessages("/issues/emails/sync");
        
        private final String h;

        private b(String str) {
            this.h = str;
        }

        public final String toString() {
            return this.h;
        }
    }

    public static class c {
        private String a;
        private String b;
        private String c;
        private String d;

        public c(String str, String str2, String str3, String str4) {
            this.a = str;
            this.b = str2;
            this.c = str3;
            this.d = str4;
        }

        public final String a() {
            return this.a;
        }

        public final String b() {
            return this.b;
        }

        public final String c() {
            return this.c;
        }

        public final String d() {
            return this.d;
        }
    }

    public enum d {
        Get("GET"),
        Post("POST"),
        put("PUT");
        
        private final String d;

        private d(String str) {
            this.d = str;
        }

        public final String toString() {
            return this.d;
        }
    }

    public static class e implements Serializable {
        private String a;
        private Object b;

        public e(String str, Object obj) {
            this.a = str;
            this.b = obj;
        }

        public final Object a() {
            return this.b;
        }

        public final String b() {
            return this.a;
        }
    }

    public c(b bVar, int i) {
        this.b = bVar.toString();
        this.a = "https://api.instabug.com/api/sdk/v2" + this.b;
        this.d$656280e9 = i;
        i();
    }

    public c(String str, int i) {
        this.a = str;
        this.d$656280e9 = i;
        i();
    }

    private void i() {
        this.e = new ArrayList();
        this.f = new ArrayList();
    }

    public final String a() {
        return this.b;
    }

    public final void a(String str) {
        this.b = str;
        this.a = "https://api.instabug.com/api/sdk/v2" + this.b;
    }

    public final String b() {
        return this.a + j();
    }

    public final void a(d dVar) {
        this.c = dVar;
    }

    public final d c() {
        return this.c;
    }

    public final int d$5363e898() {
        return this.d$656280e9;
    }

    public final c a(String str, Object obj) throws JSONException {
        if (this.c.equals(d.Get)) {
            this.e.add(new e(str, obj));
        } else {
            this.f.add(new e(str, obj));
        }
        return this;
    }

    public final ArrayList<e> a(ArrayList<e> arrayList) {
        this.f = arrayList;
        return arrayList;
    }

    public final ArrayList<e> e() {
        return this.f;
    }

    private String j() {
        Builder builder = new Builder();
        Iterator it = this.e.iterator();
        while (it.hasNext()) {
            e eVar = (e) it.next();
            builder.appendQueryParameter(eVar.b(), eVar.a().toString());
        }
        return builder.toString();
    }

    public final String f() {
        try {
            JSONObject jSONObject = new JSONObject();
            Iterator it = this.f.iterator();
            while (it.hasNext()) {
                e eVar = (e) it.next();
                jSONObject.put(eVar.b(), eVar.a());
            }
            return jSONObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    public final Multipart a(c cVar, ArrayList<e> arrayList) {
        Multipart.Builder type = new Multipart.Builder().type(Type.FORM);
        type.addPart(new Part.Builder().contentDisposition("file; name=\"" + cVar.a() + "\"; filename=\"" + cVar.b() + "\"").contentType(cVar.d()).body(new File(cVar.c())).build());
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            e eVar = (e) it.next();
            InstabugSDKLogger.v(this, "requestParameter.getKey(): " + eVar.b());
            type.addPart(new Part.Builder().contentDisposition("form-data; name=\"" + eVar.b() + "\";").contentType("text/plain").body(eVar.a().toString()).build());
        }
        return type.build();
    }

    public final c g() {
        return this.g;
    }

    public final c a(c cVar) {
        this.g = cVar;
        return this;
    }

    public final File h() {
        return this.h;
    }

    public final c b(String str) {
        this.h = new File(str);
        return this;
    }
}
