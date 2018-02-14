package com.instabug.library.model;

import android.os.Build.VERSION;
import com.instabug.library.Instabug;
import com.instabug.library.e.c.e;
import com.instabug.library.internal.a.b;
import com.instabug.library.q;
import com.instabug.library.s;
import com.instabug.library.t;
import com.instabug.library.u;
import com.instabug.library.w;
import java.util.ArrayList;

public final class f {
    private static f a;
    private u b;
    private w c;

    public static f a(u uVar, w wVar) {
        if (a == null) {
            a = new f(uVar, wVar);
        }
        return a;
    }

    private f(u uVar, w wVar) {
        this.b = uVar;
        this.c = wVar;
    }

    public final void a(d dVar, String str, b bVar) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new e("uuid", bVar.l()));
        arrayList.add(new e("type", dVar.a().toString()));
        arrayList.add(new e("application_token", s.b()));
        arrayList.add(new e("device", b.a()));
        arrayList.add(new e("os", new StringBuilder(Instabug.SDK_LEVEL).append(Integer.toString(VERSION.SDK_INT)).toString()));
        arrayList.add(new e("email", dVar.f()));
        arrayList.add(new e("comment", dVar.g()));
        arrayList.add(new e("carrier", bVar.i()));
        arrayList.add(new e("app_version", bVar.j()));
        arrayList.add(new e("battery_level", bVar.g()));
        arrayList.add(new e("battery_state", bVar.h()));
        arrayList.add(new e("created_at", Long.toString(System.currentTimeMillis() / 1000)));
        arrayList.add(new e("attachments_count", Long.toString((long) dVar.b().size())));
        arrayList.add(new e("sdk_version", Instabug.SDK_VERSION));
        arrayList.add(new e("wifi", bVar.m()));
        arrayList.add(new e("memory_free", bVar.q()));
        arrayList.add(new e("memory_used", bVar.o()));
        arrayList.add(new e("memory_total", null));
        arrayList.add(new e("storage_free", b.s()));
        arrayList.add(new e("storage_used", b.r()));
        arrayList.add(new e("storage_total", b.t()));
        arrayList.add(new e("user_data", Instabug.getUserData()));
        arrayList.add(new e("locale", bVar.u()));
        arrayList.add(new e("density", bVar.d()));
        arrayList.add(new e("orientation", bVar.c()));
        arrayList.add(new e("screen_size", bVar.e()));
        arrayList.add(new e("user_steps", this.c.toString()));
        arrayList.add(new e("current_view", this.c.a()));
        arrayList.add(new e("methods_log", q.a().j().toString()));
        arrayList.add(new e("device_rooted", bVar.f()));
        String str2 = "duration";
        long b = t.a().b();
        StringBuilder stringBuilder = new StringBuilder();
        int i = ((int) b) % 60;
        b /= 60;
        int i2 = ((int) b) % 60;
        int i3 = ((int) (b / 60)) % 60;
        if (i3 <= 9) {
            stringBuilder.append("0");
        }
        stringBuilder.append(i3);
        stringBuilder.append(":");
        if (i2 <= 9) {
            stringBuilder.append("0");
        }
        stringBuilder.append(i2);
        stringBuilder.append(":");
        if (i <= 9) {
            stringBuilder.append("0");
        }
        stringBuilder.append(i);
        arrayList.add(new e(str2, stringBuilder.toString()));
        arrayList.add(new e("console_log", bVar.n()));
        arrayList.add(new e("instabug_log", str));
        arrayList.add(new e("tags", u.H()));
        dVar.a(arrayList);
    }
}
