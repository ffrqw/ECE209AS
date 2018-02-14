package com.instabug.library.internal.d.a;

import com.instabug.library.util.InstabugSDKLogger;

public final class a extends g<String, com.instabug.library.model.a> {
    public a(String str) {
        super(str);
    }

    public final void a() {
        for (com.instabug.library.model.a aVar : b()) {
            InstabugSDKLogger.d(b.class, "Delete file: " + aVar.c().getPath() + "," + aVar.c().delete());
        }
        super.a();
    }
}
