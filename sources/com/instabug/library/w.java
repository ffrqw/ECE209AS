package com.instabug.library;

import com.instabug.library.model.k;
import java.util.ArrayList;

public final class w {
    private ArrayList<k> a = new ArrayList();
    private String b;

    public final void a(String str, int i) {
        k kVar = new k();
        this.b = str;
        kVar.a(System.currentTimeMillis());
        kVar.a(i);
        kVar.c(str);
        this.a.add(kVar);
    }

    public final String a() {
        return this.b;
    }

    public final void a(String str, String str2, String str3) {
        k kVar = new k();
        kVar.a(2561);
        kVar.c(str);
        kVar.b(str3);
        kVar.a(System.currentTimeMillis());
        if (str2 != null) {
            kVar.a(str2);
        }
        this.a.add(kVar);
    }

    public final String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        int i = 0;
        if (this.a.size() > 100) {
            i = this.a.size() - 100;
        }
        for (int i2 = i; i2 < this.a.size(); i2++) {
            ((k) this.a.get(i2)).a(stringBuilder);
            if (i2 + 1 != this.a.size()) {
                stringBuilder.append("\n");
            }
        }
        return stringBuilder.toString();
    }
}
